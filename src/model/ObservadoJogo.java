package model;

import java.util.ArrayList;

public interface ObservadoJogo {
    void adicionarObservador(ObservadorJogo observador);
    void removerObservador(ObservadorJogo observador);

    boolean existePartida();
    String getNomePersonagemAtual();
    Cor getCorJogadorAtual();
    int getFichasAtivacaoJogadorAtual();
    boolean isColunaJogadorAtualAtivada(int coluna);
    boolean podeEscolherColuna();
    boolean isTurnoEmAndamento();
    boolean isPartidaEncerrada();
    ArrayList<String> getMelhoriasTabuleiroJogador(int coluna);
    int getMoedasJogadorAtual();
    int getEspadasJogadorAtual();
    int getYurtsEstoqueJogadorAtual();
    int getTributosYurtJogadorAtual();
}
