package com.facco.firsting_spring_app.controller;

import com.facco.firsting_spring_app.model.Categoria;
import com.facco.firsting_spring_app.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Categoria criarCategoriaValida() {
        Categoria categoria = new Categoria();
        categoria.setNome("Sedan");
        categoria.setPrecoBase(BigDecimal.valueOf(100.00));
        categoria.setLimiteIdade(18);
        return categoria;
    }

    @Test
    void adicionarCategoria_Valida_DeveRetornarCreated() {
        Categoria categoria = criarCategoriaValida();
        when(categoriaService.salvarCategoria(any(Categoria.class))).thenReturn(categoria);

        ResponseEntity<?> response = categoriaController.adicionarCategoria(categoria);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void adicionarCategoria_NomeVazio_DeveRetornarBadRequest() {
        Categoria categoria = criarCategoriaValida();
        categoria.setNome("");

        ResponseEntity<?> response = categoriaController.adicionarCategoria(categoria);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("O nome da categoria é obrigatório", response.getBody());
    }

    @Test
    void adicionarCategoria_PrecoBaseNegativo_DeveRetornarBadRequest() {
        Categoria categoria = criarCategoriaValida();
        categoria.setPrecoBase(BigDecimal.valueOf(-50.00));

        ResponseEntity<?> response = categoriaController.adicionarCategoria(categoria);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("O preço base deve ser maior ou igual a 0", response.getBody());
    }

    @Test
    void adicionarCategoria_LimiteIdadeMenor18_DeveRetornarBadRequest() {
        Categoria categoria = criarCategoriaValida();
        categoria.setLimiteIdade(17);

        ResponseEntity<?> response = categoriaController.adicionarCategoria(categoria);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("O limite de idade deve ser no mínimo 18", response.getBody());
    }

    @Test
    void atualizarCategoria_Valida_DeveRetornarOk() {
        Categoria categoria = criarCategoriaValida();
        when(categoriaService.atualizarCategoria(anyLong(), any(Categoria.class))).thenReturn(categoria);

        ResponseEntity<?> response = categoriaController.atualizarCategoria(1L, categoria);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void buscarCategoriaPorId_Existente_DeveRetornarOk() {
        Categoria categoria = criarCategoriaValida();
        when(categoriaService.buscarCategoriaPorId(1L)).thenReturn(Optional.of(categoria));

        ResponseEntity<Categoria> response = categoriaController.buscarCategoriaPorId(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarCategoriaPorId_Inexistente_DeveRetornarNotFound() {
        when(categoriaService.buscarCategoriaPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Categoria> response = categoriaController.buscarCategoriaPorId(1L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void excluirCategoria_Existente_DeveRetornarNoContent() {
        doNothing().when(categoriaService).excluirCategoria(1L);

        ResponseEntity<Void> response = categoriaController.excluirCategoria(1L);

        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void excluirCategoria_Inexistente_DeveRetornarNotFound() {
        doThrow(new IllegalArgumentException("Categoria não encontrada")).when(categoriaService).excluirCategoria(1L);

        ResponseEntity<Void> response = categoriaController.excluirCategoria(1L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void listarCategorias_DeveRetornarLista() {
        when(categoriaService.listarCategorias()).thenReturn(List.of(criarCategoriaValida()));

        List<Categoria> categorias = categoriaController.listarCategorias();

        assertFalse(categorias.isEmpty());
        assertEquals(1, categorias.size());
    }
}
