package model;

import java.util.HashMap;

class Ogedei extends Personagem {

    Ogedei() {
        super("Ogedei", Cor.AZUL);
    }

    @Override
    boolean possuiHabilidadeAtivavel() {
        return true;
    }

    @Override
    boolean podeAtivarHabilidade(Turno turno) {
        if (turno == null || turno.getJogador().getTabuleiroJogador().getPersonagem() != this
                || turno.isHabilidadeEspecialUsada() || turno.isSelecaoTesouroHabilidadePendente()) {
            return false;
        }
        return turno.getJogador().getQuantidadeTributo(TipoTributo.MOEDA) > 0
                || turno.getQuantidadeRecursoVirtual(TipoMelhoria.MOEDA_VIRTUAL) > 0;
    }

    @Override
    boolean ativarHabilidade(Turno turno) {
        if (!podeAtivarHabilidade(turno)) {
            return false;
        }
        return turno.iniciarSelecaoTesouroHabilidade();
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
