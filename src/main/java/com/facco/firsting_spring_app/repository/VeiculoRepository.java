package com.facco.firsting_spring_app.repository;

import com.facco.firsting_spring_app.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    List<Veiculo> findByStatus(Veiculo.StatusVeiculo status);

    Optional<Veiculo> findByIdAndStatus(Long id, Veiculo.StatusVeiculo status);

}
