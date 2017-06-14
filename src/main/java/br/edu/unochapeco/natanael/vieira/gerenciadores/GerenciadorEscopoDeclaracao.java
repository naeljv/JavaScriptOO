package br.edu.unochapeco.natanael.vieira.gerenciadores;

import br.edu.unochapeco.natanael.vieira.entidades.Classe;
import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.entidades.Variavel;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GerenciadorEscopoDeclaracao {
    private String _nomeClasse;
    private List<EscopoDeclaracao> _escopos;

    public GerenciadorEscopoDeclaracao(String nomeClasse) {
        _nomeClasse = nomeClasse;
        _escopos = new ArrayList<>();
    }

    public void adicionarEscopo(EscopoDeclaracao escopo) {
        _escopos.add(escopo);
    }

    public EscopoDeclaracao getEscopoRaiz() {
        if (_escopos.size() > 0) {
            return _escopos.get(0);
        }

        return null;
    }

    public String getNomeClasse() {
        return _nomeClasse;
    }

    public String getNomeVariavelJSAdicionar(String nomeVariavelJava) {
        int totalVariaveisMesmoNomeEscopo = 0;

        for (int i = 0; i < _escopos.size(); i++) {
            EscopoDeclaracao escopo = _escopos.get(i);

            totalVariaveisMesmoNomeEscopo += escopo.getVariaveis().stream().filter(variavel -> variavel.getNomeJava().equals(nomeVariavelJava)).count();
        }

        return totalVariaveisMesmoNomeEscopo == 0 ? nomeVariavelJava : String.format("%s__%d", nomeVariavelJava, totalVariaveisMesmoNomeEscopo);
    }

    public String getNomeVariavelJS(EscopoDeclaracao escopo, String nomeVariavelJava) throws ExpressaoNaoSuportadaException {
        Optional<Variavel> variavel = escopo.getVariaveis().stream().filter(variavelEscopo -> variavelEscopo.getNomeJava().equals(nomeVariavelJava)).findFirst();

        if (variavel.isPresent()) {
            if (variavel.get().getNomeJS() == null) {
                throw new ExpressaoNaoSuportadaException(String.format("Variável [%s] não convertida", nomeVariavelJava));
            }

            return variavel.get().getNomeJS();
        }
        else {
            Optional<EscopoDeclaracao> escopoPai = _escopos.stream().filter(escopoDeclaracao -> escopoDeclaracao.getIdentificacaoEscopo().equals(escopo.getIdentificacaoEscopoPai())).findFirst();

            if (escopoPai.isPresent()) {
                return getNomeVariavelJS(escopoPai.get(), nomeVariavelJava);
            }
            else {
                Classe classe = GerenciadorClasse.getInstance().getClasse(_nomeClasse);
                return classe.getGerenciadorAtributo().getNomeAtrubutoJS(nomeVariavelJava);
            }
        }
    }
}