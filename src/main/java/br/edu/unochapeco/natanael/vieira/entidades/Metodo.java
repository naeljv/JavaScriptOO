package br.edu.unochapeco.natanael.vieira.entidades;

import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorClasse;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorEscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;

import java.util.List;

public final class Metodo {
    private String _identificador;
    private String _nomeClasse;
    private String _nomeJava;
    private String _nomeJS;
    private boolean _modificadorAcessoPublico;
    private List<String> _tipoParametros;

    public Metodo(String nomeClasse, String nomeJava, boolean modificadorAcessoPublico, List<String> tipoParametros) {
        _identificador = AuxiliarConversao.getInstance().getGUIID();
        _nomeClasse = nomeClasse;
        _nomeJava = nomeJava;
        _nomeJS = null;
        _modificadorAcessoPublico = modificadorAcessoPublico;
        _tipoParametros = tipoParametros;
        GerenciadorClasse.getInstance().adicionarGerenciadorEscopoDeclaracao(_nomeClasse, _identificador);
    }

    public GerenciadorEscopoDeclaracao getGerenciadorEscopoDeclaracao() {
        return GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificador);
    }

    public String getIdentificador() {
        return _identificador;
    }

    public String getNomeJava() {
        return _nomeJava;
    }

    public String getNomeJS() {
        return _nomeJS;
    }

    public List<String> getTipoParametros() {
        return _tipoParametros;
    }

    public void setNomeJS(String nome) {
        _nomeJS = nome;
    }

    public boolean isConvertidoJS() {
        return _nomeJS != null;
    }

    public boolean isPublico() {
        return _modificadorAcessoPublico;
    }
}