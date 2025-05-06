package com.facco.firsting_spring_app.service;

import com.facco.firsting_spring_app.model.*;
import com.facco.firsting_spring_app.repository.LocacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocacaoServiceTest {

    @InjectMocks
    private LocacaoService locacaoService;

    @Mock
    private LocacaoRepository locacaoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private VeiculoService veiculoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Cliente criarClienteValido() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setIdade(25);
        cliente.setLocacaoAtiva(false);
        return cliente;
    }

    private Categoria criarCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Sedan");
        categoria.setLimiteIdade(21);
        categoria.setPrecoBase(new BigDecimal("100.00"));
        return categoria;
    }

    private Veiculo criarVeiculoValido(Categoria categoria) {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setModelo("Civic");
        veiculo.setCategoria(categoria);
        veiculo.setStatus(Veiculo.StatusVeiculo.DISPONIVEL);
        return veiculo;
    }

    private Locacao criarLocacao(Cliente cliente, Veiculo veiculo) {
        Locacao locacao = new Locacao();
        locacao.setCliente(cliente);
        locacao.setVeiculo(veiculo);

        Calendar cal = Calendar.getInstance();
        Date dataInicio = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Date dataFim = cal.getTime();

        locacao.setDataInicio(dataInicio);
        locacao.setDataFim(dataFim);
        return locacao;
    }

    @Test
    void salvarLocacao_ComDadosValidos_DeveSalvarComSucesso() {
        Cliente cliente = criarClienteValido();
        Categoria categoria = criarCategoria();
        Veiculo veiculo = criarVeiculoValido(categoria);
        Locacao locacao = criarLocacao(cliente, veiculo);

        when(clienteService.buscarClientePorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoService.buscarVeiculoPorId(1L)).thenReturn(Optional.of(veiculo));
        when(locacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Locacao resultado = locacaoService.salvarLocacao(locacao);

        assertNotNull(resultado);
        assertEquals(cliente, resultado.getCliente());
        assertEquals(veiculo, resultado.getVeiculo());
        assertEquals(StatusLocacao.ABERTA, resultado.getStatus());
        assertEquals(new BigDecimal("300.00"), resultado.getValorTotal());
        assertEquals(3, resultado.getDias());
        verify(veiculoService).atualizarVeiculo(eq(1L), any());
        verify(clienteService).salvarCliente(any());
        verify(locacaoRepository).save(any());
    }

    @Test
    void salvarLocacao_ClienteNaoExiste_DeveLancarExcecao() {
        Locacao locacao = new Locacao();
        Cliente cliente = new Cliente();
        cliente.setId(99L);
        locacao.setCliente(cliente);

        when(clienteService.buscarClientePorId(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> locacaoService.salvarLocacao(locacao));
        assertEquals("Cliente não encontrado", ex.getMessage());
    }

    @Test
    void salvarLocacao_VeiculoNaoExiste_DeveLancarExcecao() {
        Cliente cliente = criarClienteValido();
        Locacao locacao = new Locacao();
        locacao.setCliente(cliente);
        Veiculo veiculo = new Veiculo();
        veiculo.setId(99L);
        locacao.setVeiculo(veiculo);

        when(clienteService.buscarClientePorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoService.buscarVeiculoPorId(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> locacaoService.salvarLocacao(locacao));
        assertEquals("Veículo não encontrado", ex.getMessage());
    }

    @Test
    void salvarLocacao_ClienteNaoAtendeIdade_DeveLancarExcecao() {
        Cliente cliente = criarClienteValido();
        cliente.setIdade(18); // Idade abaixo da categoria

        Categoria categoria = criarCategoria(); // limite 21 anos
        Veiculo veiculo = criarVeiculoValido(categoria);
        Locacao locacao = criarLocacao(cliente, veiculo);

        when(clienteService.buscarClientePorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoService.buscarVeiculoPorId(1L)).thenReturn(Optional.of(veiculo));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> locacaoService.salvarLocacao(locacao));
        assertEquals("Cliente não atende à idade mínima para esta categoria.", ex.getMessage());
    }

    @Test
    void salvarLocacao_ClienteJaTemLocacaoAtiva_DeveLancarExcecao() {
        Cliente cliente = criarClienteValido();
        cliente.setLocacaoAtiva(true);

        Categoria categoria = criarCategoria();
        Veiculo veiculo = criarVeiculoValido(categoria);
        Locacao locacao = criarLocacao(cliente, veiculo);

        when(clienteService.buscarClientePorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoService.buscarVeiculoPorId(1L)).thenReturn(Optional.of(veiculo));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> locacaoService.salvarLocacao(locacao));
        assertEquals("Cliente já possui uma locação ativa.", ex.getMessage());
    }

    @Test
    void salvarLocacao_VeiculoNaoDisponivel_DeveLancarExcecao() {
        Cliente cliente = criarClienteValido();

        Categoria categoria = criarCategoria();
        Veiculo veiculo = criarVeiculoValido(categoria);
        veiculo.setStatus(Veiculo.StatusVeiculo.ALUGADO); // não disponível

        Locacao locacao = criarLocacao(cliente, veiculo);

        when(clienteService.buscarClientePorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoService.buscarVeiculoPorId(1L)).thenReturn(Optional.of(veiculo));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> locacaoService.salvarLocacao(locacao));
        assertEquals("Veículo não está disponível para locação.", ex.getMessage());
    }

    @Test
    void atualizarLocacao_DeveAtualizarCamposBasicos() {
        Locacao locacaoExistente = new Locacao();
        locacaoExistente.setId(1L);

        Locacao atualizada = new Locacao();
        atualizada.setStatus(StatusLocacao.FINALIZADA);
        atualizada.setMulta(new BigDecimal("50.00"));
        atualizada.setDataFim(new Date());

        when(locacaoRepository.findById(1L)).thenReturn(Optional.of(locacaoExistente));
        when(locacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Locacao resultado = locacaoService.atualizarLocacao(1L, atualizada);

        assertEquals(StatusLocacao.FINALIZADA, resultado.getStatus());
        assertEquals(new BigDecimal("50.00"), resultado.getMulta());
        verify(locacaoRepository).save(any());
    }

    @Test
    void excluirLocacao_DeveRemoverComSucesso() {
        Locacao locacao = new Locacao();
        locacao.setId(1L);

        when(locacaoRepository.findById(1L)).thenReturn(Optional.of(locacao));

        locacaoService.excluirLocacao(1L);

        verify(locacaoRepository).delete(locacao);
    }

    @Test
    void listarLocacoes_DeveRetornarLista() {
        when(locacaoRepository.findAll()).thenReturn(Arrays.asList(new Locacao(), new Locacao()));

        List<Locacao> resultado = locacaoService.listarLocacoes();

        assertEquals(2, resultado.size());
    }

}
