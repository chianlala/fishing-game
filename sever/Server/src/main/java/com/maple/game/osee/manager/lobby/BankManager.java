package com.maple.game.osee.manager.lobby;

import com.maple.engine.data.ServerUser;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemData;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.proto.OseeMessage.OseeMsgCode;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage.ChangeBankPasswordResponse;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage.CheckBankPasswordResponse;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage.SaveMoneyResponse;
import com.maple.game.osee.util.FishingChallengeFightFishUtil;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGameRoom;
import com.maple.network.manager.NetManager;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * 保险箱管理类
 */
@Component
public class BankManager {

    private static RedissonClient redissonClient;

    @Resource
    public void setRedissonClient(RedissonClient redissonClient) {
        BankManager.redissonClient = redissonClient;
    }

    /**
     * 检查保险箱密码
     */
    private static boolean checkBankPassword(ServerUser user, String bankPassword) {
        return bankPassword.equals(PlayerManager.getPlayerEntity(user).getBankPassword());
    }

    /**
     * 检查保险箱密码任务
     */
    public static void checkBankPasswordTask(ServerUser user, String bankPassword) {
        CheckBankPasswordResponse.Builder builder = CheckBankPasswordResponse.newBuilder();
        builder.setPassword(bankPassword);
        builder.setSuccess(checkBankPassword(user, bankPassword));
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_CHECK_BANK_PASSWORD_RESPONSE_VALUE, builder, user);
    }

    /**
     * 存取金币任务
     */
    public static void saveMoneyTask(ServerUser user, String bankPassword, long money) {

        BaseGameRoom baseGameRoom = GameContainer.getGameRoomByPlayerId(user.getId());

        if (baseGameRoom != null) {

            NetManager.sendHintMessageToClient("当前状态无法执行存入操作！", user);
            return;

        }

        OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(user);

        synchronized (playerEntity) {

            if (money > 0) { // 存入

                if (!PlayerManager.checkItem(user, ItemId.DRAGON_CRYSTAL, money)) {
                    NetManager.sendHintMessageToClient("携带金币不足，无法执行存入操作", user);
                    return;
                }

                if (money < 50000) {
                    NetManager.sendHintMessageToClient("存入失败，单次最低存放金额为5万龙晶", user);
                    return;
                }

            } else if (money < 0) { // 取出

                if (!checkBankPassword(user, bankPassword)) {
                    NetManager.sendHintMessageToClient("保险箱密码错误，请重新输入", user);
                    return;
                }

                if (!PlayerManager.checkItem(user, ItemId.BANK_MONEY, -money)) {
                    NetManager.sendHintMessageToClient("保险箱龙晶不足，无法执行取出操作", user);
                    return;
                }

            } else {

                NetManager.sendHintMessageToClient("存取龙晶数量不能为0", user);
                return;

            }

            List<ItemData> itemDatas = new LinkedList<>();

            itemDatas.add(new ItemData(ItemId.DRAGON_CRYSTAL.getId(), -money));
            itemDatas.add(new ItemData(ItemId.BANK_MONEY.getId(), money));

            ItemChangeReason reason = money > 0 ? ItemChangeReason.BANK_IN : ItemChangeReason.BANK_OUT;

            long preBankMoney = playerEntity.getBankMoney();

            PlayerManager.addItems(user, itemDatas, reason, true);

            // 改变：gretj
            if (redissonClient
                .<String>getBucket(
                    FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + user.getId())
                .isExists()) {

                redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_GRETJ_USER_PRE + user.getId())
                    .addAndGet(money);

            }

            // 节点变化时，清除 aq相关
            FishingChallengeFightFishUtil.cleanAqData(user.getId());
        }

        SaveMoneyResponse.Builder builder = SaveMoneyResponse.newBuilder();
        builder.setSuccess(true);
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_SAVE_MONEY_RESPONSE_VALUE, builder, user);

    }

    /**
     * 修改保险箱密码任务
     */
    public static void changeBankPasswordTask(ServerUser user, String oldPassword, String newPassword) {
        if (!checkBankPassword(user, oldPassword)) {
            NetManager.sendHintMessageToClient("保险箱密码错误，请重新输入", user);
            return;
        }

        PlayerManager.getPlayerEntity(user).setBankPassword(newPassword);
        PlayerManager.updateEntities.add(PlayerManager.getPlayerEntity(user));

        ChangeBankPasswordResponse.Builder builder = ChangeBankPasswordResponse.newBuilder();
        builder.setSuccess(true);
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_CHANGE_BANK_PASSWORD_RESPONSE_VALUE, builder, user);
    }

}
