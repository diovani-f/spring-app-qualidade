package com.facco.firsting_spring_app.repository;

import com.facco.firsting_spring_app.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
