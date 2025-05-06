package com.facco.firsting_spring_app.service;

import com.facco.firsting_spring_app.model.*;
import com.facco.firsting_spring_app.repository.LocacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class LocacaoService {

    private static final Logger logger = LoggerFactory.getLogger(LocacaoService.class);

    @Autowired
    private LocacaoRepository locacaoRepository;

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private ClienteService clienteService;

    public List<Locacao> listarLocacoes() {
        return locacaoRepository.findAll();
    }

    public Optional<Locacao> buscarLocacaoPorId(Long id) {
        return locacaoRepository.findById(id);
    }

    @Transactional
    public Locacao salvarLocacao(Locacao locacao) {
        try {
            logger.info("Recebendo locacao com cliente ID: {} e veiculo ID: {}",
                    locacao.getCliente() != null ? locacao.getCliente().getId() : null,
                    locacao.getVeiculo() != null ? locacao.getVeiculo().getId() : null);

            Cliente cliente = clienteService.buscarClientePorId(locacao.getCliente().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

            Veiculo veiculo = veiculoService.buscarVeiculoPorId(locacao.getVeiculo().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

            locacao.setCliente(cliente);
            locacao.setVeiculo(veiculo);

            logger.info("Cliente idade: {}, limite idade da categoria: {}",
                    cliente.getIdade(), veiculo.getCategoria().getLimiteIdade());

            if (cliente.getIdade() == null || cliente.getIdade() < veiculo.getCategoria().getLimiteIdade()) {
                throw new IllegalArgumentException("Cliente não atende à idade mínima para esta categoria.");
            }

            if (Boolean.TRUE.equals(cliente.getLocacaoAtiva())) {
                throw new IllegalArgumentException("Cliente já possui uma locação ativa.");
            }

            if (veiculo.getStatus() != Veiculo.StatusVeiculo.DISPONIVEL) {
                throw new IllegalArgumentException("Veículo não está disponível para locação.");
            }

            long dias = (locacao.getDataFim().getTime() - locacao.getDataInicio().getTime()) / (1000 * 60 * 60 * 24);
            if (dias <= 0) dias = 1;

            BigDecimal precoBase = veiculo.getCategoria().getPrecoBase();
            if (precoBase == null) {
                throw new IllegalArgumentException("Categoria do veículo sem preço base definido.");
            }

            BigDecimal valorTotal = precoBase.multiply(BigDecimal.valueOf(dias));
            logger.info("Dias de locação: {}, valor calculado: {}", dias, valorTotal);
            locacao.setDias(dias);
            locacao.setValorTotal(valorTotal);

            veiculo.setStatus(Veiculo.StatusVeiculo.ALUGADO);
            veiculoService.atualizarVeiculo(veiculo.getId(), veiculo);

            cliente.setLocacaoAtiva(true);
            clienteService.salvarCliente(cliente);

            logger.info("Locação validada e salva com sucesso.");

            locacao.setDias(dias);
            locacao.setValorTotal(valorTotal);


            return locacaoRepository.save(locacao);

        } catch (Exception e) {
            logger.error("Erro ao salvar locação: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Locacao atualizarLocacao(Long id, Locacao locacaoAtualizada) {
        Locacao locacaoExistente = locacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Locação não encontrada"));

        locacaoExistente.setStatus(locacaoAtualizada.getStatus());
        locacaoExistente.setMulta(locacaoAtualizada.getMulta());
        locacaoExistente.setDataFim(locacaoAtualizada.getDataFim());

        return locacaoRepository.save(locacaoExistente);
    }

    @Transactional
    public void excluirLocacao(Long id) {
        Locacao locacao = locacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Locação não encontrada"));
        locacaoRepository.delete(locacao);
    }
}
