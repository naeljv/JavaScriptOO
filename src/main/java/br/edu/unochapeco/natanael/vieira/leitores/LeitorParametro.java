package br.edu.unochapeco.natanael.vieira.leitores;

import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import br.edu.unochapeco.natanael.vieira.entidades.EscopoDeclaracao;
import br.edu.unochapeco.natanael.vieira.entidades.Variavel;
import br.edu.unochapeco.natanael.vieira.excecoes.ExpressaoNaoSuportadaException;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;

final class LeitorParametro {

    public String ler(EscopoDeclaracao escopo, NodeList<Parameter> parametros) throws ExpressaoNaoSuportadaException {
        StringBuilder parametrosJS = new StringBuilder();
        int totalParametros = parametros.size();

        for (int i = 0; i < totalParametros; i++) {
            Type tipo = parametros.get(i).getType();

            if (AuxiliarConversao.getInstance().validarTipoSuportado(tipo)) {
                String nomeParametro = parametros.get(i).getNameAsString();
                escopo.adicionarVariavel(new Variavel(nomeParametro, nomeParametro));

                parametrosJS.append(nomeParametro);

                if (i < (totalParametros - 1)) {
                    parametrosJS.append(", ");
                }
            }
            else {
                throw new ExpressaoNaoSuportadaException(String.format("Tipo de dado não suportado na versão atual: [%s]", tipo.toString()));
            }
        }

        return parametrosJS.toString();
    }
}