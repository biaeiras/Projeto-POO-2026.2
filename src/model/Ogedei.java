package model;

import java.util.HashMap;

class Ogedei extends Personagem {

    Ogedei() {
        super("Ogedei", Cor.AZUL);
    }

    @Override
    HashMap<Enum<?>, Integer> getAcoesBase(Regiao regiao) {
        if (regiao == null) {
            return criarAcoesBase(2, 1, true, false);
        }
        switch (regiao) {
            case CHINA:
                return criarAcoesBase(2, 1, false, false);
            case PERSIA:
                return criarAcoesBase(1, 1, false, true);
            case RUSSIA:
                return criarAcoesBase(1, 1, true, false);
            default:
                throw new IllegalArgumentException("Região inválida.");
        }
    }
}
