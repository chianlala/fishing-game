package com.maple.game.osee.controller;

import com.google.protobuf.Message;
import com.maple.engine.data.ServerUser;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * AppController，基础控制器
 */
@Slf4j
public abstract class BaseAppController {

    /**
     * 默认检查器
     */
    public void appChecker(Method method, Message req, ServerUser user, Long exp) {

        try {

            method.invoke(this, req, user);

        } catch (InvocationTargetException e) {

            Throwable targetException = e.getTargetException();

            targetException.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

}
