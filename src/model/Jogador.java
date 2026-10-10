package model;

import java.util.ArrayList;
import java.util.HashMap;

class Jogador {

    private final ArrayList<Peao> peoes;
    private final HashMap<TipoTributo, Integer> tributos;
    private final HashMap<TipoTesouro, Integer> tesouros;
    private int yurtsDisponiveis;
    private int votos;
    private final ArrayList<ConselheiroSecreto> opcoesConselheiro;
    private ConselheiroSecreto conselheiroSecreto;
    private final TabuleiroJogador tabuleiroJogador;

    Jogador(
            ArrayList<Peao> peoesIniciais,
            ArrayList<ConselheiroSecreto> opcoesConselheiro,
            TabuleiroJogador tabuleiroJogador
    ) {
        if (peoesIniciais == null || peoesIniciais.isEmpty()) {
            throw new IllegalArgumentException(
                    "O jogador precisa ter pelo menos um peão."
            );
        }

        if (tabuleiroJogador == null) {
            throw new IllegalArgumentException(
                    "O tabuleiro do jogador deve ser informado."
            );
        }

        if (opcoesConselheiro == null
                || opcoesConselheiro.size() != 2
                || opcoesConselheiro.contains(null)) {
            throw new IllegalArgumentException(
                    "O jogador deve receber duas cartas de conselheiro válidas."
            );
        }

        this.peoes = new ArrayList<Peao>();

        for (Peao peao : peoesIniciais) {
            if (peao == null || this.peoes.contains(peao)) {
                throw new IllegalArgumentException(
                        "A lista não pode conter peões nulos ou repetidos."
                );
            }

            this.peoes.add(peao);
        }

        this.tributos = new HashMap<TipoTributo, Integer>(); //adicionar os tributos iniciais (0 yurts, 0 espadas e 1 ou 2 moedas)

        for (TipoTributo tipo : TipoTributo.values()) {
            this.tributos.put(tipo, 0);
        }
        
        this.tesouros = new HashMap<TipoTesouro, Integer>();

        for (TipoTesouro tipo : TipoTesouro.values()) {
            this.tesouros.put(tipo, 0);
        }

        this.yurtsDisponiveis = 12;
        this.votos = 0;
        this.tabuleiroJogador = tabuleiroJogador; 

        this.opcoesConselheiro =
                new ArrayList<ConselheiroSecreto>(opcoesConselheiro);

        this.conselheiroSecreto = null;
    }
    
    Peao getPeao(int indice) {
        return peoes.get(indice);
    }

    int getQuantidadePeoes() {
        return peoes.size();
    }

    boolean possuiPeao(Peao peao) {
        return peoes.contains(peao);
    }

    int getQuantidadeTributo(TipoTributo tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException(
                    "O tipo de tributo deve ser informado."
            );
        }

        return tributos.get(tipo);
    }

    boolean adicionarTributo(TipoTributo tipo, int quantidade) {
        if (tipo == null || quantidade <= 0) {
            return false;
        }

        int quantidadeAtual = tributos.get(tipo);
        tributos.put(tipo, quantidadeAtual + quantidade);

        return true;
    }

    boolean gastarTributo(TipoTributo tipo, int quantidade) {
        if (tipo == null || quantidade <= 0) {
            return false;
        }

        int quantidadeAtual = tributos.get(tipo);

        if (quantidadeAtual < quantidade) {
            return false;
        }

        tributos.put(tipo, quantidadeAtual - quantidade);

        return true;
    }
    
    int getQuantidadeTesouro(TipoTesouro tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException(
                    "O tipo de tesouro deve ser informado."
            );
        }

        return tesouros.get(tipo);
    }
    
    boolean adicionarTesouro(TipoTesouro tipo) {
        if (tipo == null) {
            return false;
        }

        int quantidadeAtual = tesouros.get(tipo);

        tesouros.put(tipo, quantidadeAtual + 1);

        return true;
    }
    
    boolean gastarTesouros(HashMap<TipoTesouro, Integer> entrega) {

        if (entrega == null || entrega.isEmpty()) {
            return false;
        }

        // Verifica se o jogador possui todos os tesouros solicitados
        for (TipoTesouro tipo : entrega.keySet()) {

            Integer quantidade = entrega.get(tipo);

            if (tipo == null || quantidade == null || quantidade <= 0) {
                return false;
            }

            if (getQuantidadeTesouro(tipo) < quantidade) {
                return false;
            }
        }

        // Remove os tesouros do inventário
        for (TipoTesouro tipo : entrega.keySet()) {

            int quantidadeAtual = tesouros.get(tipo);

            tesouros.put(
                    tipo,
                    quantidadeAtual - entrega.get(tipo)
            );
        }

        return true;
    }

    int getYurtsDisponiveis() {
        return yurtsDisponiveis;
    }

    boolean usarYurt() {

        if (yurtsDisponiveis <= 0 ||
            getQuantidadeTributo(TipoTributo.YURT) <= 0) {
            return false;
        }

        gastarTributo(TipoTributo.YURT, 1);
        yurtsDisponiveis--;

        return true;
    }

    int getVotos() {
        return votos;
    }

    void adicionarVotos(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de votos deve ser positiva."
            );
        }

        votos += quantidade;
    }

    ConselheiroSecreto getOpcaoConselheiro(int indice) {
        return opcoesConselheiro.get(indice);
    }

    boolean jaEscolheuConselheiro() {
        return conselheiroSecreto != null;
    }

    boolean escolherConselheiro(int indice) {
        if (jaEscolheuConselheiro()) {
            return false;
        }

        if (indice < 0 || indice >= opcoesConselheiro.size()) {
            return false;
        }

        conselheiroSecreto = opcoesConselheiro.get(indice);

        opcoesConselheiro.clear();

        return true;
    }

    ConselheiroSecreto getConselheiroSecreto() {
        return conselheiroSecreto;
    }

    TabuleiroJogador getTabuleiroJogador() {
        return tabuleiroJogador;
    }
}