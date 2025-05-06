package com.facco.firsting_spring_app.service;

import com.facco.firsting_spring_app.model.Cliente;
import com.facco.firsting_spring_app.repository.ClienteRepository;
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

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Cliente criarCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setIdade(25);
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("123456789");
        cliente.setLocacaoAtiva(false);
        return cliente;
    }

    @Test
    void salvarCliente_DeveSalvarComSucesso() {
        Cliente cliente = criarCliente();

        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente salvo = clienteService.salvarCliente(cliente);

        assertNotNull(salvo);
        assertEquals("João Silva", salvo.getNome());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void listarClientes_DeveRetornarLista() {
        Cliente cliente = criarCliente();

        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente));

        List<Cliente> lista = clienteService.listarClientes();

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
    }

    @Test
    void buscarClientePorId_Existente_DeveRetornarCliente() {
        Cliente cliente = criarCliente();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> encontrado = clienteService.buscarClientePorId(1L);

        assertTrue(encontrado.isPresent());
        assertEquals("João Silva", encontrado.get().getNome());
    }

    @Test
    void buscarClientePorEmail_Existente_DeveRetornarCliente() {
        Cliente cliente = criarCliente();

        when(clienteRepository.findByEmail("joao@email.com")).thenReturn(cliente);

        Cliente encontrado = clienteService.buscarClientePorEmail("joao@email.com");

        assertNotNull(encontrado);
        assertEquals(25, encontrado.getIdade());
    }

    @Test
    void atualizarCliente_ComDadosValidos_DeveAtualizar() {
        Cliente existente = criarCliente();

        Cliente atualizado = new Cliente();
        atualizado.setNome("João Atualizado");
        atualizado.setIdade(30);
        atualizado.setEmail("atualizado@email.com");
        atualizado.setTelefone("987654321");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(existente);

        Cliente resultado = clienteService.atualizarCliente(1L, atualizado);

        assertNotNull(resultado);
        assertEquals("João Atualizado", resultado.getNome());
        assertEquals(30, resultado.getIdade());
    }

    @Test
    void atualizarCliente_NaoExistente_DeveLancarExcecao() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            clienteService.atualizarCliente(1L, new Cliente());
        });
    }

    @Test
    void excluirCliente_Existente_DeveExcluir() {
        Cliente cliente = criarCliente();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        doNothing().when(clienteRepository).delete(cliente);

        assertDoesNotThrow(() -> clienteService.excluirCliente(1L));
        verify(clienteRepository, times(1)).delete(cliente);
    }

    @Test
    void excluirCliente_NaoExistente_DeveLancarExcecao() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            clienteService.excluirCliente(1L);
        });
    }
}
