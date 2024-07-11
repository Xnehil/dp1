package com.dp1.backend.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    @CrossOrigin(origins = "*")
    @GetMapping("/ejecutar/todaCiudad")
    public ResponseEntity<String> ejecutarAcoTodo() {

        ZonedDateTime ahora = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime haceDosDias = ahora.minusDays(2);
        ahora=ahora.plusWeeks(5);


        System.out.println("Fecha inicio: " + haceDosDias);
        System.out.println("Fecha fin: " + ahora);
        // return acoService.ejecutarAcoTodo(haceDosDias, ahora);

        try {
            String ejecutar=acoService.ejecutarAcoTodo(haceDosDias, ahora);
            return ResponseEntity.ok(ejecutar);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("No se pudo ejecutar el ACO: " + e.getMessage());
        }
    }
}
