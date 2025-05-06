package com.facco.firsting_spring_app.service;

import com.facco.firsting_spring_app.model.Categoria;
import com.facco.firsting_spring_app.model.Veiculo;
import com.facco.firsting_spring_app.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Veiculo criarVeiculo(Veiculo.StatusVeiculo status) {
        Categoria categoria = new Categoria();
        categoria.setId(1L);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setModelo("Civic");
        veiculo.setCategoria(categoria);
        veiculo.setQuilometragem(10000);
        veiculo.setStatus(status);

        return veiculo;
    }

    @Test
    void salvarVeiculo_ComStatusDisponivel_DeveSalvarComSucesso() {
        Veiculo veiculo = criarVeiculo(Veiculo.StatusVeiculo.DISPONIVEL);

        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        Veiculo salvo = veiculoService.salvarVeiculo(veiculo);

        assertNotNull(salvo);
        verify(veiculoRepository, times(1)).save(veiculo);
    }

    @Test
    void salvarVeiculo_ComStatusIndisponivel_DeveLancarExcecao() {
        Veiculo veiculo = criarVeiculo(Veiculo.StatusVeiculo.INDISPONIVEL);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            veiculoService.salvarVeiculo(veiculo);
        });

        assertEquals("Veículo não disponível para locação.", exception.getMessage());
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void listarVeiculos_DeveRetornarLista() {
        Veiculo veiculo = criarVeiculo(Veiculo.StatusVeiculo.DISPONIVEL);

        when(veiculoRepository.findAll()).thenReturn(Arrays.asList(veiculo));

        List<Veiculo> lista = veiculoService.listarVeiculos();

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
    }

    @Test
    void buscarVeiculoPorId_Existente_DeveRetornarVeiculo() {
        Veiculo veiculo = criarVeiculo(Veiculo.StatusVeiculo.DISPONIVEL);

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));

        Optional<Veiculo> encontrado = veiculoService.buscarVeiculoPorId(1L);

        assertTrue(encontrado.isPresent());
        assertEquals("Civic", encontrado.get().getModelo());
    }

    @Test
    void atualizarVeiculo_AlterandoCategoria_DeVeiculoAlugado_DeveLancarExcecao() {
        Veiculo existente = criarVeiculo(Veiculo.StatusVeiculo.ALUGADO);

        Categoria novaCategoria = new Categoria();
        novaCategoria.setId(2L);

        Veiculo atualizado = criarVeiculo(Veiculo.StatusVeiculo.ALUGADO);
        atualizado.setCategoria(novaCategoria); // Tentando mudar a categoria

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(existente));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            veiculoService.atualizarVeiculo(1L, atualizado);
        });

        assertEquals("Não é permitido alterar a categoria de um veículo alugado.", exception.getMessage());
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void excluirVeiculo_Existente_DeveExcluirComSucesso() {
        Veiculo veiculo = criarVeiculo(Veiculo.StatusVeiculo.DISPONIVEL);

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        doNothing().when(veiculoRepository).delete(veiculo);

        assertDoesNotThrow(() -> veiculoService.excluirVeiculo(1L));
        verify(veiculoRepository, times(1)).delete(veiculo);
    }

    @Test
    void excluirVeiculo_Inexistente_DeveLancarExcecao() {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> veiculoService.excluirVeiculo(1L));
    }
}
