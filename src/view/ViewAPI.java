package view;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowStateListener;

import controller.ControllerAPI;
import model.CidadeID;
import model.Cor;
import model.FachadaJogo;
import model.KhanID;
import model.ParadaID;
import model.ProvinciaID;

public class ViewAPI {

    private static final Map<Cor, Color> colorMap = initColorMap();
    private final TelaPrincipal janela;
    private final PainelTabuleiro painel;
    private final ControllerAPI controlador;

    private static Map<Cor, Color> initColorMap() {
        Map<Cor, Color> cores = new HashMap<>();
        cores.put(Cor.AMARELO, Color.YELLOW);
        cores.put(Cor.AZUL, Color.BLUE);
        cores.put(Cor.CINZA, Color.GRAY);
        cores.put(Cor.VERDE, Color.GREEN);
        cores.put(Cor.VERMELHO, Color.RED);
        return Collections.unmodifiableMap(cores);
    }

    public static Color getColor(Cor cor) {
        return colorMap.get(cor);
    }

    static Dimension limitarDimensoes(Dimension tamanho) {
        return new Dimension(Math.min(1400, tamanho.width), Math.min(1050, tamanho.height));
    }

    public static void configurarLimitesJanela(final JFrame janela) {
        janela.setMaximumSize(new Dimension(1400, 1050));
        // Evita que a tela cheia nativa do macOS ultrapasse o limite da disciplina.
        janela.getRootPane().putClientProperty("apple.awt.fullscreenable", Boolean.FALSE);
        Rectangle tela = new Rectangle(janela.getGraphicsConfiguration().getBounds());
        Insets margens = Toolkit.getDefaultToolkit().getScreenInsets(janela.getGraphicsConfiguration());
        tela.x += margens.left;
        tela.y += margens.top;
        tela.width -= margens.left + margens.right;
        tela.height -= margens.top + margens.bottom;
        tela.setSize(limitarDimensoes(tela.getSize()));
        janela.setMaximizedBounds(tela);
        janela.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evento) {
                Dimension limite = limitarDimensoes(janela.getSize());
                if (!limite.equals(janela.getSize())) {
                    janela.setExtendedState(JFrame.NORMAL);
                    janela.setSize(limite);
                }
            }
        });
        janela.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent evento) {
                Dimension limite = limitarDimensoes(janela.getSize());
                if (!limite.equals(janela.getSize())) {
                    janela.setExtendedState(JFrame.NORMAL);
                    janela.setSize(limite);
                }
            }
        });
        janela.setSize(limitarDimensoes(janela.getSize()));
    }

    public ViewAPI(FachadaJogo fachada) {
        this(new ControllerAPI(fachada));
    }

    public ViewAPI(ControllerAPI controlador) {
        this.controlador = controlador;
        janela = new TelaPrincipal(controlador);
        painel = new PainelTabuleiro();
        janela.definirMapa(painel);
        painel.atualizar(controlador.registrarObservador(painel));

        janela.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evento) {
                ViewAPI.this.controlador.removerObservador(painel);
            }
        });
    }

    public TelaPrincipal getTelaPrincipal() {
        return janela;
    }

    public void exibir() {
        janela.setVisible(true);
    }

    public ParadaID getClickParadaID(int x, int y) {
        return painel.getCollisionParada(x, y);
    }

    public ProvinciaID getClickProvinciaID(int x, int y) {
        return painel.getCollisionProvincia(x, y);
    }

    public KhanID getClickKhanID(int x, int y) {
        return painel.getCollisionKhan(x, y);
    }
    public int getClickTesouro(int x, int y, CidadeID id, int n) {
        return painel.getCollisionCidade(x, y, id, n);
    }

    public JPanel getTabulerioPanel() {
        return painel;
    }

    public void render() {
        painel.repaint();
    }

    public void encerrar() {
        controlador.removerObservador(painel);
        controlador.removerObservador(janela);
        janela.dispose();
    }
}
