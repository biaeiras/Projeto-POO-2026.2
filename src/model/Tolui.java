package model;

import java.util.HashMap;

class Tolui extends Personagem {

    Tolui() {
        super("Tolui", Cor.AMARELO);
    }

    @Override
    HashMap<Enum<?>, Integer> getAcoesBase(Regiao regiao) {
        if (regiao == null) {
            return criarAcoesBase(2, 1, true, false);
        }
        switch (regiao) {
            case CHINA:
                return criarAcoesBase(1, 1, false, true);
            case PERSIA:
                return criarAcoesBase(2, 1, false, false);
            case RUSSIA:
                return criarAcoesBase(1, 1, true, false);
            default:
                throw new IllegalArgumentException("Região inválida.");
        }
    }
}
