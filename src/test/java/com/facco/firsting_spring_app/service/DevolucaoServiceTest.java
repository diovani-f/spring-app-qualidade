package com.facco.firsting_spring_app.service;

import com.facco.firsting_spring_app.model.*;
import com.facco.firsting_spring_app.repository.DevolucaoRepository;
import com.facco.firsting_spring_app.repository.LocacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DevolucaoServiceTest {

    @Mock
    private DevolucaoRepository devolucaoRepository;

    @Mock
    private LocacaoRepository locacaoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private VeiculoService veiculoService;

    @InjectMocks
    private DevolucaoService devolucaoService;

    private Locacao locacao;
    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setLocacaoAtiva(true);

        veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setStatus(Veiculo.StatusVeiculo.ALUGADO);

        locacao = new Locacao();
        locacao.setId(1L);
        locacao.setCliente(cliente);
        locacao.setVeiculo(veiculo);
        locacao.setStatus(StatusLocacao.ABERTA);
        locacao.setDataInicio(new Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000)); // 10 dias atrás
        locacao.setDataFim(new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)); // 2 dias atrás
    }

    @Test
    void registrarDevolucao_DeveFinalizarLocacaoAtualizarClienteVeiculo() {
        when(locacaoRepository.findById(1L)).thenReturn(Optional.of(locacao));
        when(devolucaoRepository.save(any(Devolucao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Devolucao devolucao = devolucaoService.registrarDevolucao(1L, new Date());

        assertNotNull(devolucao);
        assertEquals(2, devolucao.getAtraso()); // 2 dias de atraso
        assertEquals(BigDecimal.valueOf(100), devolucao.getMulta()); // 2 dias * R$50

        verify(locacaoRepository, times(1)).save(any(Locacao.class));
        verify(clienteService, times(1)).salvarCliente(any(Cliente.class));
        verify(veiculoService, times(1)).atualizarVeiculo(eq(veiculo.getId()), any(Veiculo.class));
        verify(devolucaoRepository, times(1)).save(any(Devolucao.class));
    }

    @Test
    void registrarDevolucao_LocacaoNaoEncontrada_DeveLancarExcecao() {
        when(locacaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            devolucaoService.registrarDevolucao(1L, new Date());
        });
    }

    @Test
    void registrarDevolucao_LocacaoNaoAberta_DeveLancarExcecao() {
        locacao.setStatus(StatusLocacao.FINALIZADA);
        when(locacaoRepository.findById(1L)).thenReturn(Optional.of(locacao));

        assertThrows(IllegalStateException.class, () -> {
            devolucaoService.registrarDevolucao(1L, new Date());
        });
    }

    @Test
    void buscarDevolucao_Existente_DeveRetornar() {
        Devolucao devolucao = new Devolucao();
        devolucao.setId(1L);
        when(devolucaoRepository.findById(1L)).thenReturn(Optional.of(devolucao));

        Optional<Devolucao> resultado = devolucaoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    void buscarDevolucao_Inexistente_DeveRetornarVazio() {
        when(devolucaoRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Devolucao> resultado = devolucaoService.buscarPorId(1L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void excluirDevolucao_DeveExecutarSemErros() {
        doNothing().when(devolucaoRepository).deleteById(1L);

        devolucaoService.excluirDevolucao(1L);

        verify(devolucaoRepository, times(1)).deleteById(1L);
    }
}
