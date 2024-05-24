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
    private ZonedDateTime lastMessageTime = null;
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    //En esta lista se almacenarán todas las conexiones. Luego se usará para transmitir el mensaje
    private List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>()); 

    //Por cada conexión, se guarda una lista paralela de vuelos en el aire
    private Map<WebSocketSession, ArrayList<Vuelo>> vuelosEnElAire = new HashMap<>();

    @Autowired
    private DatosEnMemoriaService datosEnMemoriaService;
  
    // Este método se ejecuta cuando el cliente intenta conectarse a los sockets
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception 
    { 
        super.afterConnectionEstablished(session); 
        logger.info(session.getId() + "  conectado al socket");
        webSocketSessions.add(session); 
    } 
  
    // Cuando el cliente se desconecta del WebSocket, se llama a este método
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,@NonNull CloseStatus status)throws Exception 
    { 
        super.afterConnectionClosed(session, status); ; 
        logger.info(session.getId() + "  desconectado del socket");
        // Removing the connection info from the list 
        webSocketSessions.remove(session); 
        executorService.shutdown();
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

            // System.out.println("mensaje recibido: " + message.getPayload().toString());
            // Parse the simulated time from the message
            String tiempo = message.getPayload().toString().split(": ")[1]; // assuming the message is in the format "tiempo: <time>"
            ZonedDateTime simulatedTime = ZonedDateTime.parse(tiempo);
            simulatedTime= simulatedTime.withZoneSameLocal(ZoneId.of("GMT-5"));

            // If this is the first received time, store it
            if (lastMessageTime == null) {
                lastMessageTime = simulatedTime;
                vuelosEnElAire.put(session, datosEnMemoriaService.getVuelosEnElAire(simulatedTime));
            }
            
            // System.out.println("Last message time: " + lastMessageTime+  " zona horaria: "+lastMessageTime.getZone());
            // System.out.println("Simulated time: " + simulatedTime + " zona horaria: "+simulatedTime.getZone());
            long difference = Duration.between(lastMessageTime, simulatedTime).toMinutes();
            // System.out.println("Difference: " + difference);
            if (difference > 15) {
                // session.sendMessage(new TextMessage("1 minute has passed since the last message"));
                // logger.info("1 minute has passed since the last message");
                ArrayList<Vuelo> nuevoFetchVuelos = datosEnMemoriaService.getVuelosEnElAire(simulatedTime);
                //Vuelos nuevos que se han agregado
                ArrayList<Vuelo> diferenciaVuelos = new ArrayList<>();
                //Determinar los vuelos nuevos
                for (Vuelo vuelo : nuevoFetchVuelos) {
                    if (!vuelosEnElAire.get(session).contains(vuelo)) {
                        diferenciaVuelos.add(vuelo);
                    }
                }
                vuelosEnElAire.put(session, nuevoFetchVuelos);
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("metadata", "dataVuelos");
                messageMap.put("data", diferenciaVuelos);
                String messageJson = objectMapper.writeValueAsString(messageMap);
                session.sendMessage(new TextMessage(messageJson));

                lastMessageTime = simulatedTime;
            }
        }
    } 
}
