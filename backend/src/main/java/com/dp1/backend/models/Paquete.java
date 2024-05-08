package com.dp1.backend.models;
//Un paquete es parte de un envío

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "paquete")
@SQLDelete(sql = "UPDATE paquete SET active = false WHERE id = ?")
@SQLRestriction(value = "active = true")
public class Paquete extends BaseModel{
    // @ManyToOne
    // @JoinColumn(name = "envio")
    // private Envio envio;

    @Column(name = "codigo_envio")
    private String codigoEnvio;

    private ArrayList<ZonedDateTime> fechasRuta;
    private ArrayList<Double> costosRuta;

    @Column(name = "llego_destino")
    private boolean llegoDestino;
    //Se almacena la lista de ids de los vuelos a seguir

    public ArrayList<Double> getcostosRuta() {
        return this.costosRuta;
    }

    public void setCostosRuta(ArrayList<Double> costosRuta) {
        this.costosRuta = costosRuta;
    }

    public double costoTotalRuta(){
        double suma = 0;
        for(double costo : costosRuta){
            suma += costo;
        }
        return suma;
    }

    public boolean getLlegoDestino(){
        return this.llegoDestino;
    }

    public void setLlegoDestino(boolean llegoDestino){
        this.llegoDestino = llegoDestino;
    }
    
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
        return super.getId();
    }

    public void setIdPaquete(int idPaquete) {
        super.setId(idPaquete);
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
    public Duration getTiempoRestanteDinamico() {
        return this.tiempoRestanteDinamico;
    }

    public void setTiempoRestanteDinamico(Duration tiempoRestanteDinamico) {
        this.tiempoRestanteDinamico = tiempoRestanteDinamico;
    }
    
    @ElementCollection
    @CollectionTable(name = "ruta", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "ruta_point")
    @OrderColumn(name = "index")
    private ArrayList<Integer> ruta;

    //Tiempo restante para que el paquete llegue a su destino
    private Duration tiempoRestanteDinamico;
    private Duration tiempoRestante;


    public Paquete(int idPaquete, int idEnvío, ArrayList<Integer> ruta, Duration tiempoRestante) {
        super.setId(idPaquete);
        this.ruta = ruta;
        this.tiempoRestante = tiempoRestante;
    }

    public Paquete() {
        super.setId(0);
        this.ruta = new ArrayList<Integer>();
        this.fechasRuta = new ArrayList<ZonedDateTime>();
        this.costosRuta = new ArrayList<>();
        this.tiempoRestante = Duration.ZERO;
        this.tiempoRestanteDinamico = Duration.ZERO;
        this.llegoDestino = false;
    }
}
