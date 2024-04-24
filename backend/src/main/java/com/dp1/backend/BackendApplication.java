package com.dp1.backend;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.utils.ACO;
import com.dp1.backend.utils.Auxiliares;
import com.dp1.backend.utils.FuncionesLectura;
import com.dp1.backend.utils.MPAv2;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		// SpringApplication.run(BackendApplication.class, args);
		HashMap<String, Aeropuerto> aeropuertos = FuncionesLectura.leerAeropuertos("data/Aeropuerto.husos.v2.txt");
        HashMap<Integer, Vuelo> vuelos = FuncionesLectura.leerVuelos("data/planes.vuelo.v2.txt", aeropuertos);
        HashMap<Integer, Envio> envios = FuncionesLectura.leerEnvios("data/pack_enviado/pack_enviado_SKBO.txt", aeropuertos, 200);
        // envios = FuncionesLectura.leerEnvios("algoritmos/data/pack_enviado/pack_enviado_SEQM.txt", aeropuertos);

        
		ArrayList<Paquete> paquetes = new ArrayList<Paquete>();
		for(Envio e : envios.values()){
			paquetes.addAll(e.getPaquetes());
		}

        int tamanioSolucion=4;
        //Initialize the owo
        int[] owo = new int[tamanioSolucion*paquetes.size()];
        try {
            FileWriter writer = new FileWriter("output/results_"+LocalDate.now()+".txt");
        
            writer.write("Iteración\tTiempo de ejecución (ms)\tPaquetes entregados\tPorcentaje de paquetes entregados\n");
            double promedio=0;
            int iteraciones=1;
            for (int i = 0; i < iteraciones; i++) {
                Long startTime = System.currentTimeMillis();
                owo=MPAv2.run(aeropuertos, vuelos, envios, paquetes, 200, 70, tamanioSolucion);
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;
                int paquetesEntregados=Auxiliares.verificacionTotal(owo, aeropuertos, vuelos, envios, paquetes, tamanioSolucion);
                double porcentajeEntregados = (double)(paquetesEntregados*100)/paquetes.size();
                promedio+=porcentajeEntregados;
                String resultLine = String.format("%d\t%d\t%d\t%.2f%%\n", i, executionTime, paquetesEntregados, porcentajeEntregados);
                writer.write(resultLine);
            }
            promedio/=iteraciones;
            writer.write("\nPromedio\t\t\t"+promedio+"%\n");
        
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        //Asignar rutas a los paquetes
        for (int i = 0; i < paquetes.size(); i++) {
            ArrayList<Integer> ruta = new ArrayList<Integer>();
            for (int j = i*tamanioSolucion; j < tamanioSolucion*(i+1); j++) {
                ruta.add(owo[j]);
            }
            paquetes.get(i).setRuta(ruta);
        }
        

        //Una solución
        int verPaquete=480;        
        //System.out.println("L solucion es valida: " + esSolucionValida);
        System.out.println("Funcion validacion:  ");
        Paquete auxPaquete = paquetes.get(verPaquete);
        Boolean esSolucionValida = Auxiliares.solucionValidav2(aeropuertos, vuelos, envios, auxPaquete, true);
        System.out.println("La solución es valida: " + esSolucionValida);
        
    }



        
	}


