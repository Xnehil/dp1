package com.dp1.backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.dp1.backend.models.Envio;
import com.dp1.backend.services.EnvioService;

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
@RequestMapping("/envio")
public class EnvioController {
    @Autowired
    private EnvioService envioService;
    
    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping("/{id}")
    public Envio getEnvio(@PathVariable(name = "id", required = true) int id) {
        return envioService.getEnvio(id);
    }

    @GetMapping("/codigo/{codigo_envio}")
    public Envio getEnvio(@PathVariable(name = "codigo_envio", required = true) String codigo_envio) {
        return envioService.getEnvioCodigo(codigo_envio);
    }

    @GetMapping()
    public ArrayList<Envio> getEnvios() {
        return envioService.getEnvios();
    }

    @PostMapping
    public String createEnvio(@RequestBody Envio envio) {
        return envioService.createEnvio(envio);
    }

    @PutMapping
    public Envio updateEnvio(@RequestBody Envio envio) {
        return envioService.updateEnvio(envio);
    }

    @DeleteMapping("/{id}")
    public String deleteEnvio(@PathVariable(name = "id", required = true) int id) {
        return envioService.deleteEnvio(id);
    }
    
    
}
