package com.facco.firsting_spring_app.service;

import com.facco.firsting_spring_app.model.Pagamento;
import com.facco.firsting_spring_app.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    public List<Pagamento> listarPagamentos() {
        return pagamentoRepository.findAll();
    }

    public Optional<Pagamento> buscarPagamentoPorId(Long id) {
        return pagamentoRepository.findById(id);
    }

    @Transactional
    public Pagamento salvarPagamento(Pagamento pagamento) {
        // Calcular o valor total do pagamento com desconto e multa
        BigDecimal valorFinal = pagamento.getValor().subtract(pagamento.getDesconto()).add(pagamento.getMulta());
        pagamento.setValor(valorFinal);
        return pagamentoRepository.save(pagamento);
    }

    @Transactional
    public Pagamento atualizarPagamento(Long id, Pagamento pagamentoAtualizado) {
        Pagamento pagamentoExistente = pagamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado"));

        pagamentoExistente.setValor(pagamentoAtualizado.getValor());
        pagamentoExistente.setDesconto(pagamentoAtualizado.getDesconto());
        pagamentoExistente.setMulta(pagamentoAtualizado.getMulta());
        pagamentoExistente.setDataPagamento(pagamentoAtualizado.getDataPagamento());

        return pagamentoRepository.save(pagamentoExistente);
    }

    @Transactional
    public void excluirPagamento(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado"));
        pagamentoRepository.delete(pagamento);
    }
}
