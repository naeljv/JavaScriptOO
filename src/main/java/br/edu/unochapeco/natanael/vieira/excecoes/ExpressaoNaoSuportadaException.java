package br.edu.unochapeco.natanael.vieira.excecoes;

public final class ExpressaoNaoSuportadaException extends Exception {
    public ExpressaoNaoSuportadaException(String mensagem) {
        super(mensagem);
    }
}