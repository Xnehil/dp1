package com.dp1.backend.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
        // ZoneId zoneId = ZoneId.of("GMT-5");

        // ZonedDateTime startOfDay = ZonedDateTime.now(zoneId).with(LocalTime.MIN);
        // ZonedDateTime endOfDay = ZonedDateTime.now(zoneId).with(LocalTime.MAX);

        ZoneId zoneId = ZoneId.of("GMT-5");

        // Convertir la fecha a LocalDate
        LocalDate localDate = LocalDate.parse("2024-07-04");

        // Crear los objetos ZonedDateTime para el inicio y fin del día especificado
        ZonedDateTime startOfDay = localDate.atStartOfDay(zoneId);
        ZonedDateTime endOfDay = localDate.atTime(LocalTime.MAX).atZone(zoneId);

        System.out.println("Fecha inicio: " + startOfDay);
        System.out.println("Fecha fin: " + endOfDay);
        return acoService.ejecutarAcoTodo(startOfDay, endOfDay);
    }
}
