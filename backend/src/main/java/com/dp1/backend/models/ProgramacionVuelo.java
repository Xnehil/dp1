package com.dp1.backend.models;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;

@Entity
@Table(name = "vuelo")
@SQLDelete(sql = "UPDATE vuelo SET active = false WHERE id = ?")
@SQLRestriction(value = "active = true")
public class ProgramacionVuelo extends BaseModel{ //guarda el vuelo y la fecha del vuelo, dado que Vuelo no tiene fecha. Esto ayudará a manejar vuelos en distintos dias que se repiten
    private int idProgramacionVuelo;

    @ManyToOne
    private Vuelo planVuelo;
    private int idVuelo;
    private ZonedDateTime fechaHoraSalida;
    private ZonedDateTime fechaHoraLlegada;
    //Código programación vuelo: idVuelo+año-mes-dia 
    private String codigoProgramacionVuelo;
    private int cargaActualReal;
    private int cargaActualPlanificacion;

    @Column(name = "dia_salida")
    private Date diaSalida;

    @Column(name = "cantidad_paquetes")
    private int cantidadPaquetes;

    // Constructor
    public ProgramacionVuelo(int idProgramacionVuelo, int idVuelo, ZonedDateTime fechaHoraSalida, ZonedDateTime fechaHoraLlegada) {
        this.idProgramacionVuelo = idProgramacionVuelo;
        this.idVuelo = idVuelo;
        this.fechaHoraSalida = fechaHoraSalida;
        this.fechaHoraLlegada = fechaHoraLlegada;
        this.codigoProgramacionVuelo = idVuelo + "-" + fechaHoraSalida.getYear() + "-" + fechaHoraSalida.getMonthValue() + "-" + fechaHoraSalida.getDayOfMonth();
        this.cargaActualReal = 0;
        this.cargaActualPlanificacion = this.cargaActualReal;
    }

    // Getters y setters


    public Date getDiaSalida() {
        return this.diaSalida;
    }

    public void setDiaSalida(Date diaSalida) {
        this.diaSalida = diaSalida;
    }


    public int getCargaActualReal() {
        return this.cargaActualReal;
    }

    public void setCargaActualReal(int cargaActualReal) {
        this.cargaActualReal = cargaActualReal;
    }

    public int getCargaActualPlanificacion() {
        return this.cargaActualPlanificacion;
    }

    public void setCargaActualPlanificacion(int cargaActualPlanificacion) {
        this.cargaActualPlanificacion = cargaActualPlanificacion;
    }

    public String getCodigoProgramacionVuelo() {
        return codigoProgramacionVuelo;
    }

    public void setCodigoProgramacionVuelo(String codigoProgramacionVuelo) {
        this.codigoProgramacionVuelo = codigoProgramacionVuelo;
    }


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

