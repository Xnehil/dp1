package com.dp1.backend.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dp1.backend.models.Vuelo;
import com.dp1.backend.services.VueloService;

@RestController
@RequestMapping("/vuelo")
public class VueloController {
    @Autowired
    private VueloService vueloService;

    public VueloController(VueloService vueloService) {
        this.vueloService = vueloService;
    }

    @GetMapping("/{id}")
    public Vuelo getVuelo(@PathVariable(name = "id", required = true) int id) {
        return vueloService.getVuelo(id);
    }

    @GetMapping()
    public ArrayList<Vuelo> getVuelos() {
        return vueloService.getVuelos();
    }

    @PostMapping
    public Vuelo createVuelo(@RequestBody Vuelo vuelo) {
        return vueloService.createVuelo(vuelo);
    }

    @PutMapping
    public Vuelo updateVuelo(@RequestBody Vuelo vuelo) {
        return vueloService.updateVuelo(vuelo);
    }

    @DeleteMapping("/{id}")
    public String deleteVuelo(@PathVariable(name = "id", required = true) int id) {
        return vueloService.deleteVuelo(id);
    }

    @GetMapping("/enAire")
    public ArrayList<Vuelo> getVuelosEnAire() {
        return vueloService.getVuelosEnElAire();
    }

    @PostMapping("/deArchivo")
    public int guardarVuelosArchivo() {
        return vueloService.guardarVuelosArchivo();
    }
}
