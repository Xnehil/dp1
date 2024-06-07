package com.dp1.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dp1.backend.services.ACOService;

@RestController
@RequestMapping("/aco")
public class ACOController {
    @Autowired
    private ACOService acoService;

    public ACOController(ACOService acoService) {
        this.acoService = acoService;
    }

    @GetMapping("/ejecutar")
    public String ejecutarAco() {
        return acoService.ejecutarAcoAntiguo();
    }

    @GetMapping("/ejecutar/{codigo}")
    public String ejecutarAcoCiudad(@PathVariable(name = "codigo", required = true) String codigo) {
        return acoService.ejecutarAcoAntiguo(codigo);
    }

    @GetMapping("/ejecutar/todaCiudad")
    public String ejecutarAcoTodo() {
        return acoService.ejecutarAcoTodo();
    }
}
