package com.dp1.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dp1.backend.models.Envio;

import jakarta.transaction.Transactional;

@Repository
@Transactional


public interface EnvioRepository extends JpaRepository<Envio, Integer>{

}

