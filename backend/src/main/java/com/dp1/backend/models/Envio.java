package com.dp1.backend.models;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class Envio {
    private int idEnvio;
    private String origen;
    private String destino;
    private ZonedDateTime fechaHoraSalida;
    private ZonedDateTime fechaHoraLlegadaPrevista;
    private ZonedDateTime fechaHoraLlegadaReal;
    private int cantidadPaquetes;
    private ArrayList<Paquete> paquetes;


    public ZonedDateTime getFechaHoraLlegadaReal() {
        return this.fechaHoraLlegadaReal;
    }

    public void setFechaHoraLlegadaReal(ZonedDateTime fechaHoraLlegadaReal) {
        this.fechaHoraLlegadaReal = fechaHoraLlegadaReal;
    }


    public Envio(String origen, String destino, ZonedDateTime fechaHoraSalida, int cantidadPaquetes, ArrayList<Paquete> paquetes) {
        this.origen = origen;
        this.destino = destino;
        this.fechaHoraSalida = fechaHoraSalida;
        this.cantidadPaquetes = cantidadPaquetes;
        this.paquetes = paquetes;
    }

    public Envio() {
        this.origen = "";
        this.destino = "";
        this.fechaHoraSalida = ZonedDateTime.now();
        this.fechaHoraLlegadaPrevista = ZonedDateTime.now();
        this.cantidadPaquetes = 0;
        this.paquetes = new ArrayList<Paquete>();
    }


    public int getIdEnvio() {
        return this.idEnvio;
    }

    public void setIdEnvio(int idEnvio) {
        this.idEnvio = idEnvio;
    }

    public String getOrigen() {
        return this.origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return this.destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public ZonedDateTime getFechaHoraSalida() {
        return this.fechaHoraSalida;
    }

    public void setFechaHoraSalida(ZonedDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public ZonedDateTime getFechaHoraLlegadaPrevista() {
        return this.fechaHoraLlegadaPrevista;
    }

    public void setFechaHoraLlegadaPrevista(ZonedDateTime fechaHoraLlegada) {
        this.fechaHoraLlegadaPrevista = fechaHoraLlegada;
    }

    public int getCantidadPaquetes() {
        return this.cantidadPaquetes;
    }

    public void setCantidadPaquetes(int cantidadPaquetes) {
        this.cantidadPaquetes = cantidadPaquetes;
    }

    public ArrayList<Paquete> getPaquetes() {
        return this.paquetes;
    }

    public void setPaquetes(ArrayList<Paquete> paquetes) {
        this.paquetes = paquetes;
    }

}
