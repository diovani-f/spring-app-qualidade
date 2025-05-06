package com.facco.firsting_spring_app.service;

import com.facco.firsting_spring_app.model.*;
import com.facco.firsting_spring_app.repository.DevolucaoRepository;
import com.facco.firsting_spring_app.repository.LocacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.facco.firsting_spring_app.model.StatusLocacao;


import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
public class DevolucaoService {

    @Autowired
    private DevolucaoRepository devolucaoRepository;

    @Autowired
    private LocacaoRepository locacaoRepository;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VeiculoService veiculoService;

    @Transactional
    public Devolucao registrarDevolucao(Long locacaoId, Date dataDevolucao) {
        Locacao locacao = locacaoRepository.findById(locacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Locação não encontrada"));

        if (locacao.getStatus() != StatusLocacao.ABERTA) {
            throw new IllegalStateException("Locação já está finalizada ou cancelada.");
        }


        long atraso = (dataDevolucao.getTime() - locacao.getDataFim().getTime()) / (1000 * 60 * 60 * 24);
        int atrasoDias = (int) Math.max(0, atraso);

        BigDecimal multa = BigDecimal.valueOf(atrasoDias).multiply(BigDecimal.valueOf(50));

        // Atualizar a locação
        locacao.setStatus(StatusLocacao.FINALIZADA);
        locacao.setMulta(multa);
        locacaoRepository.save(locacao);

        // Atualizar cliente
        Cliente cliente = locacao.getCliente();
        cliente.setLocacaoAtiva(false);
        clienteService.salvarCliente(cliente);

        // Atualizar veículo
        Veiculo veiculo = locacao.getVeiculo();
        veiculo.setStatus(Veiculo.StatusVeiculo.DISPONIVEL);
        veiculoService.atualizarVeiculo(veiculo.getId(), veiculo);

        // Criar devolução
        Devolucao devolucao = new Devolucao();
        devolucao.setLocacao(locacao);
        devolucao.setDataDevolucao(dataDevolucao);
        devolucao.setAtraso(atrasoDias);
        devolucao.setMulta(multa);

        return devolucaoRepository.save(devolucao);
    }

    public Optional<Devolucao> buscarPorId(Long id) {
        return devolucaoRepository.findById(id);
    }

    public void excluirDevolucao(Long id) {
        devolucaoRepository.deleteById(id);
    }
}
