package model;

import java.util.HashMap;

class Chagatai extends Personagem {

    Chagatai() {
        super("Chagatai", Cor.VERMELHO);
    }

    @Override
    HashMap<Enum<?>, Integer> getAcoesBase(Regiao regiao) {
        if (regiao == null) {
            return criarAcoesBase(2, 1, true, false);
        }
        switch (regiao) {
            case CHINA:
                return criarAcoesBase(1, 2, true, false);
            case PERSIA:
                return criarAcoesBase(1, 1, false, true);
            case RUSSIA:
                return criarAcoesBase(2, 1, false, false);
            default:
                throw new IllegalArgumentException("Região inválida.");
        }
    }
}
