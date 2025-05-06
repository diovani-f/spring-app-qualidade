package com.facco.firsting_spring_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.facco.firsting_spring_app.model.Locacao;
import com.facco.firsting_spring_app.model.Pagamento;
import com.facco.firsting_spring_app.service.PagamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagamentoController.class)
public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagamentoService pagamentoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Pagamento pagamentoValido;

    @BeforeEach
    void setUp() {
        Locacao locacao = new Locacao();
        locacao.setId(1L);

        pagamentoValido = new Pagamento();
        pagamentoValido.setId(1L);
        pagamentoValido.setLocacao(locacao);
        pagamentoValido.setValor(BigDecimal.valueOf(100.00));
        pagamentoValido.setDataPagamento(new Date());
        pagamentoValido.setDesconto(BigDecimal.ZERO);
        pagamentoValido.setMulta(BigDecimal.ZERO);
    }

    // Teste de criação de pagamento válido
    @Test
    void adicionarPagamento_Valido_DeveRetornarCreated() throws Exception {
        when(pagamentoService.salvarPagamento(any(Pagamento.class))).thenReturn(pagamentoValido);

        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamentoValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(pagamentoValido.getId()));
    }

    // Teste locacao nula (regra)
    @Test
    void adicionarPagamento_SemLocacao_DeveRetornarBadRequest() throws Exception {
        pagamentoValido.setLocacao(null);

        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamentoValido)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A locação associada é obrigatória."));
    }

    // Teste valor <= 0 (regra)
    @Test
    void adicionarPagamento_ValorInvalido_DeveRetornarBadRequest() throws Exception {
        pagamentoValido.setValor(BigDecimal.ZERO);

        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamentoValido)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O valor do pagamento deve ser maior que zero."));
    }

    // Teste data nula (regra)
    @Test
    void adicionarPagamento_SemDataPagamento_DeveRetornarBadRequest() throws Exception {
        pagamentoValido.setDataPagamento(null);

        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamentoValido)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A data do pagamento é obrigatória."));
    }

    // Teste data no futuro (regra)
    @Test
    void adicionarPagamento_DataFutura_DeveRetornarBadRequest() throws Exception {
        Date dataFutura = new Date(System.currentTimeMillis() + 86400000); // +1 dia
        pagamentoValido.setDataPagamento(dataFutura);

        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamentoValido)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A data do pagamento não pode ser no futuro."));
    }

    // Listagem de pagamentos
    @Test
    void listarPagamentos_DeveRetornarLista() throws Exception {
        when(pagamentoService.listarPagamentos()).thenReturn(Arrays.asList(pagamentoValido));

        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    // Buscar pagamento por ID existente
    @Test
    void buscarPagamentoPorId_Existente_DeveRetornarPagamento() throws Exception {
        when(pagamentoService.buscarPagamentoPorId(1L)).thenReturn(Optional.of(pagamentoValido));

        mockMvc.perform(get("/pagamentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    // Buscar pagamento por ID inexistente
    @Test
    void buscarPagamentoPorId_Inexistente_DeveRetornarNotFound() throws Exception {
        when(pagamentoService.buscarPagamentoPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pagamentos/99"))
                .andExpect(status().isNotFound());
    }

    // Atualização de pagamento válido
    @Test
    void atualizarPagamento_Valido_DeveRetornarOk() throws Exception {
        when(pagamentoService.atualizarPagamento(eq(1L), any(Pagamento.class))).thenReturn(pagamentoValido);

        mockMvc.perform(put("/pagamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamentoValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pagamentoValido.getId()));
    }

    // Exclusão de pagamento existente
    @Test
    void excluirPagamento_Existente_DeveRetornarNoContent() throws Exception {
        doNothing().when(pagamentoService).excluirPagamento(1L);

        mockMvc.perform(delete("/pagamentos/1"))
                .andExpect(status().isNoContent());
    }

    // Exclusão de pagamento inexistente
    @Test
    void excluirPagamento_Inexistente_DeveRetornarNotFound() throws Exception {
        doThrow(new IllegalArgumentException()).when(pagamentoService).excluirPagamento(99L);

        mockMvc.perform(delete("/pagamentos/99"))
                .andExpect(status().isNotFound());
    }
}
