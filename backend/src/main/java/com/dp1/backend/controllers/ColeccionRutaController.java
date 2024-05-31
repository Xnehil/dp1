package com.dp1.backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.dp1.backend.models.ColeccionRuta;
import com.dp1.backend.services.ColeccionRutaService;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/coleccionRuta")
public class ColeccionRutaController {
    @Autowired
    private ColeccionRutaService rutaService;
    
    public ColeccionRutaController(ColeccionRutaService rutaService) {
        this.rutaService = rutaService;
    }

    @GetMapping("/{id}")
    public ColeccionRuta getRuta(@PathVariable(name = "id", required = true) int id) {
        return rutaService.getColeccionRuta(id);
    }

    @GetMapping()
    public ArrayList<ColeccionRuta> getRutas() {
        return rutaService.getAllColeccionRutas();
    }

    @PostMapping
    public ColeccionRuta createRuta(@RequestBody ColeccionRuta ruta) {
        return rutaService.createColeccionRuta(ruta);
    }

    @PutMapping
    public ColeccionRuta updateRuta(@RequestBody ColeccionRuta ruta) {
        return rutaService.updateColeccionRuta(ruta);
    }

    @DeleteMapping("/{id}")
    public String deleteRuta(@PathVariable(name = "id", required = true) int id) {
        return rutaService.deleteColeccionRuta(id);
    }
    
    
}