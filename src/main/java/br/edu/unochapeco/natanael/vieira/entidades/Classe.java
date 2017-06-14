package br.edu.unochapeco.natanael.vieira.entidades;

import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorAtributo;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorMetodo;

public final class Classe {
    private String _nome;
    private String _nomeClassePai;
    private GerenciadorAtributo _gerenciadorAtributo;
    private GerenciadorMetodo _gerenciadorMetodo;

    public Classe(String nome, String nomeClassePai) {
        _nome = nome;
        _nomeClassePai = nomeClassePai;
        _gerenciadorAtributo = new GerenciadorAtributo(_nome);
        _gerenciadorMetodo = new GerenciadorMetodo(_nome);
    }

    public GerenciadorAtributo getGerenciadorAtributo() {
        return _gerenciadorAtributo;
    }

    public GerenciadorMetodo getGerenciadorMetodo() {
        return _gerenciadorMetodo;
    }

    public String getNome() {
        return _nome;
    }

    public String getNomeClassePai() {
        return _nomeClassePai;
    }
}