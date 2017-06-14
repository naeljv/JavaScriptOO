package br.edu.unochapeco.natanael.vieira.leitores;

import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import br.edu.unochapeco.natanael.vieira.entidades.Classe;
import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.entidades.Metodo;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;

import java.util.*;
import java.util.stream.Stream;

public final class LeitorMetodo {
    private Classe _classe;
    private Map<String, String> _declaracoesMetodos;

    public LeitorMetodo(Classe classe) {
        _classe = classe;
        _declaracoesMetodos = new HashMap<>();
    }

    public void lerCabecalho(List<BodyDeclaration<?>> declaracoes) {
        declaracoes.forEach(declaracao -> {
            MethodDeclaration declaracaoMetodo = (MethodDeclaration) declaracao;
            StringBuilder metodoJS = new StringBuilder();
            int nivelIdentacao = 1;
            String prototipo = "";
            String simboloAtribuicaoFuncao = ":";

            if (_classe.getNomeClassePai() != null) {
                nivelIdentacao = 0;
                prototipo = String.format("%s.prototype.", _classe.getNome());
                simboloAtribuicaoFuncao = " =";
            }

            String nomeJava = declaracaoMetodo.getNameAsString();
            boolean modificadorAcessoPublico = !(declaracaoMetodo.isPrivate() || declaracaoMetodo.isProtected());
            List<String> tipoParametros = new ArrayList<>();

            declaracaoMetodo.getParameters().forEach(parametro -> {
                tipoParametros.add(parametro.getType().toString());
            });

            Metodo metodo = new Metodo(_classe.getNome(), nomeJava, modificadorAcessoPublico, tipoParametros);
            EscopoDeclaracao escopoRaiz = new EscopoDeclaracao(null, nivelIdentacao);
            String identacao = escopoRaiz.getIdentacao();

            metodo.getGerenciadorEscopoDeclaracao().adicionarEscopo(escopoRaiz);
            _classe.getGerenciadorMetodo().adicionarMetodo(metodo, declaracaoMetodo);

            try {
                Type tipoRetorno = declaracaoMetodo.getType();

                if (!((tipoRetorno instanceof VoidType) || (AuxiliarConversao.getInstance().validarTipoSuportado(tipoRetorno)))) {
                    throw new ExpressaoNaoSuportadaException(String.format("Tipo de dado de retorno não suportado na versão atual: [%s]", tipoRetorno.toString()));
                }

                if (declaracaoMetodo.isAbstract()) {
                    throw new ExpressaoNaoSuportadaException("Métodos abstratos não são convertidos");
                }

                if (declaracaoMetodo.isStatic()) {
                    throw new ExpressaoNaoSuportadaException("Métodos estáticos não são convertidos");
                }

                LeitorParametro leitorParametro = new LeitorParametro();
                String parametros = leitorParametro.ler(escopoRaiz, declaracaoMetodo.getParameters());

                metodo.setNomeJS(_classe.getGerenciadorMetodo().getNomeMetodoAdicionar(metodo.getNomeJava(), metodo.isPublico()));

                metodoJS.append(String.format("%s%s%s%s function(%s)", identacao, prototipo, metodo.getNomeJS(), simboloAtribuicaoFuncao, parametros));
            } catch (ExpressaoNaoSuportadaException excecao) {
                if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                    metodoJS.append(String.format("%s/* %s", identacao, excecao.getMessage()));
                }
                else {
                    metodoJS.append(String.format("%s/*", identacao));
                }

                metodoJS.append(System.lineSeparator());

                declaracaoMetodo.removeJavaDocComment();

                String metodoJava = declaracaoMetodo.toString();
                Optional<BlockStmt> metodoDeclaracoes = declaracaoMetodo.getBody();

                if (metodoDeclaracoes.isPresent()) {
                    metodoJava = metodoJava.replace(metodoDeclaracoes.get().toString(), "");
                }

                metodoJS.append(String.format("%s%s{", identacao, metodoJava));
                metodoJS.append(System.lineSeparator());
                metodoJS.append(String.format("%sBloco de código removido", AuxiliarConversao.getInstance().getIdentacao(nivelIdentacao + 1)));
                metodoJS.append(System.lineSeparator());
                metodoJS.append(String.format("%s}", identacao));
                metodoJS.append(System.lineSeparator());
                metodoJS.append(String.format("%s*/", identacao));
                metodoJS.append(System.lineSeparator());
            }

            _declaracoesMetodos.put(metodo.getIdentificador(), metodoJS.toString());
        });
    }

    public String lerEscopo() {
        StringBuilder metodosJS = new StringBuilder();
        int indiceUltimoMetodoConvertidoJS = _classe.getGerenciadorMetodo().getIndiceUltimoMetodoConvertidoJS();

        for (int i = 0; i < _classe.getGerenciadorMetodo().getMetodos().size(); i++) {
            Metodo metodo = _classe.getGerenciadorMetodo().getMetodos().get(i);
            EscopoDeclaracao escopo = metodo.getGerenciadorEscopoDeclaracao().getEscopoRaiz();

            if (metodo.isConvertidoJS() && !metodo.getNomeJava().equals(metodo.getNomeJS()) && AuxiliarConversao.getInstance().isExibirMensagens()) {
                metodosJS.append(String.format("%s// O nome do método foi alterado: %s -> %s", escopo.getIdentacao(), metodo.getNomeJava(), metodo.getNomeJS()));
                metodosJS.append(System.lineSeparator());
            }

            metodosJS.append(_declaracoesMetodos.get(metodo.getIdentificador()));

            if (metodo.isConvertidoJS()) {
                LeitorDeclaracao leitorDeclaracao = new LeitorDeclaracao(metodo.getIdentificador());
                BlockStmt blocoDeclaracoes = _classe.getGerenciadorMetodo().getBlocoDeclaracao(metodo.getIdentificador());
                String simboloFinalMetodo = _classe.getNomeClassePai() == null ? "," : String.format(";%s", System.lineSeparator());

                if ((_classe.getNomeClassePai() == null) && (i >= indiceUltimoMetodoConvertidoJS)) {
                    simboloFinalMetodo = "";
                }

                metodosJS.append(" {");
                metodosJS.append(System.lineSeparator());

                if (blocoDeclaracoes != null) {
                    metodosJS.append(leitorDeclaracao.ler(escopo, blocoDeclaracoes));
                }

                metodosJS.append(String.format("%s}%s", escopo.getIdentacao(), simboloFinalMetodo));
                metodosJS.append(System.lineSeparator());
            }
        }

        return metodosJS.toString();
    }
}