package com.dp1.backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.dp1.backend.models.Ruta;
import com.dp1.backend.services.RutaService;

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
@RequestMapping("/aeropuerto")
public class RutaController {
    @Autowired
    private RutaService rutaService;
    
    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @GetMapping("/{id}")
    public Ruta getRuta(@PathVariable(name = "id", required = true) int id) {
        return rutaService.getRuta(id);
    }

    @GetMapping()
    public ArrayList<Ruta> getRutas() {
        return rutaService.getRutas();
    }

    @PostMapping
    public Ruta createRuta(@RequestBody Ruta ruta) {
        return rutaService.createRuta(ruta);
    }

    @PutMapping
    public Ruta updateRuta(@RequestBody Ruta ruta) {
        return rutaService.updateRuta(ruta);
    }

    @DeleteMapping("/{id}")
    public String deleteRuta(@PathVariable(name = "id", required = true) int id) {
        return rutaService.deleteRuta(id);
    }
    
    
}