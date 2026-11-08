package model;

import java.util.HashMap;

abstract class Personagem {

    private final String nome;
    private final Cor cor;

    Personagem(String nome, Cor cor) {
        this.nome = nome;
        this.cor = cor;
    }

    String getNome() {
        return nome;
    }

    Cor getCor() {
        return cor;
    }
    
    // Região nula representa a coluna branca.
    abstract HashMap<Enum<?>, Integer> getAcoesBase(Regiao regiao);

    protected final HashMap<Enum<?>, Integer> criarAcoesBase(
            int movimentos, int tributos, boolean khan, boolean moedaVirtual) {
        HashMap<Enum<?>, Integer> acoes = new HashMap<>();
        acoes.put(TipoAcao.MOVIMENTO, movimentos);
        acoes.put(TipoAcao.PEGAR_TRIBUTO, tributos);
        acoes.put(TipoAcao.USAR_KHAN, khan ? 1 : 0);
        if (moedaVirtual) {
            acoes.put(TipoMelhoria.MOEDA_VIRTUAL, 1);
        }
        return acoes;
    }
    
    int getQuantidadePeoesIniciais() {
        return 1;
    }
}
