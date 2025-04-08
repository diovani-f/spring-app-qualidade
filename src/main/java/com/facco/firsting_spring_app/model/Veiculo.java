package com.facco.firsting_spring_app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "veiculos")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modelo;
    private String marca;
    private String placa;
    private int ano;
    private int quilometragem;
    private String cor;
    private String status;    // disponível, alugado, manutenção, reservado
    private String categoria; // econômico, SUV, luxo, etc.

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public int getQuilometragem() { return quilometragem; }
    public void setQuilometragem(int quilometragem) { this.quilometragem = quilometragem; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
