package com.dp1.backend;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import com.dp1.backend.utils.MPA;
import com.dp1.backend.utils.MPAv2;

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
        /*
        for (Aeropuerto aeropuerto : aeropuertos.values()) {
            System.out.println("ID: " + aeropuerto.getIdAeropuerto() + ", " +
                           "Código OACI: " + aeropuerto.getCodigoOACI() + ", " +
                           "Ciudad: " + aeropuerto.getCiudad() + ", " +
                           "País: " + aeropuerto.getPais() + ", " +
                           "País Corto: " + aeropuerto.getPaisCorto() + ", " +
                           "Continente: " + aeropuerto.getContinente() + ", " +
                           "GMT: " + aeropuerto.getGmt() + ", " +
                           "Capacidad: " + aeropuerto.getCapacidad() + ", " +
                           "Zona Horaria: " + aeropuerto.getZonaHoraria().getID() + ", " +
                           "Latitud: " + aeropuerto.getLatitud() + ", " +
                           "Longitud: " + aeropuerto.getLongitud());
        }
         */
        // for (Aeropuerto aeropuerto : aeropuertos.values()) {
        //     System.out.println("ID: " + aeropuerto.getIdAeropuerto() + ", " +
        //                    "Código OACI: " + aeropuerto.getCodigoOACI() + ", " +
        //                    "Ciudad: " + aeropuerto.getCiudad() + ", " +
        //                    "País: " + aeropuerto.getPais() + ", " +
        //                    "GMT: " + aeropuerto.getGmt() + ", " +
        //                    "Zona Horaria: " + aeropuerto.getZonaHoraria().getID());
        // }
        HashMap<Integer, Vuelo> vuelos = FuncionesLectura.leerVuelos("algoritmos/data/Planes.vuelo.v1.incompleto.txt", aeropuertos);
        HashMap<Integer, Envio> envios = FuncionesLectura.leerEnvios("algoritmos/data/pack_enviado/pack_enviado_SGAS.txt", aeropuertos);

        //Drop envios whose origen or destino is not in aeropuertos
        


        envios = FuncionesLectura.leerEnvios("algoritmos/data/pack_enviado/pack_enviado_SEQM.txt", aeropuertos);
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
        //internamente cada paquete retornará con una ruta
        // ACO.run(aeropuertos, vuelos, envios, paquetes,100);
        



        // for(Paquete p:paquetes){
        //     System.out.println(p.getIdEnvío() + "  "+ p.getIdPaquete());
        // }
        

        // for (int j = 0; j < 10; j++) {
            //Medir tiempo de ejecución
            long startTime = System.currentTimeMillis();

            //Parámetros con los que experimentar: maxIter, popSize, tamanioSolucion
            int tamanioSolucion=5;
            int[] owo=MPAv2.run(aeropuertos, vuelos, envios, paquetes, 70, 25, tamanioSolucion);

            long endTime = System.currentTimeMillis();

            System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " milisegundos");
        
            //Una solución
            int verPaquete=1;
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
                
                
                if(vuelos.get(owo[i]).getDestino().equals(destino)){
                    System.out.println("Llegó al destino");
                    System.out.println("----------------------");
                    //System.out.println("Owo es: ");
                    //for(int k=0; k<owo.length; k++)  System.out.print(owo[k]+ " ");
                    break;
                }

                validaciones.add(owo[i]);
            }
            System.out.println("Funcion validacion:  ");
            Paquete auxPaquete = envioDelPaquete.getPaquetes().get(verPaquete);
            auxPaquete.setRuta(validaciones);
            Boolean esSolucionValida = Auxiliares.solucionValidav2(owo, aeropuertos, vuelos, envios, auxPaquete);
            System.out.println("La solución es valida: " + esSolucionValida);
        }


        // }



    
        // ZonedDateTime nowGMT = ZonedDateTime.now(ZoneId.of("GMT-5"));
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss z");
        // System.out.println("Fecha y hora actual en GMT: " + nowGMT.format(formatter));
        
        // while(true){
        //     LocalTime localTime = LocalTime.now();
        //     System.out.println(localTime);
        // }
        
	}


