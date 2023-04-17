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

}