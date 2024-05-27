package com.dp1.backend.handlers;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList; 
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage; 
import org.springframework.web.socket.WebSocketSession; 
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.dp1.backend.models.Vuelo;
import com.dp1.backend.services.DatosEnMemoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

  
@Component
public class SocketConnectionHandler extends TextWebSocketHandler { 
    private static final Logger logger = LogManager.getLogger(SocketConnectionHandler.class);
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private HashMap<WebSocketSession, ZonedDateTime> lastMessageTimes = new HashMap<>();
    private HashMap<WebSocketSession, ZonedDateTime> simulatedTimes = new HashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    //En esta lista se almacenarán todas las conexiones. Luego se usará para transmitir el mensaje
    private List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>()); 

    //Por cada conexión, se guarda una lista paralela de vuelos en el aire
    private HashMap<WebSocketSession, HashMap<Integer, Vuelo>> vuelosEnElAire = new HashMap<>();

    @Autowired
    private DatosEnMemoriaService datosEnMemoriaService;
  
    // Este método se ejecuta cuando el cliente intenta conectarse a los sockets
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception 
    { 
        super.afterConnectionEstablished(session); 
        logger.info(session.getId() + "  conectado al socket");
        webSocketSessions.add(session); 
        lastMessageTimes.put(session, null);
        simulatedTimes.put(session, null);
    } 
  
    // Cuando el cliente se desconecta del WebSocket, se llama a este método
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,@NonNull CloseStatus status)throws Exception 
    { 
        super.afterConnectionClosed(session, status); ; 
        logger.info(session.getId() + "  desconectado del socket");
        // Removing the connection info from the list 
        lastMessageTimes.remove(session);
        simulatedTimes.remove(session);
        webSocketSessions.remove(session); 
        // executorService.shutdown();
    } 
  
    // Se encargará de intercambiar mensajes en la red
    // Tendrá información de la sesión que está enviando el mensaje
    // También el objeto de mensaje pasa como parámetro
    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception 
    { 
        super.handleMessage(session, message); 
        // logger.info("Mensaje recibido: " + message.getPayload().toString());
        // Si el mensaje contiene "tiempo", se imprimirá en el log
        if (message.getPayload().toString().contains("tiempo")) 
        { 
            logger.info("Mensaje recibido: " + message.getPayload().toString()); 

            // System.out.println("mensaje recibido: " + message.getPayload().toString());
            // Parse the simulated time from the message
            String tiempo = message.getPayload().toString().split(": ")[1]; // assuming the message is in the format "tiempo: <time>"
            ZonedDateTime simulatedTime = ZonedDateTime.parse(tiempo);
            simulatedTime= simulatedTime.withZoneSameLocal(ZoneId.of("GMT-5"));
            // simulatedTimes.put(session, simulatedTime);
            ArrayList<Vuelo> diferenciaVuelos;
            ZonedDateTime lastMessageTime = lastMessageTimes.get(session);
            // If this is the first received time, store it
            if (lastMessageTime == null) {
                lastMessageTime = simulatedTime;
                vuelosEnElAire.put(session, datosEnMemoriaService.getVuelosEnElAireMap(simulatedTime));
                lastMessageTimes.put(session, lastMessageTime);
                diferenciaVuelos = new ArrayList<>();
                for (Vuelo vuelo :  vuelosEnElAire.get(session).values()) {
                    diferenciaVuelos.add(vuelo);
                }
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("metadata", "dataVuelos");
                messageMap.put("data", diferenciaVuelos);
                String messageJson = objectMapper.writeValueAsString(messageMap);
                session.sendMessage(new TextMessage(messageJson));
                logger.info("Enviando # de vuelos en el aire: inicio" + diferenciaVuelos.size());
                return;
            }
            
            // System.out.println("Last message time: " + lastMessageTime+  " zona horaria: "+lastMessageTime.getZone());
            // System.out.println("Simulated time: " + simulatedTime + " zona horaria: "+simulatedTime.getZone());
            long difference = Duration.between(lastMessageTime, simulatedTime).toMinutes();
            // System.out.println("Difference: " + difference);
            try {
                if (difference > 15) {
                    // session.sendMessage(new TextMessage("15 minute has passed since the last message"));
                    // logger.info("15 minute has passed since the last message");
                    HashMap<Integer, Vuelo> nuevosVuelosMap = datosEnMemoriaService.getVuelosEnElAireMap(simulatedTime);
                    //Vuelos nuevos que se han agregado
                    diferenciaVuelos = new ArrayList<>();
                    //Determinar los vuelos nuevos
                    for (Vuelo vuelo :  nuevosVuelosMap.values()) {
                        if(!vuelosEnElAire.get(session).containsKey(vuelo.getId()))
                        {
                            diferenciaVuelos.add(vuelo);
                        }
                    }
                    vuelosEnElAire.put(session, nuevosVuelosMap);
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("metadata", "dataVuelos");
                    messageMap.put("data", diferenciaVuelos);
                    String messageJson = objectMapper.writeValueAsString(messageMap);
                    session.sendMessage(new TextMessage(messageJson));
                    logger.info("Enviando # de vuelos en el aire: " + diferenciaVuelos.size());
                    lastMessageTimes.put(session, simulatedTime);
                }
            } catch (Exception e) {
                logger.error("Error: " + e.getLocalizedMessage());
            }
            
        }
    } 
}
