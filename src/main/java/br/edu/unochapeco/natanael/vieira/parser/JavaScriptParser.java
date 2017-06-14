package br.edu.unochapeco.natanael.vieira.parser;

import br.edu.unochapeco.natanael.vieira.entidades.Classe;
import br.edu.unochapeco.natanael.vieira.entidades.DeclaracaoConstrutor;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import br.edu.unochapeco.natanael.vieira.excecoes.ConversaoParserException;
import br.edu.unochapeco.natanael.vieira.gerenciadores.GerenciadorClasse;
import br.edu.unochapeco.natanael.vieira.leitores.LeitorAtributo;
import br.edu.unochapeco.natanael.vieira.leitores.LeitorConstrutor;
import br.edu.unochapeco.natanael.vieira.leitores.LeitorMetodo;
import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import java.util.List;
import java.util.stream.Collectors;

public final class JavaScriptParser {

    public String converter(List<String> codigos) throws ConversaoParserException {
        StringBuilder classesJS = new StringBuilder();
        GerenciadorClasse.getInstance().removerTodasClasses();

        for (int i = 0; i < codigos.size(); i++) {
            try {
                ClassOrInterfaceDeclaration classe = getClasse(codigos.get(i));

                if (i > 0) {
                    classesJS.append(System.lineSeparator());
                    classesJS.append(System.lineSeparator());
                }

                classesJS.append(converter(classe));
            } catch (ExpressaoNaoSuportadaException excecao) {
                throw new ConversaoParserException(excecao.getMessage());
            }
        }

        return classesJS.toString();
    }

    private String converter(ClassOrInterfaceDeclaration classeJava) throws ExpressaoNaoSuportadaException {
        StringBuilder classeJS = new StringBuilder();
        String nomeClasse = classeJava.getNameAsString();
        NodeList<BodyDeclaration<?>> declaracoes = classeJava.getMembers();
        NodeList<ClassOrInterfaceType> classeExtendida = classeJava.getExtendedTypes();
        String nomeClassePai = classeExtendida.size() > 0 ? classeExtendida.get(0).getNameAsString() : null;
        Classe classe = new Classe(nomeClasse, nomeClassePai);

        GerenciadorClasse.getInstance().adicionarClasse(classe);

        LeitorAtributo leitorAtributo = new LeitorAtributo(classe);
        LeitorConstrutor leitorConstrutor = new LeitorConstrutor(classe);
        LeitorMetodo leitorMetodo = new LeitorMetodo(classe);

        leitorMetodo.lerCabecalho(declaracoes.stream().filter(declaracao -> declaracao instanceof MethodDeclaration).collect(Collectors.toList()));

        String atributos = leitorAtributo.ler(declaracoes.stream().filter(declaracao -> declaracao instanceof FieldDeclaration).collect(Collectors.toList()));
        String metodos = leitorMetodo.lerEscopo();
        DeclaracaoConstrutor construtor = leitorConstrutor.ler(declaracoes.stream().filter(declaracao -> declaracao instanceof ConstructorDeclaration).collect(Collectors.toList()));

        classeJS.append(String.format("function %s(%s) {", nomeClasse, construtor.getParametros()));
        classeJS.append(System.lineSeparator());
        classeJS.append(atributos);
        classeJS.append(construtor.getDeclaracao());
        classeJS.append("}");

        if (nomeClassePai != null) {
            classeJS.append(System.lineSeparator());
            classeJS.append(System.lineSeparator());
            classeJS.append(String.format("%s.prototype = Object.create(%s.prototype);", nomeClasse, nomeClassePai));
            classeJS.append(System.lineSeparator());
            classeJS.append(System.lineSeparator());
            classeJS.append(String.format("%s.prototype.constructor = %s;", nomeClasse, nomeClasse));
        }

        if (!metodos.equals("")) {
            classeJS.append(System.lineSeparator());
            classeJS.append(System.lineSeparator());

            if (nomeClassePai == null) {
                classeJS.append(String.format("%s.prototype = {", nomeClasse));
                classeJS.append(System.lineSeparator());
                classeJS.append(String.format("%sconstructor: %s,", AuxiliarConversao.getInstance().getIdentacao(1), nomeClasse));
                classeJS.append(System.lineSeparator());
                classeJS.append(metodos);
                classeJS.append("}");
            } else {
                classeJS.append(metodos.substring(0, metodos.length() - (System.lineSeparator().length() * 2)));
            }
        }

        return classeJS.toString();
    }

    private CompilationUnit criarUnidadeCompilacao(String codigo) throws ExpressaoNaoSuportadaException {
        try {
            return JavaParser.parse(codigo);
        } catch (Exception e) {
            throw new ExpressaoNaoSuportadaException(e.getMessage());
        }
    }

    private ClassOrInterfaceDeclaration getClasse(String codigo) throws ExpressaoNaoSuportadaException {
        CompilationUnit unidadeCompilacao = criarUnidadeCompilacao(codigo);
        NodeList<TypeDeclaration<?>> listaTipos = unidadeCompilacao.getTypes();

        if (listaTipos.size() != 1) {
            throw new ExpressaoNaoSuportadaException("Deve existir uma classe no arquivo");
        }

        TypeDeclaration<?> tipoDeclaracao = listaTipos.get(0);

        if (tipoDeclaracao instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration classe = (ClassOrInterfaceDeclaration) tipoDeclaracao;

            if (classe.isInterface()) {
                throw new ExpressaoNaoSuportadaException("Interfaces não são convertidas");
            }

            if (classe.isAbstract()) {
                throw new ExpressaoNaoSuportadaException("Classes abstratas não são convertidas");
            }

            if (classe.isStatic()) {
                throw new ExpressaoNaoSuportadaException("Classes estáticas não são permitidas");
            }

            if (!validarTotalConstrutoresPermitidos(classe)) {
                throw new ExpressaoNaoSuportadaException("A versão atual permite somente um construtor e o construtor vazio");
            }

            return classe;
        }
        else {
            throw new ExpressaoNaoSuportadaException("Deve existir apenas definição de classe");
        }
    }

    private boolean validarTotalConstrutoresPermitidos(ClassOrInterfaceDeclaration classe) {
        List<BodyDeclaration<?>> construtores = classe.getMembers().stream().filter(declaracao -> declaracao instanceof ConstructorDeclaration).collect(Collectors.toList());

        if (construtores.size() < 2) {
            return true;
        }
        else if (construtores.size() == 2) {
            for (BodyDeclaration declaracaoConstrutor : construtores) {
                ConstructorDeclaration construtor = (ConstructorDeclaration) declaracaoConstrutor;

                int totalParametros = construtor.getParameters().size();
                int totalDeclaracoesEscopo = construtor.getBody().getStatements().size();

                if ((totalParametros == 0) && (totalDeclaracoesEscopo == 0)) {
                    return true;
                }
            }
        }

        return false;
    }
}