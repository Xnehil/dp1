package com.dp1.backend.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Envio;
import com.dp1.backend.repository.EnvioRepository;

@Service
public class EnvioService {
    @Autowired
    private EnvioRepository envioRepository;

    public Envio createEnvio(Envio envio)
    {
        try {
            return envioRepository.save(envio);
        } catch (Exception e) {
            return null;
        }
    }

    public Envio getEnvio(int id)
    {
        try {
            return envioRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public Envio updateEnvio(Envio envio){
        try {
            if (envio == null)
            {
                return null;
            }
            return envioRepository.save(envio);
        } catch (Exception e) {
            return null;
        }
    }

    public String deleteEnvio(int id){
        try {
            Envio envio = envioRepository.findById(id).get();
            if (envio != null) {
                envioRepository.delete(envio);
            }
            else {
                return "Envio no encontrado";
            }
            return "Envio eliminado";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<Envio> getEnvios()
    {
        return (ArrayList<Envio>) envioRepository.findAll();
    }
}
