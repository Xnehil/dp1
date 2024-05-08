package com.dp1.backend.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.Vuelo;
import com.dp1.backend.repository.VueloRepository;

@Service
public class VueloService {
    @Autowired
    private VueloRepository vueloRepository;

    public VueloService(VueloRepository vueloRepository) {
        this.vueloRepository = vueloRepository;
    }

    public Vuelo createVuelo(Vuelo vuelo)
    {
        try {
            return vueloRepository.save(vuelo);
        } catch (Exception e) {
            return null;
        }
    }

    public Vuelo getVuelo(int id)
    {
        try {
            return vueloRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public Vuelo updateVuelo(Vuelo vuelo){
        try {
            if (vuelo == null)
            {
                return null;
            }
            return vueloRepository.save(vuelo);
        } catch (Exception e) {
            return null;
        }
    }

    public String deleteVuelo(int id){
        try {
            Vuelo vuelo = vueloRepository.findById(id).get();
            if (vuelo != null) {
                vueloRepository.delete(vuelo);
            }
            else {
                return "Vuelo no encontrado";
            }
            return "Vuelo eliminado";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<Vuelo> getVuelos()
    {
        try {
            return (ArrayList<Vuelo>) vueloRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }
}
