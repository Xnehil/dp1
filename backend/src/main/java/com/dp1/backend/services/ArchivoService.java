package com.dp1.backend.services;

import com.dp1.backend.models.Archivo;
import com.dp1.backend.models.Envio;
import com.dp1.backend.models.Aeropuerto;
import com.dp1.backend.repository.ArchivoRepository;
import com.dp1.backend.utils.FuncionesLectura;
import com.dp1.backend.services.DatosEnMemoriaService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private final static Logger logger = LogManager.getLogger(ArchivoService.class);

    private String workingDirectory = System.getProperty("user.dir");

    public ArchivoService(ArchivoRepository archivoRepository) {
        this.archivoRepository = archivoRepository;
        if (workingDirectory.trim().equals("/")) {
            workingDirectory = "/home/inf226.982.2b/";
        } else {
            workingDirectory = "";
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = null;
        try {
            // 1. Guardar archivo en el servidor en cierta ruta
            String uploadDir = "temporales/"; // Reemplaza con la ruta donde deseas guardar los archivos
            uploadPath = Paths.get(workingDirectory + uploadDir);

            // Crear el directorio si no existe
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Directorio creado: " + uploadPath);
            }

            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            logger.info("Archivo guardado en: " + filePath);
            // 2. Pasar la ruta del archivo a FuncionesLectura.leerEnviosGuardarBD
            // 2. Pasar la ruta del archivo a FuncionesLectura.leerEnviosGuardarBD
            HashMap<String, Aeropuerto> aeropuertos = datosenmemoriaService.getAeropuertos();
            FuncionesLectura.leerEnviosGuardarBD(filePath.toString(), aeropuertos, 10000, envioService, paqueteService);
            // FuncionesLectura.leerEnviosGuardarBD(filePath.toString(), envioService,
            // paqueteService);

            // 3. Borrar el archivo del servidor después de procesarlo
            // Files.delete(filePath);

            // Guardar detalles del archivo en la base de datos
            Archivo archivo = new Archivo();
            archivo.setName(fileName);
            archivo.setType(file.getContentType());
            archivo.setData(file.getBytes());

            return "Archivo procesado exitosamente: ";
        } catch (IOException e) {
            return "No se pudo cargar el archivo: " + e.getMessage();
        }
        // return archivoRepository.save(archivo);
    }

    public Archivo getFile(Long id) {
        return archivoRepository.findById(id).orElse(null);
    }
}
