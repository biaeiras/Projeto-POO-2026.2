package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.Cor;

public class TelaPreparacaoPartida extends JFrame {

    private final HashMap<Cor, JCheckBox> personagens =
        new HashMap<>();

    private final JPanel conteudo = new JPanel();
    private final JLabel mensagem = new JLabel(
        "Selecione de 2 a 5 personagens.",
        SwingConstants.CENTER
    );

    private final JButton confirmarPersonagens =
        new JButton("Confirmar personagens");

    private final JButton continuar =
        new JButton("Continuar");

    private final JLabel jogador = new JLabel(
        "",
        SwingConstants.CENTER
    );

    private final PainelCartaConselheiroSecreto primeiraCarta =
        new PainelCartaConselheiroSecreto();

    private final PainelCartaConselheiroSecreto segundaCarta =
        new PainelCartaConselheiroSecreto();

    public TelaPreparacaoPartida() {
        setTitle("Herdeiros de Khan - Preparação");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        raiz.add(mensagem, BorderLayout.NORTH);
        raiz.add(conteudo, BorderLayout.CENTER);

        setContentPane(raiz);
        mostrarSelecaoPersonagens();
    }

    private void mostrarSelecaoPersonagens() {
        conteudo.setLayout(new GridLayout(0, 1, 8, 8));

        adicionarPersonagem(Cor.VERDE, "Altani — verde");
        adicionarPersonagem(Cor.VERMELHO, "Chagatai — vermelho");
        adicionarPersonagem(Cor.CINZA, "Jochi — cinza");
        adicionarPersonagem(Cor.AMARELO, "Tolui — amarelo");
        adicionarPersonagem(Cor.AZUL, "Ogedei — azul");

        conteudo.add(confirmarPersonagens);
    }

    private void adicionarPersonagem(Cor cor, String descricao) {
        JCheckBox opcao = new JCheckBox(descricao);

        personagens.put(cor, opcao);
        conteudo.add(opcao);
    }

    public ArrayList<Cor> getCoresEscolhidas() {
        ArrayList<Cor> cores = new ArrayList<>();

        // Ordem estável para definir a sequência dos jogadores.
        for (Cor cor : Cor.values()) {
            if (personagens.get(cor).isSelected()) {
                cores.add(cor);
            }
        }

        return cores;
    }

    public void mostrarVezDeEscolher(String personagem) {
        conteudo.removeAll();
        conteudo.setLayout(new BorderLayout(10, 10));

        jogador.setText("Vez de " + personagem);
        mensagem.setText(
            "O jogador indicado deve continuar para ver suas cartas."
        );

        conteudo.add(jogador, BorderLayout.CENTER);
        conteudo.add(continuar, BorderLayout.SOUTH);

        conteudo.revalidate();
        conteudo.repaint();
    }

    public void mostrarConselheiros(
            String personagem,
            ArrayList<String> opcoes) {

        if (opcoes.size() != 2) {
            throw new IllegalArgumentException(
                "Devem ser fornecidas duas opções de conselheiro."
            );
        }

        conteudo.removeAll();
        conteudo.setLayout(new BorderLayout(10, 10));

        jogador.setText("Escolha de " + personagem);
        mensagem.setText(
        	    "Clique na carta para escolher seu conselheiro secreto."
        	);

        primeiraCarta.carregarCarta(
            opcoes.get(0).toLowerCase(Locale.ROOT) + ".jpg"
        );

        segundaCarta.carregarCarta(
            opcoes.get(1).toLowerCase(Locale.ROOT) + ".jpg"
        );

        JPanel cartas = new JPanel(new GridLayout(1, 2, 15, 0));

        cartas.add(primeiraCarta);
        cartas.add(segundaCarta);

        conteudo.add(jogador, BorderLayout.NORTH);
        conteudo.add(cartas, BorderLayout.CENTER);

        conteudo.revalidate();
        conteudo.repaint();
    }

    public void mostrarMensagem(String texto) {
        mensagem.setText(texto);
    }

    public JButton getBotaoConfirmarPersonagens() {
        return confirmarPersonagens;
    }

    public JButton getBotaoContinuar() {
        return continuar;
    }
    
    public PainelCartaConselheiroSecreto getPrimeiraCarta() {
        return primeiraCarta;
    }

    public PainelCartaConselheiroSecreto getSegundaCarta() {
        return segundaCarta;
    }
}