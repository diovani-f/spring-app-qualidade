package com.facco.firsting_spring_app.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private Integer idade;

    private String email;

    private String telefone;

    private Boolean locacaoAtiva = false;

    public Cliente() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getLocacaoAtiva() {
        return locacaoAtiva;
    }

    public void setLocacaoAtiva(Boolean locacaoAtiva) {
        this.locacaoAtiva = locacaoAtiva;
    }

    // Adicionando o método isLocacaoAtiva()
    public boolean isLocacaoAtiva() {
        return locacaoAtiva != null && locacaoAtiva;
    }
}
