package com.dp1.backend.services;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp1.backend.models.ProgramacionVuelo;
import com.dp1.backend.models.Vuelo;
import com.dp1.backend.repository.ProgramacionVueloRepository;
import com.dp1.backend.repository.VueloRepository;

@Service
public class ProgramacionVueloService {
    private static final Logger logger = LogManager.getLogger(VueloService.class);

    @Autowired
    private ProgramacionVueloRepository programacionVueloRepository;

    public ProgramacionVuelo createProgramacionVuelo(ProgramacionVuelo pv){
        try {
            return programacionVueloRepository.save(pv);
        } catch (Exception e) {
            return null;
        }
    }

    public ProgramacionVuelo getProgramacionVuelo(int id) {
        try {
            return programacionVueloRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public ProgramacionVuelo updateProgramacionVuelo(ProgramacionVuelo pv) {
        try {
            if (pv == null) {
                return null;
            }
            return programacionVueloRepository.save(pv);
        } catch (Exception e) {
            return null;
        }
    }

    public String deleteProgramacionVuelo(int id) {
        try {
            ProgramacionVuelo vuelo = programacionVueloRepository.findById(id).get();
            if (vuelo != null) {
                programacionVueloRepository.delete(vuelo);
            } else {
                return "Vuelo no encontrado";
            }
            return "Vuelo eliminado";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public ArrayList<ProgramacionVuelo> getVuelos() {
        try {
            return (ArrayList<ProgramacionVuelo>) programacionVueloRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }
}
