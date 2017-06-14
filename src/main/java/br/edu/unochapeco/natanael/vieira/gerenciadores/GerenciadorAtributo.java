package br.edu.unochapeco.natanael.vieira.gerenciadores;

import br.edu.unochapeco.natanael.vieira.entidades.Atributo;
import br.edu.unochapeco.natanael.vieira.entidades.Classe;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GerenciadorAtributo {
    private String _nomeClasse;
    private List<Atributo> _atributos;

    public GerenciadorAtributo(String nomeClasse) {
        _nomeClasse = nomeClasse;
        _atributos = new ArrayList<>();
    }

    public void adicionarAtributo(Atributo atributo) {
        _atributos.add(atributo);
    }

    public List<Atributo> getAtributos() {
        return _atributos;
    }

    public String getNomeAtrubutoJS(String nomeAtributoJava) throws ExpressaoNaoSuportadaException {
        Optional<Atributo> atributo = _atributos.stream().filter(a -> a.getNomeJava().equals(nomeAtributoJava)).findFirst();

        if (atributo.isPresent()) {
            if (atributo.get().getNomeJS() == null) {
                throw new ExpressaoNaoSuportadaException(String.format("Atributo [%s] não convertido", nomeAtributoJava));
            }
            else {
                return atributo.get().getNomeJS();
            }
        }
        else {
            Classe classe = GerenciadorClasse.getInstance().getClasse(_nomeClasse);

            if (classe.getNomeClassePai() == null) {
                throw new ExpressaoNaoSuportadaException("Atributo não encontrado");
            }
            else {
                Classe classePai = GerenciadorClasse.getInstance().getClasse(classe.getNomeClassePai());
                return classePai.getGerenciadorAtributo().getNomeAtrubutoJS(nomeAtributoJava);
            }
        }
    }
}