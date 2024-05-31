package com.dp1.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dp1.backend.models.RutaPosible;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface RutaPosibleRepository extends JpaRepository<RutaPosible, Integer>{

}