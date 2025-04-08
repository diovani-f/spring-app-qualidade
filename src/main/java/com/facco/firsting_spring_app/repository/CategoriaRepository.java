package com.facco.firsting_spring_app.repository;

import com.facco.firsting_spring_app.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
