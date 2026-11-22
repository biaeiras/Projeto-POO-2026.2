package model;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;


public class PartidaTest {

    @Test
    public void chagataiNaoPodeMoverDuasVezesComPrimeiraColuna() {
        ArrayList<Cor> cores = new ArrayList<>();
        cores.add(Cor.VERMELHO);
        cores.add(Cor.VERDE);
        Partida partida = new Partida(cores);
        assertTrue(partida.escolherConselheiroJogadorAtual(0));
        partida.avancarJogador();
        assertTrue(partida.escolherConselheiroJogadorAtual(0));
        partida.avancarJogador();
        assertTrue(partida.iniciarTurno(Regiao.CHINA));
        assertEquals(1, partida.getTurnoAtual().getQuantidadeAcao(TipoAcao.MOVIMENTO));
        assertTrue(partida.moverPeao(ParadaID.PEQUIM1));
        assertEquals(0, partida.getTurnoAtual().getQuantidadeAcao(TipoAcao.MOVIMENTO));
        assertFalse(partida.moverPeao(ParadaID.PEQUIM2));
    }

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

    @Test
    public void khanAbasteceTresProvinciasERespeitaLimite() {
        Partida partida = prepararPartida(Cor.AMARELO, Cor.VERMELHO);
        partida.getTabuleiroPrincipal().getProvincias().get(ProvinciaID.TL_MOEDA).restaurarCargas(3);

        assertTrue(partida.iniciarTurnoColunaBranca());
        assertTrue(partida.selecionarKhan(KhanID.TL));
        assertEquals(Integer.valueOf(3), partida.getCargasProvincias().get(ProvinciaID.TL_MOEDA));
        assertEquals(Integer.valueOf(2), partida.getCargasProvincias().get(ProvinciaID.TL_ESPADA));
        assertEquals(Integer.valueOf(2), partida.getCargasProvincias().get(ProvinciaID.TL_YURT));
        assertEquals(Integer.valueOf(1), partida.getCargasProvincias().get(ProvinciaID.TR_YURT));
        assertTrue(partida.TerminarTurno());

        assertTrue(partida.iniciarTurnoColunaBranca());
        assertFalse(partida.selecionarKhan(KhanID.TL));
        assertTrue(partida.selecionarKhan(KhanID.M));
    }

    @Test
    public void linhaCompletaDaMelhoriaDoMapaSemCustoNemEfeitoImediato() {
        Partida partida = prepararPartida(Cor.AMARELO, Cor.VERMELHO);
        Jogador jogador = partida.getJogadorAtual();
        TabuleiroJogador tabuleiro = jogador.getTabuleiroJogador();
        assertTrue(tabuleiro.adicionarMelhoria(new Melhoria(TipoMelhoria.ESPADA_VIRTUAL, Regiao.CHINA)));
        assertTrue(tabuleiro.adicionarMelhoria(new Melhoria(TipoMelhoria.ESPADA_VIRTUAL, Regiao.PERSIA)));
        Melhoria premio = new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, Regiao.RUSSIA);
        Stack<Melhoria> pilha = new Stack<>();
        pilha.push(new Melhoria(TipoMelhoria.ESPADA_VIRTUAL, Regiao.RUSSIA));
        partida.getTabuleiroPrincipal().restaurarMelhorias(
                new ArrayList<>(Arrays.asList(null, null, null, null, premio)), pilha);
        jogador.adicionarTributo(TipoTributo.MOEDA, 1);

        assertTrue(partida.iniciarTurnoColunaBranca());
        int movimentos = partida.getTurnoAtual().getQuantidadeAcao(TipoAcao.MOVIMENTO);
        assertTrue(partida.comprarMelhoriaDaPilha());
        assertTrue(partida.isMelhoriaGratisPendente());
        assertFalse(partida.TerminarTurno());
        assertTrue(partida.comprarMelhoriaTabuleiro(4));
        assertEquals(List.of(premio), tabuleiro.exportarMelhoriasBrancas());
        assertEquals(0, jogador.getQuantidadeTributo(TipoTributo.MOEDA));
        assertEquals(movimentos, partida.getTurnoAtual().getQuantidadeAcao(TipoAcao.MOVIMENTO));
        assertFalse(partida.isMelhoriaGratisPendente());
        assertFalse(partida.comprarMelhoriaTabuleiro(4));
    }

    @Test
    public void jochiSoRecebeMelhoriaGratuitaAPartirDaSegundaLinha() {
        Partida partida = prepararPartida(Cor.CINZA, Cor.VERMELHO);
        Jogador jochi = partida.getJogadorAtual();
        TabuleiroJogador tabuleiro = jochi.getTabuleiroJogador();
        Melhoria melhoria = new Melhoria(TipoMelhoria.ESPADA_VIRTUAL, Regiao.CHINA);
        Stack<Melhoria> pilha = new Stack<>();
        for (int i = 0; i < 3; i++) pilha.push(melhoria);
        partida.getTabuleiroPrincipal().restaurarMelhorias(
                new ArrayList<>(Arrays.asList(null, null, null, null, null)), pilha);
        assertTrue(tabuleiro.adicionarMelhoriaComprada(Regiao.CHINA, melhoria));
        assertTrue(tabuleiro.adicionarMelhoriaComprada(Regiao.PERSIA, melhoria));
        jochi.adicionarTributo(TipoTributo.MOEDA, 3);
        assertTrue(partida.iniciarTurno(Regiao.RUSSIA));

        assertTrue(partida.comprarMelhoriaDaPilha());
        assertTrue(partida.escolherColunaMelhoria(2));
        assertFalse(partida.isMelhoriaGratisPendente());
        assertEquals(1, tabuleiro.exportarMelhoriasBrancas().size());

        assertTrue(tabuleiro.adicionarMelhoriaComprada(Regiao.CHINA, melhoria));
        assertTrue(tabuleiro.adicionarMelhoriaComprada(Regiao.PERSIA, melhoria));
        assertTrue(partida.comprarMelhoriaDaPilha());
        assertTrue(partida.escolherColunaMelhoria(2));
        assertTrue(partida.isMelhoriaGratisPendente());
        assertTrue(partida.comprarMelhoriaDaPilha());
        assertEquals(2, tabuleiro.exportarMelhoriasBrancas().size());
        assertEquals(0, jochi.getQuantidadeTributo(TipoTributo.MOEDA));
    }

    @Test
    public void influenciaContaCidadesConquistadasEAplicaTabelaEmEmpateRegional() {
        Partida partida = new Partida(new ArrayList<>(List.of(Cor.VERMELHO, Cor.AZUL, Cor.AMARELO)));
        Jogador primeiro = partida.getJogadores().get(0);
        Jogador empatado = partida.getJogadores().get(1);
        Jogador abaixo = partida.getJogadores().get(2);
        TabuleiroPrincipal mapa = partida.getTabuleiroPrincipal();
        mapa.getCidade(CidadeID.PEQUIM).restaurarEstado(new ArrayList<>(), primeiro);
        mapa.getCidade(CidadeID.KAESONG).restaurarEstado(new ArrayList<>(), primeiro);
        mapa.adicionarYurt(empatado, mapa.getMapa().get(ParadaID.CANTAO1));
        mapa.adicionarYurt(empatado, mapa.getMapa().get(ParadaID.CANTAO2));
        mapa.adicionarYurt(abaixo, mapa.getMapa().get(ParadaID.PEQUIM1));
        Melhoria melhoria = new Melhoria(TipoMelhoria.ESPADA_VIRTUAL, Regiao.CHINA);
        for (int i = 0; i < 4; i++) {
            assertTrue(empatado.getTabuleiroJogador().adicionarMelhoria(melhoria));
            if (i < 3) assertTrue(primeiro.getTabuleiroJogador().adicionarMelhoria(melhoria));
        }
        assertTrue(abaixo.getTabuleiroJogador().adicionarMelhoria(melhoria));
        partida.restaurarEstadoGeral(0, 0, true, true, 1, 0, false);

        assertEquals(List.of(empatado), partida.calcularVencedores());
        assertEquals(2, primeiro.getVotos());
        assertEquals(3, empatado.getVotos());
        assertEquals(0, abaixo.getVotos());
    }

    @Test
    public void salvamentoPreservaOrdemVezEColunaDeCorDiferenteDoJochi() throws Exception {
        Partida partida = prepararPartida(Cor.CINZA, Cor.AMARELO, Cor.VERMELHO);
        Jogador jochi = partida.getJogadorAtual();
        Melhoria melhoria = new Melhoria(TipoMelhoria.ESPADA_VIRTUAL, Regiao.PERSIA);
        assertTrue(jochi.getTabuleiroJogador().adicionarMelhoriaComprada(Regiao.CHINA, melhoria));
        assertEquals(1, jochi.getQuantidadeTributo(TipoTributo.MOEDA));
        assertEquals(2, partida.getJogadores().get(2).getQuantidadeTributo(TipoTributo.MOEDA));
        assertTrue(partida.iniciarTurno(Regiao.RUSSIA));
        assertTrue(partida.TerminarTurno());

        File arquivo = File.createTempFile("partida-khan", ".txt");
        try {
            PersistenciaPartida.salvarPartida(partida, arquivo);
            for (byte valor : Files.readAllBytes(arquivo.toPath())) assertTrue(valor >= 0);
            Partida carregada = new PersistenciaPartida().carregarPartida(arquivo);
            assertEquals(Cor.AMARELO, carregada.getJogadorAtual().getTabuleiroJogador().getPersonagem().getCor());
            assertEquals(List.of(melhoria), carregada.getJogadores().get(0)
                    .getTabuleiroJogador().exportarMelhoriasRegiao().get(Regiao.CHINA));
            carregada.avancarJogador();
            assertEquals(Cor.VERMELHO, carregada.getJogadorAtual().getTabuleiroJogador().getPersonagem().getCor());
            carregada.avancarJogador();
            assertEquals(Cor.CINZA, carregada.getJogadorAtual().getTabuleiroJogador().getPersonagem().getCor());
        } finally {
            Files.deleteIfExists(arquivo.toPath());
        }
    }

    private Partida prepararPartida(Cor... cores) {
        Partida partida = new Partida(new ArrayList<>(Arrays.asList(cores)));
        for (int i = 0; i < cores.length; i++) {
            assertTrue(partida.escolherConselheiroJogadorAtual(0));
            if (partida.isEscolhaMelhoriaInicialPendente()) assertTrue(partida.escolherMelhoriaInicial(0));
            partida.avancarJogador();
        }
        return partida;
    }
}
