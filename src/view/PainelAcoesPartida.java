package view;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.*;
import model.ObservadoJogo;
import model.ObservadorJogo;
import model.ParadaID;

public class PainelAcoesPartida extends JPanel implements ObservadorJogo {
    private final Color pergaminho = new Color(239, 225, 190);
    private final Color texto = new Color(75, 51, 30);
    private final JButton botaoComprarMelhoria = new JButton("Comprar melhoria");
    private final JButton botaoEntregarTesouros = new JButton("Entregar tesouros");
    private final JButton botaoTerminarTurno = new JButton("Terminar turno");
    private final JLabel informacao = new JLabel("Inicie um turno para realizar ações.");
    private ObservadoJogo observado;

    public PainelAcoesPartida() {
        setLayout(new BorderLayout(0, 8));
        setBackground(new Color(190, 220, 173));
        setBorder(new CompoundBorder(new LineBorder(new Color(190, 145, 68)),
                new EmptyBorder(8, 8, 8, 8)));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        JLabel titulo = new JLabel("AÇÕES DA PARTIDA");
        titulo.setForeground(new Color(166, 45, 48));
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        add(titulo, BorderLayout.NORTH);
        JPanel botoes = new JPanel(new GridLayout(3, 1, 0, 6));
        botoes.setOpaque(false);
        JButton[] acoes = {botaoComprarMelhoria, botaoEntregarTesouros, botaoTerminarTurno};
        for (JButton botao : acoes) {
            botao.setBackground(new Color(250, 245, 226));
            botao.setForeground(texto);
            botao.setFont(new Font("SansSerif", Font.BOLD, 12));
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botao.setEnabled(false);
            botoes.add(botao);
        }
        botaoComprarMelhoria.setToolTipText("A compra de melhorias ainda não está disponível.");
        add(botoes, BorderLayout.CENTER);
        informacao.setForeground(texto);
        informacao.setFont(new Font("SansSerif", Font.PLAIN, 11));
        add(informacao, BorderLayout.SOUTH);
    }

    public JButton getBotaoComprarMelhoria() { return botaoComprarMelhoria; }
    public JButton getBotaoEntregarTesouros() { return botaoEntregarTesouros; }
    public JButton getBotaoTerminarTurno() { return botaoTerminarTurno; }

    public void mostrarInformacao(String mensagem) {
        informacao.setText("<html>" + mensagem + "</html>");
    }

    @Override
    public void atualizar(final ObservadoJogo observado) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() { atualizarInterface(observado); }
            });
        } else {
            atualizarInterface(observado);
        }
    }

    private void atualizarInterface(ObservadoJogo observado) {
        this.observado = observado;
        boolean turno = observado.isTurnoEmAndamento() && !observado.isPartidaEncerrada();
        botaoTerminarTurno.setEnabled(turno);
        botaoEntregarTesouros.setEnabled(turno && !observado.getLocaisEntregaDisponiveis().isEmpty());
        mostrarInformacao(observado.isPartidaEncerrada() ? "Partida encerrada."
                : turno ? "Use o mapa para mover, coletar e atacar."
                : "Escolha uma coluna na tela do jogador.");
    }

    public ParadaID getLocalEntregaAtual() {
        if (observado == null) return null;
        ArrayList<ParadaID> locais = observado.getLocaisEntregaDisponiveis();
        return locais.isEmpty() ? null : locais.get(0);
    }

    public HashMap<String, Integer> escolherTesourosEntrega(ParadaID local) {
        if (observado == null) return null;
        HashMap<String, Integer> exigencia = observado.getExigenciaEntrega(local);
        HashMap<String, Integer> reais = observado.getTesourosJogadorAtual();
        HashMap<String, Integer> virtuais = observado.getTesourosVirtuaisJogadorAtual();
        // A View apresenta as exigências fornecidas pelo Model.
        boolean escolhaLivre = exigencia.isEmpty();
        String[] tipos = {"BOI", "FERRO", "PELE", "TECIDO", "ALIMENTO"};
        JSpinner[] quantidades = new JSpinner[tipos.length];
        JPanel conteudo = new JPanel(new BorderLayout(8, 8));
        conteudo.setBackground(pergaminho);
        conteudo.setBorder(new EmptyBorder(12, 12, 12, 12));
        String bonus = observado.getBonusDisponivelEntrega(local);
        JLabel instrucoes = new JLabel("<html><b>"
                + local.name().replace('_', ' ') + "</b><br>"
                + (escolhaLivre ? "Escolha 3 tesouros de qualquer tipo."
                        : "Entregue o conjunto indicado abaixo.")
                + "<br>Recompensa: " + observado.getVotosEntrega(local) + " voto(s).<br>"
                + (bonus == null ? "A peça de bônus deste local já foi recolhida."
                        : "Peça de bônus disponível: " + bonus.toLowerCase(java.util.Locale.ROOT).replace('_', ' '))
                + "<br>Os tesouros virtuais disponíveis são utilizados primeiro.</html>");
        instrucoes.setForeground(texto);
        conteudo.add(instrucoes, BorderLayout.NORTH);
        JPanel grade = new JPanel(new GridLayout(0, 3, 10, 6));
        grade.setOpaque(false);
        grade.add(new JLabel("Tesouro"));
        grade.add(new JLabel("Estoque + virtuais"));
        grade.add(new JLabel("Entregar"));
        for (int i = 0; i < tipos.length; i++) {
            String tipo = tipos[i];
            int quantidade = escolhaLivre ? 0 : exigencia.getOrDefault(tipo, 0);
            quantidades[i] = new JSpinner(new SpinnerNumberModel(quantidade, 0, 3, 1));
            quantidades[i].setEnabled(escolhaLivre);
            // Impede digitação parcial: o seletor continua utilizável pelas setas.
            ((JSpinner.DefaultEditor) quantidades[i].getEditor()).getTextField().setEditable(false);
            grade.add(new JLabel(tipo));
            grade.add(new JLabel(reais.getOrDefault(tipo, 0) + " + " + virtuais.getOrDefault(tipo, 0)));
            grade.add(quantidades[i]);
        }
        conteudo.add(grade, BorderLayout.CENTER);
        int resposta = JOptionPane.showConfirmDialog(this, conteudo,
                "Confirmar entrega", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resposta != JOptionPane.OK_OPTION) return null;
        HashMap<String, Integer> entrega = new HashMap<>();
        for (int i = 0; i < tipos.length; i++) {
            int quantidade = (Integer) quantidades[i].getValue();
            if (quantidade > 0) entrega.put(tipos[i], quantidade);
        }
        return entrega;
    }

}
