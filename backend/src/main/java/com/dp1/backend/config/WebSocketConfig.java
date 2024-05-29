package com.dp1.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket; 
import org.springframework.web.socket.config.annotation.WebSocketConfigurer; 
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.dp1.backend.handlers.SocketConnectionHandler; 
  
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SocketConnectionHandler socketConnectionHandler;

    public WebSocketConfig(SocketConnectionHandler socketConnectionHandler) {
        this.socketConnectionHandler = socketConnectionHandler;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(socketConnectionHandler, "/socket")
            .setAllowedOrigins("*");
    }
}