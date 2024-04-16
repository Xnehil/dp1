package com.dp1.backend.models;

import java.util.TimeZone;

public class Aeropuerto{
    private int idAeropuerto;
    private String codigoOACI;
    private String ciudad;
    private String pais;
    private String paisCorto;
    private String continente;
    private int gmt;
    private int capacidadMaxima;
    private TimeZone zonaHoraria;
    private double latitud;
    private double longitud;

    private int cargaActual;
    private int cargaAuxiliarParaFitness;


    public int getCargaActual() {
        return this.cargaActual;
    }

    public void setCargaActual(int cargaActual) {
        this.cargaActual = cargaActual;
    }

    public int getCargaAuxiliarParaFitness() {
        return this.cargaAuxiliarParaFitness;
    }

    public void setCargaAuxiliarParaFitness(int cargaAuxiliarParaFitness) {
        this.cargaAuxiliarParaFitness = cargaAuxiliarParaFitness;
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
        this.idAeropuerto = idAeropuerto;

        //set timezone from GMT
        String[] ids = TimeZone.getAvailableIDs(gmt * 3600000);
        if (ids.length == 0) {
            System.out.println("No se encontró la zona horaria para GMT " + gmt);
        } else {
            this.zonaHoraria = TimeZone.getTimeZone(ids[0]);
        }

        this.cargaActual = 0;
        this.cargaAuxiliarParaFitness = 0;        
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

    public String getContinente() {
        return this.continente;
    }

    public void setContinente(String continente) {
        this.continente = continente;
    }


    public int getIdAeropuerto() {
        return this.idAeropuerto;
    }

    public void setIdAeropuerto(int idAeropuerto) {
        this.idAeropuerto = idAeropuerto;
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


}