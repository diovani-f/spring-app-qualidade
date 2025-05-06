package com.facco.firsting_spring_app.controller;

import com.facco.firsting_spring_app.model.Devolucao;
import com.facco.firsting_spring_app.service.DevolucaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/devolucoes")
public class DevolucaoController {

    @Autowired
    private DevolucaoService devolucaoService;

    @PostMapping("/{locacaoId}")
    public ResponseEntity<Devolucao> registrar(@PathVariable Long locacaoId,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dataDevolucao) {
        try {
            Devolucao devolucao = devolucaoService.registrarDevolucao(locacaoId, dataDevolucao);
            return ResponseEntity.ok(devolucao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Devolucao> buscar(@PathVariable Long id) {
        Optional<Devolucao> devolucao = devolucaoService.buscarPorId(id);
        return devolucao.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        devolucaoService.excluirDevolucao(id);
        return ResponseEntity.noContent().build();
    }
}
