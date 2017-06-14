package br.edu.unochapeco.natanael.vieira.entidades;

import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;

import java.util.ArrayList;
import java.util.List;

public final class EscopoDeclaracao {
    private String _identificacaoEscopo;
    private String _identificacaoEscopoPai;
    private int _nivelIdentacao;
    private List<Variavel> _variaveis;

    public EscopoDeclaracao(String identificacaoEscopoPai, int nivelIdentacao) {
        _identificacaoEscopo = AuxiliarConversao.getInstance().getGUIID();
        _identificacaoEscopoPai = identificacaoEscopoPai;
        _nivelIdentacao = nivelIdentacao;
        _variaveis = new ArrayList<>();
    }

    public void adicionarVariavel(Variavel variavel) {
        _variaveis.add(variavel);
    }

    public String getIdentacao() {
        return AuxiliarConversao.getInstance().getIdentacao(_nivelIdentacao);
    }

    public String getIdentificacaoEscopo() {
        return _identificacaoEscopo;
    }

    public String getIdentificacaoEscopoPai() {
        return _identificacaoEscopoPai;
    }

    public int getNivelIdentacao() {
        return _nivelIdentacao;
    }

    public List<Variavel> getVariaveis() {
        return _variaveis;
    }
}