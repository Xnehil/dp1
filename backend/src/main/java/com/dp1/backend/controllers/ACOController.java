package com.dp1.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}
