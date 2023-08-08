package com.example.OMPayment;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
public class WebSocketConfWithoutAuth implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Enable a simple message broker to send messages to clients
        config.setApplicationDestinationPrefixes("/app"); // Prefix for client-to-server messages
        System.out.println("yesssss");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();// Register WebSocket endpoint
    }

}
