package com.facco.firsting_spring_app.controller;

import com.facco.firsting_spring_app.model.Veiculo;
import com.facco.firsting_spring_app.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @PostMapping
    public ResponseEntity<?> adicionarVeiculo(@RequestBody Veiculo veiculo) {
        String erro = validarVeiculo(veiculo);
        if (erro != null) {
            return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
        }

        Veiculo veiculoSalvo = veiculoService.salvarVeiculo(veiculo);
        return new ResponseEntity<>(veiculoSalvo, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Veiculo> listarVeiculos() {
        return veiculoService.listarVeiculos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> buscarVeiculoPorId(@PathVariable Long id) {
        Optional<Veiculo> veiculo = veiculoService.buscarVeiculoPorId(id);
        return veiculo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarVeiculo(@PathVariable Long id, @RequestBody Veiculo veiculo) {
        String erro = validarVeiculo(veiculo);
        if (erro != null) {
            return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
        }

        try {
            Veiculo veiculoAtualizado = veiculoService.atualizarVeiculo(id, veiculo);
            return new ResponseEntity<>(veiculoAtualizado, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>("Erro ao atualizar veículo: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirVeiculo(@PathVariable Long id) {
        try {
            veiculoService.excluirVeiculo(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Validação manual da entidade Veiculo
    private String validarVeiculo(Veiculo veiculo) {
        if (veiculo.getModelo() == null || veiculo.getModelo().isBlank()) {
            return "O modelo do veículo é obrigatório";
        }
        if (veiculo.getCategoria() == null || veiculo.getCategoria().getId() == null) {
            return "A categoria do veículo é obrigatória";
        }
        if (veiculo.getQuilometragem() == null || veiculo.getQuilometragem() < 0) {
            return "A quilometragem deve ser maior ou igual a 0";
        }
        if (veiculo.getStatus() == null) {
            return "O status do veículo é obrigatório";
        }
        return null;
    }
}
