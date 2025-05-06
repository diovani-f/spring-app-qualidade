package com.facco.firsting_spring_app.repository;

import com.facco.firsting_spring_app.model.Locacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocacaoRepository extends JpaRepository<Locacao, Long> {
    // Métodos customizados podem ser adicionados aqui
}
