package com.maple.game.osee.manager.huiwei;

import android.util.Base64;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.maple.common.login.event.login.LoginEvent;
import com.maple.common.login.event.login.LoginEventManager;
import com.maple.common.login.manager.LoginManager;
import com.maple.common.login.proto.LoginMessage;
import com.maple.common.login.util.WxLoginUtil;
import com.maple.common.login.util.wxentity.WxAccessToken;
import com.maple.common.login.util.wxentity.WxUserInfo;
import com.maple.database.config.redis.RedisHelper;
import com.maple.database.data.entity.UserEntity;
import com.maple.database.data.mapper.UserAuthenticationMapper;
import com.maple.database.data.mapper.UserMapper;
import com.maple.database.log.entity.LoginLogEntity;
import com.maple.database.log.mapper.LoginLogMapper;
import com.maple.engine.container.DataContainer;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.config.HuiWeiConfig;
import com.maple.game.osee.dao.data.entity.AgentEntity;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.dao.data.mapper.AgentMapper;
import com.maple.game.osee.dao.data.mapper.OseePlayerMapper;
import com.maple.game.osee.dao.log.mapper.AppRewardRankMapper;
import com.maple.game.osee.dao.log.mapper.OseeForgingLogMapper;
import com.maple.game.osee.dao.log.mapper.OseeRechargeLogMapper;
import com.maple.game.osee.entity.fishing.csv.file.ForgingConfig;
import com.maple.game.osee.entity.fishing.csv.file.ForgingGroupConfig;
import com.maple.game.osee.entity.lobby.PhoneCheck;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.fishing.util.FishingUtil;
import com.maple.game.osee.manager.lobby.CommonLobbyManager;
import com.maple.game.osee.proto.HwLoginMessage;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.util.RandomUtil;
import com.maple.network.event.exit.ExitEventManager;
import com.maple.network.manager.NetManager;
import com.maple.network.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Component
@Slf4j
public class LoginSignManager {

    @Autowired
    private LoginEventManager loginEventManager;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private ExitEventManager exitEventManager;


    @Autowired
    private OseePlayerMapper oseePlayerMapper;

    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private UserAuthenticationMapper authenticationMapper;

    @Autowired
    private OseeRechargeLogMapper oseeRechargeLogMapper;

    @Autowired
    private OseeForgingLogMapper oseeForgingLogMapper;

    @Autowired
    private AppRewardRankMapper appRewardRankMapper;

    private static final Logger logger = LoggerFactory.getLogger(LoginSignManager.class);

    /**
     * 使用加密算法规则
     */
    private static final String SIGN_ALGORITHMS = "SHA256WithRSA";

    public static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";

    /**
     * 字符串编码
     */
    private static final String CHARSET = "UTF-8";

    /**
     * 邀请码位数限制
     */
    private static long INVITE_CODE_LIMIT = 100000;

    /**
     * 登录成功处理
     */
    private void loginSuccess(ServerUser user, boolean saveOnline) {
        UserContainer.putServerUser(user);
        user.setLoginSuccess(true);

        UserContainer.initUser(user); // 初始化玩家数据到内存
        String onlineToken = null;
        if (saveOnline) {
            String key = "OnlineToken:" + user.getId();
            long token = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
            onlineToken = Long.toHexString(token);
            RedisHelper.set(key, onlineToken);
        }

        if (user.isLoginSuccess()) {
            if (user.getLoginLogEntity() == null && saveOnline) { // 未保存过登录日志，需要记录登录日志
                LoginLogEntity loginLog = new LoginLogEntity();
                loginLog.setUserId(user.getId());
                loginLogMapper.save(loginLog);

                user.setLoginLogEntity(loginLog);
            }

            HwLoginMessage.HwLoginSuccessResponse.Builder builder = HwLoginMessage.HwLoginSuccessResponse.newBuilder();
            builder.setId(user.getId());
            builder.setName(user.getNickname());
            builder.setHeadIndex(user.getEntity().getHeadIndex());
            builder.setHeadUrl(user.getEntity().getHeadUrl());
            builder.setSex(user.getEntity().getSex());
            builder.setOnlineToken(onlineToken);
            NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_HW_LOGIN_SUCCESS_REAUTH_RESPONSE_VALUE, builder.build(),
                    user);
            loginEventManager.notifyListener(new LoginEvent(user)); // 发送登录监听事件
        }
    }

    /**
     * 发送华为重新授权的响应
     */
    private void sendHwReAuthResponse(ServerUser user) {
        HwLoginMessage.HwLoginResponse.Builder builder = HwLoginMessage.HwLoginResponse.newBuilder();
        // 返回登录码无效的响应，通知前端重新拉取授权
        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_HW_LOGIN_REAUTH_RESPONSE_VALUE, builder, user);
    }

    /**
     * 发送退出服务器消息
     */
    private void sendLogoutResponse(ServerUser user, int result) {
        LoginMessage.LogoutResponse resp = LoginMessage.LogoutResponse.newBuilder().setResult(result).build();
        NetManager.sendMessage(LoginMessage.LoginMsgCode.S_C_LOGOUT_RESPONSE_VALUE, resp, user);
    }

    /**
     * 签名字符串（SHA256WithRSA）
     *
     * @param content 待签名字符串
     * @return 字符串的签名
     */
    public void rsaSign(String content, ServerUser user) {
        if (null == content || HuiWeiConfig.HW_PAY_SECRET == null) {
            sendHwPayCheckResponse(user);
            return;
        }
        try {
            PKCS8EncodedKeySpec priPKCS8 =
                    new PKCS8EncodedKeySpec(Base64.decode(HuiWeiConfig.HW_PAY_SECRET, Base64.DEFAULT));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(CHARSET));
            byte[] signed = signature.sign();
            // 使用BASE64进行编码
            sendHwPayCheckSuccessResponse(user, Base64.encodeToString(signed, Base64.DEFAULT));
            return;
        } catch (Exception e) {
            // 这里是安全算法，为避免出现异常时泄露加密信息，这里不打印具体日志
            e.getMessage();
            sendHwPayCheckResponse(user);
        }
        return;
    }

    /**
     * 发送华为重新生成签名的响应
     */
    private void sendHwPayCheckResponse(ServerUser user) {
        HwLoginMessage.HwPayCheckAgAinResponse.Builder builder = HwLoginMessage.HwPayCheckAgAinResponse.newBuilder();
        // 返回登录码无效的响应，通知前端重新拉取授权
        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_HW_CHENK_PAY_FAILED_RESPONSE_VALUE, builder, user);
    }

    /**
     * 发送华为生成签名成功的响应
     */
    private void sendHwPayCheckSuccessResponse(ServerUser user, String sign) {
        HwLoginMessage.HwPayChenkResponse.Builder builder = HwLoginMessage.HwPayChenkResponse.newBuilder();
        builder.setSign(sign);
        // 返回成功的响应，通知前端重新拉取授权
        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_HW_CHENK_PAY_RESPONSE_VALUE, builder, user);
    }

    /**
     * 校验签名信息
     *
     * @param content   待校验字符串
     * @param sign      签名字符串
     * @param publicKey 公钥
     * @param signtype  加密类型
     */

    public static boolean doCheck(String content, String sign, String publicKey, String signtype) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = org.apache.commons.codec.binary.Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = null;
            if ("RSA256".equals(signtype)) {
                signature = java.security.Signature.getInstance(SIGN_SHA256RSA_ALGORITHMS);
            } else {
                signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            }
            signature.initVerify(pubKey);
            signature.update(content.getBytes("utf-8"));
            boolean bverify = signature.verify(org.apache.commons.codec.binary.Base64.decodeBase64(sign));
            return bverify;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据参数map获取待签名字符串
     *
     * @param params            待签名参数map
     * @param includeEmptyParam 是否包含值为空的参数： 与HMS-SDK支付能力交互的签名或验签，需要为false（不包含空参数）
     *                          由华为支付服务器回调给开发者的服务器的支付结果验签，需要为true（包含空参数）
     * @return 待签名字符串
     */
    private static String getNoSign(Map<String, Object> params, boolean includeEmptyParam) {
        StringBuilder content = new StringBuilder();
        // 按照key做排序
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String value = null;
        Object object = null;
        boolean isFirstParm = true;
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            object = params.get(key);

            if (object == null) {
                value = "";
            } else if (object instanceof String) {
                value = (String) object;
            } else {
                value = String.valueOf(object);
            }
            // 拼接成key=value&key=value&....格式的字符串
            if (includeEmptyParam || !TextUtils.isEmpty(value)) {
                content.append((isFirstParm ? "" : "&") + key + "=" + value);
                isFirstParm = false;
            } else {
                continue;
            }
        }
        // 待签名的字符串
        return content.toString();
    }

    public void checkUser(ServerUser user) {
        // OseePlayerEntity entity = oseePlayerMapper.findByUserId(user.getId());
        // if (entity == null) {
        // entity = new OseePlayerEntity();
        // entity.setUserId(user.getId());
        // entity.setMoney(10000);
        // entity.setYuGu(10);
        // entity.setSkillLock(20);
        // entity.setSkillFrozen(20);
        // entity.setSkillFast(3);
        // entity.setFenShen(3);
        // entity.setSkillEle(3);
        // entity.setSkillCrit(3);
        // // entity.setVipLevel(5);
        // // long[] vipMoney = PlayerManager.VIP_MONEY;
        // // entity.setRechargeMoney(vipMoney[5 - 1]); // 设置充值的金额为对应等级之上
        // // PlayerManager.sendVipLevelResponse(UserContainer.getUserById(entity.getUserId()));
        // // 初始炮台等级为最低等级
        // entity.setBatteryLevel(1000000);
        // oseePlayerMapper.save(entity);
        // }
        // user.putExpertData(OseePlayerEntity.EntityId, entity);
        // PlayerManager.updateEntities.add(entity);
    }

    /**
     * 登录成功处理
     */
    public void banAgent(ServerUser user, long userId, long agentId) {
        if (userId == 0 || String.valueOf(userId) == null) {
            sendBanAgentResponse(user, false);
            return;
        }
        if (agentId == 0 || String.valueOf(agentId) == null) {
            sendBanAgentResponse(user, false);
            return;
        }
        UserEntity userEntity = userMapper.findById(userId);
        if (userEntity == null) {
            sendBanAgentResponse(user, false);
            return;
        }
        AgentEntity agentEntity1 = agentMapper.getByPlayerId(userId);
        if (agentEntity1 == null) {
            agentEntity1 = new AgentEntity();
            agentEntity1.setPlayerId(userId);
            agentEntity1.setPlayerName(user.getNickname());
            agentEntity1.setAgentLevel(5);
            agentEntity1.setState(0);
            agentMapper.save(agentEntity1);
        }
        if (agentEntity1.getAgentPlayerId() != null) {
            sendBanAgentResponse(user, false);
            return;
        }
        AgentEntity agentEntity = agentMapper.getAgentById(agentId);
        agentMapper.updateByPlayerId(userId, agentId);
        sendBanAgentResponse(user, true);
        return;
    }

    /**
     * 第一次进入游戏
     */
    public void firstJoin(ServerUser user, long userId) {
        // HwLoginMessage.FirstJoinResponse.Builder builder = HwLoginMessage.FirstJoinResponse.newBuilder();
        // OseePlayerEntity entity = oseePlayerMapper.findByUserId(userId);
        // if (entity == null) {
        // entity = new OseePlayerEntity();
        // entity.setUserId(userId);
        // entity.setMoney(10000);
        // entity.setYuGu(10);
        // entity.setSkillLock(20);
        // entity.setSkillFrozen(20);
        // entity.setSkillFast(3);
        // entity.setFenShen(3);
        // entity.setSkillEle(3);
        // entity.setSkillCrit(3);
        // // entity.setVipLevel(5);
        // // long[] vipMoney = PlayerManager.VIP_MONEY;
        // // entity.setRechargeMoney(vipMoney[5 - 1]); // 设置充值的金额为对应等级之上
        // // PlayerManager.sendVipLevelResponse(UserContainer.getUserById(entity.getUserId()));
        // // 初始炮台等级为最低等级
        // entity.setBatteryLevel(1000000);
        // oseePlayerMapper.save(entity);
        // logger.info("没有实体" + userId);
        // builder.setIsFirstJoin(0);
        // } else {
        // if (entity.getMoney() == 0 && entity.getBatteryLevel() == 1) {
        // entity.setUserId(userId);
        // entity.setMoney(10000);
        // entity.setYuGu(10);
        // entity.setSkillLock(20);
        // entity.setSkillFrozen(20);
        // entity.setSkillFast(3);
        // entity.setFenShen(3);
        // entity.setSkillEle(3);
        // entity.setSkillCrit(3);
        // // entity.setVipLevel(5);
        // // long[] vipMoney = PlayerManager.VIP_MONEY;
        // // entity.setRechargeMoney(vipMoney[5 - 1]); // 设置充值的金额为对应等级之上
        // // PlayerManager.sendVipLevelResponse(UserContainer.getUserById(entity.getUserId()));
        // entity.setBatteryLevel(1000000);
        // oseePlayerMapper.update(entity);
        // }
        // logger.info("有实体" + userId);
        // logger.info("有实体" + entity.getId());
        // builder.setIsFirstJoin(1);
        // }
        // user.putExpertData(OseePlayerEntity.EntityId, entity);
        // PlayerManager.updateEntities.add(entity);
        //
        // int x = getUserTNew(user, 1);
        // RedisHelper.set("USER_T_PEAK_VALUE_NEW" + user.getId(), String.valueOf(x));
        // // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(reliefMoney));
        //
        // HwLoginMessage.ItemDataProto1.Builder builder1 = HwLoginMessage.ItemDataProto1.newBuilder();
        // builder1.setItemId(ItemId.MONEY.getId());
        // builder1.setItemNum(500000);
        // builder.addItems(builder1);
        // builder1.setItemId(ItemId.YU_GU.getId());
        // builder1.setItemNum(10);
        // builder.addItems(builder1);
        // builder1.setItemId(ItemId.SKILL_LOCK.getId());
        // builder1.setItemNum(20);
        // builder.addItems(builder1);
        // builder1.setItemId(ItemId.SKILL_FROZEN.getId());
        // builder1.setItemNum(20);
        // builder.addItems(builder1);
        // sendFirstJoinResponse(user, builder);
    }

    public static int getUserTNew(ServerUser user, int roomIndex) {
        int x = 0;
        long cx = new Double(RedisUtil.val("ALL_CX_USER" + user.getId(), 0D)).longValue();
        long bankNumber = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L) + 500000 + 800000;
        double banlace = 0L;
        if (cx > 0) {
            banlace = bankNumber;
        } else {
            banlace = cx * FishingUtil.cxPercentage[(roomIndex - 1) * 2] * 0.01 + bankNumber;
        }
        ArrayList<Long> list = new ArrayList<Long>();
        list.add(new Double(RedisUtil.val("ALL_XH_200-max" + user.getId(), 0D)).longValue());
        list.add(new Double(RedisUtil.val("ALL_XH_1-50" + user.getId(), 0D)).longValue());
        list.add(new Double(RedisUtil.val("ALL_XH_50-100" + user.getId(), 0D)).longValue());
        list.add(new Double(RedisUtil.val("ALL_XH_100-200" + user.getId(), 0D)).longValue());
        Collections.sort(list);
        for (int i = 0; i >= 0; i++) {
            int a = ThreadLocalRandom.current().nextInt(1, 4);
            switch (a) {
                case 1: {
                    RedisHelper.set("USER_T_STATUS_NEW" + user.getId(), "1");
                    x = ThreadLocalRandom.current().nextInt(
                            new Double(FishingUtil.peakMinNum1[(roomIndex - 1) * 2]).intValue(),
                            new Double(FishingUtil.peakMaxNum1[(roomIndex - 1) * 2]).intValue());
                    int b = ThreadLocalRandom.current().nextInt(1, 4);
                    if ((FishingUtil.peakMax1[(roomIndex - 1) * 2] * banlace
                            - PlayerManager.getPlayerMoney(user)) < list.get(3)) {
                        int c = ThreadLocalRandom.current().nextInt(1, 4);
                        switch (c) {
                            case 1: {
                                RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                        String.valueOf(FishingUtil.burstOne[6]));
                                RedisHelper.set("USER_T_MIN_NEW" + user.getId(), String.valueOf(FishingUtil.recOne[6]));
                                break;
                            }
                            case 2: {
                                RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                        String.valueOf(FishingUtil.burstOne[7]));
                                RedisHelper.set("USER_T_MIN_NEW" + user.getId(), String.valueOf(FishingUtil.recOne[7]));
                                break;
                            }
                            case 3: {
                                RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                        String.valueOf(FishingUtil.burstOne[8]));
                                RedisHelper.set("USER_T_MIN_NEW" + user.getId(), String.valueOf(FishingUtil.recOne[8]));
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    } else {
                        if (PlayerManager.getPlayerMoney(user) > FishingUtil.peakMax1[(roomIndex - 1) * 2] * banlace) {
                            int c = ThreadLocalRandom.current().nextInt(1, 4);
                            switch (c) {
                                case 1: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[6]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[6]));
                                    break;
                                }
                                case 2: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[7]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[7]));
                                    break;
                                }
                                case 3: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[8]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[8]));
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        } else if (PlayerManager.getPlayerMoney(user) < FishingUtil.peakMin1[(roomIndex - 1) * 2]
                                * banlace) {
                            int c = ThreadLocalRandom.current().nextInt(1, 4);
                            switch (c) {
                                case 1: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[0]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[0]));
                                    break;
                                }
                                case 2: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[1]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[1]));
                                    break;
                                }
                                case 3: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[2]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[2]));
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        } else {
                            switch (b) {
                                case 1: {
                                    int c = ThreadLocalRandom.current().nextInt(1, 4);
                                    switch (c) {
                                        case 1: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[0]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[0]));
                                            break;
                                        }
                                        case 2: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[1]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[1]));
                                            break;
                                        }
                                        case 3: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[2]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[2]));
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 2: {
                                    int c = ThreadLocalRandom.current().nextInt(1, 4);
                                    switch (c) {
                                        case 1: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[3]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[3]));
                                            break;
                                        }
                                        case 2: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[4]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[4]));
                                            break;
                                        }
                                        case 3: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[5]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[5]));
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 3: {
                                    int c = ThreadLocalRandom.current().nextInt(1, 4);
                                    switch (c) {
                                        case 1: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[6]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[6]));
                                            break;
                                        }
                                        case 2: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[7]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[7]));
                                            break;
                                        }
                                        case 3: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[8]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[8]));
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    RedisHelper.set("USER_T_STATUS_NEW" + user.getId(), "2");
                    x = ThreadLocalRandom.current().nextInt(
                            new Double(FishingUtil.peakMinNum2[(roomIndex - 1) * 2]).intValue(),
                            new Double(FishingUtil.peakMaxNum2[(roomIndex - 1) * 2]).intValue());
                    int b = ThreadLocalRandom.current().nextInt(1, 4);
                    if ((FishingUtil.peakMax2[(roomIndex - 1) * 2] * banlace
                            - PlayerManager.getPlayerMoney(user)) < list.get(3)) {
                        int c = ThreadLocalRandom.current().nextInt(1, 4);
                        switch (c) {
                            case 1: {
                                RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                        String.valueOf(FishingUtil.burstOne[6]));
                                RedisHelper.set("USER_T_MIN_NEW" + user.getId(), String.valueOf(FishingUtil.recOne[6]));
                                break;
                            }
                            case 2: {
                                RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                        String.valueOf(FishingUtil.burstOne[7]));
                                RedisHelper.set("USER_T_MIN_NEW" + user.getId(), String.valueOf(FishingUtil.recOne[7]));
                                break;
                            }
                            case 3: {
                                RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                        String.valueOf(FishingUtil.burstOne[8]));
                                RedisHelper.set("USER_T_MIN_NEW" + user.getId(), String.valueOf(FishingUtil.recOne[8]));
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    } else {
                        if (PlayerManager.getPlayerMoney(user) > FishingUtil.peakMax2[(roomIndex - 1) * 2] * banlace) {
                            int c = ThreadLocalRandom.current().nextInt(1, 4);
                            switch (c) {
                                case 1: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[6]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[6]));
                                    break;
                                }
                                case 2: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[7]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[7]));
                                    break;
                                }
                                case 3: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[8]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[8]));
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        } else if (PlayerManager.getPlayerMoney(user) < FishingUtil.peakMin2[(roomIndex - 1) * 2]
                                * banlace) {
                            int c = ThreadLocalRandom.current().nextInt(1, 4);
                            switch (c) {
                                case 1: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[0]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[0]));
                                    break;
                                }
                                case 2: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[1]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[1]));
                                    break;
                                }
                                case 3: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[2]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[2]));
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        } else {
                            switch (b) {
                                case 1: {
                                    int c = ThreadLocalRandom.current().nextInt(1, 4);
                                    switch (c) {
                                        case 1: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[0]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[0]));
                                            break;
                                        }
                                        case 2: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[1]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[1]));
                                            break;
                                        }
                                        case 3: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[2]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[2]));
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 2: {
                                    int c = ThreadLocalRandom.current().nextInt(1, 4);
                                    switch (c) {
                                        case 1: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[3]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[3]));
                                            break;
                                        }
                                        case 2: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[4]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[4]));
                                            break;
                                        }
                                        case 3: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[5]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[5]));
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 3: {
                                    int c = ThreadLocalRandom.current().nextInt(1, 4);
                                    switch (c) {
                                        case 1: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[6]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[6]));
                                            break;
                                        }
                                        case 2: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[7]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[7]));
                                            break;
                                        }
                                        case 3: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[8]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[8]));
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
                case 3: {
                    RedisHelper.set("USER_T_STATUS_NEW" + user.getId(), "3");
                    x = ThreadLocalRandom.current().nextInt(
                            new Double(FishingUtil.peakMinNum3[(roomIndex - 1) * 2]).intValue(),
                            new Double(FishingUtil.peakMaxNum3[(roomIndex - 1) * 2]).intValue());
                    int b = ThreadLocalRandom.current().nextInt(1, 4);
                    if ((FishingUtil.peakMax3[(roomIndex - 1) * 2] * banlace
                            - PlayerManager.getPlayerMoney(user)) < list.get(3)) {
                        int c = ThreadLocalRandom.current().nextInt(1, 4);
                        switch (c) {
                            case 1: {
                                RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                        String.valueOf(FishingUtil.burstOne[6]));
                                RedisHelper.set("USER_T_MIN_NEW" + user.getId(), String.valueOf(FishingUtil.recOne[6]));
                                break;
                            }
                            case 2: {
                                RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                        String.valueOf(FishingUtil.burstOne[7]));
                                RedisHelper.set("USER_T_MIN_NEW" + user.getId(), String.valueOf(FishingUtil.recOne[7]));
                                break;
                            }
                            case 3: {
                                RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                        String.valueOf(FishingUtil.burstOne[8]));
                                RedisHelper.set("USER_T_MIN_NEW" + user.getId(), String.valueOf(FishingUtil.recOne[8]));
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    } else {
                        if (PlayerManager.getPlayerMoney(user) > FishingUtil.peakMax3[(roomIndex - 1) * 2] * banlace) {
                            int c = ThreadLocalRandom.current().nextInt(1, 4);
                            switch (c) {
                                case 1: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[6]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[6]));
                                    break;
                                }
                                case 2: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[7]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[7]));
                                    break;
                                }
                                case 3: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[8]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[8]));
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        } else if (PlayerManager.getPlayerMoney(user) < FishingUtil.peakMin3[(roomIndex - 1) * 2]
                                * banlace) {
                            int c = ThreadLocalRandom.current().nextInt(1, 4);
                            switch (c) {
                                case 1: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[0]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[0]));
                                    break;
                                }
                                case 2: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[1]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[1]));
                                    break;
                                }
                                case 3: {
                                    RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.burstOne[2]));
                                    RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                            String.valueOf(FishingUtil.recOne[2]));
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        } else {
                            switch (b) {
                                case 1: {
                                    int c = ThreadLocalRandom.current().nextInt(1, 4);
                                    switch (c) {
                                        case 1: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[0]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[0]));
                                            break;
                                        }
                                        case 2: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[1]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[1]));
                                            break;
                                        }
                                        case 3: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[2]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[2]));
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 2: {
                                    int c = ThreadLocalRandom.current().nextInt(1, 4);
                                    switch (c) {
                                        case 1: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[3]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[3]));
                                            break;
                                        }
                                        case 2: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[4]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[4]));
                                            break;
                                        }
                                        case 3: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[5]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[5]));
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 3: {
                                    int c = ThreadLocalRandom.current().nextInt(1, 4);
                                    switch (c) {
                                        case 1: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[6]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[6]));
                                            break;
                                        }
                                        case 2: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[7]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[7]));
                                            break;
                                        }
                                        case 3: {
                                            RedisHelper.set("USER_T_MAX_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.burstOne[8]));
                                            RedisHelper.set("USER_T_MIN_NEW" + user.getId(),
                                                    String.valueOf(FishingUtil.recOne[8]));
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
                default: {
                    break;
                }
            }
            if (x != 0) {
                break;
            } else {
                continue;
            }
        }
        RedisHelper.set("USER_USE_PEAK" + user.getId(), "0");
        return x;
    }

    /**
     * 发送绑定授权码的响应
     */
    private void sendBanAgentResponse(ServerUser user, boolean isSuccess) {
        HwLoginMessage.BanAgentResponse.Builder builder = HwLoginMessage.BanAgentResponse.newBuilder();
        builder.setIsSuccess(isSuccess);
        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_BAN_AGENT_RESPONSE_VALUE, builder, user);
    }

    /**
     * 发送第一次进入游戏响应
     */
    private void sendFirstJoinResponse(ServerUser user, HwLoginMessage.FirstJoinResponse.Builder builder) {
        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C__FIRST_JOIN_RESPONSE_VALUE, builder, user);
    }

    /**
     * 微信code登录
     */
    public void wxLoginCode(ServerUser user, LoginMessage.WxLoginCodeRequest wxLoginCodeRequest) {

        WxAccessToken accessToken = getWxAccess(wxLoginCodeRequest.getCode());

        if (accessToken == null) {
            sendWxReAuthResponse(user);
            return;
        }

        LoginMessage.WxLoginCodeResponse.Builder builder = LoginMessage.WxLoginCodeResponse.newBuilder();
        builder.setRefreshToken(accessToken.getRefresh_token());
        builder.setType(wxLoginCodeRequest.getType());
        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C__WEIXIN_CODE_GET_RESPONSE_VALUE, builder, user);

    }

    /**
     * 发送微信重新授权的响应
     */
    private void sendWxReAuthResponse(ServerUser user) {

        LoginMessage.WxLoginReAuthResponse.Builder builder = LoginMessage.WxLoginReAuthResponse.newBuilder();
        // 返回登录码无效的响应，通知前端重新拉取授权
        NetManager.sendMessage(LoginMessage.LoginMsgCode.S_C_WX_LOGIN_REAUTH_RESPONSE_VALUE, builder, user);

    }

    /**
     * 获取微信AccessToken
     */
    public WxAccessToken getWxAccess(String code) {

        logger.info("开始执行微信登录:[{}]", code);
        if (StringUtils.isEmpty(code)) {
            return null;
        }

        logger.info("开始获取通行码令牌");

        // 获取通行码令牌
        WxAccessToken accessToken = getAccess(code);
        if (accessToken == null) {
            return null;
        }

        if (accessToken.getErrcode() != null) { // 错误码不为空就代表返回错误信息
            // 发送重新授权的响应
            logger.error("微信code登录失败：code【{}】,errMsg【{}】", code, accessToken.getErrmsg());
            return null;
        }

        return accessToken;
    }

    public static WxAccessToken getAccess(String code) {

        if (StringUtils.isEmpty(code)) {
            return null;
        }

        // 通过code获取access_token
        String response =
                HttpUtil.doGet("https://api.weixin.qq.com/sns/oauth2/access_token?" + "appid=wx56e3bd10e2603e96"
                        + "&secret=858cb5c5fefdac3638076fc184670ddd" + "&code=" + code + "&grant_type=authorization_code");

        return new Gson().fromJson(response, WxAccessToken.class);

    }

    @Resource
    LoginManager loginManager;

    /**
     * 获取昵称
     */
    @Nullable
    private String getNickname() {

        String nickname = null;

        UserEntity entity;

        for (int i = 0; i < 3; i++) {

            nickname = cn.hutool.core.util.RandomUtil.randomString(cn.hutool.core.util.RandomUtil.BASE_CHAR, 6);

            entity = userMapper.findByUsername(nickname);

            if (entity == null) {
                break;
            } else {
                nickname = null;
            }

        }

        return nickname;

    }

    /**
     * 获取微信用户信息
     */
    public WxUserInfo getWxUser(String refreshToken) {

        // 用刷新令牌获取真的AccessToken令牌
        WxAccessToken refreshAccessToken = refreshAccess(refreshToken);
        if (refreshAccessToken == null) {
            logger.error("微信获取token失败");
            return null;
        }

        if (refreshAccessToken.getErrcode() != null) { // 错误码不为空就代表返回错误信息
            logger.error("微信登录失败：refreshToken[{}],errMsg[{}]", refreshAccessToken, refreshAccessToken.getErrmsg());
            return null;
        }

        // 通过令牌获取微信用户信息
        WxUserInfo userInfo = WxLoginUtil.getUserInfo(refreshAccessToken);
        if (userInfo == null) {
            logger.error("微信用户信息获取失败：refreshToken[{}]", refreshAccessToken);
            return null;
        }

        if (!StringUtils.isEmpty(userInfo.getErrcode())) { // 错误码不为空就代表返回错误信息
            logger.error("微信登录错误：errMsg：[{}]", userInfo.getErrmsg());
            return null;
        }

        return userInfo;

    }

    public static WxAccessToken refreshAccess(String refreshToken) {

        if (StringUtils.isEmpty(refreshToken)) {
            return null;
        }

        String response = HttpUtil.doGet("https://api.weixin.qq.com/sns/oauth2/refresh_token?grant_type=refresh_token"
                + "&appid=wx56e3bd10e2603e96" + "&refresh_token=" + refreshToken);

        return new Gson().fromJson(response, WxAccessToken.class);

    }


    // gameId池
    public static final LinkedList<Integer> GAME_ID_POOL = new LinkedList<>();

    public static final int BEGIN_GAME_ID = 100000;

    @PostConstruct
    public void postConstruct() {

        synchronized (GAME_ID_POOL) {

            Set<Long> allGameIdSet = userMapper.findAllGameId();

            for (int i = BEGIN_GAME_ID; i <= 999999; i++) {

                if (allGameIdSet.contains((long) i)) {
                    continue;
                }

                GAME_ID_POOL.add(i);

            }

            log.info("gameId池，大小：{}", GAME_ID_POOL.size());

        }

    }

    /**
     * 随机获取一个：gameId
     */
    public static Long getGameId(Long gameId) {

        synchronized (GAME_ID_POOL) {

            if (GAME_ID_POOL.size() != 0) {

                // 随机获取一个 gameId
                gameId =
                        Long.valueOf(GAME_ID_POOL.remove(cn.hutool.core.util.RandomUtil.randomInt(GAME_ID_POOL.size())));

            }

        }

        return gameId;

    }

    /**
     * 标准注册
     *
     * @return 注册失败返回 null
     * isVerifyPhoneCode 是否验证 手机号
     */
    public UserEntity commonRegister(ServerUser user, String username, String password, String phoneNum,
                                     String phoneCode, String quDaoId, boolean checkFlag, Consumer<UserEntity> consumer,
                                     boolean sendMessageFlag, boolean isVerifyPhoneCode) {

        if (checkFlag) {
            if (!checkLoginInfo(username, password)) {
                NetManager.sendWarnMessageToClient("注册信息填写有误，注册失败", user);
                return null;
            }
        }

        UserEntity entity;

        synchronized (LoginManager.unusedUsernames) {

            if (isVerifyPhoneCode) {
                if (StrUtil.isNotBlank(phoneCode)) {
                    PhoneCheck phoneCheck = CommonLobbyManager.phoneCheckMap.get(user.getId());
                    if (phoneCheck == null || !phoneCheck.getPhoneNum().equals(phoneNum)) {
                        NetManager.sendHintMessageToClient("请先进行手机验证", user);
                        return null;
                    }
                    if (!phoneCode.equals(String.valueOf(phoneCheck.getCheckCode()))) {
                        NetManager.sendHintMessageToClient("验证码不正确", user);
                        return null;
                    }
                }
            }

            // 若待注册用户名在用户名池中，无需查询数据库，直接使用该用户名，并移除访用户名
            if (LoginManager.unusedUsernames.contains(username)) {
                LoginManager.unusedUsernames.remove(username);
            } else {
                entity = userMapper.findByUsername(username);
                if (entity != null) {
                    NetManager.sendWarnMessageToClient("昵称已存在", user);
                    return null;
                }
            }

            entity = new UserEntity();
            entity.setUsername(username);
            entity.setPassword(password);
            entity.setPhonenum(phoneNum);
            entity.setNickname(username);

            if (consumer != null) {
                consumer.accept(entity); // 处理一下：entity
            }

            userMapper.save(entity);

            long entityId = entity.getId();

            entity.setMyInviteCode(LoginManager.getMyInviteCode(entityId));

            // 获取：gameId
            Long gameId = getGameId(entityId);

            entity.setGameId(gameId); // 设置：gameId
            checkGameId(entity); // 检查：gameId

            userMapper.updateWithGameId(entity);

        }

        if (sendMessageFlag) {

            final HwLoginMessage.UserRegisterResponse.Builder builder =
                    HwLoginMessage.UserRegisterResponse.newBuilder();
            builder.setUsername(username);
            builder.setPassword(password);
            NetManager.sendMessage(LoginMessage.LoginMsgCode.S_C_LOGIN_SUCCESS_RESPONSE_VALUE, builder.build(), user);

        }

        return entity;

    }

    /**
     * 检查：gameId
     */
    private void checkGameId(UserEntity entity) {

        long countByGameId = userMapper.findCountByGameId(entity.getGameId(), entity.getId());

        // 如果：gameId重复了，则增加一个随机值，作为新的 gameId
        if (countByGameId > 0) {

            entity.setGameId(entity.getGameId() + RandomUtil.getRandom(10, 10000));

            checkGameId(entity);

        }

    }

    /**
     * 获取锻造/合成/强化组合
     */
    public void getForgingGroup(ServerUser user, long forgingId) {
        HwLoginMessage.GetForgingGroupResponse.Builder builder = HwLoginMessage.GetForgingGroupResponse.newBuilder();
        HwLoginMessage.ForgingGroup.Builder builder1 = HwLoginMessage.ForgingGroup.newBuilder();
        ForgingConfig forgingConfigs = DataContainer.getData(forgingId, ForgingConfig.class);
        String[] groups = forgingConfigs.getForgingGroup().split(",");
        if (RedisUtil.val("FORGING_GROUP:" + forgingId + ":" + user.getId(), 0L) == 0) {
            RedisHelper.set("FORGING_GROUP:" + forgingId + ":" + user.getId(), groups[0]);
        }
        for (String s : groups) {
            ForgingGroupConfig forgingGroupConfig = DataContainer.getData(Long.parseLong(s), ForgingGroupConfig.class);
            String[] useSciences = forgingGroupConfig.getUseScience().split(",");
            builder1.setId(forgingGroupConfig.getId());
            String[] useScience = useSciences[0].split(":");
            builder1.setMaterial1(Integer.valueOf(useScience[0]));
            builder1.setMaterial1Num(Integer.valueOf(useScience[1]));
            String[] useScience1 = useSciences[1].split(":");
            builder1.setMaterial2(Integer.valueOf(useScience1[0]));
            builder1.setMaterial2Num(Integer.valueOf(useScience1[1]));
            String[] useScience2 = useSciences[2].split(":");
            builder1.setMaterial3(Integer.valueOf(useScience2[0]));
            builder1.setMaterial3Num(Integer.valueOf(useScience2[1]));
            builder.addForgingGroup(builder1);
        }
        builder.setNowId(Long.valueOf(RedisHelper.get("FORGING_GROUP:" + forgingId + ":" + user.getId())));
        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_GET_FORGING_GROUP_RESPONSE_VALUE, builder.build(), user);
    }

    /**
     * 检查登录信息
     */
    public static boolean checkLoginInfo(String username, String password) {

        // 2-6位字母或汉字
        if (username != null && !Pattern.matches("^[a-zA-Z\u4E00-\u9FA5]{2,6}$", username)) {
            return false;
        }

        // 32位小写字母或数字[32位小写md5]
        if (password != null && !Pattern.matches("^[0-9a-f]{32}$", password)) {
            return false;
        }

        return true;

    }


    /**
     * 获取用户Ip
     */
    public void getUserIp(ServerUser user, long userId, String userIp) {
        OseePublicData.GetUserIpResponse.Builder builder = OseePublicData.GetUserIpResponse.newBuilder();
        // if(userId!=user.getId()){
        //// NetManager.sendHintMessageToClient("请先设置昵称再进行反馈！", user);
        //// return;
        //// }

        if (user.getId() == 0) {
            return;
        }

        OseePlayerEntity oseePlayerEntity = PlayerManager.getPlayerEntity(user);
        if (oseePlayerEntity == null) {
            return;
        }
        if (isIp(userIp)) {
            oseePlayerEntity.setUserIp(userIp);
            PlayerManager.updateEntities.add(oseePlayerEntity);
        }

        builder.setUserId(oseePlayerEntity.getUserId());
        builder.setUserIp(userIp);
        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_FISHING_GET_USER_IP_RESPONSE_VALUE, builder, user);
    }

    public static boolean isIp(String ip) {

        return ip.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");

    }


    private int getPoint(String key, String playerId) {
        Double point = RedisUtil.zScore(key, playerId);
        return point == null ? 0 : point.intValue();
    }


}
