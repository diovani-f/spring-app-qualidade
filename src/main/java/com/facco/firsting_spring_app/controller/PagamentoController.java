package com.facco.firsting_spring_app.controller;

import com.facco.firsting_spring_app.model.Pagamento;
import com.facco.firsting_spring_app.repository.PagamentoRepository;
import com.facco.firsting_spring_app.repository.LocacaoRepository;
import com.facco.firsting_spring_app.model.Locacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private LocacaoRepository locacaoRepository;

    @GetMapping
    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPorId(@PathVariable Long id) {
        return pagamentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Pagamento pagamento) {
        Optional<Locacao> locacao = locacaoRepository.findById(pagamento.getLocacao().getId());
        if (locacao.isPresent()) {
            pagamento.setLocacao(locacao.get());
            Pagamento salvo = pagamentoRepository.save(pagamento);
            return ResponseEntity.ok(salvo);
        } else {
            return ResponseEntity.badRequest().body("Locação não encontrada");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Pagamento novoPagamento) {
        Optional<Pagamento> existente = pagamentoRepository.findById(id);
        if (existente.isPresent()) {
            Pagamento pagamento = existente.get();
            pagamento.setValor(novoPagamento.getValor());
            pagamento.setFormaPagamento(novoPagamento.getFormaPagamento());
            pagamento.setStatus(novoPagamento.getStatus());

            Optional<Locacao> locacao = locacaoRepository.findById(novoPagamento.getLocacao().getId());
            if (locacao.isPresent()) {
                pagamento.setLocacao(locacao.get());
                return ResponseEntity.ok(pagamentoRepository.save(pagamento));
            } else {
                return ResponseEntity.badRequest().body("Locação inválida");
            }

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (pagamentoRepository.existsById(id)) {
            pagamentoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
