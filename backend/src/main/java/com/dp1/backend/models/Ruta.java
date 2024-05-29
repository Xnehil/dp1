package com.dp1.backend.models;

import com.dp1.backend.models.Aeropuerto;
import java.util.ArrayList;

public class Ruta extends BaseModel  {
    private String id;
    private String nombre;

    private ArrayList<Aeropuerto> listaDeRutas;
}
