package br.edu.unochapeco.natanael.vieira.leitores;

import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorClasse;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.DoStmt;

final class LeitorDeclaracaoDoWhile {
    private String _identificacaoProprietario;

    public LeitorDeclaracaoDoWhile(String identificacaoProprietario) {
        _identificacaoProprietario = identificacaoProprietario;
    }

    public String ler(EscopoDeclaracao escopoPai, DoStmt declaracao) {
        StringBuilder declaracaoDoWhileJS = new StringBuilder();
        EscopoDeclaracao escopo = new EscopoDeclaracao(escopoPai.getIdentificacaoEscopo(), escopoPai.getNivelIdentacao());

        GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificacaoProprietario).adicionarEscopo(escopo);

        try {
            LeitorDeclaracao leitorDeclaracao = new LeitorDeclaracao(_identificacaoProprietario);
            LeitorExpressao leitorCondicional = new LeitorExpressao(_identificacaoProprietario);
            String condicaoJS = leitorCondicional.ler(escopo, declaracao.getCondition());

            declaracaoDoWhileJS.append(String.format("%sdo {", escopo.getIdentacao()));
            declaracaoDoWhileJS.append(System.lineSeparator());
            declaracaoDoWhileJS.append(leitorDeclaracao.ler(escopo, (BlockStmt) declaracao.getBody()));
            declaracaoDoWhileJS.append(String.format("%s}", escopo.getIdentacao()));
            declaracaoDoWhileJS.append(System.lineSeparator());
            declaracaoDoWhileJS.append(String.format("%swhile (%s);", escopo.getIdentacao(), condicaoJS));
            declaracaoDoWhileJS.append(System.lineSeparator());
        }
        catch (ExpressaoNaoSuportadaException excecao) {
            if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                declaracaoDoWhileJS.append(String.format("%s/* %s", escopo.getIdentacao(), excecao.getMessage()));
            }
            else {
                declaracaoDoWhileJS.append(String.format("%s/*", escopo.getIdentacao()));
            }

            declaracaoDoWhileJS.append(System.lineSeparator());
            declaracaoDoWhileJS.append(String.format("%sdo {", escopo.getIdentacao()));
            declaracaoDoWhileJS.append(System.lineSeparator());
            declaracaoDoWhileJS.append(String.format("%s%sBloco de código removido", escopo.getIdentacao(), AuxiliarConversao.getInstance().getIdentacao(1)));
            declaracaoDoWhileJS.append(System.lineSeparator());
            declaracaoDoWhileJS.append(String.format("%s} while (%s);", escopo.getIdentacao(), declaracao.getCondition().toString()));
            declaracaoDoWhileJS.append(System.lineSeparator());
            declaracaoDoWhileJS.append(String.format("%s*/", escopo.getIdentacao()));
            declaracaoDoWhileJS.append(System.lineSeparator());
        }

        return declaracaoDoWhileJS.toString();
    }
}