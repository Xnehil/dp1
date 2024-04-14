package com.dp1.backend;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Paquete;
import com.dp1.backend.models.Vuelo;
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

        HashMap<Integer, Vuelo> vuelos = FuncionesLectura.leerVuelos("algoritmos/data/Planes.vuelo.v1.incompleto.txt", aeropuertos);
        HashMap<Integer, Envio> envios = FuncionesLectura.leerEnvios("algoritmos/data/pack_enviado/pack_enviado_SEQM.txt", aeropuertos);

        //Drop envios whose origen or destino is not in aeropuertos
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

		int[] owo=MPAv2.run(aeropuertos, vuelos, envios, paquetes, 50, 80);

        //Una solución
        int verPaquete=15;

        //Se qiiere llegar de 
        System.out.println("Se quería llegar de " + envios.get(verPaquete).getOrigen() + " a " + envios.get(verPaquete).getDestino());
        String destino=envios.get(verPaquete).getDestino();

        for (int i = verPaquete*aeropuertos.size(); i < aeropuertos.size()*(verPaquete+1); i++) {
            System.out.print(owo[i] + ": ");
            System.out.print(vuelos.get(owo[i]).getOrigen()+"  - " + vuelos.get(owo[i]).getDestino() + " \n");
            if(vuelos.get(owo[i]).getDestino().equals(destino)){
                System.out.println("Llegó al destino");
                break;
            }
        }

        

        

	}
}
