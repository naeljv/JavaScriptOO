package br.edu.unochapeco.natanael.vieira.leitores;

import br.edu.unochapeco.natanael.vieira.entidades.Classe;
import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import br.edu.unochapeco.natanael.vieira.entidades.DeclaracaoVariavel;
import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.enumeradores.TipoDeclaracao;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorClasse;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.*;
import java.util.Optional;

final class LeitorDeclaracao {
    private String _identificacaoProprietario;

    public LeitorDeclaracao(String identificacaoProprietario) {
        _identificacaoProprietario = identificacaoProprietario;
    }

    public String ler(EscopoDeclaracao escopoPai, BlockStmt blocoDeclaracoes) {
        return ler(escopoPai, blocoDeclaracoes.getStatements());
    }

    public String ler(EscopoDeclaracao escopoPai, NodeList<Statement> declaracoes) {
        StringBuilder declaracaoJS = new StringBuilder();
        EscopoDeclaracao escopo = new EscopoDeclaracao(escopoPai.getIdentificacaoEscopo(), escopoPai.getNivelIdentacao() + 1);

        GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificacaoProprietario).adicionarEscopo(escopo);

        declaracoes.forEach(declaracao -> {
            switch (getTipoDeclaracao(declaracao)) {
                case BREAK: {
                    declaracaoJS.append(String.format("%sbreak;", escopo.getIdentacao()));
                    declaracaoJS.append(System.lineSeparator());

                    break;
                }

                case CHAMADA_CONSTRUTOR_PAI: {
                    Classe classe = GerenciadorClasse.getInstance().getClasse(GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificacaoProprietario).getNomeClasse());

                    try {
                        if (classe.getNomeClassePai() == null) {
                            throw new ExpressaoNaoSuportadaException(String.format("Método [super] não encontrado na definição da classe"));
                        }

                        StringBuilder parametrosJS = new StringBuilder();
                        ExplicitConstructorInvocationStmt declaracaoChamadaConstrutorPai = (ExplicitConstructorInvocationStmt) declaracao;
                        LeitorExpressao leitorExpressao = new LeitorExpressao(_identificacaoProprietario);
                        NodeList<Expression> parametros = declaracaoChamadaConstrutorPai.getArguments();

                        for (int i = 0; i < parametros.size(); i++) {
                            if (i > 0) {
                                parametrosJS.append(", ");
                            }

                            parametrosJS.append(leitorExpressao.ler(escopo, parametros.get(i)));
                        }

                        declaracaoJS.append(String.format("%s%s.call(this, %s);", escopo.getIdentacao(), classe.getNomeClassePai(), parametrosJS.toString()));
                        declaracaoJS.append(System.lineSeparator());
                    } catch (ExpressaoNaoSuportadaException excecao) {
                        if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                            declaracaoJS.append(String.format("%s/* %s", escopo.getIdentacao(), excecao.getMessage()));
                            declaracaoJS.append(System.lineSeparator());
                            declaracaoJS.append(String.format("%s%s", escopo.getIdentacao(), declaracao.toString()));
                            declaracaoJS.append(System.lineSeparator());
                            declaracaoJS.append(String.format("%s*/", escopo.getIdentacao()));
                            declaracaoJS.append(System.lineSeparator());
                        }
                        else {
                            declaracaoJS.append(String.format("%s// %s", escopo.getIdentacao(), declaracao.toString()));
                            declaracaoJS.append(System.lineSeparator());
                        }
                    }

                    break;
                }

                case CONTINUE: {
                    declaracaoJS.append(String.format("%scontinue;", escopo.getIdentacao()));
                    declaracaoJS.append(System.lineSeparator());

                    break;
                }

                case DO_WHILE: {
                    LeitorDeclaracaoDoWhile leitorWhile = new LeitorDeclaracaoDoWhile(_identificacaoProprietario);

                    declaracaoJS.append(leitorWhile.ler(escopo, (DoStmt) declaracao));

                    break;
                }

                case EXPRESSAO: {
                    LeitorExpressao leitorValor = new LeitorExpressao(_identificacaoProprietario);

                    try {
                        String expressao = leitorValor.ler(escopo, ((ExpressionStmt) declaracao).getExpression());
                        declaracaoJS.append(String.format("%s%s;", escopo.getIdentacao(), expressao));
                        declaracaoJS.append(System.lineSeparator());
                    } catch (ExpressaoNaoSuportadaException excecao) {
                        if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                            declaracaoJS.append(String.format("%s/* %s", escopo.getIdentacao(), excecao.getMessage()));
                            declaracaoJS.append(System.lineSeparator());
                            declaracaoJS.append(String.format("%s%s", escopo.getIdentacao(), declaracao.toString()));
                            declaracaoJS.append(System.lineSeparator());
                            declaracaoJS.append(String.format("%s*/", escopo.getIdentacao()));
                            declaracaoJS.append(System.lineSeparator());
                        }
                        else {
                            declaracaoJS.append(String.format("%s// %s", escopo.getIdentacao(), declaracao.toString()));
                            declaracaoJS.append(System.lineSeparator());
                        }
                    }

                    break;
                }

                case IF: {
                    LeitorDeclaracaoIf leitorIf = new LeitorDeclaracaoIf(_identificacaoProprietario);

                    declaracaoJS.append(leitorIf.ler(escopo, (IfStmt) declaracao));

                    break;
                }

                case FOR: {
                    LeitorDeclaracaoFor leitorFor = new LeitorDeclaracaoFor(_identificacaoProprietario);

                    declaracaoJS.append(leitorFor.ler(escopo, (ForStmt) declaracao));

                    break;
                }

                case NAO_SUPORTADO: {
                    String declaracaoNaoSuportada = declaracao.toString().replace(System.lineSeparator(), String.format("%s%s", System.lineSeparator(), escopo.getIdentacao()));

                    if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                        declaracaoJS.append(String.format("%s/* Declaração não suportada na versão atual", escopo.getIdentacao()));
                        declaracaoJS.append(System.lineSeparator());
                        declaracaoJS.append(String.format("%s%s", escopo.getIdentacao(), declaracaoNaoSuportada));
                        declaracaoJS.append(System.lineSeparator());
                        declaracaoJS.append(String.format("%s*/", escopo.getIdentacao()));
                        declaracaoJS.append(System.lineSeparator());
                    }
                    else {
                        declaracaoJS.append(String.format("%s// %s", escopo.getIdentacao(), declaracaoNaoSuportada));
                        declaracaoJS.append(System.lineSeparator());
                    }

                    break;
                }

                case VARIAVEL: {
                    LeitorDeclaracaoVariavel letorVariavel = new LeitorDeclaracaoVariavel(_identificacaoProprietario);
                    VariableDeclarationExpr expressaoDeclaracao = (VariableDeclarationExpr)(((ExpressionStmt) declaracao).getExpression());

                    try {
                        DeclaracaoVariavel declaracaoVariavel = letorVariavel.ler(escopo, expressaoDeclaracao);

                        declaracaoJS.append(declaracaoVariavel.getComentario());
                        declaracaoJS.append(String.format("%s%s;", escopo.getIdentacao(), declaracaoVariavel.getDeclaracao()));
                        declaracaoJS.append(System.lineSeparator());
                    } catch (ExpressaoNaoSuportadaException excecao) {
                        if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                            declaracaoJS.append(String.format("%s/* %s", escopo.getIdentacao(), excecao.getMessage()));
                            declaracaoJS.append(System.lineSeparator());
                            declaracaoJS.append(String.format("%s%s", escopo.getIdentacao(), expressaoDeclaracao.toString()));
                            declaracaoJS.append(System.lineSeparator());
                            declaracaoJS.append(String.format("%s*/", escopo.getIdentacao()));
                            declaracaoJS.append(System.lineSeparator());
                        }
                        else {
                            declaracaoJS.append(String.format("%s// %s", escopo.getIdentacao(), expressaoDeclaracao.toString()));
                            declaracaoJS.append(System.lineSeparator());
                        }
                    }

                    break;
                }

                case WHILE: {
                    LeitorDeclaracaoWhile leitorWhile = new LeitorDeclaracaoWhile(_identificacaoProprietario);

                    declaracaoJS.append(leitorWhile.ler(escopo, (WhileStmt) declaracao));

                    break;
                }

                case RETURN: {
                    try {
                        Optional<Expression> expressao = ((ReturnStmt) declaracao).getExpression();
                        String expressaoRetorno = "";

                        if (expressao.isPresent()) {
                            LeitorExpressao leitorExpressao = new LeitorExpressao(_identificacaoProprietario);
                            expressaoRetorno = String.format(" %s", leitorExpressao.ler(escopo, expressao.get()));
                        }

                        declaracaoJS.append(String.format("%sreturn%s;", escopo.getIdentacao(), expressaoRetorno));
                        declaracaoJS.append(System.lineSeparator());
                    } catch (ExpressaoNaoSuportadaException excecao) {
                        if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                            declaracaoJS.append(String.format("%s/* %s", escopo.getIdentacao(), excecao.getMessage()));
                            declaracaoJS.append(System.lineSeparator());
                            declaracaoJS.append(String.format("%s%s", escopo.getIdentacao(), declaracao.toString()));
                            declaracaoJS.append(System.lineSeparator());
                            declaracaoJS.append(String.format("%s*/", escopo.getIdentacao()));
                            declaracaoJS.append(System.lineSeparator());
                        }
                        else {
                            declaracaoJS.append(String.format("%s// %s", escopo.getIdentacao(), declaracao.toString()));
                            declaracaoJS.append(System.lineSeparator());
                        }
                    }

                    break;
                }

                case SWITCH: {
                    LeitorDeclaracaoSwitch leitorSwitch = new LeitorDeclaracaoSwitch(_identificacaoProprietario);

                    declaracaoJS.append(leitorSwitch.ler(escopo, (SwitchStmt) declaracao));

                    break;
                }
            }
        });

        return declaracaoJS.toString();
    }

    private TipoDeclaracao getTipoDeclaracao(Statement declaracao) {
        if (declaracao instanceof BreakStmt) {
            return TipoDeclaracao.BREAK;
        }
        else if (declaracao instanceof ExplicitConstructorInvocationStmt) {
            return TipoDeclaracao.CHAMADA_CONSTRUTOR_PAI;
        }
        else if (declaracao instanceof ContinueStmt) {
            return TipoDeclaracao.CONTINUE;
        }
        else if (declaracao instanceof DoStmt) {
            return TipoDeclaracao.DO_WHILE;
        }
        else if (declaracao instanceof ExpressionStmt) {
            if (((ExpressionStmt) declaracao).getExpression() instanceof VariableDeclarationExpr) {
                return TipoDeclaracao.VARIAVEL;
            }

            return TipoDeclaracao.EXPRESSAO;
        }
        else if (declaracao instanceof ForStmt) {
            return TipoDeclaracao.FOR;
        }
        else if (declaracao instanceof IfStmt) {
            return TipoDeclaracao.IF;
        }
        else if (declaracao instanceof WhileStmt) {
            return TipoDeclaracao.WHILE;
        }
        else if (declaracao instanceof ReturnStmt) {
            return TipoDeclaracao.RETURN;
        }
        else if (declaracao instanceof SwitchStmt) {
            return TipoDeclaracao.SWITCH;
        }

        return TipoDeclaracao.NAO_SUPORTADO;
    }
}