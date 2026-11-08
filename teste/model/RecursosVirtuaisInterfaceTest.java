package model;

import static org.junit.Assert.*;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.*;
import org.junit.Test;
import controller.ControllerAPI;
import controller.ControladorTabuleiroJogador;
import view.PainelAcoesPartida;
import view.TelaJogador;

public class RecursosVirtuaisInterfaceTest {
    private Partida criarPartida() {
        Partida partida = new Partida(new ArrayList<>(Arrays.asList(Cor.VERDE, Cor.VERMELHO)),
                Arrays.asList(TipoConselheiroAberto.SEIS_YURTS, TipoConselheiroAberto.DEZ_YURTS,
                        TipoConselheiroAberto.DUAS_PECAS_BONUS, TipoConselheiroAberto.TRES_PECAS_BONUS));
        for (int i = 0; i < 2; i++) {
            assertTrue(partida.escolherConselheiroJogadorAtual(0));
            partida.avancarJogador();
        }
        return partida;
    }

    private Parada encontrarParada(Partida partida, ParadaID id) {
        ArrayList<Parada> fila = new ArrayList<>();
        fila.add(partida.getJogadorAtual().getPeao(0).getPosicao());
        for (int i = 0; i < fila.size(); i++) {
            Parada atual = fila.get(i);
            if (atual.getId() == id) return atual;
            for (Parada vizinha : atual.getVizinhos().values()) if (!fila.contains(vizinha)) fila.add(vizinha);
        }
        throw new AssertionError("Parada ausente");
    }

    private void melhoria(Jogador jogador, TipoMelhoria tipo) {
        assertTrue(jogador.getTabuleiroJogador().adicionarMelhoria(Regiao.PERSIA,
                new Melhoria(tipo, Regiao.PERSIA)));
    }

    private JLabel campo(Container raiz, String nome) {
        for (Component componente : raiz.getComponents()) {
            if (nome.equals(componente.getName())) return (JLabel) componente;
            if (componente instanceof Container) {
                JLabel resultado = campo((Container) componente, nome);
                if (resultado != null) return resultado;
            }
        }
        return null;
    }

    private String valor(TelaJogador tela, String nome) {
        JLabel campo = campo(tela, nome);
        assertNotNull(nome, campo);
        return campo.getText();
    }

    @Test
    public void telaMostraReaisMaisVirtuaisEApagaVirtuaisAoTerminarTurno() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                Partida partida = criarPartida();
                Jogador jogador = partida.getJogadorAtual();
                jogador.adicionarTributo(TipoTributo.ESPADA, 2);
                jogador.adicionarTributo(TipoTributo.YURT, 1);
                melhoria(jogador, TipoMelhoria.MOEDA_VIRTUAL);
                melhoria(jogador, TipoMelhoria.ESPADA_VIRTUAL);
                melhoria(jogador, TipoMelhoria.YURT_VIRTUAL);
                FachadaJogo fachada = FachadaJogo.getInstancia();
                fachada.definirPartida(partida);
                TelaJogador tela = new TelaJogador();
                ControladorTabuleiroJogador controlador = new ControladorTabuleiroJogador(tela);
                try {
                    assertEquals("1", valor(tela, "recurso.MOEDA"));
                    assertEquals(Integer.valueOf(0), fachada.getTributosVirtuaisJogadorAtual().get("MOEDA"));
                    assertTrue(fachada.ativarColunaJogadorAtual(1));
                    assertEquals("2 (1 + 1 virtual)", valor(tela, "recurso.MOEDA"));
                    assertEquals("3 (2 + 1 virtual)", valor(tela, "recurso.ESPADA"));
                    assertEquals("2 (1 + 1 virtual)", valor(tela, "recurso.YURT"));
                    assertEquals("12", valor(tela, "estoque.YURTS"));
                    assertEquals(1, fachada.getMoedasJogadorAtual());
                    assertEquals(2, fachada.getEspadasJogadorAtual());
                    assertEquals(1, fachada.getTributosYurtJogadorAtual());
                    fachada.getTributosVirtuaisJogadorAtual().put("ESPADA", 99);
                    assertEquals(Integer.valueOf(1), fachada.getTributosVirtuaisJogadorAtual().get("ESPADA"));
                    assertTrue(fachada.terminarTurnoJogadorAtual());
                    for (Integer quantidade : fachada.getTributosVirtuaisJogadorAtual().values()) assertEquals(0, (int) quantidade);
                    assertEquals("0", valor(tela, "recurso.ESPADA"));
                    assertTrue(fachada.ativarColunaJogadorAtual(2));
                    assertTrue(fachada.terminarTurnoJogadorAtual());
                    assertEquals("2", valor(tela, "recurso.ESPADA"));
                    assertEquals("1", valor(tela, "recurso.YURT"));
                    assertEquals("1", valor(tela, "recurso.MOEDA"));
                } finally { controlador.encerrar(); }
            }
        });
    }

    @Test
    public void espadaVirtualDiminuiNaTelaDepoisDoAtaque() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                Partida partida = criarPartida();
                Jogador jogador = partida.getJogadorAtual();
                jogador.adicionarTributo(TipoTributo.ESPADA, 1);
                melhoria(jogador, TipoMelhoria.ESPADA_VIRTUAL);
                Cidade cidade = encontrarParada(partida, ParadaID.PEQUIM1).getCidade();
                cidade.getTesouros().clear();
                cidade.getTesouros().add(TipoTesouro.FERRO);
                FachadaJogo fachada = FachadaJogo.getInstancia();
                fachada.definirPartida(partida);
                TelaJogador tela = new TelaJogador();
                ControladorTabuleiroJogador controlador = new ControladorTabuleiroJogador(tela);
                try {
                    assertTrue(fachada.ativarColunaJogadorAtual(1));
                    assertEquals("2 (1 + 1 virtual)", valor(tela, "recurso.ESPADA"));
                    fachada.moverPeao(ParadaID.PEQUIM1);
                    assertTrue(fachada.atacarCidade(CidadeID.PEQUIM, 0));
                    assertEquals("1", valor(tela, "recurso.ESPADA"));
                    assertEquals("1", valor(tela, "tesouro.FERRO.real"));
                    assertEquals(1, jogador.getQuantidadeTributo(TipoTributo.ESPADA));
                } finally { controlador.encerrar(); }
            }
        });
    }

    @Test
    public void yurtVirtualEhConsumidoAntesDoRealESempreGastaEstoque() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                Partida partida = criarPartida();
                Jogador jogador = partida.getJogadorAtual();
                jogador.adicionarTributo(TipoTributo.YURT, 1);
                melhoria(jogador, TipoMelhoria.YURT_VIRTUAL);
                FachadaJogo fachada = FachadaJogo.getInstancia();
                fachada.definirPartida(partida);
                TelaJogador tela = new TelaJogador();
                ControladorTabuleiroJogador controlador = new ControladorTabuleiroJogador(tela);
                try {
                    assertTrue(fachada.ativarColunaJogadorAtual(1));
                    fachada.construirYurt(ParadaID.KARAKORUM);
                    assertEquals("2 (1 + 1 virtual)", valor(tela, "recurso.YURT"));
                    assertEquals(12, jogador.getYurtsDisponiveis());
                    fachada.moverPeao(ParadaID.PEQUIM1);
                    fachada.construirYurt(ParadaID.PEQUIM1);
                    assertEquals("1", valor(tela, "recurso.YURT"));
                    assertEquals("11", valor(tela, "estoque.YURTS"));
                    assertEquals(1, jogador.getQuantidadeTributo(TipoTributo.YURT));
                    assertEquals(0, partida.getQuantidadeTributoVirtual(TipoTributo.YURT));
                    fachada.moverPeao(ParadaID.PEQUIM2);
                    fachada.construirYurt(ParadaID.PEQUIM2);
                    assertEquals("0", valor(tela, "recurso.YURT"));
                    assertEquals("10", valor(tela, "estoque.YURTS"));
                } finally { controlador.encerrar(); }
            }
        });
    }

    @Test
    public void permiteConstruirSemTributoRealMasNaoSemYurtsNoEstoque() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        melhoria(jogador, TipoMelhoria.YURT_VIRTUAL);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.moverPeao(ParadaID.PEQUIM1));
        assertTrue(partida.construirYurt(ParadaID.PEQUIM1));
        assertEquals(11, jogador.getYurtsDisponiveis());
        assertEquals(0, jogador.getQuantidadeTributo(TipoTributo.YURT));
        assertEquals(0, partida.getQuantidadeTributoVirtual(TipoTributo.YURT));
        Partida semEstoque = criarPartida();
        jogador = semEstoque.getJogadorAtual();
        for (int i = 0; i < 12; i++) assertTrue(jogador.usarYurt(true));
        melhoria(jogador, TipoMelhoria.YURT_VIRTUAL);
        assertTrue(semEstoque.iniciarTurno(Regiao.PERSIA));
        assertTrue(semEstoque.moverPeao(ParadaID.PEQUIM1));
        assertFalse(semEstoque.construirYurt(ParadaID.PEQUIM1));
        assertEquals(1, semEstoque.getQuantidadeTributoVirtual(TipoTributo.YURT));
    }

    @Test
    public void abaTesourosMostraEstoqueVirtuaisETotalEAcompanhaEntrega() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                Partida partida = criarPartida();
                Jogador jogador = partida.getJogadorAtual();
                Parada local = encontrarParada(partida, ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA);
                Peao peao = jogador.getPeao(0);
                peao.getPosicao().removePeao(peao);
                peao.setPosicao(local);local.insertPeao(peao);
                jogador.adicionarTesouro(TipoTesouro.PELE);
                jogador.adicionarTesouro(TipoTesouro.TECIDO);
                jogador.adicionarTesouro(TipoTesouro.TECIDO);
                melhoria(jogador, TipoMelhoria.PELE_VIRTUAL);
                melhoria(jogador, TipoMelhoria.TECIDO_VIRTUAL);
                FachadaJogo fachada = FachadaJogo.getInstancia();
                fachada.definirPartida(partida);
                TelaJogador tela = new TelaJogador();
                ControladorTabuleiroJogador controlador = new ControladorTabuleiroJogador(tela);
                try {
                    assertTrue(fachada.ativarColunaJogadorAtual(1));
                    assertEquals("1", valor(tela, "tesouro.PELE.real"));
                    assertEquals("1", valor(tela, "tesouro.PELE.virtual"));
                    assertEquals("2", valor(tela, "tesouro.PELE.total"));
                    HashMap<String, Integer> entrega = new HashMap<>();entrega.put("PELE", 2);
                    assertTrue(fachada.entregarTesourosJogadorAtual(local.getId(), entrega));
                    assertEquals("0", valor(tela, "tesouro.PELE.total"));
                    assertEquals("3", valor(tela, "tesouro.TECIDO.total"));
                    assertTrue(fachada.terminarTurnoJogadorAtual());
                    assertTrue(fachada.ativarColunaJogadorAtual(2));
                    assertTrue(fachada.terminarTurnoJogadorAtual());
                    assertEquals("2", valor(tela, "tesouro.TECIDO.real"));
                    assertEquals("0", valor(tela, "tesouro.TECIDO.virtual"));
                    assertEquals("2", valor(tela, "tesouro.TECIDO.total"));
                    assertFalse(temBotaoDeAcao(tela));
                } finally { controlador.encerrar(); }
            }
        });
    }

    private boolean temBotaoDeAcao(Container raiz) {
        for (Component c : raiz.getComponents()) {
            if (c instanceof JButton && Arrays.asList("Terminar turno", "Terminar Turno", "Entregar tesouros", "Comprar melhoria")
                    .contains(((JButton) c).getText())) return true;
            if (c instanceof Container && temBotaoDeAcao((Container) c)) return true;
        }
        return false;
    }

    @Test
    public void botoesTransferidosChamamFachadaECancelarEntregaNaoGastaNada() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                Partida partida = criarPartida();
                Jogador jogador = partida.getJogadorAtual();
                jogador.adicionarTesouro(TipoTesouro.FERRO);
                jogador.adicionarTesouro(TipoTesouro.PELE);
                jogador.adicionarTesouro(TipoTesouro.BOI);
                encontrarParada(partida, ParadaID.KARAKORUM).setBonus(null);
                FachadaJogo fachada = FachadaJogo.getInstancia();
                fachada.definirPartida(partida);
                final boolean[] cancelar = {true};
                PainelAcoesPartida acoes = new PainelAcoesPartida() {
                    @Override
                    public HashMap<String, Integer> escolherTesourosEntrega(ParadaID local) {
                        if (cancelar[0]) return null;
                        HashMap<String, Integer> entrega = new HashMap<>();
                        entrega.put("FERRO", 1);entrega.put("PELE", 1);entrega.put("BOI", 1);
                        return entrega;
                    }
                };
                ControllerAPI controlador = new ControllerAPI();
                controlador.configurarAcoes(acoes);
                try {
                    assertFalse(acoes.getBotaoTerminarTurno().isEnabled());
                    assertFalse(acoes.getBotaoComprarMelhoria().isEnabled());
                    assertTrue(fachada.ativarColunaJogadorAtual(1));
                    assertTrue(acoes.getBotaoEntregarTesouros().isEnabled());
                    acoes.getBotaoEntregarTesouros().doClick();
                    assertEquals(3, jogador.getTotalTesouros());
                    assertEquals(0, jogador.getVotos());
                    cancelar[0] = false;
                    acoes.getBotaoEntregarTesouros().doClick();
                    assertEquals(0, jogador.getTotalTesouros());
                    assertEquals(2, jogador.getVotos());
                    acoes.getBotaoTerminarTurno().doClick();
                    assertFalse(fachada.isTurnoEmAndamento());
                    assertFalse(acoes.getBotaoEntregarTesouros().isEnabled());
                } finally { controlador.removerObservador(acoes); }
            }
        });
    }

    @Test
    public void botaoDeTerminarTurnoContinuaRespeitandoPendenciaDoKhan() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                FachadaJogo fachada = FachadaJogo.getInstancia();
                fachada.definirPartida(criarPartida());
                PainelAcoesPartida acoes = new PainelAcoesPartida();
                ControllerAPI controlador = new ControllerAPI();
                controlador.configurarAcoes(acoes);
                try {
                    assertTrue(fachada.ativarColunaJogadorAtual(0));
                    acoes.getBotaoTerminarTurno().doClick();
                    assertTrue(fachada.isTurnoEmAndamento());
                    assertTrue(fachada.selecionarKhan(KhanID.M));
                    acoes.getBotaoTerminarTurno().doClick();
                    assertFalse(fachada.isTurnoEmAndamento());
                } finally { controlador.removerObservador(acoes); }
            }
        });
    }
}
