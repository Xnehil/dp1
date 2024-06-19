package com.dp1.backend.controllers;

import com.dp1.backend.models.Archivo;
import com.dp1.backend.services.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/archivo")
public class ArchivoController {

    @Autowired
    private ArchivoService archivoService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String savedFile = archivoService.saveFile(file);
            return ResponseEntity.ok(savedFile);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("No se pudo cargar el archivo: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        Archivo archivo = archivoService.getFile(id);

        if (archivo == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo.getName() + "\"")
                .body(archivo.getData());
    }
}