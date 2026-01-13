package com.jeesite.modules.osee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 开启WebSocket支持
 * <p>
 * 通过@EnableWebSocketMessageBroker注解开启试用STOMP协议来传输基于代理（message broker）的消息，
 * 这时控制器支持使用@MessageMapping就像使用@RequestMapping一样
 *
 * @author zjl
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 册为STOMP端点，这个路径与发送和接收消息的目的路径有所不同， 这是一个端点，客户端在订阅或发布消息到目的地址前，要连接该端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        // 注册一个STOMP的endpoint,并指定使用SockJS协议
        stompEndpointRegistry.addEndpoint("/pushEndpoint").withSockJS();
    }

    /**
     * 配置消息代理（Message Broker）
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 服务端发送消息前缀
        registry.enableSimpleBroker("/topic", "/push");
        // 在客户端发送–>服务端时的地址前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 可以不设置，客户端订阅点对点地址时的地址前缀，默认不设置时是/user/
        // registry.setUserDestinationPrefix("/user/");
    }
}
