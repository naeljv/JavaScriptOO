package br.edu.unochapeco.natanael.vieira.leitores;

import br.edu.unochapeco.natanael.vieira.entidades.Atributo;
import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import br.edu.unochapeco.natanael.vieira.entidades.Classe;
import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorClasse;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.Type;
import java.util.List;
import java.util.Optional;

public final class LeitorAtributo {
    private Classe _classe;

    public LeitorAtributo(Classe classe) {
        _classe = classe;
    }

    public String ler(List<BodyDeclaration<?>> declaracoes) {
        StringBuilder declaracaoAtributosJS = new StringBuilder();
        EscopoDeclaracao escopo = new EscopoDeclaracao(null, 1);
        LeitorExpressao leitorExpressao = new LeitorExpressao(_classe.getNome());
        String identacao = AuxiliarConversao.getInstance().getIdentacao(1);

        GerenciadorClasse.getInstance().adicionarGerenciadorEscopoDeclaracao(_classe.getNome(), _classe.getNome());
        GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_classe.getNome()).adicionarEscopo(escopo);

        declaracoes.forEach(declaracao -> {
            FieldDeclaration declaracaoAtributos = (FieldDeclaration) declaracao;
            Type tipo = declaracaoAtributos.getVariable(0).getType();
            boolean tipoSuportado = AuxiliarConversao.getInstance().validarTipoSuportado(tipo);
            String modificadorAcesso = (declaracaoAtributos.isPrivate() || declaracaoAtributos.isProtected()) ? "_" : "";

            declaracaoAtributos.getVariables().forEach(declaracaoAtributo -> {
                String nomeAtributoJava = declaracaoAtributo.getNameAsString();
                Atributo atributo = new Atributo(nomeAtributoJava);

                _classe.getGerenciadorAtributo().adicionarAtributo(atributo);

                if (tipoSuportado) {
                    try {
                        Optional<Expression> initializer = declaracaoAtributo.getInitializer();
                        String valorInicial = "undefined";

                        if (initializer.isPresent()) {
                            valorInicial = leitorExpressao.ler(escopo, initializer.get());
                        }

                        String nomeAtributoJS = String.format("this.%s%s", modificadorAcesso, nomeAtributoJava);

                        atributo.setNomeJS(nomeAtributoJS);
                        declaracaoAtributosJS.append(String.format("%s%s = %s;%s", identacao, nomeAtributoJS, valorInicial, System.lineSeparator()));
                    } catch (ExpressaoNaoSuportadaException excecao) {
                        if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                            declaracaoAtributosJS.append(String.format("%s/* %s", identacao, excecao.getMessage()));
                            declaracaoAtributosJS.append(System.lineSeparator());
                            declaracaoAtributosJS.append(String.format("%s%s", identacao, declaracaoAtributo.toString()));
                            declaracaoAtributosJS.append(System.lineSeparator());
                            declaracaoAtributosJS.append(String.format("%s*/", identacao));
                            declaracaoAtributosJS.append(System.lineSeparator());
                        } else {
                            declaracaoAtributosJS.append(String.format("%s// %s", identacao, declaracaoAtributo.toString()));
                            declaracaoAtributosJS.append(System.lineSeparator());
                        }
                    }
                }
            });

            if (!tipoSuportado) {
                if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                    declaracaoAtributosJS.append(String.format("%s/* Tipo de dado não suportado na versão atual: [%s]", identacao, tipo.toString()));
                    declaracaoAtributosJS.append(System.lineSeparator());
                    declaracaoAtributosJS.append(String.format("%s%s", identacao, declaracao.toString()));
                    declaracaoAtributosJS.append(System.lineSeparator());
                    declaracaoAtributosJS.append(String.format("%s*/", identacao));
                    declaracaoAtributosJS.append(System.lineSeparator());
                }
                else {
                    declaracaoAtributosJS.append(String.format("%s// %s", identacao, declaracao.toString()));
                    declaracaoAtributosJS.append(System.lineSeparator());
                }
            }
        });

        return declaracaoAtributosJS.toString();
    }
}