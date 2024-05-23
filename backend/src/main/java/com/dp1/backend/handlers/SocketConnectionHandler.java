package com.dp1.backend.handlers;

import java.io.IOException;
import java.util.ArrayList; 
import java.util.Collections; 
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage; 
import org.springframework.web.socket.WebSocketSession; 
import org.springframework.web.socket.handler.TextWebSocketHandler;

  

public class SocketConnectionHandler extends TextWebSocketHandler { 
    private static final Logger logger = LogManager.getLogger(SocketConnectionHandler.class);
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    //En esta lista se almacenarán todas las conexiones. Luego se usará para transmitir el mensaje
    List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>()); 
  
    // Este método se ejecuta cuando el cliente intenta conectarse a los sockets
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception 
    { 
        super.afterConnectionEstablished(session); 
        logger.info(session.getId() + "  conectado al socket");
        webSocketSessions.add(session); 

        executorService.scheduleAtFixedRate(() -> {
            try {
                // Send a message to the session
                session.sendMessage(new TextMessage("Hello from server, client!"+session.getId()));
            } catch (IOException e) {
                logger.error("Error sending message", e);
            }
        }, 0,10, TimeUnit.SECONDS);
    } 
  
    // Cuando el cliente se desconecta del WebSocket, se llama a este método
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,@NonNull CloseStatus status)throws Exception 
    { 
        super.afterConnectionClosed(session, status); ; 
        logger.info(session.getId() + "  desconectado del socket");
        // Removing the connection info from the list 
        webSocketSessions.remove(session); 
    } 
  
    // Se encargará de intercambiar mensajes en la red
    // Tendrá información de la sesión que está enviando el mensaje
    // También el objeto de mensaje pasa como parámetro
    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception 
    { 
        super.handleMessage(session, message); 
        // Si el mensaje contiene "tiempo", se imprimirá en el log
        if (message.getPayload().toString().contains("tiempo")) 
        { 
            logger.trace("Mensaje recibido: " + message.getPayload().toString()); 
        }
    } 
}
