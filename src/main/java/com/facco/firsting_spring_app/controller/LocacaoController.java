package com.facco.firsting_spring_app.controller;

import com.facco.firsting_spring_app.model.Locacao;
import com.facco.firsting_spring_app.service.LocacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/locacoes")
public class LocacaoController {

    @Autowired
    private LocacaoService locacaoService;

    @PostMapping
    public ResponseEntity<?> adicionarLocacao(@RequestBody Locacao locacao) {
        String erro = validarLocacao(locacao);
        if (erro != null) {
            return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
        }

        try {
            Locacao locacaoSalva = locacaoService.salvarLocacao(locacao);
            return new ResponseEntity<>(locacaoSalva, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public List<Locacao> listarLocacoes() {
        return locacaoService.listarLocacoes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Locacao> buscarLocacaoPorId(@PathVariable Long id) {
        Optional<Locacao> locacao = locacaoService.buscarLocacaoPorId(id);
        return locacao.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarLocacao(@PathVariable Long id, @RequestBody Locacao locacao) {
        String erro = validarLocacao(locacao);
        if (erro != null) {
            return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
        }

        try {
            Locacao locacaoAtualizada = locacaoService.atualizarLocacao(id, locacao);
            return new ResponseEntity<>(locacaoAtualizada, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Locação não encontrada.", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirLocacao(@PathVariable Long id) {
        try {
            locacaoService.excluirLocacao(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private String validarLocacao(Locacao locacao) {
        if (locacao.getCliente() == null || locacao.getCliente().getId() == null) {
            return "Cliente é obrigatório.";
        }

        if (locacao.getVeiculo() == null || locacao.getVeiculo().getId() == null) {
            return "Veículo é obrigatório.";
        }

        if (locacao.getDataInicio() == null) {
            return "Data de início é obrigatória.";
        }

        if (locacao.getDataFim() == null) {
            return "Data de fim é obrigatória.";
        }

        if (locacao.getDataInicio().after(locacao.getDataFim())) {
            return "Data de início não pode ser depois da data de fim.";
        }

        return null;
    }
}
