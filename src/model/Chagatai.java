package model;

import java.util.HashMap;

class Chagatai extends Personagem {

    Chagatai() {
        super("Chagatai", Cor.VERMELHO);
    }

    @Override
    boolean possuiHabilidadeAtivavel() {
        return true;
    }

    @Override
    boolean podeAtivarHabilidade(Turno turno) {
        return turno != null
                && turno.getJogador().getTabuleiroJogador().getPersonagem() == this
                && !turno.isHabilidadeEspecialUsada()
                && turno.getQuantidadeAcao(TipoAcao.MOVIMENTO) > 0;
    }

    @Override
    boolean ativarHabilidade(Turno turno) {
        if (!podeAtivarHabilidade(turno)) {
            return false;
        }
        if (!turno.usarAcao(TipoAcao.MOVIMENTO)) {
            return false;
        }
        turno.getJogador().adicionarTributo(TipoTributo.ESPADA, 1);
        turno.registrarUsoHabilidadeEspecial();
        return true;
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
