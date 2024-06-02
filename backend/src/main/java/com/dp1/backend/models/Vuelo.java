package com.dp1.backend.models;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


@Entity
@Table(name = "plan_vuelo", indexes = {
    @Index(name = "idx_origen_vuelo", columnList = "origen"),
})
@SQLDelete(sql = "UPDATE plan_vuelo SET active = false WHERE id = ?")
@SQLRestriction(value = "active = true")
public class Vuelo extends BaseModel {
    @Column(name="origen")
    private String origen;

    @Column(name="destino")
    private String destino;

    @Column(name="hora_salida")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private ZonedDateTime fechaHoraSalida;
    
    @Column(name="hora_llegada")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private ZonedDateTime fechaHoraLlegada;

    @Column(name="capacidad")
    private int capacidad;

    @Column(name="cambio_de_dia")
    private int cambioDeDia;

    @Column(name="duracion_vuelo")
    private long duracionVuelo;

    @Column(name="distancia_vuelo")
    private double distanciaVuelo;

    private HashMap<LocalDate, Integer> cargaPorDia;

    public HashMap<LocalDate,Integer> getCargaPorDia() {
        return this.cargaPorDia;
    }

    public void setCargaPorDia(HashMap<LocalDate,Integer> cargaPorDia) {
        this.cargaPorDia = cargaPorDia;
    }


    public long getDuracionVuelo() {
        return this.duracionVuelo;
    }

    public void setDuracionVuelo(long duracionVuelo) {
        this.duracionVuelo = duracionVuelo;
    }

    public double getDistanciaVuelo() {
        return this.distanciaVuelo;
    }

    public void setDistanciaVuelo(double distanciaVuelo) {
        this.distanciaVuelo = distanciaVuelo;
    }

    public int getCambioDeDia() {
        return this.cambioDeDia;
    }

    public void setCambioDeDia(int cambioDeDia) {
        this.cambioDeDia = cambioDeDia;
    }

    public Vuelo(String origen, String destino, ZonedDateTime fechaHoraSalida, ZonedDateTime fechaHoraLlegada, int capacidad, double distanciaVuelo) {
        this.origen = origen;
        this.destino = destino;
        this.fechaHoraSalida = fechaHoraSalida;
        this.fechaHoraLlegada = fechaHoraLlegada;
        this.capacidad = capacidad;
        this.cargaPorDia = new HashMap<LocalDate, Integer>();
        this.distanciaVuelo = distanciaVuelo;
        this.cambioDeDia = 0;

        ZonedDateTime auxInicio = fechaHoraSalida;
        ZonedDateTime auxFin = fechaHoraLlegada;
        // Cambio de día sucece si la hora de llegada es antes de la hora de salida. Ya se consideran las zonas horarias
        // auxInicio = auxInicio.withZoneSameInstant(auxFin.getZone());
        if (auxFin.isBefore(auxInicio)) {
            // System.out.println("Hora de llegada antes de la hora de salida detectado en vuelo: " + this.getIdVuelo() + "de " + this.origen + " a las " + this.fechaHoraSalida + " a " + this.destino + " a las " + this.fechaHoraLlegada);
            while (auxFin.isBefore(auxInicio)) {
                // System.out.println("Cambio de día de " + auxFin + " a " + auxInicio);
                auxFin = auxFin.plusDays(1);
                this.cambioDeDia++;
            }            
        } 

        this.duracionVuelo = Duration.between(auxInicio, auxFin).toMinutes();
        if(this.duracionVuelo < 0) {
            System.out.println("Duracion vuelo negativa: " + this.duracionVuelo);
            this.duracionVuelo = 1440 - (this.duracionVuelo*-1);
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
        return super.getId();
    }

    public void setIdVuelo(int idVuelo) {
        super.setId(idVuelo);
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

    public static int getVueloRandomDesde(HashMap<Integer, Vuelo> vuelos, Envio envio) {
        //Devuelve un vuelo aleatorio desde el aeropuerto origen
        Random rand = new Random();
        Vuelo closestFlight = null;
        Duration smallestDifference = null;

        for (Vuelo vuelo : vuelos.values()) {
            if(vuelo.getOrigen().equals(envio.getOrigen()) && vuelo.getDestino().equals(envio.getDestino())){
                if (vuelo.getFechaHoraSalida().with(envio.getFechaHoraSalida().toLocalDate()).isAfter(envio.getFechaHoraSalida())) {
                    Duration difference = Duration.between(envio.getFechaHoraSalida(), vuelo.getFechaHoraSalida());
                    if (smallestDifference == null || difference.compareTo(smallestDifference) < 0) {
                        smallestDifference = difference;
                        closestFlight = vuelo;
                    }
                }
            }
        }
        int max = vuelos.size();
        int min = 1;
        if (closestFlight == null) {
            return (int)(Math.random()*(max-min+1)+min);
        }
        return closestFlight.getIdVuelo();
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
