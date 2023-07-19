package com.example.smartufopa.moldes;

import com.google.gson.annotations.SerializedName;

public class Dados_da_Denuncia {
    @SerializedName("data")
    public String data;
    @SerializedName("hora")
    public String hora;
    @SerializedName("tipo")
    public String tipo;
    @SerializedName("nome")
    public String nome;
    @SerializedName("telefone")
    public String telefone;
    @SerializedName("endereco")
    public String endereco;
    @SerializedName("localizacao")
    public String localizacao;
    @SerializedName("descricao")
    public String descricao;
    public Dados_da_Denuncia(String data, String hora, String tipo, String nome, String telefone, String endereco, String localizacao, String descricao) {
        this.data = data;
        this.hora = hora;
        this.tipo = tipo;
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.localizacao = localizacao;
        this.descricao = descricao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}