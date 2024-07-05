package com.dp1.backend.models;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "envio", indexes = {
    @Index(name = "idx_envio", columnList = "codigoEnvio"),
})
@SQLDelete(sql = "UPDATE envio SET active = false WHERE id = ?")
@SQLRestriction(value = "active = true")
public class Envio extends BaseModel {
    @Column(name = "codigo_envio",  unique = true)
    private String codigoEnvio;

    @Column(name = "origen")
    private String origen;

    @Column(name = "destino")
    private String destino;

    @Column(name = "hora_salida")
    private ZonedDateTime fechaHoraSalida;

    @Column(name = "hora_llegada_prevista")
    private ZonedDateTime fechaHoraLlegadaPrevista;

    @Column(name = "hora_llegada_real")
    private ZonedDateTime fechaHoraLlegadaReal;

    @Column(name = "cantidad_paquetes")
    private int cantidadPaquetes;
    
    //@OneToMany(fetch = FetchType.LAZY, mappedBy = "codigoEnvio", cascade = CascadeType.REMOVE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "codigoEnvio", cascade = CascadeType.REMOVE)
    // @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paquete> paquetes = new ArrayList<Paquete>();

    @ManyToOne(optional = true)
    @JoinColumn(name = "emisor_id", insertable = false, updatable = false, referencedColumnName = "id")
    private Cliente emisor;

    @ManyToOne(optional = true)
    @JoinColumn(name = "receptor_id", insertable = false, updatable = false, referencedColumnName = "id")
    private Cliente receptor;

    @Column(name = "emisor_id")
    private int emisorID;

    @Column(name = "receptor_id")
    private int receptorID;


    public int getEmisorID() {
        return this.emisorID;
    }

    public void setEmisorID(int emisorID) {
        this.emisorID = emisorID;
    }

    public int getReceptorID() {
        return this.receptorID;
    }

    public void setReceptorID(int receptorID) {
        this.receptorID = receptorID;
    }

    public Cliente getEmisor() {
        return this.emisor;
    }

    public void setEmisor(Cliente emisor) {
        this.emisor = emisor;
    }

    public Cliente getReceptor() {
        return this.receptor;
    }

    public void setReceptor(Cliente receptor) {
        this.receptor = receptor;
    }


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
        this.emisor = new Cliente();
        this.receptor= new Cliente();
    }

    public Envio() {
        this.origen = "";
        this.destino = "";
        this.fechaHoraSalida = ZonedDateTime.now();
        this.fechaHoraLlegadaPrevista = ZonedDateTime.now();
        this.cantidadPaquetes = 0;
        this.paquetes = new ArrayList<Paquete>();
    }


    public String getCodigoEnvio() {
        return this.codigoEnvio;
    }

    public void setCodigoEnvio(String codigoEnvio) {
        this.codigoEnvio = codigoEnvio;
    }



    public int getIdEnvio() {
        return super.getId();
    }

    public void setIdEnvio(int idEnvio) {
        super.setId(idEnvio);
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

    public List<Paquete> getPaquetes() {
        return this.paquetes;
    }

    public void setPaquetes(List<Paquete> paquetes) {
        this.paquetes = paquetes;
    }

    public String toString() {
        return "Envio: " + this.getId() + " Origen: " + this.origen + " Destino: " + this.destino + " FechaHoraSalida: " + this.fechaHoraSalida + " FechaHoraLlegadaPrevista: " + this.fechaHoraLlegadaPrevista + " CantidadPaquetes: " + this.cantidadPaquetes ;
    }

}
