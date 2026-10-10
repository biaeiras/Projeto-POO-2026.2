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
    
    HashMap<TipoAcao, Integer> getAcoesBase(Regiao regiao) {
        HashMap<TipoAcao, Integer> acoes = new HashMap<>();

        acoes.put(TipoAcao.MOVIMENTO, 1);
        acoes.put(TipoAcao.PEGAR_TRIBUTO, 1);
        acoes.put(TipoAcao.USAR_KHAN, 0);

        return acoes;
    }
    
    
}