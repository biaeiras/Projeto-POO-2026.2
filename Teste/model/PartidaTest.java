package model;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class PartidaTest {

    @Test
    public void testeRegistrarVotosValidos() {
    	// Cria uma partida com dois jogadores
        ArrayList<Cor> cores = new ArrayList<>();
        cores.add(Cor.VERDE);
        cores.add(Cor.VERMELHO);

        Partida partida = new Partida(cores);
        Jogador jogador = partida.getJogadorAtual();

        int votosIniciais = jogador.getVotos();

        // Registra dois votos para o jogador
        boolean resultado = partida.registrarVoto(jogador, 2);

        // Verifica se a operação foi aceita
        assertTrue(resultado);

        // Verifica se os votos do jogador foram atualizados
        assertEquals(votosIniciais + 2, jogador.getVotos());
   
    }    

    @Test
    public void testeNaoPermiteIniciarTurnoSemEscolherConselheiro() {

        ArrayList<Cor> cores = new ArrayList<>();
        cores.add(Cor.VERDE);
        cores.add(Cor.VERMELHO);

        Partida partida = new Partida(cores);

        // Nenhum jogador escolheu seu conselheiro ainda
        assertFalse(partida.todosEscolheramConselheiro());

        // Não deve ser possível iniciar o turno
        assertFalse(partida.iniciarTurno(Regiao.RUSSIA));

        // O turno continua sem ter sido iniciado
        assertNull(partida.getTurnoAtual());
    }
    
    @Test
    public void testeRejeitaQuantidadeInvalidaDeVotos() {

        ArrayList<Cor> cores = new ArrayList<>();
        cores.add(Cor.VERDE);
        cores.add(Cor.VERMELHO);

        Partida partida = new Partida(cores);
        Jogador jogador = partida.getJogadorAtual();

        int votosIniciais = jogador.getVotos();

        // Quantidade zero deve ser rejeitada
        assertFalse(partida.registrarVoto(jogador, 0));

        // Quantidade negativa também deve ser rejeitada
        assertFalse(partida.registrarVoto(jogador, -1));

        // Os votos não devem ser alterados
        assertEquals(votosIniciais, jogador.getVotos());
    }
}