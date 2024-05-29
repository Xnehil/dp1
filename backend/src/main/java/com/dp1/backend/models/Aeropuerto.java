package com.dp1.backend.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "aeropuerto", indexes = {
    @Index(name = "idx_aeropuerto_codigo_oaci", columnList = "codigo_oaci")
})
@SQLDelete(sql = "UPDATE aeropuerto SET active = false WHERE id = ?")
@SQLRestriction(value = "active = true")
public class Aeropuerto extends BaseModel {
    @Column(name = "codigo_oaci")
    private String codigoOACI;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "pais")
    private String pais;

    @Column(name = "pais_corto")
    private String paisCorto;

    @Column(name = "continente")
    private String continente;

    @Column(name = "gmt")
    private int gmt;

    private ZoneId zoneId;

    @Column(name = "capacidad_maxima")
    private int capacidadMaxima;
    private TimeZone zonaHoraria;

    @Column(name = "latitud")
    private double latitud;

    @Column(name = "longitud")
    private double longitud;
    //Estos tienen la zona horaria del aeropuerto
    private TreeMap<LocalDateTime, Integer> cantPaqParaPlanificacion;
    private TreeMap<LocalDateTime, Integer> cantPaqReal;


    public TreeMap<LocalDateTime,Integer> getCantPaqReal() {
        return this.cantPaqReal;
    }

    public void setCantPaqReal(TreeMap<LocalDateTime,Integer> cantPaqReal) {
        this.cantPaqReal = cantPaqReal;
    }


    public TreeMap<LocalDateTime,Integer> getCantPaqParaPlanificacion() {
        return this.cantPaqParaPlanificacion;
    }

    public void setCantPaqParaPlanificacion(TreeMap<LocalDateTime,Integer> estadoAlmacen) {
        this.cantPaqParaPlanificacion = estadoAlmacen;
    }

    public double getLatitud() {
        return this.latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return this.longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public TimeZone getZonaHoraria() {
        return this.zonaHoraria;
    }

    public void setZonaHoraria(TimeZone zonaHoraria) {
        this.zonaHoraria = zonaHoraria;
    }

    public Aeropuerto(int idAeropuerto, String codigoOACI, String ciudad, String pais, String paisCorto, int gmt, int capacidad) {
        this.codigoOACI = codigoOACI;
        this.ciudad = ciudad;
        this.pais = pais;
        this.paisCorto = paisCorto;
        this.gmt = gmt;
        this.capacidadMaxima = capacidad;
        super.setId(idAeropuerto);
        this.cantPaqParaPlanificacion = new TreeMap<LocalDateTime, Integer>();
        this.cantPaqReal = new TreeMap<LocalDateTime, Integer>();


        ZoneOffset offset = ZoneOffset.ofHours(gmt);
        ZoneId zoneId = offset.normalized();

        this.zonaHoraria = TimeZone.getTimeZone(zoneId);
        this.zoneId = zoneId;
    }

    public Aeropuerto() {
        this.codigoOACI = "";
        this.ciudad = "";
        this.pais = "";
        this.paisCorto = "";
        this.gmt = 0;
        this.capacidadMaxima = 0;
        this.continente = "";
    }

    public ZoneId getZoneId(){
        return this.zoneId;
    }
    public String getContinente() {
        return this.continente;
    }

    public void setContinente(String continente) {
        this.continente = continente;
    }


    public int getIdAeropuerto() {
        return super.getId();
    }

    public void setIdAeropuerto(int idAeropuerto) {
        super.setId(idAeropuerto);
    }

    public String getCodigoOACI() {
        return this.codigoOACI;
    }

    public void setCodigoOACI(String codigoOACI) {
        this.codigoOACI = codigoOACI;
    }

    public String getCiudad() {
        return this.ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return this.pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getPaisCorto() {
        return this.paisCorto;
    }

    public void setPaisCorto(String paisCorto) {
        this.paisCorto = paisCorto;
    }

    public int getGmt() {
        return this.gmt;
    }

    public void setGmt(int gmt) {
        this.gmt = gmt;
    }

    public int getCapacidadMaxima() {
        return this.capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidad) {
        this.capacidadMaxima = capacidad;
    }

    public void paqueteEntraPlanificacion(LocalDateTime time) {
        Map.Entry<LocalDateTime, Integer> entry = cantPaqParaPlanificacion.floorEntry(time);
        int lastValue = (entry != null) ? entry.getValue() : paquetesAEstaHoraReal(time);;

        cantPaqParaPlanificacion.put(time, lastValue + 1);
        updateFutureCounts(time, 1);
    }

    public void paqueteSalePlanificacion(LocalDateTime time) {
        Map.Entry<LocalDateTime, Integer> entry = cantPaqParaPlanificacion.floorEntry(time);
        int lastValue = (entry != null) ? entry.getValue() : paquetesAEstaHoraReal(time);

        cantPaqParaPlanificacion.put(time, lastValue - 1);
        updateFutureCounts(time, -1);
    }

    public int paquetesAEstaHoraPlanificacion(LocalDateTime time) {
        Map.Entry<LocalDateTime, Integer> entry = cantPaqParaPlanificacion.floorEntry(time);
        int lastValue = (entry != null) ? entry.getValue() : paquetesAEstaHoraReal(time);
        return lastValue;
    }

    private void updateFutureCounts(LocalDateTime time, int delta) {
        SortedMap<LocalDateTime, Integer> tailMap = cantPaqParaPlanificacion.tailMap(time);
        for (Map.Entry<LocalDateTime, Integer> entry : tailMap.entrySet()) {
            cantPaqParaPlanificacion.put(entry.getKey(), entry.getValue() + delta);
        }
    }

    public void paqueteEntraReal(LocalDateTime time) {
        Map.Entry<LocalDateTime, Integer> entry = cantPaqReal.floorEntry(time);
        int lastValue = (entry != null) ? entry.getValue() : 0;
        
        cantPaqReal.put(time, lastValue + 1);
    }

    public void paqueteSaleReal(LocalDateTime time) {
        Map.Entry<LocalDateTime, Integer> entry = cantPaqReal.floorEntry(time);
        int lastValue = (entry != null) ? entry.getValue() : 0;
        
        cantPaqReal.put(time, lastValue - 1);
    }

    public int paquetesAEstaHoraReal(LocalDateTime time) {
        Map.Entry<LocalDateTime, Integer> entry = cantPaqReal.floorEntry(time);
        return (entry != null) ? entry.getValue() : 0;
    }


}