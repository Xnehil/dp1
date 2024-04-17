package com.dp1.backend.models;
//Un paquete es parte de un envío

import java.time.Duration;
import java.util.ArrayList;

public class Paquete {
    private int idPaquete;
    private int idEnvío;
    //Se almacena la lista de ids de los vuelos a seguir

    public int getIdPaquete() {
        return this.idPaquete;
    }

    public void setIdPaquete(int idPaquete) {
        this.idPaquete = idPaquete;
    }

    public int getIdEnvío() {
        return this.idEnvío;
    }

    public void setIdEnvío(int idEnvío) {
        this.idEnvío = idEnvío;
    }

    public ArrayList<Integer> getRuta() {
        return this.ruta;
    }

    public void setRuta(ArrayList<Integer> ruta) {
        this.ruta = ruta;
    }

    public Duration getTiempoRestante() {
        return this.tiempoRestante;
    }

    public void setTiempoRestante(Duration tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    
    private ArrayList<Integer> ruta;

    //Tiempo restante para que el paquete llegue a su destino
    private Duration tiempoRestante;


    public Paquete(int idPaquete, int idEnvío, ArrayList<Integer> ruta, Duration tiempoRestante) {
        this.idPaquete = idPaquete;
        this.idEnvío = idEnvío;
        this.ruta = ruta;
        this.tiempoRestante = tiempoRestante;
    }

    public Paquete() {
        this.idPaquete = 0;
        this.idEnvío = 0;
        this.ruta = new ArrayList<Integer>();
        this.tiempoRestante = Duration.ZERO;
    }
}
