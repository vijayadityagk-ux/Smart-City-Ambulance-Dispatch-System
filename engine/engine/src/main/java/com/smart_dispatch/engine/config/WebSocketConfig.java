package com.smart_dispatch.engine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Turns on the WebSocket server
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // The prefix for channels the frontend will LISTEN to (e.g., /topic/alerts)
        config.enableSimpleBroker("/topic");
        
        // The prefix for channels the frontend will SEND messages to
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The URL the frontend uses to establish the initial connection.
        // We use setAllowedOriginPatterns("*") so your React/JS app can connect from a different port without CORS errors.
        registry.addEndpoint("/smart-city-ws").setAllowedOriginPatterns("*").withSockJS();
    }
}