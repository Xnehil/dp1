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

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry webSocketHandlerRegistry) 
    { 
        // Para agregar un controlador, damos la clase de controlador que
        // creamos antes. También estamos gestionando la política CORS para los controladores
        // para que otros dominios también puedan acceder al socket
        webSocketHandlerRegistry 
            .addHandler(new SocketConnectionHandler(),"/socket") 
            .setAllowedOrigins("*"); 
    } 
}