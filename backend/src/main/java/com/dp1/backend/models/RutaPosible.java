package com.dp1.backend.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "ruta_posible")
public class RutaPosible extends BaseModel{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_coleccion_ruta")
    private ColeccionRuta coleccionRuta;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ruta_vuelos", joinColumns = @JoinColumn(name = "ruta_posible_id"))
    @OrderColumn(name = "vuelo_index")
    private List<ItemRutaPosible> flights;


    public ColeccionRuta getColeccionRuta() {
        return this.coleccionRuta;
    }

    public void setColeccionRuta(ColeccionRuta coleccionRuta) {
        this.coleccionRuta = coleccionRuta;
    }

    public List<ItemRutaPosible> getFlights() {
        return this.flights;
    }

    public void setFlights(ArrayList<ItemRutaPosible> flights) {
        this.flights = flights;
    }

    
}