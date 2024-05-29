package com.dp1.backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.dp1.backend.models.Paquete;
import com.dp1.backend.services.PaqueteService;

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
@RequestMapping("/paquete")
public class PaqueteController {
    @Autowired
    private PaqueteService paqueteService;
    
    public PaqueteController(PaqueteService paqueteService) {
        this.paqueteService = paqueteService;
    }

    @GetMapping("/{id}")
    public Paquete getPaquete(@PathVariable(name = "id", required = true) int id) {
        return paqueteService.getPaquete(id);
    }

    @GetMapping()
    public ArrayList<Paquete> getPaquetes() {
        return paqueteService.getPaquetes();
    }

    @PostMapping
    public Paquete createPaquete(@RequestBody Paquete paquete) {
        return paqueteService.createPaquete(paquete);
    }

    @PutMapping
    public Paquete updatePaquete(@RequestBody Paquete paquete) {
        return paqueteService.updatePaquete(paquete);
    }

    @DeleteMapping("/{id}")
    public String deletePaquete(@PathVariable(name = "id", required = true) int id) {
        return paqueteService.deletePaquete(id);
    }
    
    
}
