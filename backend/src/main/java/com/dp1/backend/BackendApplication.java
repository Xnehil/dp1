package com.dp1.backend;


import java.util.ArrayList;
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

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		// SpringApplication.run(BackendApplication.class, args);
		HashMap<String, Aeropuerto> aeropuertos = FuncionesLectura.leerAeropuertos("algoritmos/data/Aeropuerto.husos.v1.incompleto.txt");
        for (Aeropuerto a : aeropuertos.values()) {
            if (a.getGmt() <0 ){
                a.setContinente("América del Sur");
            }
            else{
                a.setContinente("Europa");
            }
        }
        HashMap<Integer, Vuelo> vuelos = FuncionesLectura.leerVuelos("algoritmos/data/Planes.vuelo.v1.incompleto.txt", aeropuertos);
        HashMap<Integer, Envio> envios = FuncionesLectura.leerEnvios("algoritmos/data/pack_enviado/pack_enviado_SKBO.txt", aeropuertos);
        // envios = FuncionesLectura.leerEnvios("algoritmos/data/pack_enviado/pack_enviado_SEQM.txt", aeropuertos);
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        for (int i : envios.keySet()) {
            if (!aeropuertos.containsKey(envios.get(i).getOrigen()) || !aeropuertos.containsKey(envios.get(i).getDestino())) {
                toRemove.add(i);
            }
        }
        for (int i : toRemove) {
            envios.remove(i);
        }
        
		ArrayList<Paquete> paquetes = new ArrayList<Paquete>();
		for(Envio e : envios.values()){
			paquetes.addAll(e.getPaquetes());
		}


        try {
            FileWriter writer = new FileWriter("results_big_"+LocalDate.now()+".txt");
        
            writer.write("Iteración\tTiempo de ejecución (ms)\tPaquetes entregados\tPorcentaje de paquetes entregados\n");
            double promedio=0;
            int iteraciones=1;
            int tamanioSolucion=4;
            for (int i = 0; i < iteraciones; i++) {
                Long startTime = System.currentTimeMillis();
                int[] owo=MPAv2.run(aeropuertos, vuelos, envios, paquetes, 80, 50, tamanioSolucion);
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
    
        /*

        //Una solución
        int verPaquete=10;
        Envio envioDelPaquete=envios.get(paquetes.get(verPaquete).getIdEnvío());

        
        //System.out.println("L solucion es valida: " + esSolucionValida);


        //Se qiiere llegar de 
        System.out.println("Se quería llegar de " + envioDelPaquete.getOrigen() + " a " + envioDelPaquete.getDestino());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDateTime = envioDelPaquete.getFechaHoraSalida().format(formatter);
        System.out.println("El paquete se dejó el " + formattedDateTime);
        String destino=envioDelPaquete.getDestino();
        
        ArrayList<Integer> validaciones = new ArrayList<Integer>();

        for (int i = (verPaquete-1)*tamanioSolucion; i < tamanioSolucion*(verPaquete); i++) {
            System.out.print(owo[i] + ": ");
            System.out.print(vuelos.get(owo[i]).getOrigen() + " (Departure: " + vuelos.get(owo[i]).getFechaHoraSalida().format(formatter) + ") - ");
            System.out.print(vuelos.get(owo[i]).getDestino() + " (Arrival: " + vuelos.get(owo[i]).getFechaHoraLlegada().format(formatter) + ")\n");
            
            validaciones.add(owo[i]);
            if(vuelos.get(owo[i]).getDestino().equals(destino)){
                System.out.println("Llegó al destino");
                System.out.println("----------------------");
                break;
            }

            
        }
        System.out.println("Funcion validacion:  ");
        Paquete auxPaquete = envioDelPaquete.getPaquetes().get(verPaquete);
        auxPaquete.setRuta(validaciones);
        Boolean esSolucionValida = Auxiliares.solucionValidav2(aeropuertos, vuelos, envios, auxPaquete);
        System.out.println("La solución es valida: " + esSolucionValida);
        */
    }



        
	}


