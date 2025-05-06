package com.facco.firsting_spring_app.service;

import com.facco.firsting_spring_app.model.Veiculo;
import com.facco.firsting_spring_app.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional
    public Veiculo salvarVeiculo(Veiculo veiculo) {
        // Verificar se o status do veículo é "indisponível", "manutenção" ou "alugado"
        if (veiculo.getStatus() == Veiculo.StatusVeiculo.INDISPONIVEL ||
                veiculo.getStatus() == Veiculo.StatusVeiculo.MANUTENCAO ||
                veiculo.getStatus() == Veiculo.StatusVeiculo.ALUGADO) {
            throw new IllegalStateException("Veículo não disponível para locação.");
        }
        return veiculoRepository.save(veiculo);
    }

    public List<Veiculo> listarVeiculos() {
        return veiculoRepository.findAll();
    }

    public Optional<Veiculo> buscarVeiculoPorId(Long id) {
        return veiculoRepository.findById(id);
    }

    @Transactional
    public Veiculo atualizarVeiculo(Long id, Veiculo veiculoAtualizado) {
        Veiculo veiculoExistente = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        // Verificar se a categoria foi alterada após locação
        if (veiculoExistente.getStatus() == Veiculo.StatusVeiculo.ALUGADO &&
                !veiculoExistente.getCategoria().equals(veiculoAtualizado.getCategoria())) {
            throw new IllegalStateException("Não é permitido alterar a categoria de um veículo alugado.");
        }


        veiculoExistente.setModelo(veiculoAtualizado.getModelo());
        veiculoExistente.setCategoria(veiculoAtualizado.getCategoria());
        veiculoExistente.setQuilometragem(veiculoAtualizado.getQuilometragem());
        veiculoExistente.setStatus(veiculoAtualizado.getStatus());

        return veiculoRepository.save(veiculoExistente);
    }

    @Transactional
    public void excluirVeiculo(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        veiculoRepository.delete(veiculo);
    }
}
