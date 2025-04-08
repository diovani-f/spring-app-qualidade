package com.facco.firsting_spring_app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "locacoes")
public class Locacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @Column(name = "data_retirada", nullable = false)
    private LocalDateTime dataRetirada;

    @Column(name = "data_prevista_devolucao", nullable = false)
    private LocalDateTime dataPrevistaDevolucao;

    @Column(name = "data_real_devolucao")
    private LocalDateTime dataRealDevolucao;

    @Column(name = "km_inicial", nullable = false)
    private int kmInicial;

    @Column(name = "km_final")
    private Integer kmFinal;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public LocalDateTime getDataRetirada() { return dataRetirada; }
    public void setDataRetirada(LocalDateTime dataRetirada) { this.dataRetirada = dataRetirada; }

    public LocalDateTime getDataPrevistaDevolucao() { return dataPrevistaDevolucao; }
    public void setDataPrevistaDevolucao(LocalDateTime dataPrevistaDevolucao) { this.dataPrevistaDevolucao = dataPrevistaDevolucao; }

    public LocalDateTime getDataRealDevolucao() { return dataRealDevolucao; }
    public void setDataRealDevolucao(LocalDateTime dataRealDevolucao) { this.dataRealDevolucao = dataRealDevolucao; }

    public int getKmInicial() { return kmInicial; }
    public void setKmInicial(int kmInicial) { this.kmInicial = kmInicial; }

    public Integer getKmFinal() { return kmFinal; }
    public void setKmFinal(Integer kmFinal) { this.kmFinal = kmFinal; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}
