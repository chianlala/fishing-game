package com.maple.game.osee.util;

import com.maple.database.data.mapper.UserMapper;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.entity.fishing.FishingGameRoom;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengePlayer;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengeRoom;
import com.maple.game.osee.entity.fishing.grandprix.FishingGrandPrixPlayer;
import com.maple.game.osee.entity.fishing.grandprix.FishingGrandPrixRoom;
import com.maple.game.osee.manager.fishing.FishingGrandPrixManager;
import com.maple.game.osee.manager.fishing.FishingManager;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.BaseGameRoom;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.maple.game.osee.manager.fishing.FishingChallengeManager.sendJoinRoomResponse;

@Component
@Slf4j
public class GameUtil {


    private static UserMapper userMapper;

    @Resource
    public void setUserMapper(UserMapper userMapper) {
        GameUtil.userMapper = userMapper;
    }

    private static FishingGrandPrixManager fishingGrandPrixManager;


    @Resource
    public void setFishingGrandPrixManager(FishingGrandPrixManager fishingGrandPrixManager) {
        GameUtil.fishingGrandPrixManager = fishingGrandPrixManager;
    }

    private static FishingManager fishingManager;

    @Resource
    public void setFishingManager(FishingManager fishingManager) {
        GameUtil.fishingManager = fishingManager;
    }


    // 个控，误差值
    public static final int SINGLE_DIFF_VALUE = 10;

    /**
     * 通过：gameId获取 user
     */
    @Nullable
    public static ServerUser getServerUserByGameId(long targetGameId) {

        ServerUser serverUser = UserContainer.getUserById(targetGameId);

        if (serverUser == null) {

            Long userIdTmp = userMapper.findIdByGameId(targetGameId);

            if (userIdTmp == null) {
                return null;
            }

            serverUser = UserContainer.getUserById(userIdTmp);

        } else {

            long userId = serverUser.getId();

            boolean findIdByGameIdFlag;

            findIdByGameIdFlag = serverUser.getGameId() != targetGameId; // 如果目标的 gameId，不等于 targetGameId，则重新再找一遍


            if (findIdByGameIdFlag) {

                Long userIdTmp = userMapper.findIdByGameId(targetGameId);

                if (userIdTmp == null) {
                    return null;
                }

                serverUser = UserContainer.getUserById(userIdTmp);

            }

        }

        return serverUser;

    }

    /**
     * 加入房间，之前的操作
     */
    public static Integer joinRoomPre(Long userId, Integer roomCode) {

        BaseGamePlayer gamePlayer = GameContainer.getPlayerById(userId);

        if (gamePlayer == null) {

            return null;

        }

        BaseGameRoom gameRoom = GameContainer.getGameRoomByCode(gamePlayer.getRoomCode());

        if (gameRoom == null) {

            return null;

        }

        int oldRoomCode = gameRoom.getCode();
        if (gameRoom instanceof FishingChallengeRoom) { // 捕鱼挑战赛

            if (roomCode != null && oldRoomCode == roomCode) {

                sendJoinRoomResponse((FishingChallengeRoom) gameRoom, (FishingChallengePlayer) gamePlayer); // 防止反复加入：发送加入房间响应

            } else {

                FishingChallengeUtil.exitRoom((FishingChallengePlayer) gamePlayer, (FishingChallengeRoom) gameRoom);

            }

        } else if (gameRoom instanceof FishingGameRoom) { // 普通捕鱼

            fishingManager.exitFishingRoom((FishingGameRoom) gameRoom, gamePlayer.getUser());

        } else if (gameRoom instanceof FishingGrandPrixRoom) { // 大奖赛

            FishingGrandPrixManager.exitRoom((FishingGrandPrixPlayer) gamePlayer, (FishingGrandPrixRoom) gameRoom);

        }

        return oldRoomCode;

    }

    /**
     * 退出房间
     */
    public static void exitRoom(Long userId) {

        BaseGamePlayer baseGamePlayer = GameContainer.getPlayerById(userId);

        if (baseGamePlayer == null) {
            return;
        }

        BaseGameRoom baseGameRoom = GameContainer.getGameRoomByCode(baseGamePlayer.getRoomCode());

        if (baseGameRoom == null) {
            return;
        }
        if (baseGameRoom instanceof FishingChallengeRoom) { // 捕鱼挑战赛

            FishingChallengeUtil.exitRoom((FishingChallengePlayer) baseGamePlayer, (FishingChallengeRoom) baseGameRoom);

        } else if (baseGameRoom instanceof FishingGrandPrixRoom) { // 大奖赛

            FishingGrandPrixManager.exitRoom((FishingGrandPrixPlayer) baseGamePlayer,
                    (FishingGrandPrixRoom) baseGameRoom);

        }

    }

    /**
     * 检查：是否可以加入房间
     *
     * @return true 可以加入 false 不可以加入
     */
    public static boolean joinRoomCheck(ServerUser user) {
        return true;

    }

}
