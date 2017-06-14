package br.edu.unochapeco.natanael.vieira.controller;

import br.edu.unochapeco.natanael.vieira.compiladorJava.CompiladorJava;
import br.edu.unochapeco.natanael.vieira.excecoes.CompilacaoException;
import br.edu.unochapeco.natanael.vieira.excecoes.ConversaoException;
import br.edu.unochapeco.natanael.vieira.excecoes.ConversaoParserException;
import br.edu.unochapeco.natanael.vieira.parser.JavaScriptParser;
import java.util.List;

public final class Conversor {

    public String converter(List<String> codigos) throws ConversaoException {
        try {
            CompiladorJava compiladorJava = new CompiladorJava();
            compiladorJava.compilar(codigos);

            JavaScriptParser parser = new JavaScriptParser();
            return parser.converter(codigos);
        } catch (CompilacaoException excecaoCompilacao) {
            throw new ConversaoException(excecaoCompilacao.getMessage());
        } catch (ConversaoParserException excecaoConversao) {
            throw new ConversaoException(excecaoConversao.getMessage());
        }
    }
}