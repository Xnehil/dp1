package com.dp1.backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.dp1.backend.models.RutaPosible;
import com.dp1.backend.services.RutaPosibleService;

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
@RequestMapping("/rutaPosible")
public class RutaPosibleController {
    @Autowired
    private RutaPosibleService rutaPosibleService;
    
    public RutaPosibleController(RutaPosibleService rutaPosibleService) {
        this.rutaPosibleService = rutaPosibleService;
    }

    @GetMapping("/{id}")
    public RutaPosible getRutaPosible(@PathVariable(name = "id", required = true) int id) {
        return rutaPosibleService.getRutaPosible(id);
    }

    @GetMapping()
    public ArrayList<RutaPosible> getRutasPosibles() {
        return rutaPosibleService.getAllColeccioRutaPosibles();
    }

    @PostMapping
    public RutaPosible createRutaPosible(@RequestBody RutaPosible ruta) {
        return rutaPosibleService.createRutaPosible(ruta);
    }

    @PutMapping
    public RutaPosible updateRutaPosible(@RequestBody RutaPosible ruta) {
        return rutaPosibleService.updateRutaPosible(ruta);
    }

    @DeleteMapping("/{id}")
    public String deleteRutaPosible(@PathVariable(name = "id", required = true) int id) {
        return rutaPosibleService.deleteRutaPosible(id);
    }
    
    
}