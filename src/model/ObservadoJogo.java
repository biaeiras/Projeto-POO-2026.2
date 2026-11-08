package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public interface ObservadoJogo {
    void adicionarObservador(ObservadorJogo observador);
    void removerObservador(ObservadorJogo observador);

    boolean existePartida();
    String getNomePersonagemAtual();
    String getNomeConselheiroSecretoJogadorAtual();
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
    ArrayList<String> getConselheirosAbertos();
    HashMap<String, Integer> getPlacarVotos();
    int getVotosFichaNeutra();
    HashMap<ProvinciaID, Integer> getCargasProvincias();
    HashMap<ParadaID, ArrayList<Cor>> getYurts();
    HashMap<ParadaID, ArrayList<Cor>> getPosicoesPeoes();
    HashSet<ParadaID> getAlcancaveisTotais();
    HashSet<ParadaID> getVizinhosPossiveis();
    HashSet<ProvinciaID> getProvincias();
    boolean isFimDisparado();
    ArrayList<String> getNomesVencedores();
    int[] getOrdemPecasBonusJogadorAtual();
    boolean[] getPecasBonusUsadasJogadorAtual();
    
}
