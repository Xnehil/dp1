package com.dp1.backend.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.ColeccionRuta;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.ItemRutaPosible;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.ProgramacionVuelo;
import com.dp1.backend.models.RutaPosible;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.utils.FuncionesLectura;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class DatosEnMemoriaService {
    private HashMap<String, Aeropuerto> aeropuertos = new HashMap<>();
    private HashMap<Integer, Vuelo> vuelos = new HashMap<>();
    private HashMap<String, Envio> envios = new HashMap<>();

    // Mapas para rutas
    private HashMap<String, ColeccionRuta> rutasPosibles = new HashMap<>();
    private HashSet<String> rutasPosiblesSet = new HashSet<>();

    // Services para ColeccionRuta y RutaPosible
    // Estructuras que se usarán en la planificación (ejecución del algoritmo)
    //HashMap<Integer, Double[]>tabla, HashMap<Integer, ProgramacionVuelo> vuelosProgramados, ArrayList<LocalDate>fechasVuelos)
    private HashMap<Integer, Double[]> tabla = new HashMap<>();
    private HashMap<Integer, ProgramacionVuelo> vuelosProgramados = new HashMap<>();
    private ArrayList<LocalDate>fechasVuelos = new ArrayList<>();


    //Services para ColeccionRuta y RutaPosible
    @Autowired
    private ColeccionRutaService coleccionRutaService;

    @Autowired
    private RutaPosibleService rutaPosibleService;

    private final static Logger logger = LogManager.getLogger(DatosEnMemoriaService.class);
    private String workingDirectory = System.getProperty("user.dir");

    public DatosEnMemoriaService() {
        logger.info("Inicializando DatosEnMemoriaService con working directory: " + workingDirectory);
        if (workingDirectory.trim().equals("/")) {
            workingDirectory = "/home/inf226.982.2b/";
        } else {
            workingDirectory = "";
        }
        try{
            aeropuertos.putAll(FuncionesLectura.leerAeropuertos(workingDirectory + "data/Aeropuerto.husos.v3.20240619.txt"));
            logger.info("Aeropuertos cargados: " + aeropuertos.size());
            vuelos.putAll(FuncionesLectura.leerVuelos(workingDirectory + "data/planes_vuelo.v4.20240619.txt", aeropuertos));
            logger.info("Vuelos cargados: " + vuelos.size());
        }
        catch (Exception e){
            logger.error("Error al cargar aeropuertos y vuelos: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @PostConstruct
    @Transactional
    public void init() {
        logger.info("Leyendo rutas posibles");

        try {
            coleccionRutaService.getAllColeccionRutas().forEach(cr -> {
                // logger.info("Coleccion ruta: " + cr.getCodigoRuta());
                rutasPosibles.put(cr.getCodigoRuta(), cr);
                String ruta = cr.getCodigoRuta();
                for (RutaPosible rp : cr.getRutasPosibles()) {

                    String sucesionVuelos = "";
                    for (ItemRutaPosible itemVuelo : rp.getFlights()) {
                        int vueloId = itemVuelo.getIdVuelo();
                        sucesionVuelos += ("-" + vueloId);
                    }
                    // logger.info("Ruta posible: " + sucesionVuelos);
                    ruta += sucesionVuelos;
                    if (!rutasPosiblesSet.contains(ruta)) {
                        rutasPosiblesSet.add(ruta);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Error al leer rutas posibles: " + e.getLocalizedMessage());
        }

        logger.info("Colecciones rutas: " + rutasPosibles.size());
        logger.info("Rutas posibles set: " + rutasPosiblesSet.size());
    }

    public HashMap<Integer, Double[]> getTabla() {
        return this.tabla;
    }
    public void setTabla(HashMap<Integer, Double[]> tabla) {
        this.tabla = tabla;
    }
    public HashMap<Integer, ProgramacionVuelo> getVuelosProgramados() {
        return this.vuelosProgramados;
    }
    public void setVuelosProgramados(HashMap<Integer, ProgramacionVuelo> vuelosProgramados) {
        this.vuelosProgramados = vuelosProgramados;
    }
    public ArrayList<LocalDate> getFechasVuelos() {
        return this.fechasVuelos;
    }
    public void setFechasVuelos(ArrayList<LocalDate> fechasVuelos) {
        this.fechasVuelos = fechasVuelos;
    }
    public HashSet<String> getRutasPosiblesSet() {
        return this.rutasPosiblesSet;
    }

    public void setRutasPosiblesSet(HashSet<String> rutasPosiblesSet) {
        this.rutasPosiblesSet = rutasPosiblesSet;
    }

    public String getWorkingDirectory() {
        return this.workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public ColeccionRutaService getColeccionRutaService() {
        return this.coleccionRutaService;
    }

    public void setColeccionRutaService(ColeccionRutaService coleccionRutaService) {
        this.coleccionRutaService = coleccionRutaService;
    }

    public HashMap<String, Aeropuerto> getAeropuertos() {
        return this.aeropuertos;
    }

    public void setAeropuertos(HashMap<String, Aeropuerto> aeropuertosVuelosMap) {
        this.aeropuertos = aeropuertosVuelosMap;
    }

    public HashMap<Integer, Vuelo> getVuelos() {
        return this.vuelos;
    }

    public void setVuelos(HashMap<Integer, Vuelo> vuelosMap) {
        this.vuelos = vuelosMap;
    }

    public ArrayList<Vuelo> getVuelosEnElAire(ZonedDateTime horaActual) {
        try {
            // Keep only flights that have taken off and have not landed
            ArrayList<Vuelo> vuelosEnElAire = new ArrayList<Vuelo>();
            for (Vuelo vuelo : vuelos.values()) {
                ZonedDateTime horaDespegue = vuelo.getFechaHoraSalida();
                ZonedDateTime horaAterrizaje = vuelo.getFechaHoraLlegada();

                horaDespegue = horaDespegue.with(horaActual.toLocalDate());
                horaAterrizaje = horaAterrizaje.with(horaActual.toLocalDate());
                horaAterrizaje = horaAterrizaje.plusDays(vuelo.getCambioDeDia());
                if (horaActual.isAfter(horaDespegue) && horaActual.isBefore(horaAterrizaje)) {
                    vuelosEnElAire.add(vuelo);
                }
            }
            // logger.info("Vuelos en el aire: " + vuelosEnElAire.size());
            return vuelosEnElAire;
        } catch (Exception e) {
            System.out.println("Error: " + e.getLocalizedMessage());
            return null;
        }
    }

    public HashMap<Integer, Vuelo> getVuelosEnElAireMap(ZonedDateTime horaActual) {
        try {
            // Keep only flights that have taken off and have not landed
            HashMap<Integer, Vuelo> vuelosEnElAire = new HashMap<Integer, Vuelo>();
            for (Vuelo vuelo : vuelos.values()) {
                ZonedDateTime horaDespegue = vuelo.getFechaHoraSalida();
                ZonedDateTime horaAterrizaje = vuelo.getFechaHoraLlegada();
                if (vuelo.getIdVuelo()==128){
                    logger.debug("ola");
                }
                horaDespegue = horaDespegue.with(horaActual.toLocalDate());
                horaAterrizaje = horaAterrizaje.with(horaActual.toLocalDate());
                if (vuelo.getCambioDeDia() != 0){
                    // Si la hora despegue es menor a la hora actual, estamos en caso izquierda
                    if (horaActual.isAfter(horaDespegue) && horaActual.isAfter(horaAterrizaje)) {
                        horaAterrizaje = horaAterrizaje.plusDays(vuelo.getCambioDeDia());
                    } else if (horaActual.isBefore(horaDespegue) && horaActual.isBefore(horaAterrizaje)) {
                        horaDespegue = horaDespegue.minusDays(vuelo.getCambioDeDia());
                    }
                }

                if ((horaActual.isAfter(horaDespegue) && horaActual.isBefore(horaAterrizaje))
                    || (horaActual.minusDays(1).isAfter(horaDespegue) && horaActual.minusDays(1).isBefore(horaAterrizaje))){
                    // logger.info("Vuelo en el aire: " + vuelo.getId());
                    // logger.info("porque hora actual: y hora despegue: y hora aterrizaje");
                    // //Print in utc
                    // logger.info(horaActual.withZoneSameInstant(ZoneId.of("UTC")));
                    // logger.info(horaDespegue.withZoneSameInstant(ZoneId.of("UTC")));
                    // logger.info(horaAterrizaje.withZoneSameInstant(ZoneId.of("UTC")));
                    vuelosEnElAire.put(vuelo.getId(), vuelo);
                }
            }
            // logger.info("Vuelos en el aire: " + vuelosEnElAire.size());
            return vuelosEnElAire;
        } catch (Exception e) {
            System.out.println("Error: " + e.getLocalizedMessage());
            return null;
        }
    }

    public HashMap<String, ColeccionRuta> getRutasPosibles() {
        return this.rutasPosibles;
    }

    public void setRutasPosibles(HashMap<String, ColeccionRuta> rutasPosibles) {
        this.rutasPosibles = rutasPosibles;
    }

    public boolean seTieneruta(String ruta) {
        return rutasPosiblesSet.contains(ruta);
    }

    public void insertarRuta(Envio envio, Paquete paquete) {
        String llave = envio.getOrigen() + envio.getDestino();
        ColeccionRuta cr = rutasPosibles.get(llave);
        if (cr == null) {
            cr = new ColeccionRuta();
            cr.setCodigoRuta(llave);
            cr.setRutasPosibles(new ArrayList<RutaPosible>());
            RutaPosible rp = new RutaPosible();
            rp.setColeccionRuta(cr);

            rp.setFlights(cargarVuelosARutaPosible(paquete));
            cr.getRutasPosibles().add(rp);
            rutasPosibles.put(llave, cr);
            // logger.info("Ruta creada: " + llave);
            // Guardar cr en bd
            coleccionRutaService.createColeccionRuta(cr);
        }
        RutaPosible rp = new RutaPosible();
        rp.setColeccionRuta(cr);
        rp.setFlights(cargarVuelosARutaPosible(paquete));
        cr.getRutasPosibles().add(rp);

        String llave2 = envio.getOrigen() + envio.getDestino();
        for (int i : paquete.getRuta()) {
            llave2 += "-" + i;
        }
        rutasPosiblesSet.add(llave2);
        // Guardar llave2 en bd
        rutaPosibleService.createRutaPosible(rp);
        // logger.info("Ruta agregada en set: " + llave2);

        //
    }

    private ArrayList<ItemRutaPosible> cargarVuelosARutaPosible(Paquete paquete) {
        ArrayList<ItemRutaPosible> items = new ArrayList<>();
        ZonedDateTime inicio = paquete.getFechasRuta().get(0);
        try {
            int index = 0;
            for (int i : paquete.getRuta()) {
                ZonedDateTime fechaVuelo = paquete.getFechasRuta().get(index);
                ItemRutaPosible irp = new ItemRutaPosible();
                irp.setIdVuelo(i);
                irp.setDiaRelativo((int) (fechaVuelo.toLocalDate().toEpochDay() - inicio.toLocalDate().toEpochDay()));
                items.add(irp);
                index++;
            }
        } catch (Exception e) {
            logger.error("Error al cargar vuelos a ruta posible: " + e.getLocalizedMessage());
        }
        return items;
    }

    public void cargarEnviosDesdeHasta(ZonedDateTime horaActual) {
        envios.clear();
        // Modifico la horaActual para que pueda tener data para el inicio de mi
        // simulación semanal, la cual tomará
        // como máximo de 3 dias antes de la fecha de simulación.
        ZonedDateTime horaActualMenos3Dias = horaActual.minusDays(3);
        ZonedDateTime horaFin = horaActual.plusDays(7);
        try (Stream<Path> paths = Files.walk(Paths.get(workingDirectory + "data/"))) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().startsWith("pack_enviado_"))
                    .forEach(p -> {
                        logger.info("Leyendo archivo: " + p.toString());
                        envios.putAll(
                                FuncionesLectura.leerEnviosDesdeHasta(p.toString(), aeropuertos, horaActualMenos3Dias,
                                        horaFin));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Envios cargados: " + envios.size());
    }

    public HashMap<String, Envio> devolverEnviosDesdeHasta(ZonedDateTime horaInicio, ZonedDateTime horaFin) {
        HashMap<String, Envio> enviosDesdeHasta = new HashMap<>();
        for (Envio envio : envios.values()) {
            if (envio.getFechaHoraSalida().isAfter(horaInicio) && envio.getFechaHoraSalida().isBefore(horaFin)) {
                enviosDesdeHasta.put(envio.getCodigoEnvio(), envio);
            }
        }
        return enviosDesdeHasta;
    }

    public HashMap<String, Envio> getEnvios() {
        return this.envios;
    }

    public void setEnvios(HashMap<String, Envio> envios) {
        this.envios = envios;
    }

    public void limpiarMemoria() {
        envios.clear();
        for (Aeropuerto a : aeropuertos.values()) {
            a.setCantPaqReal(new TreeMap<LocalDateTime, Integer>());
            a.setCantPaqParaPlanificacion(new TreeMap<LocalDateTime, Integer>());
        }
    }

}
