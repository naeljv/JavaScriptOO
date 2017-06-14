package br.edu.unochapeco.natanael.vieira.servicos;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import java.util.UUID;

public class AuxiliarConversao {
    private boolean _exibirMensagens;
    private int _numeroEspacosIdentar;
    private static AuxiliarConversao _auxiliarConversao;

    private AuxiliarConversao() {
        _exibirMensagens = true;
        _numeroEspacosIdentar = 2;
    }

    public static AuxiliarConversao getInstance() {
        if (_auxiliarConversao == null) {
            _auxiliarConversao = new AuxiliarConversao();
        }

        return _auxiliarConversao;
    }

    public String getGUIID() {
        UUID guiId = UUID.randomUUID();
        return guiId.toString();
    }

    public String getIdentacao(int nivelIdentacao) {
        int totalEspacos = _numeroEspacosIdentar * nivelIdentacao;
        StringBuilder retorno = new StringBuilder();

        while (retorno.length() < totalEspacos) {
            retorno.append(" ");
        }

        return retorno.toString();
    }

    public boolean isExibirMensagens() {
        return _exibirMensagens;
    }

    public void setExibirMensagens(boolean exibirMensagens) {
        _exibirMensagens = exibirMensagens;
    }

    public int getNumeroEspacosIdentar() {
        return _numeroEspacosIdentar;
    }

    public void setNumeroEspacosIdentar(int numeroEspacosIdentar) {
        this._numeroEspacosIdentar = numeroEspacosIdentar;
    }

    public boolean validarTipoSuportado(Type tipo) {
        if ((tipo instanceof PrimitiveType) || ((tipo instanceof ClassOrInterfaceType) && (tipo.toString().equals("String")))) {
            return true;
        }

        return false;
    }
}