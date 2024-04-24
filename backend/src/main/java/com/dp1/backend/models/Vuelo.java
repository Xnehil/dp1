package com.dp1.backend.models;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;

public class Vuelo {
    private int idVuelo;
    private String origen;
    private String destino;
    private ZonedDateTime fechaHoraSalida;
    private ZonedDateTime fechaHoraLlegada;
    private int capacidad;
    private HashMap<LocalDate, Integer> cargaPorDia;

    public HashMap<LocalDate,Integer> getCargaPorDia() {
        return this.cargaPorDia;
    }

    public void setCargaPorDia(HashMap<LocalDate,Integer> cargaPorDia) {
        this.cargaPorDia = cargaPorDia;
    }


    private Boolean cambioDeDia;

    public Boolean isCambioDeDia() {
        return this.cambioDeDia;
    }

    public Boolean getCambioDeDia() {
        return this.cambioDeDia;
    }

    public void setCambioDeDia(Boolean cambioDeDia) {
        this.cambioDeDia = cambioDeDia;
    }

    public Vuelo(String origen, String destino, ZonedDateTime fechaHoraSalida, ZonedDateTime fechaHoraLlegada, int capacidad) {
        this.origen = origen;
        this.destino = destino;
        this.fechaHoraSalida = fechaHoraSalida;
        this.fechaHoraLlegada = fechaHoraLlegada;
        this.capacidad = capacidad;
        this.cargaPorDia = new HashMap<LocalDate, Integer>();

        ZonedDateTime auxInicio = fechaHoraSalida;
        ZonedDateTime auxFin = fechaHoraLlegada;

        // Cambio de día sucece si la hora de llegada es antes de la hora de salida. Ya se consideran las zonas horarias
        if (auxFin.isBefore(auxInicio)) {
            this.cambioDeDia = true;
        } else {
            this.cambioDeDia = false;
        }
    }

    public Vuelo() {
        this.origen = "";
        this.destino = "";
        this.fechaHoraSalida = ZonedDateTime.now();
        this.fechaHoraLlegada = ZonedDateTime.now();
        this.capacidad = 0;
    }

    public int getIdVuelo() {
        return this.idVuelo;
    }

    public void setIdVuelo(int idVuelo) {
        this.idVuelo = idVuelo;
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

    public ZonedDateTime getFechaHoraLlegada() {
        return this.fechaHoraLlegada;
    }

    public void setFechaHoraLlegada(ZonedDateTime fechaHoraLlegada) {
        this.fechaHoraLlegada = fechaHoraLlegada;
    }

    public int getCapacidad() {
        return this.capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public static int getVueloRandomDesde(HashMap<Integer, Vuelo> vuelos, String origen) {
        //Devuelve un vuelo aleatorio desde el aeropuerto origen
        int idVuelo = 0;
        for (Vuelo vuelo : vuelos.values()) {
            if (vuelo.getOrigen().equals(origen)) {
                idVuelo = vuelo.getIdVuelo();
                break;
            }
        }
        return idVuelo;
    }
    public double calcularMinutosDeVuelo() {
        //Calcular la diferencia de tiempo entre la fecha y hora de salida y la fecha y hora de llegada
        //No se consideran segundos
        double duracionVuelo = Duration.between(fechaHoraSalida, fechaHoraLlegada).toMinutes();
        //Si te devuelve negativo, es porque está calculado de un dia para otro. Te devuelve -(1440 - minutosQueQuiero)
        if(duracionVuelo < 0) duracionVuelo = 1440 - (duracionVuelo*-1);
        return duracionVuelo;
    }
}
