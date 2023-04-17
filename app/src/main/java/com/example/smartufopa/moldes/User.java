package com.example.smartufopa.moldes;

public class User {
    String id;
    String nome;
    String Sobrenome;
    String email;
    String telefone;
    String senha;
    String Corfirmasenha;
    String Endereco;
    String Bairro;

    public User() {
    }

    public User(String id, String nome, String sobrenome, String email, String telefone, String senha, String corfirmasenha, String endereco, String bairro) {
        this.id = id;
        this.nome = nome;
        Sobrenome = sobrenome;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        Corfirmasenha = corfirmasenha;
        Endereco = endereco;
        Bairro = bairro;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return Sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        Sobrenome = sobrenome;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCorfirmasenha() {
        return Corfirmasenha;
    }

    public void setCorfirmasenha(String corfirmasenha) {
        Corfirmasenha = corfirmasenha;
    }

    public String getEndereco() {
        return Endereco;
    }

    public void setEndereco(String endereco) {
        Endereco = endereco;
    }

    public String getBairro() {
        return Bairro;
    }

    public void setBairro(String bairro) {
        Bairro = bairro;
    }
}
