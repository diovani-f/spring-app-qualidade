package com.facco.firsting_spring_app.repository;

import com.facco.firsting_spring_app.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
}
