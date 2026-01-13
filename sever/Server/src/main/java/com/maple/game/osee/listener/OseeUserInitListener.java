package com.maple.game.osee.listener;

import com.maple.engine.data.ServerUser;
import com.maple.engine.event.userinit.IUserInitEventListener;
import com.maple.engine.event.userinit.UserInitEvent;
import com.maple.game.osee.dao.data.mapper.MessageMapper;
import com.maple.game.osee.dao.data.mapper.OseePlayerMapper;
import com.maple.game.osee.manager.MessageManager;
import com.maple.game.osee.manager.PlayerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户初始化监听器
 */
@Component
public class OseeUserInitListener implements IUserInitEventListener {

    private static final Logger logger = LoggerFactory.getLogger(OseeUserInitListener.class);

    @Autowired
    private OseePlayerMapper playerMapper;
    @Resource
    MessageMapper messageMapper;
    @Resource
    MessageManager messageManager;

    @Override
    public void handleUserInitEvent(UserInitEvent event) {

        ServerUser user = event.getUser();
        PlayerManager.getPlayerEntity(user, true); // 初始化用户

    }

}
