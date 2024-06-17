package com.dp1.backend.services;

import com.dp1.backend.models.Archivo;
import com.dp1.backend.repository.ArchivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ArchivoService {

    @Autowired
    private ArchivoRepository archivoRepository;

    public Archivo saveFile(MultipartFile file) throws IOException {
        Archivo archivo = new Archivo();
        archivo.setName(file.getOriginalFilename());
        archivo.setType(file.getContentType());
        archivo.setData(file.getBytes());

        return archivoRepository.save(archivo);
    }

    public Archivo getFile(Long id) {
        return archivoRepository.findById(id).orElse(null);
    }
}
