package com.dp1.backend.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Paquete;
import com.dp1.backend.repository.PaqueteRepository;

@Service
public class PaqueteService {
    @Autowired
    private PaqueteRepository paqueteRepository;

    public Paquete createPaquete(Paquete paquete)
    {
        try {
            return paqueteRepository.save(paquete);
        } catch (Exception e) {
            return null;
        }
    }

    public Paquete getPaquete(int id)
    {
        try {
            return paqueteRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public Paquete updatePaquete(Paquete paquete){
        try {
            if (paquete == null)
            {
                return null;
            }
            return paqueteRepository.save(paquete);
        } catch (Exception e) {
            return null;
        }
    }

    public String deletePaquete(int id){
        try {
            Paquete paquete = paqueteRepository.findById(id).get();
            if (paquete != null) {
                paqueteRepository.delete(paquete);
            }
            else {
                return "Paquete no encontrado";
            }
            return "Paquete eliminado";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<Paquete> getPaquetes()
    {
        return (ArrayList<Paquete>) paqueteRepository.findAll();
    }
}