package com.dp1.backend.handlers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Env;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.services.ACOService;
import com.dp1.backend.services.DatosEnMemoriaService;
import com.dp1.backend.services.EnvioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class SocketConnectionHandler extends TextWebSocketHandler {
    private static final Logger logger = LogManager.getLogger(SocketConnectionHandler.class);
    private HashMap<WebSocketSession, ZonedDateTime> lastMessageTimes = new HashMap<>();
    private HashMap<WebSocketSession, ZonedDateTime> lastAlgorTimes = new HashMap<>();
    private HashMap<WebSocketSession, ZonedDateTime> simulatedTimes = new HashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a", Locale.ENGLISH);
    private HashMap<WebSocketSession, Integer> tipoConexion = new HashMap<>();
    //0: no se ha conectado, 1: se ha conectado en simulacion, 2: se ha conectado en tiempo real

    // En esta lista se almacenarán todas las conexiones. Luego se usará para
    // transmitir el mensaje
    private List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());

    // Por cada conexión, se guarda una lista paralela de vuelos en el aire
    private HashMap<WebSocketSession, HashMap<Integer, Vuelo>> vuelosEnElAire = new HashMap<>();

    //Para los envíos en operaciones en tiempo real se guardar un mapa de envíos
    private HashMap<WebSocketSession, HashMap<String, Envio>> enviosEnOperacion = new HashMap<>();

    @Autowired
    private DatosEnMemoriaService datosEnMemoriaService;
    
    @Autowired
    private ACOService acoService;

    @Autowired
    private EnvioService envioService;
    
    // Este método se ejecuta cuando el cliente intenta conectarse a los sockets
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        logger.info(session.getId() + "  conectado al socket");
        webSocketSessions.add(session);
        lastMessageTimes.put(session, null);
        simulatedTimes.put(session, null);
        lastAlgorTimes.put(session, null);
    }

    // Cuando el cliente se desconecta del WebSocket, se llama a este método
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        ;
        logger.info(session.getId() + "  desconectado del socket");
        // Removing the connection info from the list
        lastMessageTimes.remove(session);
        simulatedTimes.remove(session);
        lastAlgorTimes.remove(session);
        webSocketSessions.remove(session);
        datosEnMemoriaService.limpiarMemoria();
        // executorService.shutdown();
        tipoConexion.remove(session);
    }

    // Se encargará de intercambiar mensajes en la red
    // Tendrá información de la sesión que está enviando el mensaje
    // También el objeto de mensaje pasa como parámetro
    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message)
            throws Exception {
        super.handleMessage(session, message);
        // logger.info("Mensaje recibido: " + message.getPayload().toString());
        // Si el mensaje contiene "tiempo", se imprimirá en el log
        if (message.getPayload().toString().contains("tiempo")) 
        { 
            // Assuming `message` is the incoming message
            String[] parts = message.getPayload().toString().split(":", 2); // Split the message into two parts

            String identifier = parts[0].trim(); // The identifier is the first part
            String timeMessage = parts[1].trim(); // The rest of the message is the second part
            String tiempo = timeMessage.split(": ")[1]; // assuming the message is in the format "tiempo: <time>"
            //Parsear tiempo que llega en el formato "6/2/2024, 3:57:01 PM
            ZonedDateTime simulatedTime = LocalDateTime.parse(tiempo, formatter).atZone(ZoneId.of("America/Lima"));
            ArrayList<Vuelo> diferenciaVuelos = new ArrayList<>();
            ZonedDateTime lastMessageTime = lastMessageTimes.get(session);
            ZonedDateTime algorLastTime = lastAlgorTimes.get(session);

            //Si es la primera vez que se conecta
            if (tipoConexion.get(session) == null) {
                if (identifier.equals("vuelosEnVivo")) {
                    tipoConexion.put(session, 2);
                } else if (identifier.equals("simulacionSemanal")) {
                    tipoConexion.put(session, 1);
                }
            }

            if(tipoConexion.get(session) == 1){
                if (lastMessageTime == null) {
                    handlePrimerContactoSimulacion(simulatedTime, session, lastMessageTime, algorLastTime, diferenciaVuelos);
                    return;
                }
                handleDifferenceSimulacion(lastMessageTime, simulatedTime, session, diferenciaVuelos, algorLastTime);
            }
            else if(tipoConexion.get(session) == 2){
                if (lastMessageTime == null) {
                    handlePrimerContactoReal(simulatedTime, session, lastMessageTime, diferenciaVuelos);
                    return;
                }
                handleDifferenceReal(lastMessageTime, simulatedTime, session, diferenciaVuelos);
            }
            else{
                logger.error("Tipo de conexión no reconocido");
            }
        }
    }

    private void handlePrimerContactoReal(ZonedDateTime time, WebSocketSession session, ZonedDateTime lastMessageTime, ArrayList<Vuelo> diferenciaVuelos) throws IOException {
        lastMessageTime = time;
        vuelosEnElAire.put(session, datosEnMemoriaService.getVuelosEnElAireMap(time));
        lastMessageTimes.put(session, lastMessageTime);

        logger.info("Cargando envios antes de: " + lastMessageTime);
        //Enviamos la data por primera vez. Tenemos que enviar los paquetes de los últimos dos días. Tal vez todos o solo los que faltan llegar
        enviosEnOperacion.put(session, envioService.getEnviosEntrev2(lastMessageTime.minusDays(1), lastMessageTime.plusSeconds(5)));//cargamos todos los envios de 1 día atrás

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("metadata", "primeraCarga");
        messageMap.put("data", enviosEnOperacion.get(session));
        String paquetesRutasJSON = objectMapper.writeValueAsString(messageMap);
        session.sendMessage(new TextMessage(paquetesRutasJSON));
        logger.info("Enviando # de envios en operación: inicio" + enviosEnOperacion.get(session).size());

        diferenciaVuelos = new ArrayList<>();
        for (Vuelo vuelo : vuelosEnElAire.get(session).values()) {
            diferenciaVuelos.add(vuelo);
        }
        messageMap = new HashMap<>();
        messageMap.put("metadata", "dataVuelos");
        messageMap.put("data", diferenciaVuelos);
        String messageJson = objectMapper.writeValueAsString(messageMap);
        session.sendMessage(new TextMessage(messageJson));           
        logger.info("Enviando # de vuelos en el aire: inicio" + diferenciaVuelos.size());
        return;
    }

    private void handleDifferenceReal(ZonedDateTime lastMessageTime, ZonedDateTime time, WebSocketSession session, ArrayList<Vuelo> diferenciaVuelos) throws IOException {
        long difference = Duration.between(lastMessageTime, time).toSeconds();
        try {
            if (difference > 30) {
                HashMap<Integer, Vuelo> nuevosVuelosMap = datosEnMemoriaService.getVuelosEnElAireMap(time);
                // Vuelos nuevos que se han agregado
                diferenciaVuelos = new ArrayList<>();
                // Determinar los vuelos nuevos
                for (Vuelo vuelo : nuevosVuelosMap.values()) {
                    if (!vuelosEnElAire.get(session).containsKey(vuelo.getId())) {
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
                
                
                HashMap<String, Envio> enviosNuevos = envioService.getEnviosEntrev2(lastMessageTime, time);
                messageMap = new HashMap<>();
                messageMap.put("metadata", "nuevosEnvios");
                messageMap.put("data", enviosNuevos);

                messageJson = objectMapper.writeValueAsString(messageMap);
                session.sendMessage(new TextMessage(messageJson));
                logger.info("Enviando # de envios nuevos: " + enviosNuevos.size());

                HashMap<String, Envio> enviosEnOperacion = envioService.getEnviosEntrev2(lastMessageTime.minusDays(1), lastMessageTime);
                messageMap.put("metadata", "enviosEnOperacion");
                messageMap.put("data", enviosEnOperacion);

                messageJson = objectMapper.writeValueAsString(messageMap);
                session.sendMessage(new TextMessage(messageJson));
                logger.info("Enviando # de envios en operación: " + enviosEnOperacion.size());
                lastMessageTimes.put(session, time);
            }
        } catch (Exception e) {
            logger.error("Error en diferencia de vuelos: " + e.getLocalizedMessage());
        }
    }

    private void handlePrimerContactoSimulacion(ZonedDateTime simulatedTime, WebSocketSession session, ZonedDateTime lastMessageTime, ZonedDateTime algorLastTime, ArrayList<Vuelo> diferenciaVuelos) throws IOException {
        lastMessageTime = simulatedTime;
        algorLastTime = simulatedTime;
        vuelosEnElAire.put(session, datosEnMemoriaService.getVuelosEnElAireMap(simulatedTime));
        lastMessageTimes.put(session, lastMessageTime);
        algorLastTime = simulatedTime;
        lastAlgorTimes.put(session, algorLastTime);

        //Enviamos la data por primera vez
        datosEnMemoriaService.cargarEnviosDesdeHasta(lastMessageTime);//cargamos todos los envios de la semana
        String paquetesConRutas = acoService.ejecutarAcoInicial(simulatedTime.minusDays(1),simulatedTime);
        session.sendMessage(new TextMessage(paquetesConRutas));

        diferenciaVuelos = new ArrayList<>();
        for (Vuelo vuelo : vuelosEnElAire.get(session).values()) {
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

    private void handleDifferenceSimulacion(ZonedDateTime lastMessageTime, ZonedDateTime simulatedTime, WebSocketSession session,
            ArrayList<Vuelo> diferenciaVuelos, ZonedDateTime algorLastTime
    ) throws IOException {
            
        long difference = Duration.between(lastMessageTime, simulatedTime).toMinutes();
            // System.out.println("Difference: " + difference);
            try {
                if (difference > 20) {
                    // session.sendMessage(new TextMessage("15 minute has passed since the last
                    // message"));
                    // logger.info("15 minute has passed since the last message");
                    HashMap<Integer, Vuelo> nuevosVuelosMap = datosEnMemoriaService.getVuelosEnElAireMap(simulatedTime);
                    // Vuelos nuevos que se han agregado
                    diferenciaVuelos = new ArrayList<>();
                    // Determinar los vuelos nuevos
                    for (Vuelo vuelo : nuevosVuelosMap.values()) {
                        if (!vuelosEnElAire.get(session).containsKey(vuelo.getId())) {
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
                logger.error("Error en diferencia de vuelos: " + e.getLocalizedMessage());
            }
            // System.out.println("Ejecutando sección del algoritmo");
            difference = Duration.between(algorLastTime, simulatedTime).toMinutes();
            try {
                if (difference > 180) {
                    String paquetesConRutas = acoService.ejecutarAcoSimulacion(simulatedTime);
                    session.sendMessage(new TextMessage(paquetesConRutas));
                    logger.info("Enviando resultado del algoritmo 'para los vuelos en el aire'");
                    lastAlgorTimes.put(session, simulatedTime);
                }
            } catch (Exception e) {
                logger.error("Error en ejecución del algoritmo: " + e.getLocalizedMessage());
            }
    }

}