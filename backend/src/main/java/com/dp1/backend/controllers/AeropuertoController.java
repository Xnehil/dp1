package com.dp1.backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.services.AeropuertoService;

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
public class AeropuertoController {
    @Autowired
    private AeropuertoService aeropuertoService;
    
    public AeropuertoController(AeropuertoService aeropuertoService) {
        this.aeropuertoService = aeropuertoService;
    }

    @GetMapping("/{id}")
    public Aeropuerto getAeropuerto(@PathVariable(name = "id", required = true) int id) {
        return aeropuertoService.getAeropuerto(id);
    }

    @GetMapping("/codigo/{codigo}")
    public Aeropuerto getAeropuertoByCodigo(@PathVariable(name = "codigo", required = true) String codigo) {
        return aeropuertoService.getAeropuertoByCodigo(codigo);
    }

    @GetMapping()
    public ArrayList<Aeropuerto> getAeropuertos() {
        return aeropuertoService.getAeropuertosMemoria();
    }

    @PostMapping
    public Aeropuerto createAeropuerto(@RequestBody Aeropuerto aeropuerto) {
        return aeropuertoService.createAeropuerto(aeropuerto);
    }

    @PutMapping
    public Aeropuerto updateAeropuerto(@RequestBody Aeropuerto aeropuerto) {
        return aeropuertoService.updateAeropuerto(aeropuerto);
    }

    @DeleteMapping("/{id}")
    public String deleteAeropuerto(@PathVariable(name = "id", required = true) int id) {
        return aeropuertoService.deleteAeropuerto(id);
    }
    
    
}