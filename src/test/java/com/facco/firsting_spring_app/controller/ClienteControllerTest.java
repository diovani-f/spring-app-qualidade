package com.facco.firsting_spring_app.controller;

import com.facco.firsting_spring_app.model.Cliente;
import com.facco.firsting_spring_app.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Cliente criarClienteValido() {
        Cliente cliente = new Cliente();
        cliente.setNome("Carlos Silva");
        cliente.setIdade(25);
        cliente.setEmail("carlos@email.com");
        cliente.setTelefone("123456789");
        return cliente;
    }

    private Cliente criarClienteMenorDeIdade() {
        Cliente cliente = new Cliente();
        cliente.setNome("Junior Silva");
        cliente.setIdade(16); // idade inválida
        cliente.setEmail("junior@email.com");
        cliente.setTelefone("987654321");
        return cliente;
    }

    @Test
    void adicionarCliente_IdadeValida_DeveRetornarCreated() {
        Cliente cliente = criarClienteValido();

        when(clienteService.salvarCliente(any(Cliente.class))).thenReturn(cliente);

        ResponseEntity<?> response = clienteController.adicionarCliente(cliente);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Cliente);
        assertEquals("Carlos Silva", ((Cliente) response.getBody()).getNome());
    }

    @Test
    void adicionarCliente_IdadeInvalida_DeveRetornarBadRequest() {
        Cliente cliente = criarClienteMenorDeIdade();

        ResponseEntity<?> response = clienteController.adicionarCliente(cliente);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Idade deve ser maior ou igual a 18.", response.getBody());
    }

}
