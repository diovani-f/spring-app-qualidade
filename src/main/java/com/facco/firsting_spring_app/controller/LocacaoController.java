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
import java.time.LocalDate;
import java.time.Period;
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
    public ResponseEntity<?> criar(@RequestBody com.facco.firsting_spring_app.dto.LocacaoDTO dto) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(dto.getCliente());
        Optional<Veiculo> veiculoOpt = veiculoRepository.findById(dto.getVeiculo());

        if (clienteOpt.isPresent() && veiculoOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();

            if (cliente.getDataNascimento() == null) {
                return ResponseEntity.badRequest().body("Data de nascimento do cliente não informada.");
            }

            int idade = Period.between(cliente.getDataNascimento(), LocalDate.now()).getYears();
            if (idade < 18) {
                return ResponseEntity.badRequest().body("Cliente deve ter pelo menos 18 anos para alugar um veículo.");
            }

            if (locacaoRepository.clientePossuiLocacaoAtiva(cliente.getId())) {
                return ResponseEntity.badRequest().body("Cliente já possui uma locação ativa.");
            }

            long devolucoesAtrasadas = locacaoRepository.contarDevolucoesAtrasadas(cliente.getId());
            if (devolucoesAtrasadas > 4) {
                return ResponseEntity.badRequest().body("Cliente bloqueado por mais de 4 devoluções atrasadas.");
            }

            // Criar e preencher a entidade Locacao
            Locacao locacao = new Locacao();
            locacao.setCliente(cliente);
            locacao.setVeiculo(veiculoOpt.get());
            locacao.setDataRetirada(dto.getDataRetirada());
            locacao.setDataPrevistaDevolucao(dto.getDataPrevistaDevolucao());
            locacao.setKmInicial(dto.getKmInicial());

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
