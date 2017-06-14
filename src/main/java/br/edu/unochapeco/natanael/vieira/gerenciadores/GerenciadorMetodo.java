package br.edu.unochapeco.natanael.vieira.gerenciadores;

import br.edu.unochapeco.natanael.vieira.entidades.Classe;
import br.edu.unochapeco.natanael.vieira.entidades.Metodo;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class GerenciadorMetodo {
    private String _nomeClasse;
    private List<Metodo> _metodos;
    private Map<String, BlockStmt> _blocosDeclaracoes;

    public GerenciadorMetodo(String nomeClasse) {
        _nomeClasse = nomeClasse;
        _metodos = new ArrayList<>();
        _blocosDeclaracoes = new HashMap<>();
    }

    public void adicionarMetodo(Metodo metodo, BlockStmt declaracoes) {
        _metodos.add(metodo);
        _blocosDeclaracoes.put(metodo.getIdentificador(), declaracoes);
    }

    public void adicionarMetodo(Metodo metodo, MethodDeclaration declaracao) {
        Optional<BlockStmt> declaracoesMetodo = declaracao.getBody();

        adicionarMetodo(metodo, declaracoesMetodo.isPresent() ? declaracoesMetodo.get() : null);
    }

    public BlockStmt getBlocoDeclaracao(String identificadorMetodo) {
        return _blocosDeclaracoes.get(identificadorMetodo);
    }

    public int getIndiceUltimoMetodoConvertidoJS() {
        int indiceUltimoMetodoConvertidoJS = -1;

        for (int i = 0; i < _metodos.size(); i++) {
            if (_metodos.get(i).isConvertidoJS()) {
                indiceUltimoMetodoConvertidoJS = i;
            }
        }

        return indiceUltimoMetodoConvertidoJS;
    }

    public String getNomeMetodoJSClassePai(String nomeMetodoJava, int totalParametros) throws ExpressaoNaoSuportadaException {
        Classe classe = GerenciadorClasse.getInstance().getClasse(_nomeClasse);

        if (classe.getNomeClassePai() == null) {
            throw new ExpressaoNaoSuportadaException(String.format("Método [%s] não encontrado na definição da classe", nomeMetodoJava));
        }
        else {
            Classe classePai = GerenciadorClasse.getInstance().getClasse(classe.getNomeClassePai());
            return classePai.getGerenciadorMetodo().getNomeMetodoJS(nomeMetodoJava, totalParametros);
        }
    }

    public String getNomeMetodoJS(String nomeMetodoJava, int totalParametros) throws ExpressaoNaoSuportadaException {
        List<Metodo> metodosFiltradosNome = _metodos.stream().filter(metodo -> metodo.getNomeJava().equals(nomeMetodoJava)).collect(Collectors.toList());

        if (metodosFiltradosNome.size() == 0) {
            Classe classe = GerenciadorClasse.getInstance().getClasse(_nomeClasse);

            if (classe.getNomeClassePai() == null) {
                throw new ExpressaoNaoSuportadaException(String.format("Método [%s] não encontrado na definição da classe", nomeMetodoJava));
            }
            else {
                Classe classePai = GerenciadorClasse.getInstance().getClasse(classe.getNomeClassePai());
                return classePai.getGerenciadorMetodo().getNomeMetodoJS(nomeMetodoJava, totalParametros);
            }
        } else if (metodosFiltradosNome.size() == 1) {
            Metodo metodo = metodosFiltradosNome.stream().findFirst().get();

            if (metodo.isConvertidoJS()) {
                return metodo.getNomeJS();
            } else {
                throw new ExpressaoNaoSuportadaException("Método não convertido");
            }
        } else {
            List<Metodo> metodosFiltradosTotalParamentros = metodosFiltradosNome.stream().filter(metodo -> metodo.getTipoParametros().size() == totalParametros).collect(Collectors.toList());

            if (metodosFiltradosTotalParamentros.size() == 0) {
                throw new ExpressaoNaoSuportadaException("Método não encontrado na definição da classe");
            } else if (metodosFiltradosTotalParamentros.size() == 1) {
                Metodo metodo = metodosFiltradosTotalParamentros.stream().findFirst().get();

                if (metodo.isConvertidoJS()) {
                    return metodo.getNomeJS();
                } else {
                    throw new ExpressaoNaoSuportadaException("Método não convertido");
                }
            } else {
                throw new ExpressaoNaoSuportadaException("Método com o mesmo nome e quantidade de parâmetros não suportado na versão atual");
            }
        }
    }

    public List<Metodo> getMetodos() {
        return _metodos;
    }

    public String getNomeMetodoAdicionar(String nomeMetodoJava, boolean modificadorAcessoPublico) {
        int totalMetodosMesmoNomeModificadorAcesso = 0;
        String modificadorAcesso = modificadorAcessoPublico ? "" : "_";

        for (int i = 0; i < _metodos.size(); i++) {
            Metodo metodo = _metodos.get(i);

            if (metodo.isConvertidoJS() && (metodo.isPublico() == modificadorAcessoPublico) && (metodo.getNomeJava().equals(nomeMetodoJava))) {
                totalMetodosMesmoNomeModificadorAcesso++;
            }
        }

        if (totalMetodosMesmoNomeModificadorAcesso == 0) {
            return String.format("%s%s", modificadorAcesso, nomeMetodoJava);
        }

        return String.format("%s%s__%d", modificadorAcesso, nomeMetodoJava, totalMetodosMesmoNomeModificadorAcesso);
    }
}