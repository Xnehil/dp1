package com.dp1.backend;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.services.AeropuertoService;
import com.dp1.backend.services.VueloService;
import com.dp1.backend.utils.ACO;
import com.dp1.backend.utils.Auxiliares;
import com.dp1.backend.utils.FuncionesLectura;


@SpringBootApplication
@EnableCaching
public class BackendApplication {

    public static void main(String[] args) {
        // SpringApplication.run(BackendApplication.class, args);

        HashMap<String, Aeropuerto> aeropuertos = FuncionesLectura.leerAeropuertos("data/Aeropuerto.husos.v2.txt");
        HashMap<Integer, Vuelo> vuelos = FuncionesLectura.leerVuelos("data/planes_vuelo.v3.txt", aeropuertos);
        String rutaArchivos = "data/pack_enviado_";
        String[] ciudades = { "SKBO", "SEQM", "SUAA", "SCEL", "SABE", "EBCI", "EHAM", "WMKK", "VIDP", "ZBAA"};
        HashMap<String, Envio> envios = new HashMap<String, Envio>();
        for (int i = 0; i < ciudades.length; i++) {
            envios.putAll(FuncionesLectura.leerEnvios(rutaArchivos + ciudades[i] + ".txt", aeropuertos,20));
        }

        ArrayList<Paquete> paquetes = new ArrayList<Paquete>();
        for (Envio e : envios.values()) {
            paquetes.addAll(e.getPaquetes());
        }
        System.out.println("Envios: " + envios.size());
        System.out.println("Paquetes: " + paquetes.size());
        int numPaqEntregados = ACO.run_v2(aeropuertos, vuelos, envios, paquetes, 20);

        int numPaqNoEntregados = 0;
        numPaqEntregados=Auxiliares.verificacionTotalPaquetes(aeropuertos, vuelos, envios, paquetes);
        System.out.println("Paquetes entregados: " + numPaqEntregados);
        System.out.println("Paquetes no entregados: " + (paquetes.size()-numPaqEntregados));
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST","PUT", "DELETE");
            }
        };
    }
    
}
