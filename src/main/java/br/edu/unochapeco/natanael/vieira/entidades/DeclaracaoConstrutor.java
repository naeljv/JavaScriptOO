package br.edu.unochapeco.natanael.vieira.entidades;

public final class DeclaracaoConstrutor {
    private String _declaracao;
    private String _parametros;

    public DeclaracaoConstrutor() {
        _declaracao = "";
        _parametros = "";
    }

    public String getParametros() {
        return _parametros;
    }

    public void setParametros(String parametros) {
        _parametros = parametros;
    }

    public String getDeclaracao() {
        return _declaracao;
    }

    public void setDeclaracao(String declaracao) {
        _declaracao = declaracao;
    }
}