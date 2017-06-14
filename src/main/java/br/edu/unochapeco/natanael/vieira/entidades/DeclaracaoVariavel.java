package br.edu.unochapeco.natanael.vieira.entidades;

public final class DeclaracaoVariavel {
    private String _comentario;
    private String _declaracao;

    public DeclaracaoVariavel(String declaracao, String comentario) {
        _comentario = comentario;
        _declaracao = declaracao;
    }

    public String getComentario() {
        return _comentario;
    }

    public String getDeclaracao() {
        return _declaracao;
    }
}