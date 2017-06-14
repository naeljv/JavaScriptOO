package br.edu.unochapeco.natanael.vieira.entidades;

public final class Atributo {
    private String _nomeJava;
    private String _nomeJS;

    public Atributo(String nomeJava) {
        _nomeJava = nomeJava;
        _nomeJS = null;
    }

    public String getNomeJava() {
        return _nomeJava;
    }

    public String getNomeJS() {
        return _nomeJS;
    }

    public void setNomeJS(String nomeJS) {
        _nomeJS = nomeJS;
    }
}