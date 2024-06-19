package com.dp1.backend.services;

import com.dp1.backend.models.Archivo;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.repository.ArchivoRepository;
import com.dp1.backend.utils.FuncionesLectura;
import com.dp1.backend.services.DatosEnMemoriaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.nio.file.Path;

@Service
public class ArchivoService {

    @Autowired
    private ArchivoRepository archivoRepository;

    @Autowired
    private EnvioService envioService;

    @Autowired
    private PaqueteService paqueteService;

    @Autowired
    private DatosEnMemoriaService datosenmemoriaService;

    public Archivo saveFile(MultipartFile file) throws IOException {

        // 1. Guardar archivo en el servidor en cierta ruta
        String uploadDir = "/path/to/upload/directory"; // Reemplaza con la ruta donde deseas guardar los archivos
        Path uploadPath = Paths.get(uploadDir);

        // Crear el directorio si no existe
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        // 2. Pasar la ruta del archivo a FuncionesLectura.leerEnviosGuardarBD
        HashMap<String, Aeropuerto> aeropuertos = datosenmemoriaService.getAeropuertos();
        FuncionesLectura.leerEnviosGuardarBD(filePath.toString(), aeropuertos ,10000, envioService, paqueteService);

        // 3. Borrar el archivo del servidor después de procesarlo
        Files.delete(filePath);

        // Guardar detalles del archivo en la base de datos
        Archivo archivo = new Archivo();
        archivo.setName(fileName);
        archivo.setType(file.getContentType());
        archivo.setData(file.getBytes());

        return archivoRepository.save(archivo);

    }

    public Archivo getFile(Long id) {
        return archivoRepository.findById(id).orElse(null);
    }
}
