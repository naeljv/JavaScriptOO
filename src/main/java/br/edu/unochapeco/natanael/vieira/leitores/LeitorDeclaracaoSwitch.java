package br.edu.unochapeco.natanael.vieira.leitores;

import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorClasse;
import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import java.util.Optional;

final class LeitorDeclaracaoSwitch {
    private String _identificacaoProprietario;

    public LeitorDeclaracaoSwitch(String identificacaoProprietario) {
        _identificacaoProprietario = identificacaoProprietario;
    }

    public String ler(EscopoDeclaracao escopoPai, SwitchStmt declaracao) {
        StringBuilder declaracaoSwitchJS = new StringBuilder();
        EscopoDeclaracao escopo = new EscopoDeclaracao(escopoPai.getIdentificacaoEscopo(), escopoPai.getNivelIdentacao());

        GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificacaoProprietario).adicionarEscopo(escopo);

        try {
            LeitorExpressao leitorExpressao = new LeitorExpressao(_identificacaoProprietario);
            String expressaoJS = leitorExpressao.ler(escopo, declaracao.getSelector());

            declaracaoSwitchJS.append(String.format("%sswitch (%s) {", escopo.getIdentacao(), expressaoJS));
            declaracaoSwitchJS.append(System.lineSeparator());
            declaracaoSwitchJS.append(lerDeclaracoesCase(escopo, declaracao.getEntries()));
            declaracaoSwitchJS.append(String.format("%s}", escopo.getIdentacao()));
            declaracaoSwitchJS.append(System.lineSeparator());
        }
        catch (ExpressaoNaoSuportadaException excecao) {
            if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                declaracaoSwitchJS.append(String.format("%s/* %s", escopo.getIdentacao(), excecao.getMessage()));
            }
            else {
                declaracaoSwitchJS.append(String.format("%s/*", escopo.getIdentacao()));
            }

            declaracaoSwitchJS.append(System.lineSeparator());
            declaracaoSwitchJS.append(String.format("%sswitch (%s) {",escopo.getIdentacao(), declaracao.getSelector().toString()));
            declaracaoSwitchJS.append(System.lineSeparator());
            declaracaoSwitchJS.append(String.format("%s%sBloco de código removido", escopo.getNivelIdentacao(), AuxiliarConversao.getInstance().getIdentacao(1)));
            declaracaoSwitchJS.append(System.lineSeparator());
            declaracaoSwitchJS.append(String.format("%s}", escopo.getIdentacao()));
            declaracaoSwitchJS.append(System.lineSeparator());
            declaracaoSwitchJS.append(String.format("%s*/", escopo.getIdentacao()));
            declaracaoSwitchJS.append(System.lineSeparator());
        }

        return declaracaoSwitchJS.toString();
    }

    private String lerDeclaracoesCase(EscopoDeclaracao escopoPai, NodeList<SwitchEntryStmt> declaracoesCase) {
        StringBuilder declaracoesSwitchCase = new StringBuilder();
        LeitorDeclaracao leitorDeclaracao = new LeitorDeclaracao(_identificacaoProprietario);
        LeitorExpressao leitorCondicaoCase = new LeitorExpressao(_identificacaoProprietario);

        declaracoesCase.forEach(declaracao -> {
            EscopoDeclaracao escopo = new EscopoDeclaracao(escopoPai.getIdentificacaoEscopo(), escopoPai.getNivelIdentacao() + 1);

            GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificacaoProprietario).adicionarEscopo(escopo);

            Optional<Expression> condicaoCase = declaracao.getLabel();
            boolean declaracaoCaseEntreParenteses = false;

            try {
                if (condicaoCase.isPresent()) {
                    String condicaoCaseJS = leitorCondicaoCase.ler(escopo, condicaoCase.get());
                    declaracoesSwitchCase.append(String.format("%scase %s:", escopo.getIdentacao(), condicaoCaseJS));
                }
                else {
                    declaracoesSwitchCase.append(String.format("%sdefault:", escopo.getIdentacao()));
                }

                NodeList<Statement> declaracoes = declaracao.getStatements();

                if (declaracoes.size() == 1) {
                    if (declaracoes.get(0) instanceof BlockStmt) {
                        declaracoes = ((BlockStmt) declaracoes.get(0)).getStatements();
                        declaracaoCaseEntreParenteses = true;
                        declaracoesSwitchCase.append(" {");
                    }
                }

                declaracoesSwitchCase.append(System.lineSeparator());
                declaracoesSwitchCase.append(leitorDeclaracao.ler(escopo, declaracoes));

                if (declaracaoCaseEntreParenteses) {
                    declaracoesSwitchCase.append(String.format("%s}", escopo.getIdentacao()));
                    declaracoesSwitchCase.append(System.lineSeparator());
                }
            } catch (ExpressaoNaoSuportadaException excecao) {
                if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                    declaracoesSwitchCase.append(String.format("%s/* %s", escopo.getIdentacao(), excecao.getMessage()));
                }
                else {
                    declaracoesSwitchCase.append(String.format("%s/*", escopo.getIdentacao()));
                }

                declaracoesSwitchCase.append(System.lineSeparator());

                if (condicaoCase.isPresent()) {
                    declaracoesSwitchCase.append(String.format("%scase %s:", escopo.getIdentacao(), condicaoCase.get().toString()));
                }
                else {
                    declaracoesSwitchCase.append(String.format("%sdefault:", escopo.getIdentacao()));
                }

                declaracoesSwitchCase.append(String.format("%s%sBloco de código removido", escopo.getIdentacao(), AuxiliarConversao.getInstance().getIdentacao(1)));
                declaracoesSwitchCase.append(System.lineSeparator());
                declaracoesSwitchCase.append(String.format("%s*/", escopo.getIdentacao()));
                declaracoesSwitchCase.append(System.lineSeparator());
            }
        });

        return declaracoesSwitchCase.toString();
    }
}