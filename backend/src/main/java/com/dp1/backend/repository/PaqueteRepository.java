package com.dp1.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dp1.backend.models.Paquete;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface PaqueteRepository extends JpaRepository<Paquete, Integer> {
    List<Paquete> findByCodigoEnvio(String codigoEnvio);

}

