package com.dp1.backend.models;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import java.time.ZonedDateTime;

public class ProgramacionVuelo { //guarda el vuelo y la fecha del vuelo, dado que Vuelo no tiene fecha. Esto ayudará a manejar vuelos en distintos dias que se repiten
    private int idProgramacionVuelo;
    private int idVuelo;
    private ZonedDateTime fechaHoraSalida;
    private ZonedDateTime fechaHoraLlegada;
    // Constructor
    public ProgramacionVuelo(int idProgramacionVuelo, int idVuelo, ZonedDateTime fechaHoraSalida, ZonedDateTime fechaHoraLlegada) {
        this.idProgramacionVuelo = idProgramacionVuelo;
        this.idVuelo = idVuelo;
        this.fechaHoraSalida = fechaHoraSalida;
        this.fechaHoraLlegada = fechaHoraLlegada;
    }

    // Getters y setters
    public int getIdProgramacionVuelo() {
        return idProgramacionVuelo;
    }

    public void setIdProgramacionVuelo(int idProgramacionVuelo) {
        this.idProgramacionVuelo = idProgramacionVuelo;
    }

    public int getIdVuelo() {
        return idVuelo;
    }

    public void setIdVuelo(int idVuelo) {
        this.idVuelo = idVuelo;
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
}

