package com.dp1.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ItemRutaPosible {
    @Column(name = "id_vuelo")
    private int idVuelo;

    @Column(name = "dia_relativo")
    private int diaRelativo;


    public int getIdVuelo() {
        return this.idVuelo;
    }

    public void setIdVuelo(int idVuelo) {
        this.idVuelo = idVuelo;
    }

    public int getDiaRelativo() {
        return this.diaRelativo;
    }

    public void setDiaRelativo(int diaRelativo) {
        this.diaRelativo = diaRelativo;
    }

}
