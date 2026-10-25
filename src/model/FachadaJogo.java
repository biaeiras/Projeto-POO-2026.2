package model;

import java.util.ArrayList;

public class FachadaJogo {

    // Singleton
    private static FachadaJogo instancia = null;
    
 // Impede a criação de outras instâncias
    private FachadaJogo() {
    }

    // Único ponto de acesso à fachada
    public static FachadaJogo getInstancia() {
        if (instancia == null) {
        	instancia = new FachadaJogo();
        }
    	return instancia;
    }

    // Partida gerenciada pela fachada
    private Partida partida;

    
    // Inicia uma partida com as cores escolhidas
    public void iniciarPartida(ArrayList<Cor> coresEscolhidas) {
        definirPartida(new Partida(coresEscolhidas));
    }

    // Associa a partida criada à fachada
    void definirPartida(Partida partida) {

        if (partida == null) {
            throw new IllegalArgumentException(
                "A partida não pode ser nula."
            );
        }

        this.partida = partida;
    }

    // Consulta se existe uma partida
    public boolean existePartida() {
        return partida != null;
    }

    // Consulta as melhorias de uma coluna do jogador atual
    public ArrayList<String> getMelhoriasTabuleiroJogador(
            int coluna
    ) {

        if (partida == null) {
            throw new IllegalStateException(
                "Nenhuma partida foi iniciada."
            );
        }

        if (coluna < 0 || coluna > 3) {
            throw new IllegalArgumentException(
                "Coluna inválida."
            );
        }

        Jogador jogador = partida.getJogadorAtual();

        TabuleiroJogador tabuleiro =
                jogador.getTabuleiroJogador();

        ArrayList<Melhoria> melhorias;

        switch (coluna) {

            case 0:
                melhorias = tabuleiro.getMelhorias(
                        Regiao.CHINA
                );
                break;

            case 1:
                melhorias = tabuleiro.getMelhorias(
                        Regiao.PERSIA
                );
                break;

            case 2:
                melhorias = tabuleiro.getMelhorias(
                        Regiao.RUSSIA
                );
                break;

            case 3:
                melhorias =
                        tabuleiro.getMelhoriasColunaBranca();
                break;

            default:
                throw new IllegalArgumentException(
                    "Coluna inválida."
                );
        }

        ArrayList<String> nomesImagens = new ArrayList<>();

        for (Melhoria melhoria : melhorias) {

            String nomeArquivo =
                    melhoria.getTipo().name().toLowerCase()
                    + "_"
                    + melhoria.getRegiao().name().toLowerCase()
                    + ".jpg";

            nomesImagens.add(nomeArquivo);
        }

        return nomesImagens;
    }
}
