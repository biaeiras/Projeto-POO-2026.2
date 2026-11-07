package model;

import java.util.HashMap;

class Jochi extends Personagem {

    Jochi() {
        super("Jochi", Cor.CINZA);
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
                return criarAcoesBase(1, 1, true, false);
            case RUSSIA:
                return criarAcoesBase(2, 1, false, false);
            default:
                throw new IllegalArgumentException("Região inválida.");
        }
    }
}
