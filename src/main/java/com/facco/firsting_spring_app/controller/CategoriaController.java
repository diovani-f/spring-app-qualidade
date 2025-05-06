package com.facco.firsting_spring_app.controller;

import com.facco.firsting_spring_app.model.Categoria;
import com.facco.firsting_spring_app.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<?> adicionarCategoria(@RequestBody Categoria categoria) {
        String erro = validarCategoria(categoria);
        if (erro != null) {
            return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
        }

        Categoria categoriaSalva = categoriaService.salvarCategoria(categoria);
        return new ResponseEntity<>(categoriaSalva, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
        String erro = validarCategoria(categoria);
        if (erro != null) {
            return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
        }

        try {
            Categoria categoriaAtualizada = categoriaService.atualizarCategoria(id, categoria);
            return new ResponseEntity<>(categoriaAtualizada, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Categoria não encontrada", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaService.listarCategorias();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarCategoriaPorId(@PathVariable Long id) {
        Optional<Categoria> categoria = categoriaService.buscarCategoriaPorId(id);
        return categoria.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCategoria(@PathVariable Long id) {
        try {
            categoriaService.excluirCategoria(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Validação manual da entidade Categoria
    private String validarCategoria(Categoria categoria) {
        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            return "O nome da categoria é obrigatório";
        }
        if (categoria.getPrecoBase() == null || categoria.getPrecoBase().compareTo(BigDecimal.ZERO) < 0) {
            return "O preço base deve ser maior ou igual a 0";
        }
        if (categoria.getLimiteIdade() == null || categoria.getLimiteIdade() < 18) {
            return "O limite de idade deve ser no mínimo 18";
        }
        return null; // tudo certo
    }
}
