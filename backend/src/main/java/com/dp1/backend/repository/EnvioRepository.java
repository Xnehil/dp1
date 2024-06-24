package com.dp1.backend.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dp1.backend.models.Envio;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface EnvioRepository extends JpaRepository<Envio, Integer>{
        // Fetch all Envio records ordered by hora_salida in descending order
    List<Envio> findAllByOrderByFechaHoraSalidaDesc();

    // Fetch a specific number of recent Envio records
    // This method uses Pageable to limit the results to a specific number
    List<Envio> findTopByOrderByFechaHoraSalidaDesc(Pageable pageable);

    // Example using @Query to achieve the same
    @Query("SELECT e FROM Envio e ORDER BY e.fechaHoraSalida DESC")
    List<Envio> findMostRecentEnvios(Pageable pageable);

    // Encontrar envios por fecha de salida
    List<Envio> findByFechaHoraSalidaBefore(ZonedDateTime fechaHoraSalida);
    List<Envio> findByFechaHoraSalidaAfter(ZonedDateTime fechaHoraSalida);

    // Encontrar envios por codigo de envio
    Envio findByCodigoEnvio(String codigoEnvio);
    
}

