package br.edu.unochapeco.natanael.vieira.compiladorJava;

import br.edu.unochapeco.natanael.vieira.excecoes.CompilacaoException;
import java.util.ArrayList;
import java.util.List;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public final class CompiladorJava {

    public void compilar(List<String> codigos) throws CompilacaoException {
        JavaCompiler compilador = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> listaDiagnostico = new DiagnosticCollector<JavaFileObject>();
        List<JavaFileObject> arquivos = new ArrayList<>();

        for (String codigo : codigos) {
            arquivos.add(new JavaSourceFromString(getNomeClasse(codigo), codigo));
        }

        Iterable<? extends JavaFileObject> unidadesCompilacao = arquivos;
        JavaCompiler.CompilationTask task = compilador.getTask(null, null, listaDiagnostico, null, null, unidadesCompilacao);

        if (!task.call()) {
            StringBuilder mensagemErro = new StringBuilder();

            listaDiagnostico.getDiagnostics().forEach(diagnostico -> {
                mensagemErro.append(String.format("linha: %s - Coluna: %s", diagnostico.getLineNumber(), diagnostico.getColumnNumber()));
                mensagemErro.append(System.lineSeparator());
                mensagemErro.append(String.format("%s - %s", diagnostico.getCode(), diagnostico.getMessage(null)));
                mensagemErro.append(System.lineSeparator());
            });

            throw new CompilacaoException(mensagemErro.toString());
        }
    }

    private String getNomeClasse(String codigo) {
        int indicePalavraReservadaClass = codigo.indexOf("class");

        if (indicePalavraReservadaClass >= 0) {
            codigo = codigo.substring((indicePalavraReservadaClass + 5)).replace(System.lineSeparator(), " ").trim();
            int indiceNomeClasse = codigo.indexOf(" ");

            if (indiceNomeClasse > 0) {
                return codigo.substring(0, indiceNomeClasse);
            }
        }

        return "NomePadrao";
    }
}