package com.facco.firsting_spring_app.controller;

import com.facco.firsting_spring_app.model.Locacao;
import com.facco.firsting_spring_app.repository.ClienteRepository;
import com.facco.firsting_spring_app.repository.VeiculoRepository;
import com.facco.firsting_spring_app.repository.LocacaoRepository;
import com.facco.firsting_spring_app.model.Cliente;
import com.facco.firsting_spring_app.model.Veiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/locacoes")
public class LocacaoController {

    @Autowired
    private LocacaoRepository locacaoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @GetMapping
    public List<Locacao> listarTodos() {
        return locacaoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Locacao> buscarPorId(@PathVariable Long id) {
        return locacaoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Locacao locacao) {
        Optional<Cliente> cliente = clienteRepository.findById(locacao.getCliente().getId());
        Optional<Veiculo> veiculo = veiculoRepository.findById(locacao.getVeiculo().getId());

        if (cliente.isPresent() && veiculo.isPresent()) {
            locacao.setCliente(cliente.get());
            locacao.setVeiculo(veiculo.get());
            return ResponseEntity.ok(locacaoRepository.save(locacao));
        }

        return ResponseEntity.badRequest().body("Cliente ou veículo inválido");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Locacao novaLocacao) {
        Optional<Locacao> existente = locacaoRepository.findById(id);
        if (existente.isPresent()) {
            Locacao locacao = existente.get();

            Optional<Cliente> cliente = clienteRepository.findById(novaLocacao.getCliente().getId());
            Optional<Veiculo> veiculo = veiculoRepository.findById(novaLocacao.getVeiculo().getId());

            if (cliente.isEmpty() || veiculo.isEmpty()) {
                return ResponseEntity.badRequest().body("Cliente ou veículo inválido");
            }

            locacao.setCliente(cliente.get());
            locacao.setVeiculo(veiculo.get());
            locacao.setDataRetirada(novaLocacao.getDataRetirada());
            locacao.setDataPrevistaDevolucao(novaLocacao.getDataPrevistaDevolucao());
            locacao.setDataRealDevolucao(novaLocacao.getDataRealDevolucao());
            locacao.setKmInicial(novaLocacao.getKmInicial());
            locacao.setKmFinal(novaLocacao.getKmFinal());
            locacao.setValorTotal(novaLocacao.getValorTotal());

            return ResponseEntity.ok(locacaoRepository.save(locacao));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (locacaoRepository.existsById(id)) {
            locacaoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
