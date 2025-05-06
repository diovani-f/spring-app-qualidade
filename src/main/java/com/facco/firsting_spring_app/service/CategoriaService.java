package com.facco.firsting_spring_app.service;

import com.facco.firsting_spring_app.model.Categoria;
import com.facco.firsting_spring_app.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    @Transactional
    public Categoria salvarCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria atualizarCategoria(Long id, Categoria categoriaAtualizada) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        categoriaExistente.setNome(categoriaAtualizada.getNome());
        categoriaExistente.setPrecoBase(categoriaAtualizada.getPrecoBase());
        categoriaExistente.setLimiteIdade(categoriaAtualizada.getLimiteIdade());

        return categoriaRepository.save(categoriaExistente);
    }

    @Transactional
    public void excluirCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        categoriaRepository.delete(categoria);
    }

}
