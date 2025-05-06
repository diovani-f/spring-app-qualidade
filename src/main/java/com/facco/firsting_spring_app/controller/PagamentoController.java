package com.facco.firsting_spring_app.controller;

import com.facco.firsting_spring_app.model.Pagamento;
import com.facco.firsting_spring_app.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping
    public ResponseEntity<?> adicionarPagamento(@RequestBody Pagamento pagamento) {
        String erro = validarPagamento(pagamento);
        if (erro != null) {
            return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
        }

        Pagamento pagamentoSalvo = pagamentoService.salvarPagamento(pagamento);
        return new ResponseEntity<>(pagamentoSalvo, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Pagamento> listarPagamentos() {
        return pagamentoService.listarPagamentos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPagamentoPorId(@PathVariable Long id) {
        Optional<Pagamento> pagamento = pagamentoService.buscarPagamentoPorId(id);
        return pagamento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPagamento(@PathVariable Long id, @RequestBody Pagamento pagamento) {
        String erro = validarPagamento(pagamento);
        if (erro != null) {
            return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
        }

        try {
            Pagamento pagamentoAtualizado = pagamentoService.atualizarPagamento(id, pagamento);
            return new ResponseEntity<>(pagamentoAtualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Pagamento não encontrado.", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPagamento(@PathVariable Long id) {
        try {
            pagamentoService.excluirPagamento(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Validação manual de Pagamento
    private String validarPagamento(Pagamento pagamento) {
        if (pagamento.getLocacao() == null || pagamento.getLocacao().getId() == null) {
            return "A locação associada é obrigatória.";
        }

        if (pagamento.getValor() == null || pagamento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            return "O valor do pagamento deve ser maior que zero.";
        }

        if (pagamento.getDataPagamento() == null) {
            return "A data do pagamento é obrigatória.";
        }

        Date hoje = new Date();
        if (pagamento.getDataPagamento().after(hoje)) {
            return "A data do pagamento não pode ser no futuro.";
        }

        return null;
    }
}
