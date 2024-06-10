package com.dp1.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ItemRutaPosible {
    @Column(name = "id_vuelo")
    private Integer idVuelo;

    @Column(name = "dia_relativo")
    private Integer diaRelativo;
}
