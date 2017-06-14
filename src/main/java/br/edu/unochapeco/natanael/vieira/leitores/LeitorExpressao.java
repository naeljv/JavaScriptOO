package br.edu.unochapeco.natanael.vieira.leitores;

import br.edu.unochapeco.natanael.vieira.entidades.Classe;
import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.enumeradores.TipoExpressao;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorClasse;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;

import java.util.Optional;

final class LeitorExpressao {
    private String _identificacaoProprietario;
    private EscopoDeclaracao _escopo;

    public LeitorExpressao(String identificacaoProprietario) {
        _identificacaoProprietario = identificacaoProprietario;
    }

    public String ler(EscopoDeclaracao escopo, Expression expressao) throws ExpressaoNaoSuportadaException {
        _escopo = escopo;

        return lerExpressaoTipo(expressao);
    }

    private String lerExpressaoAtribuicao(AssignExpr expressaoAtribuicao) throws ExpressaoNaoSuportadaException {
        StringBuilder retorno = new StringBuilder();
        Expression objeto = expressaoAtribuicao.getTarget();
        Expression valor = expressaoAtribuicao.getValue();
        String operador = expressaoAtribuicao.getOperator().asString();

        retorno.append(lerExpressaoTipo(objeto));
        retorno.append(String.format(" %s ", operador));
        retorno.append(lerExpressaoTipo(valor));

        return retorno.toString();
    }

    private String lerExpressaoBinaria(BinaryExpr expressaoBinaria) throws ExpressaoNaoSuportadaException {
        StringBuilder retorno = new StringBuilder();
        Expression expressaoDireita = expressaoBinaria.getRight();
        Expression expressaoEsquerda = expressaoBinaria.getLeft();
        String operador = expressaoBinaria.getOperator().asString();

        retorno.append(lerExpressaoTipo(expressaoEsquerda));
        retorno.append(String.format(" %s ", operador));
        retorno.append(lerExpressaoTipo(expressaoDireita));

        return retorno.toString();
    }

    private String lerExpressaoChamadaMetodo(MethodCallExpr chamadaMetodo) throws ExpressaoNaoSuportadaException {
        StringBuilder parametrosJS = new StringBuilder();
        String nomeMetodoJava = chamadaMetodo.getNameAsString();
        NodeList<Expression> parametros = chamadaMetodo.getArguments();
        int totalParametros = parametros.size();

        for (int i = 0; i < totalParametros; i++) {
            if (i > 0) {
                parametrosJS.append(", ");
            }

            parametrosJS.append(lerExpressaoTipo(parametros.get(i)));
        }

        Classe classe = GerenciadorClasse.getInstance().getClasse(GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificacaoProprietario).getNomeClasse());
        Optional<Expression> escopoChamada = chamadaMetodo.getScope();
        String nomeMetodoJS = "";

        if (escopoChamada.isPresent() && (escopoChamada.get() instanceof SuperExpr)) {
            nomeMetodoJS = classe.getGerenciadorMetodo().getNomeMetodoJSClassePai(nomeMetodoJava, totalParametros);
            String parametrosJsClassePai = parametrosJS.toString().equals("") ? "" : String.format(", %s", parametrosJS.toString());
            return String.format("%s.prototype.%s.call(this%s)", classe.getNomeClassePai(), nomeMetodoJS, parametrosJsClassePai);
        }
        else {
            nomeMetodoJS = classe.getGerenciadorMetodo().getNomeMetodoJS(nomeMetodoJava, totalParametros);
            return String.format("this.%s(%s)", nomeMetodoJS, parametrosJS.toString());
        }
    }

    private String lerExpressaoCondicional(ConditionalExpr expressaoCondicional) throws ExpressaoNaoSuportadaException {
        StringBuilder retorno = new StringBuilder();
        Expression condicao = expressaoCondicional.getCondition();
        Expression expressaoVerdadeira = expressaoCondicional.getThenExpr();
        Expression expressaoFalsa = expressaoCondicional.getElseExpr();

        retorno.append(lerExpressaoTipo(condicao));
        retorno.append(" ? ");
        retorno.append(lerExpressaoTipo(expressaoVerdadeira));
        retorno.append(" : ");
        retorno.append(lerExpressaoTipo(expressaoFalsa));

        return retorno.toString();
    }

    private String lerExpressaoEntreParenteses(EnclosedExpr expressaoEntreParenteses) throws ExpressaoNaoSuportadaException {
        Optional<Expression> expressao = expressaoEntreParenteses.getInner();

        if (expressao.isPresent()) {
            return lerExpressaoTipo(expressao.get());
        }

        return "";
    }

    private String lerExpressaoTipo(Expression expressao) throws ExpressaoNaoSuportadaException {
        TipoExpressao tipoExpressao = getTipoExpressao(expressao);

        switch (tipoExpressao) {
            case ATRIBUICAO: {
                return lerExpressaoAtribuicao((AssignExpr) expressao);
            }

            case BINARIA: {
                return lerExpressaoBinaria((BinaryExpr) expressao);
            }

            case OPERADOR_CONDICIONAL: {
                return lerExpressaoCondicional((ConditionalExpr) expressao);
            }

            case ENTRE_PARENTESES: {
                StringBuilder retorno = new StringBuilder();

                retorno.append("(");
                retorno.append(lerExpressaoEntreParenteses((EnclosedExpr) expressao));
                retorno.append(")");

                return retorno.toString();
            }

            case UNARIA: {
                return lerExpressaoUnaria((UnaryExpr) expressao);
            }

            default: {
                return lerExpressaoValor(expressao);
            }
        }
    }

    private String lerExpressaoUnaria(UnaryExpr expressaoUnaria) throws ExpressaoNaoSuportadaException {
        StringBuilder retorno = new StringBuilder();
        Expression expressao = expressaoUnaria.getExpression();
        UnaryExpr.Operator operador = expressaoUnaria.getOperator();

        if (operador.isPrefix()) {
            retorno.append(operador.asString());
            retorno.append(lerExpressaoTipo(expressao));
        }
        else {
            retorno.append(lerExpressaoTipo(expressao));
            retorno.append(operador.asString());
        }

        return retorno.toString();
    }

    private String lerExpressaoValor(Expression expressaoValor) throws ExpressaoNaoSuportadaException {
        TipoExpressao tipoExpressao = getTipoExpressao(expressaoValor);

        switch (tipoExpressao) {
            case ATRIBUTO: {
                Classe classe = GerenciadorClasse.getInstance().getClasse(GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificacaoProprietario).getNomeClasse());
                return classe.getGerenciadorAtributo().getNomeAtrubutoJS(((FieldAccessExpr) expressaoValor).getNameAsString());
            }

            case BOOLEAN_LITERAL: {
                return ((BooleanLiteralExpr) expressaoValor).getValue() ? "true" : "false";
            }

            case CHAMADA_METODO: {
                return lerExpressaoChamadaMetodo((MethodCallExpr) expressaoValor);
            }

            case CHAR_LITERAL: {
                return String.format("\"%s\"", ((CharLiteralExpr) expressaoValor).getValue());
            }

            case DOUBLE_LITERAL: {
                return ((DoubleLiteralExpr) expressaoValor).getValue();
            }

            case INTEGER_LITERAL: {
                return ((IntegerLiteralExpr) expressaoValor).getValue();
            }

            case NULL_LITERAL: {
                return "null";
            }

            case STRING_LITERAL: {
                return String.format("\"%s\"", ((StringLiteralExpr) expressaoValor).getValue());
            }

            case VARIAVEL: {
                return GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificacaoProprietario).getNomeVariavelJS(_escopo, expressaoValor.toString());
            }

            default: {
                throw new ExpressaoNaoSuportadaException(String.format("Expressão não suportada na versão atual: [%s]", expressaoValor.toString()));
            }
        }
    }

    private TipoExpressao getTipoExpressao(Expression expressao) {
        if (expressao instanceof AssignExpr) {
            return TipoExpressao.ATRIBUICAO;
        }
        else if (expressao instanceof FieldAccessExpr) {
            return TipoExpressao.ATRIBUTO;
        }
        else if (expressao instanceof BinaryExpr) {
            return TipoExpressao.BINARIA;
        }
        else if (expressao instanceof BooleanLiteralExpr) {
            return TipoExpressao.BOOLEAN_LITERAL;
        }
        else if(expressao instanceof MethodCallExpr) {
            return TipoExpressao.CHAMADA_METODO;
        }
        else if (expressao instanceof CharLiteralExpr) {
            return TipoExpressao.CHAR_LITERAL;
        }
        else if (expressao instanceof DoubleLiteralExpr) {
            return TipoExpressao.DOUBLE_LITERAL;
        }
        else if (expressao instanceof EnclosedExpr) {
            return TipoExpressao.ENTRE_PARENTESES;
        }
        else if (expressao instanceof IntegerLiteralExpr) {
            return TipoExpressao.INTEGER_LITERAL;
        }
        else if (expressao instanceof NullLiteralExpr) {
            return TipoExpressao.NULL_LITERAL;
        }
        else if (expressao instanceof ConditionalExpr) {
            return TipoExpressao.OPERADOR_CONDICIONAL;
        }
        else if (expressao instanceof StringLiteralExpr) {
            return TipoExpressao.STRING_LITERAL;
        }
        else if (expressao instanceof UnaryExpr) {
            return TipoExpressao.UNARIA;
        }
        else if (expressao instanceof NameExpr) {
            return TipoExpressao.VARIAVEL;
        }
        else {
            return TipoExpressao.NAO_SUPORTADA;
        }
    }
}