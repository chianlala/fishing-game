package com.maple.game.osee.manager;

import com.maple.database.util.RedissonUtil;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.dao.data.entity.MessageEntity;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.dao.data.mapper.MessageMapper;
import com.maple.game.osee.dao.log.mapper.AppRankLogMapper;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemData;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.manager.lobby.CommonLobbyManager;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage;
import com.maple.game.osee.util.CallBack;
import com.maple.game.osee.util.FishingChallengeFightFishUtil;
import com.maple.network.manager.NetManager;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 消息/邮件管理类
 *
 * @author Junlong
 */
@Component
public class MessageManager {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 每位玩家最多可以接收的消息条数
     */
    public static final int MAX_MESSAGE_NUM = 20;

    private static MessageMapper messageMapper;

    @Autowired
    private AppRankLogMapper rankLogMapper;

    private static RedissonClient redissonClient;

    @Autowired
    public MessageManager(MessageMapper messageMapper, RedissonClient redissonClient) {

        MessageManager.messageMapper = messageMapper;
        MessageManager.redissonClient = redissonClient;

    }


    /**
     * 撤回邮件
     *
     * @param id 邮件id
     */
    public String revokeMessage(long id) {

        return RedissonUtil.doLock(MESSAGE_ID_LOCK_PRE + id, () -> {

            MessageEntity message = messageMapper.getById(id);

            if (message == null) {
                return "消息不存在";
            }

            // 设为撤回状态
            message.setState(2);
            messageMapper.update(message);

            return null;

        });

    }


    /**
     * 获取玩家消息列表
     */
    public void getMessageList(ServerUser user) {

        List<MessageEntity> list = messageMapper.getByToId(user.getId());
        OseeLobbyMessage.MessageListResponse.Builder builder = OseeLobbyMessage.MessageListResponse.newBuilder();

        for (MessageEntity message : list) {

            OseePublicData.MessageInfoProto.Builder messageInfo = OseePublicData.MessageInfoProto.newBuilder();
            messageInfo.setId(message.getId());
            messageInfo.setTime(message.getCreateTime().getTime());
            messageInfo.setRead(message.getRead());
            messageInfo.setReceive(message.getReceive());
            messageInfo.setTitle(message.getTitle());
            // 列表就不发生消息内容，避免数据量大
            // messageInfo.setContent(message.getContent());
            // if (message.getItems() != null) {
            // // 附件信息
            // for (ItemData itemData : message.getItems()) {
            // OseePublicData.ItemDataProto.Builder itemDataProto = OseePublicData.ItemDataProto.newBuilder();
            // itemDataProto.setItemId(itemData.getItemId());
            // itemDataProto.setItemNum(itemData.getCount());
            // messageInfo.addItems(itemDataProto);
            // }
            // }
            builder.addMessageInfo(messageInfo);

        }

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_MESSAGE_LIST_RESPONSE_VALUE, builder, user);

    }


    /**
     * 发送消息
     */
    public static long sendMessage(MessageEntity message) {

        if (message == null) {
            return 0L;
        }

        messageMapper.save(message);

        return message.getId();

    }

    /**
     * 读取消息
     */
    public void readMessage(long id, ServerUser user) {

        MessageEntity message = messageMapper.getById(id);
        if (message == null) {
            NetManager.sendErrorMessageToClient("消息已不存在！", user);
            return;
        }

        // 设为已读
        message.setRead(true);
        messageMapper.update(message);

        OseeLobbyMessage.ReadMessageResponse.Builder builder = OseeLobbyMessage.ReadMessageResponse.newBuilder();

        OseePublicData.MessageInfoProto.Builder messageInfo = OseePublicData.MessageInfoProto.newBuilder();
        messageInfo.setId(message.getId());
        messageInfo.setRead(message.getRead());
        messageInfo.setReceive(message.getReceive());
        messageInfo.setTime(message.getCreateTime().getTime());
        messageInfo.setContent(message.getContent());
        messageInfo.setTitle(message.getTitle());
        if (message.getItems() != null) {
            // 附件信息
            for (ItemData itemData : message.getItems()) {
                OseePublicData.ItemDataProto.Builder itemDataProto = OseePublicData.ItemDataProto.newBuilder();
                itemDataProto.setItemId(itemData.getItemId());
                itemDataProto.setItemNum(itemData.getCount());
                messageInfo.addItems(itemDataProto);
            }
        }

        builder.setMessage(messageInfo);
        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_READ_MESSAGE_RESPONSE_VALUE, builder, user);

    }

    public static final String MESSAGE_ID_LOCK_PRE = "MESSAGE_ID_LOCK_PRE:"; // 邮件 id锁，前缀，锁：邮件 id

    /**
     * 领取消息
     */
    public String receiveMessageItems(long id, ServerUser user, CallBack<String> errorMsgCallBack) {

        OseePublicData.ReceiveItemsResponse.Builder builder = OseePublicData.ReceiveItemsResponse.newBuilder();

        MessageEntity messageEntity = RedissonUtil.doLock(MESSAGE_ID_LOCK_PRE + id, () -> {

            MessageEntity message = messageMapper.getById(id);

            if (message == null) {
                String errorMsg = "邮件已不存在！";
                NetManager.sendErrorMessageToClient(errorMsg, user);
                if (errorMsgCallBack != null) {
                    errorMsgCallBack.setValue(errorMsg);
                }
                return null;
            }

            if (message.getReceive()) {
                String errorMsg = "请勿重复领取！";
                NetManager.sendErrorMessageToClient(errorMsg, user);
                if (errorMsgCallBack != null) {
                    errorMsgCallBack.setValue(errorMsg);
                }
                return null;
            }

            if (user.getId() != message.getToId()) {
                String errorMsg = "非法操作！";
                NetManager.sendErrorMessageToClient(errorMsg, user);
                if (errorMsgCallBack != null) {
                    errorMsgCallBack.setValue(errorMsg);
                }
                return null;
            }

            OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(user);

            synchronized (playerEntity) {


                long preDiamond = playerEntity.getDiamond();

                long preGoldTorpedo = playerEntity.getGoldTorpedo();

                long sufGoldTorpedo = preGoldTorpedo;

                ItemChangeReason itemChangeReason = ItemChangeReason.GIVE_GIFT;

                if (message.getTitle().contains("月卡")) {
                    itemChangeReason = ItemChangeReason.MONTH_CARD;
                } else if (message.getTitle().contains("大奖赛")) {
                    itemChangeReason = ItemChangeReason.FISHING_GRANDPRIX_CAERD;
                } else if (message.getTitle().contains("VIP")) {
                    itemChangeReason = ItemChangeReason.VIP_DAY_CARD;
                }

                ItemData[] items = message.getItems();

                if (items != null) { // 如果消息有附件就要领取

                    long count = 0;

                    for (ItemData itemData : items) {

                        OseePublicData.ItemDataProto.Builder itemDataProto = OseePublicData.ItemDataProto.newBuilder();

                        PlayerManager.addItem(user, itemData.getItemId(), itemData.getCount(), itemChangeReason, true);

                        itemDataProto.setItemId(itemData.getItemId());
                        itemDataProto.setItemNum(itemData.getCount());
                        builder.addItems(itemDataProto);

                        if (itemData.getItemId() == ItemId.DRAGON_CRYSTAL.getId()) {

                            count = count - itemData.getCount();

                        }

                    }

                    if (count != 0) {

                        // 改变：gretj
                        if (redissonClient.<String>getBucket(
                                        FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE
                                                + user.getId())
                                .isExists()) {

                            redissonClient
                                    .getAtomicDouble(FishingChallengeFightFishUtil.FISHING_GRETJ_USER_PRE + user.getId())
                                    .addAndGet(count);

                        }

                        // 节点变化时，清除 aq相关
                        FishingChallengeFightFishUtil.cleanAqData(user.getId());

                    }

                    sufGoldTorpedo = playerEntity.getGoldTorpedo();

                    message.setRead(true);
                    message.setReceive(true);
                    message.setReceiveTime(new Date());
                    messageMapper.update(message);
                    rankLogMapper.updateByEamilId(id, new Date());

                }

                builder.setFromPlayerId(message.getFromId());
                builder.setTime(message.getCreateTime().getTime());
                builder.setToPlayerId(message.getToId());

                builder.setFromGamePlayerId(message.getFromGameId());
                builder.setToGamePlayerId(message.getToGameId());

                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_RECEIVE_MESSAGE_ITEMS_RESPONSE_VALUE,
                        OseeLobbyMessage.ReceiveMessageItemsResponse.newBuilder().setResult(true), user);

                if (message.getFromId() > 0) {
                    logger.info("-------------发送人------------" + message.getFromId());
                    NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_RECEIVE_MESSAGE_ITEMS_RESPONSE_VALUE, builder,
                            user);
                }

                return message;

            }

        });

        return null;

    }


    /**
     * 删除消息
     */
    public void deleteMessage(long id, ServerUser user) {

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(id);
        messageMapper.deleteLogical(messageEntity);

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_RECEIVE_MESSAGE_ITEMS_RESPONSE_VALUE,
                OseeLobbyMessage.DeleteMessageResponse.newBuilder().setResult(true), user);

    }

}
