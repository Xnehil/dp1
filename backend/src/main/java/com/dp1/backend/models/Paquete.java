package com.dp1.backend.models;
//Un paquete es parte de un envío

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;


public class Paquete {
    private int idPaquete;
    private int idEnvio;
    private String codigoEnvio;
    private ArrayList<ZonedDateTime> fechasRuta;
    //Se almacena la lista de ids de los vuelos a seguir


    public ZonedDateTime getFechaLlegadaUltimoVuelo(){
        return fechasRuta.get(fechasRuta.size()-1);
    }

    public ArrayList<ZonedDateTime> getFechasRuta() {
        return this.fechasRuta;
    }

    public void setFechasRuta(ArrayList<ZonedDateTime> fechasRuta) {
        this.fechasRuta = fechasRuta;
    }


    public String getCodigoEnvio() {
        return this.codigoEnvio;
    }

    public void setCodigoEnvio(String codigoEnvio) {
        this.codigoEnvio = codigoEnvio;
    }


    public int getIdPaquete() {
        return this.idPaquete;
    }

    public void setIdPaquete(int idPaquete) {
        this.idPaquete = idPaquete;
    }

    public int getIdEnvio() {
        return this.idEnvio;
    }

    public void setIdEnvio(int idEnvío) {
        this.idEnvio = idEnvío;
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
        this.idEnvio = idEnvío;
        this.ruta = ruta;
        this.tiempoRestante = tiempoRestante;
    }

    public Paquete() {
        this.idPaquete = 0;
        this.idEnvio = 0;
        this.ruta = new ArrayList<Integer>();
        this.fechasRuta = new ArrayList<ZonedDateTime>();
        this.tiempoRestante = Duration.ZERO;
    }
}
