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

		ArrayList<Paquete> paquetes = new ArrayList<Paquete>();
		for(Envio e : envios.values()){
			paquetes.addAll(e.getPaquetes());
		}

		int[] owo=MPA.run(aeropuertos, vuelos, envios, paquetes, 40, 20);

        //Una solución
        for (int i = 0; i < aeropuertos.size(); i++) {
            System.out.print(owo[i] + ": ");
            System.out.print(vuelos.get(owo[i]).getOrigen()+"  - " + vuelos.get(owo[i]).getDestino() + " \n");
        }

        //Se quería llegar de 
        System.out.println("Se quería llegar de " + envios.get(1).getOrigen() + " a " + envios.get(1).getDestino());




	}
}
