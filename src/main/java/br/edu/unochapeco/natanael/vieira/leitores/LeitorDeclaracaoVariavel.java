package br.edu.unochapeco.natanael.vieira.leitores;

import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import br.edu.unochapeco.natanael.vieira.entidades.DeclaracaoVariavel;
import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.entidades.Variavel;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorClasse;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class LeitorDeclaracaoVariavel {
    private String _identificacaoProprietario;

    public LeitorDeclaracaoVariavel(String identificacaoProprietario) {
        _identificacaoProprietario = identificacaoProprietario;
    }

    public DeclaracaoVariavel ler(EscopoDeclaracao escopo, VariableDeclarationExpr expressaoDeclaracaoVariavel) throws ExpressaoNaoSuportadaException {
        return ler(escopo, expressaoDeclaracaoVariavel, true);
    }

    public DeclaracaoVariavel ler(EscopoDeclaracao escopo, VariableDeclarationExpr expressaoDeclaracaoVariavel, boolean permitirVariaveisNaoConvertidas) throws ExpressaoNaoSuportadaException {
        StringBuilder comentarioAlteracaoNomeVariavel = new StringBuilder();
        StringBuilder comentarioVariavelNaoConvertida = new StringBuilder();
        List<String> declaracoes = new ArrayList<>();
        NodeList<VariableDeclarator> variaveis = expressaoDeclaracaoVariavel.getVariables();
        Type tipo = variaveis.get(0).getType();
        String mensagemErro = "";
        boolean tipoSuportado =  AuxiliarConversao.getInstance().validarTipoSuportado(tipo);
        int totalComentarios = 0;
        int totalVariaveisNaoConvertidas = 0;

        for (int i = 0; i < variaveis.size(); i++) {
            VariableDeclarator variavel = variaveis.get(i);
            String nomeVariavelJava = variavel.getNameAsString();

            if (tipoSuportado && (permitirVariaveisNaoConvertidas || mensagemErro.equals(""))) {
                String nomeVariavelJS = GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificacaoProprietario).getNomeVariavelJSAdicionar(nomeVariavelJava);
                String valorInicialVariavel = "";
                Optional<Expression> valorInicial = variavel.getInitializer();

                if (valorInicial.isPresent()) {
                    LeitorExpressao leitorAtribuicao = new LeitorExpressao(_identificacaoProprietario);

                    try{
                        valorInicialVariavel = String.format(" = %s", leitorAtribuicao.ler(escopo, valorInicial.get()));
                    }
                    catch (ExpressaoNaoSuportadaException excecao) {
                        if (permitirVariaveisNaoConvertidas) {
                            escopo.adicionarVariavel(new Variavel(nomeVariavelJava, null));

                            if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                                comentarioVariavelNaoConvertida.append(String.format("%s%s - %s", escopo.getIdentacao(), variavel.toString(), excecao.getMessage()));
                            }
                            else {
                                comentarioVariavelNaoConvertida.append(String.format("%s// %s", escopo.getIdentacao(), variavel.toString()));
                            }

                            comentarioVariavelNaoConvertida.append(System.lineSeparator());
                            totalVariaveisNaoConvertidas++;
                        }
                        else {
                            mensagemErro = excecao.getMessage();
                        }

                        continue;
                    }
                }

                if (!nomeVariavelJS.equals(nomeVariavelJava)) {
                    comentarioAlteracaoNomeVariavel.append(String.format("%s%s -> %s", escopo.getIdentacao(), nomeVariavelJava, nomeVariavelJS));
                    comentarioAlteracaoNomeVariavel.append(System.lineSeparator());
                    totalComentarios++;
                }

                escopo.adicionarVariavel(new Variavel(nomeVariavelJava, nomeVariavelJS));
                declaracoes.add(String.format("%s%s", nomeVariavelJS, valorInicialVariavel));
            }
            else {
                escopo.adicionarVariavel(new Variavel(nomeVariavelJava, null));
            }
        }

        if (!tipoSuportado) {
            throw new ExpressaoNaoSuportadaException(String.format("Tipo de dado não suportado na versão atual: [%s]", tipo.toString()));
        }

        if (!(permitirVariaveisNaoConvertidas || mensagemErro.equals(""))) {
            throw new ExpressaoNaoSuportadaException(mensagemErro);
        }

        if (declaracoes.size() > 0) {
            StringBuilder declaracaoVariaveisJS = new StringBuilder();
            StringBuilder comentario = new StringBuilder();

            if (totalComentarios > 0) {
                if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                    if (totalComentarios > 1) {
                        comentario.append(String.format("%s/* As seguintes variáveis tiveram seus nomes alterados", escopo.getIdentacao()));
                        comentario.append(System.lineSeparator());
                        comentario.append(String.format("%s", comentarioAlteracaoNomeVariavel.toString()));
                        comentario.append(String.format("%s*/", escopo.getIdentacao()));
                        comentario.append(System.lineSeparator());
                    } else {
                        comentario.append(String.format("%s// O nome da variável foi alterado: %s", escopo.getIdentacao(), comentarioAlteracaoNomeVariavel.toString().trim()));
                        comentario.append(System.lineSeparator());
                    }
                }
            }

            if (totalVariaveisNaoConvertidas > 0) {
                if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                    String cabecalhoComentario = totalVariaveisNaoConvertidas > 1 ? "As seguintes declarações apresentaram erro na conversão" : "A seguinte declaração apresentou erro na conversão";

                    comentario.append(String.format("%s/* %s", escopo.getIdentacao(), cabecalhoComentario));
                    comentario.append(System.lineSeparator());
                    comentario.append(comentarioVariavelNaoConvertida.toString());
                    comentario.append(String.format("%s*/", escopo.getIdentacao()));
                    comentario.append(System.lineSeparator());
                }
                else {
                    comentario.append(comentarioVariavelNaoConvertida.toString());
                }
            }

            declaracaoVariaveisJS.append("var ");

            for (int i = 0; i < declaracoes.size(); i++) {
                declaracaoVariaveisJS.append(declaracoes.get(i));

                if (i < (declaracoes.size() - 1)) {
                    declaracaoVariaveisJS.append(", ");
                }
            }

            return new DeclaracaoVariavel(declaracaoVariaveisJS.toString(), comentario.toString());
        }
        else {
            StringBuilder excecao = new StringBuilder();
            String cabecalhoComentario = totalVariaveisNaoConvertidas > 1 ? "As seguintes variáveis apresentaram erro na conversão" : "A seguinte variávei apresentou erro na conversão";

            excecao.append(String.format("%s", cabecalhoComentario));
            excecao.append(System.lineSeparator());
            excecao.append(String.format("%s ", comentarioVariavelNaoConvertida.toString()));

            throw new ExpressaoNaoSuportadaException(excecao.toString());
        }
    }
}