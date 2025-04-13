package com.facco.firsting_spring_app.controller;

import com.facco.firsting_spring_app.model.Veiculo;
import com.facco.firsting_spring_app.repository.LocacaoRepository;
import com.facco.firsting_spring_app.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoRepository veiculoRepository;
    private LocacaoRepository locacaoRepository;


    // Listar todos
    @GetMapping
    public List<Veiculo> listarTodos() {
        return veiculoRepository.findAll();
    }

    // Criar novo
    @PostMapping
    public Veiculo criar(@RequestBody Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> buscarPorId(@PathVariable Long id) {
        Optional<Veiculo> veiculo = veiculoRepository.findById(id);
        return veiculo.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<Veiculo> atualizar(@PathVariable Long id, @RequestBody Veiculo novoVeiculo) {
        Optional<Veiculo> veiculoExistente = veiculoRepository.findById(id);
        if (veiculoExistente.isPresent()) {
            Veiculo veiculo = veiculoExistente.get();

            // REGRA 3: Bloqueia alteração de categoria se já houve locação
            boolean jaAlugado = locacaoRepository.existsByVeiculoId(id);
            if (jaAlugado && !veiculo.getCategoria().equals(novoVeiculo.getCategoria())) {
                return ResponseEntity.badRequest().build(); // pode retornar uma mensagem se preferir
            }

            veiculo.setModelo(novoVeiculo.getModelo());
            veiculo.setMarca(novoVeiculo.getMarca());
            veiculo.setPlaca(novoVeiculo.getPlaca());
            veiculo.setAno(novoVeiculo.getAno());
            veiculo.setQuilometragem(novoVeiculo.getQuilometragem());
            veiculo.setCor(novoVeiculo.getCor());
            veiculo.setStatus(novoVeiculo.getStatus());
            veiculo.setCategoria(novoVeiculo.getCategoria());

            Veiculo atualizado = veiculoRepository.save(veiculo);
            return ResponseEntity.ok(atualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (veiculoRepository.existsById(id)) {
            veiculoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
