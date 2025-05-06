package com.facco.firsting_spring_app.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "devolucoes")
public class Devolucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "locacao_id", nullable = false)
    private Locacao locacao;

    @Temporal(TemporalType.DATE)
    private Date dataDevolucao;

    private Integer atraso;

    private BigDecimal multa;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Locacao getLocacao() { return locacao; }
    public void setLocacao(Locacao locacao) { this.locacao = locacao; }

    public Date getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(Date dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public Integer getAtraso() { return atraso; }
    public void setAtraso(Integer atraso) { this.atraso = atraso; }

    public BigDecimal getMulta() { return multa; }
    public void setMulta(BigDecimal multa) { this.multa = multa; }
}
