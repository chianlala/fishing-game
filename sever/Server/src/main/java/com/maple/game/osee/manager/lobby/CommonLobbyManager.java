package com.maple.game.osee.manager.lobby;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.unit.DataSize;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.maple.common.login.manager.LoginManager;
import com.maple.database.config.redis.RedisHelper;
import com.maple.database.data.entity.UserAuthenticationEntity;
import com.maple.database.data.entity.UserEntity;
import com.maple.database.data.mapper.UserAuthenticationMapper;
import com.maple.database.data.mapper.UserMapper;
import com.maple.engine.config.AliSmsConfig;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.engine.manager.GsonManager;
import com.maple.engine.utils.DateUtils;
import com.maple.engine.utils.ThreadPoolUtils;
import com.maple.game.osee.dao.data.entity.AgentEntity;
import com.maple.game.osee.dao.data.entity.MessageEntity;
import com.maple.game.osee.dao.data.entity.OseeNoticeEntity;
import com.maple.game.osee.dao.data.mapper.AgentMapper;
import com.maple.game.osee.dao.data.mapper.MessageMapper;
import com.maple.game.osee.dao.data.mapper.OseeNoticeMapper;
import com.maple.game.osee.dao.data.mapper.OseePlayerMapper;
import com.maple.game.osee.dao.log.entity.OseeExpendLogEntity;
import com.maple.game.osee.dao.log.mapper.OseeExpendLogMapper;
import com.maple.game.osee.dao.log.mapper.OseeRechargeLogMapper;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemData;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.entity.lobby.FunctionEnum;
import com.maple.game.osee.entity.lobby.PhoneCheck;
import com.maple.game.osee.entity.lobby.WechatShare;
import com.maple.game.osee.entity.tencentcloudapi.common.Credential;
import com.maple.game.osee.entity.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.maple.game.osee.entity.tencentcloudapi.common.profile.ClientProfile;
import com.maple.game.osee.entity.tencentcloudapi.common.profile.HttpProfile;
import com.maple.game.osee.entity.tencentcloudapi.sms.v20190711.SmsClient;
import com.maple.game.osee.entity.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.maple.game.osee.entity.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.maple.game.osee.entity.tencentcloudapi.sms.v20190711.models.SendStatus;
import com.maple.game.osee.manager.MessageManager;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.proto.HwLoginMessage;
import com.maple.game.osee.proto.OseeMessage.OseeMsgCode;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.proto.OseePublicData.ItemDataProto;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage.*;
import com.maple.game.osee.util.MyFileTypeUtil;
import com.maple.game.osee.util.MyRefreshFishingUtil;
import com.maple.game.osee.util.SmsUtil;
import com.maple.game.osee.util.ValidateUtil;
import com.maple.network.manager.NetManager;
import com.maple.network.util.HttpUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.maple.game.osee.manager.PlayerManager.getItemNum;
import static com.maple.game.osee.proto.OseeMessage.OseeMsgCode.S_C_OSEE_RESET_PASSWORD_RESPONSE_VALUE;

/**
 * 大厅基本功能管理类
 */
@Component
public class CommonLobbyManager {

    private static Logger logger = LoggerFactory.getLogger(CommonLobbyManager.class);

    private static UserMapper userMapper;

    @Resource
    public void setUserMapper(UserMapper userMapper) {
        CommonLobbyManager.userMapper = userMapper;
    }

    @Autowired
    private OseeNoticeMapper noticeMapper;

    @Autowired
    private UserAuthenticationMapper authenticationMapper;

    @Autowired
    private OseeExpendLogMapper expendLogMapper;

    @Autowired
    private AliSmsConfig aliSmsConfig;

    @Autowired
    private MessageManager messageManager;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private OseeRechargeLogMapper rechargeLogMapper;

    @Autowired
    private OseePlayerMapper playerMapper;


    private static RedissonClient redissonClient;

    @Resource
    public void setRedissonClient(RedissonClient redissonClient) {
        CommonLobbyManager.redissonClient = redissonClient;
    }

    /**
     * VIP每日登录的奖励
     */
    private ItemData[][] VIP_REWARD_ITEMS =
            {{new ItemData(ItemId.SKILL_LOCK.getId(), 5), new ItemData(ItemId.SKILL_FROZEN.getId(), 5)}, // vip1
                    {new ItemData(ItemId.SKILL_LOCK.getId(), 10), new ItemData(ItemId.SKILL_FROZEN.getId(), 5),
                            new ItemData(ItemId.SKILL_FAST.getId(), 2)}, // vip2
                    {new ItemData(ItemId.SKILL_LOCK.getId(), 15), new ItemData(ItemId.SKILL_FROZEN.getId(), 5),
                            new ItemData(ItemId.SKILL_FAST.getId(), 5), new ItemData(ItemId.SKILL_CRIT.getId(), 2)}, // vip3
                    {new ItemData(ItemId.SKILL_LOCK.getId(), 20), new ItemData(ItemId.SKILL_FROZEN.getId(), 5),
                            new ItemData(ItemId.SKILL_FAST.getId(), 10), new ItemData(ItemId.SKILL_CRIT.getId(), 2)}, // vip4
                    {new ItemData(ItemId.SKILL_LOCK.getId(), 25), new ItemData(ItemId.SKILL_FROZEN.getId(), 5),
                            new ItemData(ItemId.SKILL_FAST.getId(), 10), new ItemData(ItemId.SKILL_CRIT.getId(), 2)}, // vip5
                    {new ItemData(ItemId.SKILL_LOCK.getId(), 30), new ItemData(ItemId.SKILL_FROZEN.getId(), 10),
                            new ItemData(ItemId.SKILL_FAST.getId(), 15), new ItemData(ItemId.SKILL_CRIT.getId(), 5)}, // vip6
                    {new ItemData(ItemId.SKILL_LOCK.getId(), 35), new ItemData(ItemId.SKILL_FROZEN.getId(), 15),
                            new ItemData(ItemId.SKILL_FAST.getId(), 15), new ItemData(ItemId.SKILL_CRIT.getId(), 5)}, // vip7
                    {new ItemData(ItemId.SKILL_LOCK.getId(), 40), new ItemData(ItemId.SKILL_FROZEN.getId(), 15),
                            new ItemData(ItemId.SKILL_FAST.getId(), 20), new ItemData(ItemId.SKILL_CRIT.getId(), 5)}, // vip8
                    {new ItemData(ItemId.SKILL_LOCK.getId(), 50), new ItemData(ItemId.SKILL_FROZEN.getId(), 20),
                            new ItemData(ItemId.SKILL_FAST.getId(), 30), new ItemData(ItemId.SKILL_CRIT.getId(), 10)}, // vip9
            };

    /**
     * 客服微信key
     */
    private final String SERVICE_WECHAT_KEY = "Server:Support:Wechat";

    /**
     * 客服二维码
     */
    private final String SERVICE_QRODE_KEY = "Server:Support:QRCode";

    /**
     * 首充记录Redis保存键命名空间
     */
    public static final String FIRST_CHARGE_KEY_NAMESPACE = "Server:FirstCharge:%d";

    /**
     * 月卡每日奖励赠送状态
     */
    public static final String MONTH_CARD_DAILY_REWARDS_KEY_NAMESPACE = "Server:MonthCard:DailyRewards:%d";

    /**
     * vip等级每日登录奖励
     */
    public static final String VIP_DAILY_REWARDS_KEY_NAMESPACE = "Server:Vip:DailyRewards:%d";

    /**
     * vip金币的补足记录key
     */
    public static final String VIP_MONEY_ENOUGH_KEY_NAMESPACE = "Server:Vip:MoneyEnough:%d";

    /**
     * 公告列表
     */
    private List<OseeNoticeEntity> notices = new LinkedList<>();

    /**
     * 实名认证验证表
     */
    public static Map<Long, PhoneCheck> phoneCheckMap = new ConcurrentHashMap<>();

    /**
     * 重置密码验证表
     */
    public static Map<String, PhoneCheck> resetPasswordCheckMap = new ConcurrentHashMap<>();

    /**
     * 手机验证cd
     */
    private final int coolDown = 300000;


    public CommonLobbyManager() {
        ThreadPoolUtils.TASK_SERVICE_POOL.schedule(this::refreshNotice, 5, TimeUnit.SECONDS);
    }

    /**
     * 刷新公告列表
     */
    public void refreshNotice() {
        notices = noticeMapper.getAll();
        for (int i = 1; i <= notices.size(); i++) {
            if (notices.get(i - 1).getIndex() != i) {
                notices.get(i - 1).setIndex(i);
                noticeMapper.update(notices.get(i - 1));
            }
        }
    }

    /**
     * 交换公告顺序
     */
    public boolean changeNotice(long id, int type) {
        int index = -1;
        for (int i = 0; i < notices.size(); i++) {
            if (notices.get(i).getId() == id) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false;
        }
        if (index + type < 0 || index + type >= notices.size()) {
            return false;
        }

        notices.get(index + type).setIndex(index);
        notices.get(index).setIndex(index + type);
        noticeMapper.update(notices.get(index + type));
        noticeMapper.update(notices.get(index));

        refreshNotice();

        return true;
    }

    /**
     * 用户实名认证手机验证
     */
    public void authenticatePhoneCheck(ServerUser user, String phone) {
        long nowTime = System.currentTimeMillis();
        PhoneCheck checkLog = phoneCheckMap.get(user.getId());
        if (checkLog != null && nowTime - checkLog.getCheckTime().getTime() < coolDown) {
            NetManager.sendHintMessageToClient("请勿频繁获取验证码", user);
            return;
        }
        int checkCode = ThreadLocalRandom.current().nextInt(100000, 1000000);

        // aliSmsConfig.setSign("欢乐海洋");
        // aliSmsConfig.setAccessKeyId("LTAIC6Mh3tOiIhHd");
        // aliSmsConfig.setAccessSecret("eDZQMdzROxeElmLRcHvfjvqmxLixvj");
        if (SmsUtil.send(phone, String.valueOf(checkCode)) == null) {
            PhoneCheck phoneCheck = new PhoneCheck();
            phoneCheck.setCheckCode(checkCode);
            phoneCheck.setCheckTime(new Date());
            phoneCheck.setPhoneNum(phone);

            phoneCheckMap.put(user.getId(), phoneCheck);
            AuthenticatePhoneCheckResponse.Builder builder = AuthenticatePhoneCheckResponse.newBuilder();
            builder.setResult(true);
            NetManager.sendMessage(OseeMsgCode.S_C_OSEE_AUTHENTICATE_PHONE_CHECK_RESPONSE_VALUE, builder, user);
        } else {
            NetManager.sendHintMessageToClient("验证短信发送失败,请稍后尝试", user);
        }
    }

    /**
     * 用户重置密码手机验证
     */
    public void resetPasswordPhoneCheck(ServerUser user, String username) {

        try {

            String phone = userMapper.findByUsername(username).getPhonenum();

            if (StrUtil.isNotBlank(phone)) {

                synchronized (user) {

                    long nowTime = System.currentTimeMillis();
                    PhoneCheck checkLog = resetPasswordCheckMap.get(username);

                    if (checkLog != null && nowTime - checkLog.getCheckTime().getTime() < coolDown) {

                        NetManager.sendHintMessageToClient("请勿频繁获取验证码", user);
                        return;

                    }

                    int checkCode = ThreadLocalRandom.current().nextInt(100000, 1000000);

                    String message = SmsUtil.forgetPassword(phone, String.valueOf(checkCode));

                    if (message == null) {

                        PhoneCheck phoneCheck = new PhoneCheck();
                        phoneCheck.setCheckCode(checkCode);
                        phoneCheck.setCheckTime(new Date());
                        phoneCheck.setPhoneNum(phone);

                        resetPasswordCheckMap.put(username, phoneCheck);

                        ResetPasswordPhoneCheckResponse.Builder builder = ResetPasswordPhoneCheckResponse.newBuilder();
                        builder.setResult(true);
                        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_RESET_PASSWORD_PHONE_CHECK_RESPONSE_VALUE, builder,
                                user);

                    } else {

                        if (message.contains("LIMIT_CONTROL")) {

                            NetManager.sendHintMessageToClient("验证码发送次数过多，请明日再尝试", user);

                        } else {

                            NetManager.sendHintMessageToClient("验证短信发送失败，请稍后尝试", user);

                        }

                    }

                }

            } else {

                NetManager.sendHintMessageToClient("该账号未绑定手机号，请联系客服", user);

            }

        } catch (Exception e) {

            NetManager.sendHintMessageToClient("该账号不存在", user);

        }

    }

    /**
     * 用户重置密码手机验证
     */
    public void changePhoneCheck(ServerUser user, String phone) {
        try {
            UserEntity entity = userMapper.findById(user.getId());
            String phone1 = entity.getPhonenum();
            if (phone.equals(phone1)) {
                NetManager.sendHintMessageToClient("与绑定手机号不一致", user);
                return;
            }
            if (!StringUtils.isEmpty(phone)) {
                long nowTime = System.currentTimeMillis();
                PhoneCheck checkLog = resetPasswordCheckMap.get(entity.getUsername());
                if (checkLog != null && nowTime - checkLog.getCheckTime().getTime() < coolDown) {
                    NetManager.sendHintMessageToClient("请勿频繁获取验证码", user);
                    return;
                }
                int checkCode = ThreadLocalRandom.current().nextInt(100000, 1000000);

                // aliSmsConfig.setSign("欢乐海洋");
                // aliSmsConfig.setAccessKeyId("LTAIC6Mh3tOiIhHd");
                // aliSmsConfig.setAccessSecret("eDZQMdzROxeElmLRcHvfjvqmxLixvj");
                if (SmsUtil.send(phone, String.valueOf(checkCode)) == null) {
                    PhoneCheck phoneCheck = new PhoneCheck();
                    phoneCheck.setCheckCode(checkCode);
                    phoneCheck.setCheckTime(new Date());
                    phoneCheck.setPhoneNum(phone);

                    resetPasswordCheckMap.put(entity.getUsername(), phoneCheck);
                    HwLoginMessage.ChangePhoneResponse.Builder builder =
                            HwLoginMessage.ChangePhoneResponse.newBuilder();
                    builder.setIsSuccess(true);
                    NetManager.sendMessage(OseeMsgCode.S_C_CHANGE_PHONE_RESPONSE_VALUE, builder, user);
                } else {
                    NetManager.sendHintMessageToClient("验证短信发送失败,请稍后尝试", user);
                }
            } else {
                NetManager.sendHintMessageToClient("该账号未绑定手机号，请联系客服微信:" + getSupportWechat(), user);
            }
        } catch (Exception e) {
            NetManager.sendHintMessageToClient("该账号不存在", user);
        }
    }

    /**
     * 用户实名认证
     */
    public void userAuthenticate(ServerUser user, String realName, String idCardNum, String phoneNum, int checkCode) {
        PhoneCheck phoneCheck = phoneCheckMap.get(user.getId());
        if (phoneCheck == null || !phoneCheck.getPhoneNum().equals(phoneNum)) {
            NetManager.sendHintMessageToClient("请先进行手机验证", user);
            return;
        }

        if (phoneCheck.getCheckCode() != checkCode) {
            NetManager.sendHintMessageToClient("验证码不正确", user);
            return;
        }

        UserAuthenticationEntity entity = authenticationMapper.getByUserId(user.getId());
        if (entity != null) {
            NetManager.sendHintMessageToClient("请勿重复认证", user);
            return;
        }

        List<UserAuthenticationEntity> entitys = authenticationMapper.getByIdcardNo(idCardNum);
        if (entitys.size() >= 5) {
            NetManager.sendHintMessageToClient("该身份证实名账号个数已达上限", user);
            return;
        }

        int entities = userMapper.findByIdCardNo(idCardNum);
        if (entities >= 5) {
            NetManager.sendHintMessageToClient("该身份证绑定账号个数已达上限", user);
            return;
        }

        UserEntity entity1 = userMapper.findById(user.getId());
        if (!"".equals(entity1.getPhonenum())) {
            if (entity1.getPhonenum().equals(phoneNum)) {
                NetManager.sendHintMessageToClient("该手机号与绑定手机号不一致", user);
                return;
            }
        }

        if (checkIdCardNum(realName, idCardNum)) {
            phoneCheckMap.remove(user.getId());

            entity = new UserAuthenticationEntity();
            entity.setIdcardNo(idCardNum);
            entity.setName(realName);
            entity.setPhoneNo(phoneNum);
            entity.setUserId(user.getId());
            authenticationMapper.save(entity);

            sendAuthentication(user);

            List<ItemData> itemDatas = new LinkedList<>();
            itemDatas.add(new ItemData(ItemId.MONEY.getId(), 10000));
            // itemDatas.add(new ItemData(ItemId.LOTTERY.getId(), 30));
            PlayerManager.addItems(user, itemDatas, ItemChangeReason.AUTHENTICATION, true);

            OseeExpendLogEntity log = new OseeExpendLogEntity();
            log.setUserId(user.getId());
            log.setNickname(user.getNickname());
            log.setPayType(1);
            log.setMoney(50000);
            // log.setLottery(30);
            expendLogMapper.save(log);

            SubmitAuthenticateResponse.Builder builder = SubmitAuthenticateResponse.newBuilder();
            builder.addRewards(ItemDataProto.newBuilder().setItemId(ItemId.MONEY.getId()).setItemNum(10000)); // 1w金币
            // builder.addRewards(ItemDataProto.newBuilder().setItemId(ItemId.LOTTERY.getId()).setItemNum(30)); // 30奖券
            NetManager.sendMessage(OseeMsgCode.S_C_OSEE_SUBMIT_AUTHENTICATE_RESPONSE_VALUE, builder, user);
        } else {
            NetManager.sendHintMessageToClient("绑定失败，请输入正确的身份证信息", user);
        }
    }

    /**
     * 用户实名认证
     */
    public String bangIdcardAll(ServerUser user, String realName, String idCardNum, String phoneNum, String checkCode) {
        PhoneCheck phoneCheck = phoneCheckMap.get(user.getId());
        if (phoneCheck == null || !phoneCheck.getPhoneNum().equals(phoneNum)) {
            NetManager.sendHintMessageToClient("请先进行手机验证", user);
            return null;
        }

        if (checkCode.equals(phoneCheck.getCheckCode())) {
            NetManager.sendHintMessageToClient("验证码不正确", user);
            return null;
        }

        UserAuthenticationEntity entity = authenticationMapper.getByUserId(user.getId());
        if (entity != null) {
            if (entity.getIdcardNo() != idCardNum) {
                NetManager.sendHintMessageToClient("输入身份信息与实名认证不一致", user);
                return null;
            }
        }

        List<UserAuthenticationEntity> entitys = authenticationMapper.getByIdcardNo(idCardNum);
        if (entitys.size() >= 5) {
            NetManager.sendHintMessageToClient("该身份证实名账号个数已达上限", user);
            return null;
        }
        int entities = userMapper.findByIdCardNo(idCardNum);
        if (entities >= 5) {
            NetManager.sendHintMessageToClient("该身份证绑定账号个数已达上限", user);
            return null;
        }
        UserEntity entity1 = userMapper.findById(user.getId());
        if (!"".equals(entity1.getPhonenum())) {
            if (!entity1.getPhonenum().equals(phoneNum)) {
                NetManager.sendHintMessageToClient("该手机号与绑定手机号不一致", user);
                return null;
            }
        }

        if (checkIdCardNum(realName, idCardNum)) {
            entity = new UserAuthenticationEntity();
            entity.setIdcardNo(idCardNum);
            entity.setName(realName);
            entity.setPhoneNo(phoneNum);
            entity.setUserId(user.getId());
            authenticationMapper.save(entity);
            entity1.setName(realName);
            entity1.setIdcardNo(idCardNum);
            userMapper.update(entity1);
            user.getEntity().setName(realName);
            user.getEntity().setIdcardNo(idCardNum);
            return phoneNum;
        } else {
            NetManager.sendHintMessageToClient("绑定失败，请输入正确的身份证信息", user);
            return null;
        }
    }

    /**
     * 重置密码
     */
    public void resetPassword(ServerUser user, String username, String password, String checkCode) {

        try {

            boolean oldPasswordChangeFlag; // 是否允许旧密码修改

            UserEntity entity = user.getEntity();

            if (entity == null) { // 如果没有登录

                ServerUser serverUser = UserContainer.getUserByUsername(username);

                if (serverUser == null) {

                    entity = userMapper.findByUsername(username);

                } else {

                    entity = serverUser.getEntity();

                }

                if (entity == null) {

                    NetManager.sendHintMessageToClient("该账号不存在", user);
                    return;

                }

                if (StrUtil.isBlank(entity.getPhonenum())) {

                    NetManager.sendHintMessageToClient("请先进行手机验证", user);
                    return;

                }

                oldPasswordChangeFlag = false;

            } else { // 如果登录了

                if (entity.getId() == 0) {
                    return;
                }

                oldPasswordChangeFlag = checkCode.length() > 6;

            }

            if (oldPasswordChangeFlag) {

                String psd = entity.getPassword();

                if (StrUtil.isNotBlank(psd) && psd.equals(checkCode)) {

                    if (BooleanUtil.isFalse(resetPassword(user, password, entity))) {
                        return;
                    }

                } else {

                    NetManager.sendHintMessageToClient("密码修改失败,密码不正确", user);

                }

            } else { // 如果有手机号

                PhoneCheck phoneCheck = resetPasswordCheckMap.get(username);

                if (phoneCheck == null || !phoneCheck.getPhoneNum().equals(entity.getPhonenum())) {

                    NetManager.sendHintMessageToClient("请先进行手机验证", user);
                    return;

                }

                if (phoneCheck.getCheckCode() != Convert.toInt(checkCode, 0)) {

                    NetManager.sendHintMessageToClient("验证码不正确", user);
                    return;

                }

                if (BooleanUtil.isFalse(resetPassword(user, password, entity))) {
                    return;
                }

                resetPasswordCheckMap.remove(username);

            }

        } catch (Exception e) {

            NetManager.sendHintMessageToClient("该账号不存在", user);

        }

    }

    /**
     * 重置密码操作
     */
    private boolean resetPassword(ServerUser user, String password, UserEntity userEntity) {

        AgentEntity agentEntity = agentMapper.getAgentByPlayerId(user.getId());

        if (agentEntity != null && agentEntity.getAgentLevel() <= 2) {

            NetManager.sendHintMessageToClient("操作失败：请在代理后台进行修改密码操作", user);
            return false;

        }

        userEntity.setPassword(password);
        userMapper.update(userEntity);

        ResetPasswordResponse.Builder builder = ResetPasswordResponse.newBuilder();
        builder.setResult(true);

        NetManager.sendMessage(S_C_OSEE_RESET_PASSWORD_RESPONSE_VALUE, builder, user);

        return true;

    }

    /**
     * 微信分享
     */
    public void wechatShare(ServerUser user) {
        WechatShare share = new Gson().fromJson(RedisHelper.get("Wechat:Share:" + user.getId()), WechatShare.class);
        if (share == null || !DateUtils.isSameDay(new Date(), share.getShareDate())) {
            RedisHelper.set("Wechat:Share:" + user.getId(), GsonManager.gson.toJson(new WechatShare(new Date())));
            WechatShareResponse.Builder builder = WechatShareResponse.newBuilder();
            builder.setRewardMoney(3000);
            PlayerManager.addItem(user, ItemId.MONEY, 3000, ItemChangeReason.WECHAT_SHARE, true);
            NetManager.sendMessage(OseeMsgCode.S_C_OSEE_WECHAT_SHARE_RESPONSE_VALUE, builder, user);
        } else {
            NetManager.sendHintMessageToClient("今日分享奖励已领取", user);
        }
    }

    /**
     * 检查身份证号码
     */
    @SuppressWarnings("unchecked")
    private boolean checkIdCardNum(String realName, String idCardNum) {
        String host = "https://idenauthen.market.alicloudapi.com";
        String path = "/idenAuthentication";
        String appcode = "a3fd78268d0645dd9c5fedfcc30a00bf";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appcode);
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> bodys = new HashMap<>();
        bodys.put("idNo", idCardNum);
        bodys.put("name", realName);

        try {
            String response = HttpUtil.doPost(host + path, bodys, headers);
            Map<String, Object> resultMap = GsonManager.gson.fromJson(response, Map.class);
            if (Integer.parseInt((String) resultMap.get("respCode")) == 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 发送重置密码手机号消息
     */
    public void sendGetResetPasswordPhoneNumResponse(ServerUser user, String username) {

        UserEntity entity = userMapper.findByUsername(username);

        if (entity != null) {

            GetResetPasswordPhoneNumResponse.Builder builder = GetResetPasswordPhoneNumResponse.newBuilder();
            builder.setUsername(username);
            String phoneNo = entity.getPhonenum();
            char[] phoneNoArray = phoneNo.toCharArray();
            try {
                for (int i = 3; i < 7; i++) {
                    phoneNoArray[i] = '*';
                }
            } catch (RuntimeException e) {
                NetManager.sendHintMessageToClient("该账号未绑定手机号，请联系客服", user);
                throw new RuntimeException("该账号未绑定手机号,导致空指针异常");
            }

            phoneNo = new String(phoneNoArray);
            builder.setPhoneNum(phoneNo);
            int msgCode = OseeMsgCode.S_C_OSEE_GET_RESET_PASSWORD_PHONE_NUM_RESPONSE_VALUE;
            NetManager.sendMessage(msgCode, builder, user);

        } else {

            NetManager.sendHintMessageToClient("该手机号未绑定账号，请联系客服", user);

        }

    }

    /**
     * 发送客服微信消息
     */
    public void sendServiceWechatResponse(ServerUser user) {
        ServiceWechatResponse.Builder builder = ServiceWechatResponse.newBuilder();
        builder.setWechat(getSupportWechat());
        builder.setQrcode(getSupportQRCode());
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_SERVICE_WECHAT_RESPONSE_VALUE, builder, user);
    }

    /**
     * 发送公告消息
     */
    public void sendNoticeListResponse(ServerUser user) {
        NoticeListResponse.Builder builder = NoticeListResponse.newBuilder();
        for (OseeNoticeEntity notice : notices) {
            long nowTime = System.currentTimeMillis();
            if (notice.getStartTime().getTime() < nowTime && notice.getEndTime().getTime() > nowTime) {
                NoticeProto.Builder proto = NoticeProto.newBuilder();
                proto.setTitle(notice.getTitle());
                proto.setContent(notice.getContent());
                builder.addNotice(proto);
            }
        }
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_NOTICE_LIST_RESPONSE_VALUE, builder, user);
    }

    /**
     * 发送认证消息
     */
    public void sendAuthentication(ServerUser user) {
        UserAuthenticationEntity entity = authenticationMapper.getByUserId(user.getId());
        AuthenticateInfoResponse.Builder builder = AuthenticateInfoResponse.newBuilder();
        if (entity != null) {
            builder.setIdCardNum(entity.getIdcardNo());
            builder.setPhoneNum(entity.getPhoneNo());
            builder.setRealName(entity.getName());
        }
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_AUTHENTICATE_INFO_RESPONSE_VALUE, builder, user);
    }

    /**
     * 获取客服微信
     */
    public String getSupportWechat() {
        String wechat = RedisHelper.get(SERVICE_WECHAT_KEY);
        return StringUtils.isEmpty(wechat) ? "" : wechat;
    }

    /**
     * 设置客服微信
     */
    public void setSupportWechat(String wechat) {
        RedisHelper.set(SERVICE_WECHAT_KEY, wechat);
    }

    /**
     * 获取客服二维码
     */
    public String getSupportQRCode() {
        String QRCode = RedisHelper.get(SERVICE_QRODE_KEY);
        return StringUtils.isEmpty(QRCode) ? "" : QRCode;
    }

    /**
     * 设置客服二维码
     */
    public void setSupportQRCode(String QRCode) {
        RedisHelper.set(SERVICE_QRODE_KEY, QRCode);
    }

    /**
     * 用户设置账号手机号验证
     */
    public void accountPhoneCheck(String phoneNum, ServerUser user) {

        if (!ValidateUtil.isPhoneNumber(phoneNum)) {
            NetManager.sendErrorMessageToClient("输入的手机号码格式错误！", user);
            return;
        }

        long count = userMapper.findCountByPhonenum(phoneNum);
        if (count >= 3) {
            NetManager.sendHintMessageToClient("每个手机号最多绑定3个账号", user);
            return;
        }

        long nowTime = System.currentTimeMillis();
        PhoneCheck checkLog = phoneCheckMap.get(user.getId());

        // 冷却时间
        long coolDown = 60 * 1000;

        synchronized (user) {

            if (checkLog != null && nowTime - checkLog.getCheckTime().getTime() < coolDown) {

                NetManager.sendHintMessageToClient("请勿频繁获取验证码", user);
                return;

            }

            // aliSmsConfig.setSign("欢乐海洋");
            // aliSmsConfig.setAccessKeyId("LTAIC6Mh3tOiIhHd");
            // aliSmsConfig.setAccessSecret("eDZQMdzROxeElmLRcHvfjvqmxLixvj");
            int checkCode = ThreadLocalRandom.current().nextInt(100000, 1000000);
            // String sms = "SMS_182680674";

            String message = SmsUtil.signIn(phoneNum, String.valueOf(checkCode));

            // 发送短信
            if (message == null) {

                PhoneCheck phoneCheck = new PhoneCheck();
                phoneCheck.setCheckCode(checkCode);
                phoneCheck.setCheckTime(new Date());
                phoneCheck.setPhoneNum(phoneNum);
                // 放入内存储存
                phoneCheckMap.put(user.getId(), phoneCheck);
                AccountPhoneCheckResponse.Builder builder = AccountPhoneCheckResponse.newBuilder();
                builder.setResult(true);
                NetManager.sendMessage(OseeMsgCode.S_C_TTMY_ACCOUNT_PHONE_CHECK_RESPONSE_VALUE, builder, user);

            } else {

                if (message.contains("LIMIT_CONTROL")) {

                    NetManager.sendHintMessageToClient("验证码发送次数过多，请明日再尝试", user);

                } else {

                    NetManager.sendHintMessageToClient("验证短信发送失败，请稍后尝试", user);

                }

            }

        }

    }

    /**
     * 玩家设置账号
     */
    public void accountSet(AccountSetRequest request, ServerUser user) {
        String phoneNum = request.getPhoneNum();
        int checkCode = request.getCheckCode();
        // 传输的已经加密的内容
        String password = request.getPassword();

        if (!ValidateUtil.isPhoneNumber(phoneNum)) {
            NetManager.sendErrorMessageToClient("输入的手机号码格式错误！", user);
            return;
        }
        // 判断验证码是否正确
        PhoneCheck phoneCheck = phoneCheckMap.get(user.getId());
        if (phoneCheck == null || !phoneCheck.getPhoneNum().equals(phoneNum)) {
            NetManager.sendHintMessageToClient("请先进行手机验证", user);
            return;
        }
        if (phoneCheck.getCheckCode() != checkCode) {
            NetManager.sendHintMessageToClient("验证码不正确", user);
            return;
        }
        if (userMapper.findByUsername(phoneNum) != null) {
            NetManager.sendHintMessageToClient("该手机号已被使用！请更换！", user);
            return;
        }
        UserEntity userEntity = user.getEntity();
        // userEntity.setUsername(phoneNum);
        userEntity.setPassword(password);
        // 账号即为手机号
        userEntity.setPhonenum(phoneNum);
        // 更新玩家信息
        userMapper.update(userEntity);
        // 奖励金币一万
        PlayerManager.addItem(user, ItemId.MONEY, 10000, ItemChangeReason.ACCOUNT_SET, true);

        AccountSetResponse.Builder builder = AccountSetResponse.newBuilder();
        builder.addRewards(ItemDataProto.newBuilder().setItemId(ItemId.MONEY.getId()).setItemNum(10000)); // 奖励金币一万
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_ACCOUNT_SET_RESPONSE_VALUE, builder, user);
    }

    /**
     * 更改用户昵称
     */
    public void changeNickname(ServerUser user, String nickname) {

        if (!checkLoginInfo(nickname)) {
            NetManager.sendWarnMessageToClient("输入昵称格式有误！", user);
            return;
        }
        if (StringUtils.isEmpty(nickname)) {
            NetManager.sendErrorMessageToClient("昵称不能为空！", user);
            return;
        }
        if (nickname.length() > 20) {
            NetManager.sendErrorMessageToClient("昵称长度不能超过20！", user);
            return;
        }
        if (user.getNickname().equals(nickname)) {
            NetManager.sendErrorMessageToClient("不能修改和之前一样的昵称！", user);
            return;
        }

        synchronized (LoginManager.unusedUsernames) {

            // 若待注册用户名在用户名池中，无需查询数据库，直接使用该用户名，并移除访用户名
            if (LoginManager.unusedUsernames.contains(nickname)) {

                LoginManager.unusedUsernames.remove(nickname);

            } else {

                UserEntity entity = userMapper.findByUsername(nickname);
                if (entity != null) {
                    NetManager.sendWarnMessageToClient("昵称已存在", user);
                    return;
                }

            }

            int cost = 10000;

            // 检查改名需要花费的钻石是否足够
            if (!PlayerManager.checkItem(user, ItemId.DRAGON_CRYSTAL, cost)) {
                NetManager.sendErrorMessageToClient("钻石不足！", user);
                return;
            }

            // 扣除金币
            PlayerManager.addItem(user, ItemId.DRAGON_CRYSTAL, -cost, ItemChangeReason.CHANGE_NICKNAME, true);
            UserEntity entity = user.getEntity();
            entity.setNickname(nickname);
            entity.setUsername(nickname);

            // 更改昵称
            userMapper.update(entity);

        }

        ChangeNicknameResponse.Builder builder = ChangeNicknameResponse.newBuilder();
        builder.setNickname(nickname);
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_CHANGE_NICKNAME_RESPONSE_VALUE, builder, user);
    }

    /**
     * 赠送玩家月卡每日赠送奖励
     */
    // TODO: 2022年5月9日 去掉
    public void sendDailyMonthCardRewards(ServerUser user) {
        // if (getItemNum(user, ItemId.MONTH_CARD) <= 0) { // 月卡到期了或者没开通月卡
        // return;
        // }
        // String key = String.format(MONTH_CARD_DAILY_REWARDS_KEY_NAMESPACE, user.getId());
        // String value = RedisHelper.get(key);
        // boolean give = true;
        // if (!StringUtils.isEmpty(value)) {
        // // 判断今天是否已经赠送了每日奖励
        // give = !LocalDate.parse(value).isEqual(LocalDate.now());
        // }
        // if (give) {
        // MessageEntity message = new MessageEntity();
        // message.setTitle("月卡每日奖励");
        // message.setContent("");
        // message.setToId(user.getId());
        // // 每日奖励：锁定卡8张、2张电磁炮、6张急速卡、4张分身卡
        // message.setItems(new ItemData[]{
        // new ItemData(ItemId.SKILL_LOCK.getId(), 8),
        // new ItemData(ItemId.SKILL_ELETIC.getId(), 2),
        // new ItemData(ItemId.SKILL_FAST.getId(), 6),
        // new ItemData(ItemId.FEN_SHEN.getId(), 4),
        // });
        // messageManager.sendMessage(message);
        // // 今日领取信息存入数据库
        // RedisHelper.set(key, LocalDate.now().toString());
        // }
    }

    /**
     * 赠送每日VIP登录奖励
     */
    public void sendDailyVipRewards(ServerUser user) {
        int vipLevel = PlayerManager.getPlayerVipLevel(user);
        if (vipLevel < 1) { // 不是VIP
            return;
        }
        String key = String.format(VIP_DAILY_REWARDS_KEY_NAMESPACE, user.getId());
        String value = RedisHelper.get(key);
        boolean give = true;
        if (!StringUtils.isEmpty(value)) {
            // 判断今天是否已经赠送了每日奖励
            give = !LocalDate.parse(value).isEqual(LocalDate.now());
        }
        if (give) {
            MessageEntity message = new MessageEntity();
            message.setTitle("VIP每日奖励");
            message.setContent("");
            message.setToId(user.getId());
            // 每日奖励物品
            // 获取对应vip等级的奖励
            ItemData[] vipRewardItem = VIP_REWARD_ITEMS[vipLevel - 1];
            message.setItems(vipRewardItem);
            MessageManager.sendMessage(message);
            // 今日领取信息存入数据库
            RedisHelper.set(key, LocalDate.now().toString());
        }
    }

    /**
     * 检查VIP7及以上的金币数量，每日上线金币不足指定数量，自动补足
     */
    public void checkVipMoneyEnough(ServerUser user) {
        int vipLevel = PlayerManager.getPlayerVipLevel(user);
        if (vipLevel < 7 || vipLevel > 9) {
            return;
        }
        String key = String.format(VIP_MONEY_ENOUGH_KEY_NAMESPACE, user.getId());
        String value = RedisHelper.get(key);
        boolean give = true;
        if (!StringUtils.isEmpty(value)) {
            // 判断今天是否已经补足过金币
            give = !LocalDate.parse(value).isEqual(LocalDate.now());
        }
        if (give) {
            long[] moneyLimit = {1000000, 2000000, 3000000};
            // 跟金币阈值的差值
            long limit = getItemNum(user, ItemId.MONEY) - moneyLimit[vipLevel - 7];
            if (limit < 0) { // 金币不够就补足
                PlayerManager.addItem(user, ItemId.MONEY.getId(), -limit, null, true);
                // 补足信息存入数据库
                RedisHelper.set(key, LocalDate.now().toString());
            }
        }
    }

    /**
     * 获取玩家今日的剩余充值限额
     */
    public void moneyLimitRest(ServerUser user) {
        // 获取玩家今日充值的金额数量 数据库中存的是分为单位
        long todayRecharge = rechargeLogMapper.getTodayRecharge(user.getId()) / 100;
        RechargeLimitRestResponse.Builder builder = RechargeLimitRestResponse.newBuilder();
        builder.setLimitRest(3000 - todayRecharge); // 每日充值剩余限额 2w的上限
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_RECHARGE_LIMIT_REST_RESPONSE_VALUE, builder, user);
    }

    /**
     * 获取各功能模块启用状态
     */
    public void functionState(ServerUser user) {
        FunctionStateResponse.Builder builder = FunctionStateResponse.newBuilder();
        builder.addFunctionState(
                OseePublicData.FunctionStateProto.newBuilder().setFuncId(FunctionEnum.BUY_SKILL.getId()).setState(true)); // 1-商城购买技能功能
        // 判断6元首充是否有效
        boolean firstCharge = false;
        String key = String.format(FIRST_CHARGE_KEY_NAMESPACE, user.getId());
        String value = RedisHelper.get(key);
        if (!StringUtils.isEmpty(value)) { // 已经首充了
            firstCharge = true;
        }
        builder.addFunctionState(OseePublicData.FunctionStateProto.newBuilder()
                .setFuncId(FunctionEnum.FIRST_CHARGE.getId()).setState(!firstCharge)); // 2-首充功能

        boolean chessCards = false;
        AgentEntity agentEntity = agentMapper.getByPlayerId(user.getId());
        if (agentEntity != null && (agentEntity.getAgentLevel() != 3 || agentEntity.getAgentPlayerId() != null)) { // 有代理信息
            long agentId = 0;
            if (agentEntity.getAgentLevel() == 3) { // 会员
                ServerUser user1 = UserContainer.getUserById(agentEntity.getAgentPlayerId());
                if (((int) (double) PlayerManager.getPlayerEntity(user1).getPlayerType() + 1) == 3) {// 线上代理
                    agentId = agentEntity.getAgentPlayerId();
                    logger.info("线上代理：" + agentId);
                } else {
                    AgentEntity secondAgent = agentMapper.getByPlayerId(agentEntity.getAgentPlayerId());
                    if (secondAgent != null && secondAgent.getAgentPlayerId() != null) {
                        agentId = secondAgent.getAgentPlayerId();
                        logger.info("线下代理：" + agentId);
                    }
                }
            } else if (agentEntity.getAgentLevel() == 2) { // 二级代理
                if (agentEntity.getAgentPlayerId() != null) {
                    agentId = agentEntity.getAgentPlayerId();
                }
            } else if (agentEntity.getAgentLevel() == 1) { // 一级代理
                agentId = agentEntity.getPlayerId();
            }
            String openChessCardsStr = RedisHelper.get("Agent:OpenChessCards:" + agentId);
            if (!StringUtils.isEmpty(openChessCardsStr)) {
                long openChessCards = Long.parseLong(openChessCardsStr);
                if (openChessCards == 1) { // 开启了棋牌模块
                    chessCards = true;
                }
            }
        }
        builder.addFunctionState(
                OseePublicData.FunctionStateProto.newBuilder().setFuncId(FunctionEnum.CHESS_CARDS.getId()).setState(true) // 3-棋牌功能
                        .build());
        UserEntity entity = userMapper.findById(user.getId()); // 获取用户信息
        boolean isBang = true;
        if (entity != null) {
            if (StringUtils.isEmpty(entity.getIdcardNo())) {
                isBang = false;
            }
        } else {
            isBang = false;
        }
        builder.addFunctionState(
                OseePublicData.FunctionStateProto.newBuilder().setFuncId(FunctionEnum.BANG_IDCARD.getId()).setState(isBang) // 4-绑定身份证
                        .build());
        boolean isSend = true;
        if (StringUtils.isEmpty(entity.getSendPassword())) {
            isSend = false;
        }
        boolean isPassword = true;
        if (StringUtils.isEmpty(entity.getPassword())) {
            isPassword = false;
        }
        builder.addFunctionState(OseePublicData.FunctionStateProto.newBuilder()
                .setFuncId(FunctionEnum.HAVE_NICKNAME.getId()).setState(isPassword) // 6-设置昵称
                .build());
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_FUNCTION_STATE_RESPONSE_VALUE, builder, user);
    }

    /**
     * 检测玩家是否需要救济金并给予救济金
     */
    public static void checkReliefMoney(ServerUser user) {

        if (MyRefreshFishingUtil.FISH_GAME_CONFIG.getReliefMoneyFlag() != 1) {
            return;
        }

        synchronized (user) {

            long playerMoney = PlayerManager.getPlayerEntity(user).getDragonCrystal();

            int reliefMoney = 0;

            if (playerMoney <= 0) {

                String key = String.format("Server:ReliefMoney:%d", user.getId());

                // 获取：救济金的剩余次数
                int restTimes = getReliefMoneyRestTimes(key);

                if (restTimes <= 0) { // 今日领取次数已用完
                    return;
                }

                restTimes--;
                // 更新记录数据
                RedisHelper.set(key, LocalDate.now() + "," + restTimes);

                // vip领取的金币翻等级+1倍
                int vipLevel = PlayerManager.getPlayerVipLevel(user);
                if (vipLevel > 1) {
                    reliefMoney *= vipLevel;
                }

                // if(RedisUtil.val("USER_T_STATUS"+user.getId(),0L)!=0){
                // x = getUserT(user);
                // }else{
                // x = LoginSignManager.getUserTNew(user);
                // }
                // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(reliefMoney));

                // 增加金币
                PlayerManager.addItem(user, ItemId.DRAGON_CRYSTAL, reliefMoney, null, true);

                GetReliefMoneyResponse.Builder builder = GetReliefMoneyResponse.newBuilder();

                builder.setMoney(reliefMoney);
                builder.setRestTimes(restTimes);

                NetManager.sendMessage(OseeMsgCode.S_C_TTMY_GET_RELIEF_MONEY_RESPONSE_VALUE, builder, user);

                // FishingManager.joinchangePeak(user, 1);
                // logger.info("玩家[{}]领取救济金[{}],剩余次数[{}]", user.getNickname(), reliefMoney, restTimes);
                // RedisHelper.set("USER_LOSE_ALL" + user.getId(), "0");

            }

        }

    }

    /**
     * 获取：救济金的剩余次数
     */
    public static int getReliefMoneyRestTimes(String key) {

        String value = RedisHelper.get(key);

        int restTimes; // 剩余次数

        if (!StringUtils.isEmpty(value)) {

            String[] split = value.split(",");

            LocalDate date = LocalDate.parse(split[0]);

            if (!date.isEqual(LocalDate.now())) { // 不是当天的，重置为新的领取

                restTimes = 4;

            } else {

                restTimes = Integer.parseInt(split[1]);

            }

        } else {

            // 领取新的救济
            restTimes = 4;
        }

        return restTimes;

    }

    // public static int getUserT(ServerUser user,int roomIndex){
    // int x = 0;
    // long cx = new Double(RedisUtil.val("ALL_CX_USER"+user.getId(),0D)).longValue();
    // long bankNumber = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER"+user.getId(),0L)+500000;
    // double banlace = 0L;
    // if(roomIndex>=10 && roomIndex<13){
    // cx = new Double(RedisUtil.val("ALL_CX_USER_CHALLENG"+user.getId(),0D)).longValue();
    // bankNumber = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_CHALLENGE"+user.getId(),0L)+5000;
    // if(cx>0){
    // banlace = bankNumber;
    // }else{
    // banlace = cx * FishingUtil.cxPercentage[roomIndex]*0.01 + bankNumber;
    // }
    // ArrayList<Long> list = new ArrayList<Long>();
    // list.add(new Double(RedisUtil.val("ALL_XH_200-max_CHALLENGE"+user.getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_1-50_CHALLENG"+user.getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_50-100_CHALLENG"+user.getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_100-200_CHALLENG"+user.getId(),0D)).longValue());
    // Collections.sort(list);
    // for(int i=0;i>=0;i++){
    // int a = ThreadLocalRandom.current().nextInt(1,4);
    // switch (a){
    // case 1:{
    // RedisHelper.set("USER_T_STATUS"+user.getId(),"1");
    // x = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.peakMinNum1[roomIndex]).intValue(),new
    // Double(FishingUtil.peakMaxNum1[roomIndex]).intValue());
    // int b = ThreadLocalRandom.current().nextInt(1,4);
    //// if((FishingUtil.peakMax1[roomIndex]*banlace - PlayerManager.getPlayerMoney(user))<list.get(3)){
    //// int c = ThreadLocalRandom.current().nextInt(1,4);
    //// switch (c){
    //// case 1:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    //// break;
    //// }
    //// case 2:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    //// break;
    //// }
    //// case 3:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    //// break;
    //// }
    //// default:{
    //// break;
    //// }
    //// }
    //// }else{
    // if(PlayerManager.getPlayerEntity(user).getDragonCrystal()>FishingUtil.peakMax1[roomIndex]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else if(PlayerManager.getPlayerEntity(user).getDragonCrystal()<FishingUtil.peakMin1[roomIndex]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else{
    // switch (b){
    // case 1:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 2:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[3]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[4]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[5]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 3:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    //// }
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_STATUS"+user.getId(),"2");
    // x = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.peakMinNum2[roomIndex]).intValue(),new
    // Double(FishingUtil.peakMaxNum2[roomIndex]).intValue());
    // int b = ThreadLocalRandom.current().nextInt(1,4);
    //// if((FishingUtil.peakMax2[roomIndex]*banlace - PlayerManager.getPlayerMoney(user))<list.get(3)){
    //// int c = ThreadLocalRandom.current().nextInt(1,4);
    //// switch (c){
    //// case 1:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    //// break;
    //// }
    //// case 2:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    //// break;
    //// }
    //// case 3:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    //// break;
    //// }
    //// default:{
    //// break;
    //// }
    //// }
    //// }else{
    // if(PlayerManager.getPlayerEntity(user).getDragonCrystal()>FishingUtil.peakMax2[roomIndex]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else if(PlayerManager.getPlayerEntity(user).getDragonCrystal()<FishingUtil.peakMin2[roomIndex]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else{
    // switch (b){
    // case 1:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 2:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[3]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[4]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[5]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 3:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    //// }
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_STATUS"+user.getId(),"3");
    // x = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.peakMinNum3[roomIndex]).intValue(),new
    // Double(FishingUtil.peakMaxNum3[roomIndex]).intValue());
    // int b = ThreadLocalRandom.current().nextInt(1,4);
    //// if((FishingUtil.peakMax3[roomIndex]*banlace - PlayerManager.getPlayerMoney(user))<list.get(3)){
    //// int c = ThreadLocalRandom.current().nextInt(1,4);
    //// switch (c){
    //// case 1:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    //// break;
    //// }
    //// case 2:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    //// break;
    //// }
    //// case 3:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    //// break;
    //// }
    //// default:{
    //// break;
    //// }
    //// }
    //// }else{
    // if(PlayerManager.getPlayerEntity(user).getDragonCrystal()>FishingUtil.peakMax3[roomIndex]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else if(PlayerManager.getPlayerEntity(user).getDragonCrystal()<FishingUtil.peakMin3[roomIndex]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else{
    // switch (b){
    // case 1:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 2:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[3]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[4]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[5]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 3:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    //// }
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // if(x!=0){
    // break;
    // }else{
    // continue;
    // }
    // }
    // RedisHelper.set("USER_USE_PEAK"+user.getId(),"0");
    // return x;
    // }
    // else if(roomIndex==13){
    // if(cx>0){
    // banlace = bankNumber;
    // }else{
    // banlace = cx * FishingUtil.cxPercentage[roomIndex]*0.01 + bankNumber;
    // }
    // ArrayList<Long> list = new ArrayList<Long>();
    // list.add(new Double(RedisUtil.val("ALL_XH_200-max"+user.getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_1-50"+user.getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_50-100"+user.getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_100-200"+user.getId(),0D)).longValue());
    // Collections.sort(list);
    // for(int i=0;i>=0;i++){
    // int a = ThreadLocalRandom.current().nextInt(1,4);
    // switch (a){
    // case 1:{
    // RedisHelper.set("USER_T_STATUS"+user.getId(),"1");
    // x = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.peakMinNum1[roomIndex]).intValue(),new
    // Double(FishingUtil.peakMaxNum1[roomIndex]).intValue());
    // int b = ThreadLocalRandom.current().nextInt(1,4);
    // if(PlayerManager.getPlayerMoney(user)>FishingUtil.peakMax1[roomIndex]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else if(PlayerManager.getPlayerMoney(user)<FishingUtil.peakMin1[roomIndex]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else{
    // switch (b){
    // case 1:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 2:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[3]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[4]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[5]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 3:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_STATUS"+user.getId(),"2");
    // x = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.peakMinNum2[roomIndex]).intValue(),new
    // Double(FishingUtil.peakMaxNum2[roomIndex]).intValue());
    // int b = ThreadLocalRandom.current().nextInt(1,4);
    // if(PlayerManager.getPlayerMoney(user)>FishingUtil.peakMax2[roomIndex]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else if(PlayerManager.getPlayerMoney(user)<FishingUtil.peakMin2[roomIndex]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else{
    // switch (b){
    // case 1:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 2:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[3]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[4]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[5]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 3:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_STATUS"+user.getId(),"3");
    // x = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.peakMinNum3[roomIndex]).intValue(),new
    // Double(FishingUtil.peakMaxNum3[roomIndex]).intValue());
    // int b = ThreadLocalRandom.current().nextInt(1,4);
    // if(PlayerManager.getPlayerMoney(user)>FishingUtil.peakMax3[roomIndex]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else if(PlayerManager.getPlayerMoney(user)<FishingUtil.peakMin3[roomIndex]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else{
    // switch (b){
    // case 1:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 2:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[3]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[4]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[5]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 3:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // if(x!=0){
    // break;
    // }else{
    // continue;
    // }
    // }
    // RedisHelper.set("USER_USE_PEAK"+user.getId(),"0");
    // return x;
    // }
    // else{
    // if(cx>0){
    // banlace = bankNumber;
    // }else{
    // banlace = cx * FishingUtil.cxPercentage[roomIndex*2-1]*0.01 + bankNumber;
    // }
    // ArrayList<Long> list = new ArrayList<Long>();
    // list.add(new Double(RedisUtil.val("ALL_XH_200-max"+user.getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_1-50"+user.getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_50-100"+user.getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_100-200"+user.getId(),0D)).longValue());
    // Collections.sort(list);
    // for(int i=0;i>=0;i++){
    // int a = ThreadLocalRandom.current().nextInt(1,4);
    // switch (a){
    // case 1:{
    // RedisHelper.set("USER_T_STATUS"+user.getId(),"1");
    // x = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.peakMinNum1[roomIndex*2-1]).intValue(),new
    // Double(FishingUtil.peakMaxNum1[roomIndex*2-1]).intValue());
    // int b = ThreadLocalRandom.current().nextInt(1,4);
    //// if((FishingUtil.peakMax1[roomIndex]*banlace - PlayerManager.getPlayerMoney(user))<list.get(3)){
    //// int c = ThreadLocalRandom.current().nextInt(1,4);
    //// switch (c){
    //// case 1:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    //// break;
    //// }
    //// case 2:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    //// break;
    //// }
    //// case 3:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    //// break;
    //// }
    //// default:{
    //// break;
    //// }
    //// }
    //// }else{
    // if(PlayerManager.getPlayerMoney(user)>FishingUtil.peakMax1[roomIndex*2-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else if(PlayerManager.getPlayerMoney(user)<FishingUtil.peakMin1[roomIndex*2-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else{
    // switch (b){
    // case 1:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 2:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[3]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[4]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[5]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 3:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    //// }
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_STATUS"+user.getId(),"2");
    // x = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.peakMinNum2[roomIndex*2-1]).intValue(),new
    // Double(FishingUtil.peakMaxNum2[roomIndex*2-1]).intValue());
    // int b = ThreadLocalRandom.current().nextInt(1,4);
    //// if((FishingUtil.peakMax2[roomIndex]*banlace - PlayerManager.getPlayerMoney(user))<list.get(3)){
    //// int c = ThreadLocalRandom.current().nextInt(1,4);
    //// switch (c){
    //// case 1:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    //// break;
    //// }
    //// case 2:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    //// break;
    //// }
    //// case 3:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    //// break;
    //// }
    //// default:{
    //// break;
    //// }
    //// }
    //// }else{
    // if(PlayerManager.getPlayerMoney(user)>FishingUtil.peakMax2[roomIndex*2-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else if(PlayerManager.getPlayerMoney(user)<FishingUtil.peakMin2[roomIndex*2-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else{
    // switch (b){
    // case 1:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 2:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[3]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[4]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[5]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 3:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    //// }
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_STATUS"+user.getId(),"3");
    // x = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.peakMinNum3[roomIndex*2-1]).intValue(),new
    // Double(FishingUtil.peakMaxNum3[roomIndex*2-1]).intValue());
    // int b = ThreadLocalRandom.current().nextInt(1,4);
    //// if((FishingUtil.peakMax3[roomIndex]*banlace - PlayerManager.getPlayerMoney(user))<list.get(3)){
    //// int c = ThreadLocalRandom.current().nextInt(1,4);
    //// switch (c){
    //// case 1:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    //// break;
    //// }
    //// case 2:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    //// break;
    //// }
    //// case 3:{
    //// RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    //// RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    //// break;
    //// }
    //// default:{
    //// break;
    //// }
    //// }
    //// }else{
    // if(PlayerManager.getPlayerMoney(user)>FishingUtil.peakMax3[roomIndex*2-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else if(PlayerManager.getPlayerMoney(user)<FishingUtil.peakMin3[roomIndex*2-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    // else{
    // switch (b){
    // case 1:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 2:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[3]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[4]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[5]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // case 3:{
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+user.getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+user.getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // }
    //// }
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // if(x!=0){
    // break;
    // }else{
    // continue;
    // }
    // }
    // RedisHelper.set("USER_USE_PEAK"+user.getId(),"0");
    // return x;
    // }
    // }

    /**
     * 检查登录信息
     */
    private boolean checkLoginInfo(String username) {
        // 2-6位字母或汉字
        if (username != null && !Pattern.matches("^[a-zA-Z\u4E00-\u9FA5]{2,6}$", username)) {
            return false;
        }
        return true;
    }

    /**
     * 改变头像
     */
    public void changeHead(ServerUser user, ChangeHeadRequest request) {

        UserEntity userEntity = user.getEntity();

        if (userEntity == null) {
            return;
        }

        synchronized (userEntity) {

            byte[] byteArr = request.getHeadImage().toByteArray();

            InputStream inputStream = new ByteArrayInputStream(byteArr);

            String fileType = MyFileTypeUtil.getType(inputStream, null);

            if (BooleanUtil.isFalse(MyFileTypeUtil.IMG_FILE_TYPE_LIST.contains(fileType))) {

                NetManager.sendErrorMessageToClient("上传失败：请上传图片类型的文件", user);
                return;

            }

            try {

                BufferedImage bufferedImage = ImgUtil.toImage(byteArr);

                int size = 600;

                if (bufferedImage.getWidth() >= size && bufferedImage.getHeight() >= size) {
                    NetManager.sendErrorMessageToClient("图片尺寸超过600*600，上传失败！", user);
                    return;
                }

                DataSize dataSize = DataSize.ofMegabytes(5);

                if (byteArr.length >= dataSize.toBytes()) {
                    NetManager.sendErrorMessageToClient("图片大小超过 20MB，上传失败！", user);
                    return;
                }

                String filePathPre = "/serverFile/headImg/" + user.getId();

                String filePath = filePathPre + "/headImg." + fileType;

                // 创建文件
                File file = FileUtil.touch(filePath);

                // 把数据写入文件里
                FileUtil.writeBytes(byteArr, file);

                // 移除：其他头像
                for (File item : FileUtil.ls(filePathPre)) {

                    if (BooleanUtil.isFalse(item.equals(file))) { // 移除：其他头像文件
                        FileUtil.del(item);
                    }

                }

                // 创建文件：64 * 64
                File file64x64 = FileUtil.touch(filePathPre + "/headImg64x64." + fileType);

                ImgUtil.scale(file, file64x64, 64, 64, null);

                // 更新：数据库
                userEntity.setHeadUrl(filePath);
                userMapper.update(userEntity);

                ChangeHeadResponse.Builder builder = ChangeHeadResponse.newBuilder();

                builder.setHeadImage(""); // 备注：这里不需要回传

                NetManager.sendMessage(OseeMsgCode.S_C_TTMY_CHANGE_HEAD_RESPONSE_VALUE, builder, user);

            } catch (Exception e) {

                NetManager.sendErrorMessageToClient("上传失败：请上传图片类型的文件", user);

            }

        }

    }

    // 用户：是否开通小金库
    public static final String PRE_USER_OPEN_TREASURY_FLAG = "PRE_USER_OPEN_TREASURY_FLAG:";

    public static RBucket<Boolean> getOpenTreasuryFlag(long userId) {

        return redissonClient.getBucket(PRE_USER_OPEN_TREASURY_FLAG + userId);

    }


}
