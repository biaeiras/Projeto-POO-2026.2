package model;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class TurnoTest {

    @Test
    public void testeProcessamentoAcoesEMelhorias() {

        // Simula o resultado da ativação de uma coluna
        HashMap<Enum<?>, Integer> resultadoColuna = new HashMap<>();

        // Ações básicas
        resultadoColuna.put(TipoAcao.MOVIMENTO, 2);
        resultadoColuna.put(TipoAcao.PEGAR_TRIBUTO, 1);
        resultadoColuna.put(TipoAcao.USAR_KHAN, 1);

        // Melhorias presentes na coluna
        resultadoColuna.put(TipoMelhoria.MOVIMENTO_EXTRA, 2);
        resultadoColuna.put(TipoMelhoria.PEGAR_TRIBUTO_EXTRA, 1);
        resultadoColuna.put(TipoMelhoria.ESPADA_VIRTUAL, 1);
        resultadoColuna.put(TipoMelhoria.MOEDA_VIRTUAL, 2);

        // Cria o turno com as informações da coluna
        Turno turno = new Turno(null, resultadoColuna);

        // 2 movimentos básicos + (2 melhorias x 2 movimentos)
        assertEquals(6, turno.getQuantidadeAcao(TipoAcao.MOVIMENTO));

        // 1 ação básica + 1 melhoria de coleta
        assertEquals(2, turno.getQuantidadeAcao(TipoAcao.PEGAR_TRIBUTO));

        // Verifica os recursos virtuais
        assertEquals(1,
                turno.getQuantidadeRecursoVirtual(TipoMelhoria.ESPADA_VIRTUAL));

        assertEquals(2,
                turno.getQuantidadeRecursoVirtual(TipoMelhoria.MOEDA_VIRTUAL));

        // O Khan deve ser utilizado neste turno
        assertTrue(turno.isKhanPendente());

        // Consome dois movimentos
        assertTrue(turno.usarAcao(TipoAcao.MOVIMENTO));
        assertTrue(turno.usarAcao(TipoAcao.MOVIMENTO));

        // Restam quatro movimentos
        assertEquals(4, turno.getQuantidadeAcao(TipoAcao.MOVIMENTO));

        // Consome uma espada virtual
        assertTrue(turno.usarRecursoVirtual(TipoMelhoria.ESPADA_VIRTUAL));

        // A espada virtual foi consumida
        assertEquals(0,
                turno.getQuantidadeRecursoVirtual(TipoMelhoria.ESPADA_VIRTUAL));

        // Não pode utilizar outra espada virtual
        assertFalse(turno.usarRecursoVirtual(TipoMelhoria.ESPADA_VIRTUAL));

        // O uso da espada não altera os movimentos
        assertEquals(4, turno.getQuantidadeAcao(TipoAcao.MOVIMENTO));

        // Conclui a ação obrigatória do Khan
        turno.concluirAcaoKhan();

        assertFalse(turno.isKhanPendente());
    }
}