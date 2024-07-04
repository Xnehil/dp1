package com.dp1.backend.services;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.ProgramacionVuelo;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.models.ColeccionRuta;
import com.dp1.backend.utils.ACO;
import com.dp1.backend.utils.Auxiliares;
import com.dp1.backend.utils.FuncionesLectura;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class ACOService {
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final Logger logger = LogManager.getLogger(ACOService.class);
    private ArrayList<Paquete> paquetes = new ArrayList<Paquete>();

    @Autowired
    private ACO aco;
    @Autowired
    private DatosEnMemoriaService datosEnMemoriaService;
    @Autowired
    private EnvioService envioService;
    @Autowired
    private PaqueteService paqueteService;

    public String ejecutarAco(ZonedDateTime horaActual) {
        System.out.println("SIMULACIÓN SIGUIENTE START");
        System.out.println("Hora actual: " + horaActual);
        paquetes.clear();

        HashMap<String, Aeropuerto> aeropuertos = datosEnMemoriaService.getAeropuertos();
        HashMap<Integer, Vuelo> vuelos = datosEnMemoriaService.getVuelos();
        logger.info("Desde - hasta: " + horaActual.minusHours(3) + " - " + horaActual);
        HashMap<String, Envio> envios = datosEnMemoriaService.devolverEnviosDesdeHasta(horaActual.minusHours(3),
                horaActual);
        for (Envio e : envios.values()) {
            paquetes.addAll(e.getPaquetes());
        }
        // Imprimir datos
        logger.info("Ejecutando ACO para: ");
        logger.info("Aeropuertos: " + aeropuertos.size());
        logger.info("Vuelos: " + vuelos.size());
        logger.info("Envios: " + envios.size());
        logger.info("Paquetes: " + paquetes.size());

        try {
            // Medit tiempo de ejecución
            Long startTime = System.currentTimeMillis();
            paquetes = aco.run_v2(aeropuertos, vuelos, envios, paquetes, 20);
            Long endTime = System.currentTimeMillis();
            Long totalTime = endTime - startTime;
            logger.info("Tiempo de ejecución: " + totalTime + " ms");
            int rutasAntes = datosEnMemoriaService.getRutasPosiblesSet().size();
            int paquetesEntregados = Auxiliares.verificacionTotalPaquetes(aeropuertos, vuelos, envios, paquetes,
                    datosEnMemoriaService);
            int rutasDespues = datosEnMemoriaService.getRutasPosiblesSet().size();
            // logger.info("Rutas antes: " + rutasAntes);
            // logger.info("Rutas después: " + rutasDespues);
            logger.info("Paquetes entregados con función André: " + paquetesEntregados);

        } catch (Exception e) {
            logger.error("Error en ejecutarAco: " + e.getLocalizedMessage());
            return null;
        }
        // Enviar data en formato JSON (String)
        try {
            // ArrayList<Vuelo> auxVuelos = new ArrayList<>();
            // for(Vuelo v: vuelos.values())
            // auxVuelos.add(v);
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("metadata", "correrAlgoritmo");
            messageMap.put("data", envios);
            String paquetesRutasJSON = objectMapper.writeValueAsString(messageMap);
            System.out.println("SIMULACIÓN SIGUIENTE FIN");
            return paquetesRutasJSON;
        } catch (Exception e) {
            logger.error("Error en enviar los vuelos de prueba en formato JSON: " + e.getMessage());
            return null;
        }

    }

    public String ejecutarAcoInicial(ZonedDateTime horaInicio, ZonedDateTime horaFin) {
        System.out.println("SIMULACIÓN INICIAL START");
        System.out.println("Hora de inicio: " + horaInicio);
        System.out.println("Hora de fin: " + horaFin);
        paquetes.clear();

        HashMap<String, Aeropuerto> aeropuertos = datosEnMemoriaService.getAeropuertos();
        HashMap<Integer, Vuelo> vuelos = datosEnMemoriaService.getVuelos();
        HashMap<String, Envio> envios = datosEnMemoriaService.devolverEnviosDesdeHasta(horaInicio, horaFin);
        for (Envio e : envios.values()) {
            paquetes.addAll(e.getPaquetes());
        }
        // Imprimir datos
        logger.info("Ejecutando ACO para: ");
        logger.info("Aeropuertos: " + aeropuertos.size());
        logger.info("Vuelos: " + vuelos.size());
        logger.info("Envios: " + envios.size());
        logger.info("Paquetes: " + paquetes.size());

        try {
            // Medit tiempo de ejecución
            Long startTime = System.currentTimeMillis();
            paquetes = aco.run_v2(aeropuertos, vuelos, envios, paquetes, 20);
            Long endTime = System.currentTimeMillis();
            Long totalTime = endTime - startTime;
            logger.info("Tiempo de ejecución: " + totalTime + " ms");
            int rutasAntes = datosEnMemoriaService.getRutasPosiblesSet().size();
            int paquetesEntregados = Auxiliares.verificacionTotalPaquetes(aeropuertos, vuelos, envios, paquetes,
                    datosEnMemoriaService);
            int rutasDespues = datosEnMemoriaService.getRutasPosiblesSet().size();
            // logger.info("Rutas antes: " + rutasAntes);
            // logger.info("Rutas después: " + rutasDespues);
            logger.info("Paquetes entregados con función André: " + paquetesEntregados);

        } catch (Exception e) {
            logger.error("Error en ejecutarAco: " + e.getLocalizedMessage());
            return null;
        }
        // Enviar data en formato JSON (String)
        try {
            // ArrayList<Vuelo> auxVuelos = new ArrayList<>();
            // for(Vuelo v: vuelos.values())
            // auxVuelos.add(v);
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("metadata", "primeraCarga");
            messageMap.put("data", envios);
            String paquetesRutasJSON = objectMapper.writeValueAsString(messageMap);
            System.out.println("SIMULACIÓN INICIAL FIN");
            return paquetesRutasJSON;
        } catch (Exception e) {
            logger.error("Error en enviar los vuelos de prueba en formato JSON: " + e.getMessage());
            return null;
        }

    }

    public String ejecutarAcoAntiguo() {
        paquetes.clear();

        HashMap<String, Aeropuerto> aeropuertos = datosEnMemoriaService.getAeropuertos();
        HashMap<Integer, Vuelo> vuelos = datosEnMemoriaService.getVuelos();
        HashMap<String, Envio> envios = new HashMap<String, Envio>();
        cargarDatos(aeropuertos, envios, paquetes,
                // new String[] { "SKBO", "SEQM", "SVMI", "SBBR", "SPIM", "SLLP", "SCEL",
                // "SABE", "SGAS", "SUAA", "LATI", "EDDI", "LOWW", "EBCI", "UMMS", "LBSF",
                // "LKPR", "LDZA", "EKCH", "EHAM", "VIDP", "OSDI", "OERK", "OMDB", "OAKB",
                // "OOMS", "OYSN", "OPKC", "UBBB", "OJAI" });
                new String[] { "SKBO" });
        // for (Envio e : envios.values()) {
        // paquetes.addAll(e.getPaquetes());
        // }
        // Imprimir datos
        logger.info("Ejecutando ACO para: ");
        logger.info("Aeropuertos: " + aeropuertos.size());
        logger.info("Vuelos: " + vuelos.size());
        logger.info("Envios: " + envios.size());
        logger.info("Paquetes: " + paquetes.size());

        try {
            // Medit tiempo de ejecución
            Long startTime = System.currentTimeMillis();
            paquetes = aco.run_v2(aeropuertos, vuelos, envios, paquetes, 20);
            System.out.println("Numero de paquetes: " + paquetes.size());
            Long endTime = System.currentTimeMillis();
            Long totalTime = endTime - startTime;
            logger.info("Tiempo de ejecución: " + totalTime + " ms");
            int rutasAntes = datosEnMemoriaService.getRutasPosiblesSet().size();
            int paquetesEntregados = Auxiliares.verificacionTotalPaquetes(aeropuertos, vuelos, envios, paquetes,
                    datosEnMemoriaService);
            int rutasDespues = datosEnMemoriaService.getRutasPosiblesSet().size();
            // logger.info("Rutas antes: " + rutasAntes);
            // logger.info("Rutas después: " + rutasDespues);
            logger.info("Paquetes entregados con función André: " + paquetesEntregados);

        } catch (Exception e) {
            logger.error("Error en ejecutarAco: " + e.getLocalizedMessage());
            return null;
        }
        // Guardando en la base de datos los paquetes planificados

        for (Paquete p : paquetes) {
            System.out.println(p.getRutaPosible().getId());
            System.out.println("Funcion verificar ruta. rp inf: " + p.getRutaPosible().getId() + " "
                    + p.getRutaPosible().getFlights());
        }

        for (Envio e : envios.values()) {
            try {
                e.getEmisor().setId(23);
                e.setEmisorID(23);

                e.getReceptor().setId(23);
                e.setReceptorID(23);

                envioService.updateEnvio(e);
            } catch (Exception ex) {
                // Manejo de la excepción: puedes imprimir un mensaje de error, registrar la
                // excepción, o realizar alguna acción específica según tu necesidad.
                System.err.println("Error al actualizar envío: " + ex.getMessage());
                ex.printStackTrace(); // Esto imprime la traza completa del error
                // Puedes decidir si quieres continuar con el siguiente envío o detener el
                // proceso aquí.
            }
        }

        // Enviar data en formato JSON (String)
        try {
            // ArrayList<Vuelo> auxVuelos = new ArrayList<>();
            // for(Vuelo v: vuelos.values())
            // auxVuelos.add(v);
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("metadata", "correrAlgoritmo");
            messageMap.put("data", envios);
            String paquetesRutasJSON = objectMapper.writeValueAsString(messageMap);

            return paquetesRutasJSON;
        } catch (Exception e) {
            logger.error("Error en enviar los vuelos de prueba en formato JSON: " + e.getMessage());
            return null;
        }

    }

    public boolean guardarRutas() {
        HashMap<String, ColeccionRuta> rutas = new HashMap<String, ColeccionRuta>();
        try {
            // To do fátima
        } catch (Exception e) {
            logger.error("Error en guardarRutas: " + e.getMessage());
            return false;
        }
        return true;
    }

    public String ejecutarAcoAntiguo(String codigo) {
        paquetes.clear();

        HashMap<String, Aeropuerto> aeropuertos = datosEnMemoriaService.getAeropuertos();
        HashMap<Integer, Vuelo> vuelos = datosEnMemoriaService.getVuelos();
        HashMap<String, Envio> envios = new HashMap<String, Envio>();
        String[] ciudades = new String[] { codigo };
        cargarDatos(aeropuertos, envios, paquetes, ciudades);
        for (Envio e : envios.values()) {
            paquetes.addAll(e.getPaquetes());
        }
        // Imprimir datos
        logger.info("Ejecutando ACO para: ");
        logger.info("Aeropuertos: " + aeropuertos.size());
        logger.info("Vuelos: " + vuelos.size());
        logger.info("Envios: " + envios.size());
        logger.info("Paquetes: " + paquetes.size());

        try {
            // Medit tiempo de ejecución
            Long startTime = System.currentTimeMillis();
            paquetes = aco.run_v2(aeropuertos, vuelos, envios, paquetes, 20);
            Long endTime = System.currentTimeMillis();
            Long totalTime = endTime - startTime;
            logger.info("Tiempo de ejecución: " + totalTime + " ms");
            int rutasAntes = datosEnMemoriaService.getRutasPosiblesSet().size();
            int paquetesEntregados = Auxiliares.verificacionTotalPaquetes(aeropuertos, vuelos, envios, paquetes,
                    datosEnMemoriaService);
            int rutasDespues = datosEnMemoriaService.getRutasPosiblesSet().size();
            // logger.info("Rutas antes: " + rutasAntes);
            // logger.info("Rutas después: " + rutasDespues);
            logger.info("Paquetes entregados con función André: " + paquetesEntregados);

        } catch (Exception e) {
            logger.error("Error en ejecutarAco: " + e.getLocalizedMessage());
            return null;
        }
        // Enviar data en formato JSON (String)
        try {
            // ArrayList<Vuelo> auxVuelos = new ArrayList<>();
            // for(Vuelo v: vuelos.values())
            // auxVuelos.add(v);
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("metadata", "correrAlgoritmo");
            messageMap.put("data", envios);
            String paquetesRutasJSON = objectMapper.writeValueAsString(messageMap);

            return paquetesRutasJSON;
        } catch (Exception e) {
            logger.error("Error en enviar los vuelos de prueba en formato JSON: " + e.getMessage());
            return null;
        }

    }

    public String ejecutarAcoTodo() {
        paquetes.clear();

        HashMap<String, Aeropuerto> aeropuertos = datosEnMemoriaService.getAeropuertos();
        HashMap<Integer, Vuelo> vuelos = datosEnMemoriaService.getVuelos();
        HashMap<String, Envio> envios = new HashMap<String, Envio>();

        HashMap<Integer, Double[]> tabla = datosEnMemoriaService.getTabla();
        HashMap<Integer, ProgramacionVuelo> vuelosProgramados = datosEnMemoriaService.getVuelosProgramados();
        ArrayList<LocalDate> fechasVuelos = datosEnMemoriaService.getFechasVuelos();
        String[] ciudades = new String[] {
                "SKBO", "SEQM", "SVMI", "SBBR", "SPIM", "SLLP", "SCEL", "SABE", "SGAS", "SUAA", "LATI", "EDDI", "LOWW",
                "EBCI", "UMMS", "LBSF", "LKPR", "LDZA", "EKCH", "EHAM", "VIDP", "OSDI", "OERK", "OMDB", "OAKB", "OOMS",
                "OYSN", "OPKC", "UBBB", "OJAI"
        };

        cargarDatosDesdeBD(aeropuertos, envios, paquetes, ciudades);
        for (Envio e : envios.values()) {
            paquetes.addAll(e.getPaquetes());
        }
        // Imprimir datos
        logger.info("Ejecutando ACO para: ");
        logger.info("Aeropuertos: " + aeropuertos.size());
        logger.info("Vuelos: " + vuelos.size());
        logger.info("Envios: " + envios.size());
        logger.info("Paquetes: " + paquetes.size());

        try {
            // Medit tiempo de ejecución
            Long startTime = System.currentTimeMillis();
            paquetes = aco.run_v3(aeropuertos, vuelos, envios, paquetes, 20, tabla, vuelosProgramados, fechasVuelos);
            Long endTime = System.currentTimeMillis();
            Long totalTime = endTime - startTime;
            logger.info("Tiempo de ejecución: " + totalTime + " ms");
            int rutasAntes = datosEnMemoriaService.getRutasPosiblesSet().size();
            int paquetesEntregados = Auxiliares.verificacionTotalPaquetes(aeropuertos, vuelos, envios, paquetes,
                    datosEnMemoriaService);
            int rutasDespues = datosEnMemoriaService.getRutasPosiblesSet().size();
            // logger.info("Rutas antes: " + rutasAntes);
            // logger.info("Rutas después: " + rutasDespues);
            logger.info("Paquetes entregados con función André: " + paquetesEntregados);

        } catch (Exception e) {
            logger.error("Error en ejecutarAco: " + e.getLocalizedMessage());
            return null;
        }
        // Enviar data en formato JSON (String)
        try {
            // ArrayList<Vuelo> auxVuelos = new ArrayList<>();
            // for(Vuelo v: vuelos.values())
            // auxVuelos.add(v);
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("metadata", "correrAlgoritmo");
            messageMap.put("data", envios);
            String paquetesRutasJSON = objectMapper.writeValueAsString(messageMap);

            return paquetesRutasJSON;
        } catch (Exception e) {
            logger.error("Error en enviar los vuelos de prueba en formato JSON: " + e.getMessage());
            return null;
        }

    }

    private void cargarDatos(HashMap<String, Aeropuerto> aeropuertos, HashMap<String, Envio> envios,
            ArrayList<Paquete> paquetes,
            String[] ciudades) {
        // Ahora mismo está leyendo datos de archivos, pero debería leer de la base de
        // datos
        String workingDirectory = System.getProperty("user.dir");
        if (workingDirectory.trim().equals("/")) {
            workingDirectory = "/home/inf226.982.2b/";
        } else {
            workingDirectory = "";
        }
        String rutaArchivos = "data/pack_enviado_";
        for (int i = 0; i < ciudades.length; i++) {
            envios.putAll(FuncionesLectura.leerEnvios(rutaArchivos + ciudades[i] + ".txt", aeropuertos, 100));
        }

        for (Envio e : envios.values()) {
            paquetes.addAll(e.getPaquetes());
        }
    }

    private void cargarDatosDesdeBD(HashMap<String, Aeropuerto> aeropuertos, HashMap<String, Envio> envios,
            ArrayList<Paquete> paquetes,
            String[] ciudades) {
        ArrayList<Envio> enviosDesdeBD = envioService.getEnvios();
        for (int i = 0; i < enviosDesdeBD.size(); i++)
            envios.put(enviosDesdeBD.get(i).getCodigoEnvio(), enviosDesdeBD.get(i));

        paquetes = paqueteService.getPaquetes();
    }

    private void cargarDatosV2(HashMap<String, Aeropuerto> aeropuertos, HashMap<Integer, Vuelo> vuelos,
            HashMap<String, Envio> envios, ArrayList<Paquete> paquetes,
            ZonedDateTime horaActual) {
        // Ahora mismo está leyendo datos de archivos, pero debería leer de la base de
        // datos
        String workingDirectory = System.getProperty("user.dir");
        if (workingDirectory.trim().equals("/")) {
            workingDirectory = "/home/inf226.982.2b/";
        } else {
            workingDirectory = "";
        }
        aeropuertos
                .putAll(FuncionesLectura.leerAeropuertos(workingDirectory + "data/Aeropuerto.husos.v3.20240619.txt"));
        vuelos.putAll(FuncionesLectura.leerVuelos(workingDirectory + "data/planes_vuelo.v4.20240619.txt", aeropuertos));
        String rutaArchivos = "data/pack_enviado_";
        String[] ciudades = { "SKBO", "SEQM", "SUAA", "SCEL", "SABE", "EBCI", "EHAM", "WMKK", "VIDP", "ZBAA" };
        for (int i = 0; i < ciudades.length; i++) {
            envios.putAll(FuncionesLectura.leerEnvios(rutaArchivos + ciudades[i] + ".txt", aeropuertos, 20));
        }

        HashMap<String, Envio> enviosActual = new HashMap<>();

        for (Map.Entry<String, Envio> entry : envios.entrySet()) {
            Envio envio = entry.getValue();
            // Verificar si la hora del envío es posterior a la hora actual
            if (envio.getFechaHoraLlegadaPrevista().isAfter(horaActual)) {
                // Agregar al nuevo HashMap
                enviosActual.put(entry.getKey(), envio);
            }
        }

        for (Envio e : enviosActual.values()) {
            paquetes.addAll(e.getPaquetes());
        }
    }

}
