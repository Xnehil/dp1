package com.dp1.backend.repository;


import com.dp1.backend.models.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
}
