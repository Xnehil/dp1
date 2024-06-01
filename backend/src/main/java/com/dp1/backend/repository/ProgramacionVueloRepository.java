package com.dp1.backend.repository;

import org.springframework.stereotype.Repository;

import com.dp1.backend.models.ProgramacionVuelo;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface ProgramacionVueloRepository extends JpaRepository<ProgramacionVuelo, Integer> {
    
}
