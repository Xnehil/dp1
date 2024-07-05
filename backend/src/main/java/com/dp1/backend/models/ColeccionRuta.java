package com.dp1.backend.models;

import com.dp1.backend.models.Aeropuerto;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "coleccion_rutas", indexes = {
    @Index(name = "idx_codigo_rutas", columnList = "codigo_ruta"),
})
@SQLDelete(sql = "UPDATE lista_rutas SET active = false WHERE id = ?")
@SQLRestriction(value = "active = true")
public class ColeccionRuta extends BaseModel  {
    @Column(name = "codigo_ruta")
    private String codigoRuta;
    //CodigoCiudadOrigen-CodigoCiudadDestino

    //Una ruta puede tener varias alternativas 
    @JsonManagedReference
    @OneToMany(mappedBy = "coleccionRuta",  cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RutaPosible>  rutasPosibles;


    public String getCodigoRuta() {
        return this.codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public List<RutaPosible> getRutasPosibles() {
        return this.rutasPosibles;
    }

    public void setRutasPosibles(List<RutaPosible> rutasPosibles) {
        this.rutasPosibles = rutasPosibles;
    }
}
