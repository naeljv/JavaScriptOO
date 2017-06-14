package br.edu.unochapeco.natanael.vieira.leitores;

import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorClasse;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class LeitorDeclaracaoIf {
    private String _identificacaoProprietario;
    private List<IfStmt> _listaDeclaracaoCondicional;
    private BlockStmt _blocoElse;

    public LeitorDeclaracaoIf(String identificacaoProprietario) {
        _identificacaoProprietario = identificacaoProprietario;
    }

    public String ler(EscopoDeclaracao escopoPai, IfStmt declaracao) {
        StringBuilder declaracaoCondicionalJS = new StringBuilder();
        EscopoDeclaracao escopo = new EscopoDeclaracao(escopoPai.getIdentificacaoEscopo(), escopoPai.getNivelIdentacao());
        LeitorDeclaracao leitorDeclaracao = new LeitorDeclaracao(_identificacaoProprietario);
        LeitorExpressao leitorCondicional = new LeitorExpressao(_identificacaoProprietario);
        int totalDeclaracoesIf = 0;

        _listaDeclaracaoCondicional = new ArrayList<>();
        _blocoElse = null;

        GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificacaoProprietario).adicionarEscopo(escopo);
        _listaDeclaracaoCondicional.add(declaracao);

        carregarListaDeclaracaoCondicional(declaracao);

        for(int i = 0; i < _listaDeclaracaoCondicional.size(); i++) {
            IfStmt declaracaoIf = _listaDeclaracaoCondicional.get(i);

            try {
                String condicaoJS = leitorCondicional.ler(escopo, declaracaoIf.getCondition());

                totalDeclaracoesIf += 1;

                declaracaoCondicionalJS.append(String.format("%s%sif (%s) {", escopo.getIdentacao(), (totalDeclaracoesIf > 1) ? "else " : "", condicaoJS));
                declaracaoCondicionalJS.append(System.lineSeparator());
                declaracaoCondicionalJS.append(leitorDeclaracao.ler(escopo, (BlockStmt) declaracaoIf.getThenStmt()));
                declaracaoCondicionalJS.append(String.format("%s}", escopo.getIdentacao()));
                declaracaoCondicionalJS.append(System.lineSeparator());
            }
            catch (ExpressaoNaoSuportadaException excecao) {
                if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                    declaracaoCondicionalJS.append(String.format("%s/* %s", escopo.getIdentacao(), excecao.getMessage()));
                }
                else {
                    declaracaoCondicionalJS.append(String.format("%s/*", escopo.getIdentacao()));
                }

                declaracaoCondicionalJS.append(System.lineSeparator());
                declaracaoCondicionalJS.append(String.format("%s%sif (%s) {", escopo.getIdentacao(), (totalDeclaracoesIf > 1) ? "else " : "", declaracaoIf.getCondition().toString()));
                declaracaoCondicionalJS.append(System.lineSeparator());
                declaracaoCondicionalJS.append(String.format("%s%sBloco de código removido", escopo.getIdentacao(), AuxiliarConversao.getInstance().getIdentacao(1)));
                declaracaoCondicionalJS.append(System.lineSeparator());
                declaracaoCondicionalJS.append(String.format("%s}", escopo.getIdentacao()));
                declaracaoCondicionalJS.append(System.lineSeparator());
                declaracaoCondicionalJS.append(String.format("%s*/", escopo.getIdentacao()));
                declaracaoCondicionalJS.append(System.lineSeparator());
            }
        }

        if (_blocoElse != null) {
            if (totalDeclaracoesIf == 0) {
                if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                    declaracaoCondicionalJS.append(String.format("%s/* Declaração else comentada, não existe nenhuma declaração if", escopo.getIdentacao()));
                }
                else {
                    declaracaoCondicionalJS.append(String.format("%s/*", escopo.getIdentacao()));
                }

                declaracaoCondicionalJS.append(System.lineSeparator());
                declaracaoCondicionalJS.append(String.format("%selse {", escopo.getIdentacao()));
                declaracaoCondicionalJS.append(System.lineSeparator());
                declaracaoCondicionalJS.append(String.format("%s%sBloco de código removido", escopo.getIdentacao(), AuxiliarConversao.getInstance().getIdentacao(1)));
                declaracaoCondicionalJS.append(System.lineSeparator());
                declaracaoCondicionalJS.append(String.format("%s}", escopo.getIdentacao()));
                declaracaoCondicionalJS.append(System.lineSeparator());
                declaracaoCondicionalJS.append(String.format("%s*/", escopo.getIdentacao()));
                declaracaoCondicionalJS.append(System.lineSeparator());
            }
            else {
                declaracaoCondicionalJS.append(String.format("%selse {", escopo.getIdentacao()));
                declaracaoCondicionalJS.append(System.lineSeparator());
                declaracaoCondicionalJS.append(leitorDeclaracao.ler(escopo, _blocoElse.getStatements()));
                declaracaoCondicionalJS.append(String.format("%s}", escopo.getIdentacao()));
                declaracaoCondicionalJS.append(System.lineSeparator());
            }
        }

        return declaracaoCondicionalJS.toString();
    }

    private void carregarListaDeclaracaoCondicional(IfStmt declaracaoCondicional) {
        Optional<Statement> declaracao = declaracaoCondicional.getElseStmt();

        if (declaracao.isPresent()) {
            if (declaracao.get() instanceof IfStmt) {
                IfStmt declaracaoElseIf = (IfStmt) declaracao.get();

                _listaDeclaracaoCondicional.add(declaracaoElseIf);

                carregarListaDeclaracaoCondicional(declaracaoElseIf);
            }
            else {
                _blocoElse = (BlockStmt) declaracao.get();
            }
        }
    }
}