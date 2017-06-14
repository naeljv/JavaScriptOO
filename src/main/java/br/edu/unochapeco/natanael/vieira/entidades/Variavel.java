package br.edu.unochapeco.natanael.vieira.entidades;

public final class Variavel {
    private String _nomeJava;
    private String _nomeJS;

    public Variavel(String nomeJava, String nomeJS) {
        _nomeJava = nomeJava;
        _nomeJS = nomeJS;
    }

    public String getNomeJava() {
        return _nomeJava;
    }

    public String getNomeJS() {
        return _nomeJS;
    }
}