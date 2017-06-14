package br.edu.unochapeco.natanael.vieira.leitores;

import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import br.edu.unochapeco.natanael.vieira.entidades.DeclaracaoVariavel;
import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorClasse;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import java.util.Optional;

final class LeitorDeclaracaoFor {
    private String _identificacaoProprietario;

    public LeitorDeclaracaoFor(String identificacaoProprietario) {
        _identificacaoProprietario = identificacaoProprietario;
    }

    public String ler(EscopoDeclaracao escopoPai, ForStmt declaracao) {
        StringBuilder declaracaoForJS = new StringBuilder();
        EscopoDeclaracao escopo = new EscopoDeclaracao(escopoPai.getIdentificacaoEscopo(), escopoPai.getNivelIdentacao());
        NodeList<Expression> expressaoInicializacao = declaracao.getInitialization();
        Optional<Expression> expressaoComparacao =  declaracao.getCompare();
        NodeList<Expression> expressaoIncremento = declaracao.getUpdate();

        GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificacaoProprietario).adicionarEscopo(escopo);

        try {
            LeitorDeclaracao leitorDeclaracao = new LeitorDeclaracao(_identificacaoProprietario);
            LeitorExpressao leitorExpressao = new LeitorExpressao(_identificacaoProprietario);
            StringBuilder incremento = new StringBuilder();
            StringBuilder inicializacao = new StringBuilder();
            String comentario = "";
            String comparacao = "";
            boolean permitirVariaveisNaoConvertidas = false;

            for (int i = 0; i < expressaoInicializacao.size(); i++) {
                Expression expressao = expressaoInicializacao.get(i);

                if (expressao instanceof VariableDeclarationExpr) {
                    LeitorDeclaracaoVariavel leitorVariavel = new LeitorDeclaracaoVariavel(_identificacaoProprietario);
                    DeclaracaoVariavel variavel = leitorVariavel.ler(escopo, ((VariableDeclarationExpr) expressao), permitirVariaveisNaoConvertidas);

                    comentario = variavel.getComentario();
                    inicializacao.append(variavel.getDeclaracao());
                }
                else {
                    if (i > 0) {
                        inicializacao.append(", ");
                    }

                    inicializacao.append(leitorExpressao.ler(escopo, expressao));
                }
            }

            if (expressaoComparacao.isPresent()) {
                comparacao = leitorExpressao.ler(escopo, expressaoComparacao.get());
            }

            for (int i = 0; i < expressaoIncremento.size(); i++) {
                Expression expressao = expressaoIncremento.get(i);

                if (i > 0) {
                    incremento.append(", ");
                }

                incremento.append(leitorExpressao.ler(escopo, expressao));
            }

            if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                declaracaoForJS.append(comentario);
            }

            declaracaoForJS.append(String.format("%sfor (%s; %s; %s) {", escopo.getIdentacao(), inicializacao.toString(), comparacao, incremento.toString()));
            declaracaoForJS.append(System.lineSeparator());
            declaracaoForJS.append(leitorDeclaracao.ler(escopo, (BlockStmt) declaracao.getBody()));
            declaracaoForJS.append(String.format("%s}", escopo.getIdentacao()));
            declaracaoForJS.append(System.lineSeparator());
        }
        catch (ExpressaoNaoSuportadaException excecao) {
            StringBuilder incremento = new StringBuilder();
            StringBuilder inicializacao = new StringBuilder();
            String comparacao = "";

            for (int i = 0; i < expressaoInicializacao.size(); i++) {
                if (i > 0) {
                    inicializacao.append(", ");
                }

                inicializacao.append(expressaoInicializacao.get(i).toString());
            }

            if (expressaoComparacao.isPresent()) {
                comparacao = expressaoComparacao.get().toString();
            }

            for (int i = 0; i < expressaoIncremento.size(); i++) {
                if (i > 0) {
                    incremento.append(", ");
                }

                incremento.append(expressaoIncremento.get(i).toString());
            }

            if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                declaracaoForJS.append(String.format("%s/* %s", escopo.getIdentacao(), excecao.getMessage()));
            }
            else {
                declaracaoForJS.append(String.format("%s/*", escopo.getIdentacao()));
            }

            declaracaoForJS.append(System.lineSeparator());
            declaracaoForJS.append(String.format("%sfor (%s, %s, %s) {", escopo.getIdentacao(), inicializacao.toString(), comparacao, incremento.toString()));
            declaracaoForJS.append(System.lineSeparator());
            declaracaoForJS.append(String.format("%s%sBloco de código removido", escopo.getIdentacao(), AuxiliarConversao.getInstance().getIdentacao(1)));
            declaracaoForJS.append(System.lineSeparator());
            declaracaoForJS.append(String.format("%s}", escopo.getIdentacao()));
            declaracaoForJS.append(System.lineSeparator());
            declaracaoForJS.append(String.format("%s*/", escopo.getIdentacao()));
            declaracaoForJS.append(System.lineSeparator());
        }

        return declaracaoForJS.toString();
    }
}