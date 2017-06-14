package br.edu.unochapeco.natanael.vieira.leitores;

import br.edu.unochapeco.natanael.vieira.entidades.Classe;
import br.edu.unochapeco.natanael.vieira.entidades.DeclaracaoConstrutor;
import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorClasse;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorEscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import java.util.Arrays;
import java.util.List;

public final class LeitorConstrutor {
    private Classe _classe;

    public LeitorConstrutor(Classe classe) {
        _classe = classe;
    }

    public DeclaracaoConstrutor ler(List<BodyDeclaration<?>> declaracoes) {
        DeclaracaoConstrutor construtorJS = new DeclaracaoConstrutor();
        boolean construtorVazioDeclarado = declaracoes.size() == 2;

        for (BodyDeclaration declaracaoConstrutor : declaracoes) {
            ConstructorDeclaration construtor = (ConstructorDeclaration) declaracaoConstrutor;
            int totalParametros = construtor.getParameters().size();
            int totalDeclaracoesEscopo = construtor.getBody().getStatements().size();

            if ((totalParametros > 0) || (totalDeclaracoesEscopo > 0)) {
                EscopoDeclaracao escopoRaiz = new EscopoDeclaracao(null, (construtorVazioDeclarado && (totalParametros > 0)) ? 1 : 0);

                try {
                    if (totalParametros > 0) {
                        LeitorParametro leitorParametro = new LeitorParametro();
                        construtorJS.setParametros(leitorParametro.ler(escopoRaiz, construtor.getParameters()));
                    }

                    if (totalDeclaracoesEscopo > 0) {
                        GerenciadorClasse.getInstance().adicionarGerenciadorEscopoDeclaracao(_classe.getNome(), _classe.getNome());

                        StringBuilder declaracaoConstrutorJS = new StringBuilder();
                        GerenciadorEscopoDeclaracao gerenciadorEscopoDeclaracao = GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_classe.getNome());
                        LeitorDeclaracao leitorDeclaracao = new LeitorDeclaracao(_classe.getNome());

                        gerenciadorEscopoDeclaracao.adicionarEscopo(escopoRaiz);

                        if (construtorVazioDeclarado && (totalParametros > 0)) {
                            List<String> parametrosJS = Arrays.asList(construtorJS.getParametros().split(","));

                            declaracaoConstrutorJS.append(String.format("%sif %s", escopoRaiz.getIdentacao(), totalParametros > 1 ? "(" : ""));

                            for (int i = 0; i < totalParametros; i++) {
                                declaracaoConstrutorJS.append(String.format("(%s !== undefined)%s", parametrosJS.get(i).trim(), (i < (parametrosJS.size() - 1) ? " && " : "")));
                            }

                            declaracaoConstrutorJS.append(String.format("%s {", totalParametros > 1 ? ")" : ""));
                            declaracaoConstrutorJS.append(System.lineSeparator());
                        }

                        declaracaoConstrutorJS.append(leitorDeclaracao.ler(escopoRaiz, construtor.getBody()));

                        if (construtorVazioDeclarado && (totalParametros > 0)) {
                            declaracaoConstrutorJS.append(String.format("%s}", escopoRaiz.getIdentacao()));
                            declaracaoConstrutorJS.append(System.lineSeparator());
                        }

                        construtorJS.setDeclaracao(declaracaoConstrutorJS.toString());
                    }
                } catch (ExpressaoNaoSuportadaException excecao) {
                    StringBuilder declaracaoConstrutorJS = new StringBuilder();
                    String identacao = AuxiliarConversao.getInstance().getIdentacao(1);

                    if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                        declaracaoConstrutorJS.append(String.format("%s/* %s", identacao, excecao.getMessage()));
                    }
                    else {
                        declaracaoConstrutorJS.append(String.format("%s/*", identacao));
                    }

                    declaracaoConstrutorJS.append(System.lineSeparator());

                    construtor.removeJavaDocComment();

                    String metodoJava = construtor.toString().replace(construtor.getBody().toString(), "");

                    declaracaoConstrutorJS.append(String.format("%s%s{", identacao, metodoJava));
                    declaracaoConstrutorJS.append(System.lineSeparator());
                    declaracaoConstrutorJS.append(String.format("%s%sBloco de código removido", identacao, identacao));
                    declaracaoConstrutorJS.append(System.lineSeparator());
                    declaracaoConstrutorJS.append(String.format("%s}", identacao));
                    declaracaoConstrutorJS.append(System.lineSeparator());
                    declaracaoConstrutorJS.append(String.format("%s*/", identacao));
                    declaracaoConstrutorJS.append(System.lineSeparator());

                    construtorJS.setDeclaracao(declaracaoConstrutorJS.toString());
                }
            }
        }

        return construtorJS;
    }
}