package br.edu.unochapeco.natanael.vieira.gerenciadores;

import br.edu.unochapeco.natanael.vieira.entidades.Classe;
import java.util.*;

public final class GerenciadorClasse {
    private List<Classe> _classes;
    private Map<String, GerenciadorEscopoDeclaracao> _gerenciadoresEscopoDeclaracao;
    private static GerenciadorClasse _gerenciadorClasse;

    private GerenciadorClasse() {
        _classes = new ArrayList<>();
        _gerenciadoresEscopoDeclaracao = new HashMap<>();
    }

    public static GerenciadorClasse getInstance() {
        if (_gerenciadorClasse == null) {
            _gerenciadorClasse = new GerenciadorClasse();
        }

        return _gerenciadorClasse;
    }

    public void adicionarClasse(Classe classe) {
        _classes.add(classe);
    }

    public void adicionarGerenciadorEscopoDeclaracao(String nomeClasse, String identificacaoProprietario) {
        _gerenciadoresEscopoDeclaracao.put(identificacaoProprietario, new GerenciadorEscopoDeclaracao(nomeClasse));
    }

    public Classe getClasse(String nomeClasse) {
        Optional<Classe> classeRetorno =  _classes.stream().filter(classe -> classe.getNome().equals(nomeClasse)).findFirst();

        if (classeRetorno.isPresent()) {
            return classeRetorno.get();
        }

        return null;
    }

    public GerenciadorEscopoDeclaracao getGerenciadorEscopoDeclaracao(String identificacaoProprietario) {
        return _gerenciadoresEscopoDeclaracao.get(identificacaoProprietario);
    }

    public void removerTodasClasses() {
        _classes.clear();
    }
}