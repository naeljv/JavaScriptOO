package br.edu.unochapeco.natanael.vieira.leitores;

import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorClasse;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

final class LeitorDeclaracaoWhile {
    private String _identificacaoProprietario;

    public LeitorDeclaracaoWhile(String identificacaoProprietario) {
        _identificacaoProprietario = identificacaoProprietario;
    }

    public String ler(EscopoDeclaracao escopoPai, WhileStmt declaracao) {
        StringBuilder declaracaoWhileJS = new StringBuilder();
        EscopoDeclaracao escopo = new EscopoDeclaracao(escopoPai.getIdentificacaoEscopo(), escopoPai.getNivelIdentacao());

        GerenciadorClasse.getInstance().getGerenciadorEscopoDeclaracao(_identificacaoProprietario).adicionarEscopo(escopo);

        try {
            LeitorDeclaracao leitorDeclaracao = new LeitorDeclaracao(_identificacaoProprietario);
            LeitorExpressao leitorCondicional = new LeitorExpressao(_identificacaoProprietario);
            String condicaoJS = leitorCondicional.ler(escopo, declaracao.getCondition());

            declaracaoWhileJS.append(String.format("%swhile (%s) {", escopo.getIdentacao(), condicaoJS));
            declaracaoWhileJS.append(System.lineSeparator());
            declaracaoWhileJS.append(leitorDeclaracao.ler(escopo, (BlockStmt) declaracao.getBody()));
            declaracaoWhileJS.append(String.format("%s}", escopo.getIdentacao()));
            declaracaoWhileJS.append(System.lineSeparator());
        }
        catch (ExpressaoNaoSuportadaException excecao) {
            if (AuxiliarConversao.getInstance().isExibirMensagens()) {
                declaracaoWhileJS.append(String.format("%s/* %s", escopo.getIdentacao(), excecao.getMessage()));
            }
            else {
                declaracaoWhileJS.append(String.format("%s/*", escopo.getIdentacao()));
            }

            declaracaoWhileJS.append(System.lineSeparator());
            declaracaoWhileJS.append(String.format("%swhile (%s) {",escopo.getIdentacao(), declaracao.getCondition().toString()));
            declaracaoWhileJS.append(System.lineSeparator());
            declaracaoWhileJS.append(String.format("%s%sBloco de código removido", escopo.getIdentacao(), AuxiliarConversao.getInstance().getIdentacao(1)));
            declaracaoWhileJS.append(System.lineSeparator());
            declaracaoWhileJS.append(String.format("%s}", escopo.getIdentacao()));
            declaracaoWhileJS.append(System.lineSeparator());
            declaracaoWhileJS.append(String.format("%s*/", escopo.getIdentacao()));
            declaracaoWhileJS.append(System.lineSeparator());
        }

        return declaracaoWhileJS.toString();
    }
}