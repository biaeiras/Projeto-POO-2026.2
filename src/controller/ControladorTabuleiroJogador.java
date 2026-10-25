package controller;

import java.util.ArrayList;

import model.FachadaJogo;
import view.PainelTabuleiroJogador;
import view.PainelTabuleiroJogador.MelhoriaVisual;

public class ControladorTabuleiroJogador {

    private FachadaJogo fachada;
    private PainelTabuleiroJogador painel;

    public ControladorTabuleiroJogador(
            PainelTabuleiroJogador painel
    ) {
        this.fachada = FachadaJogo.getInstancia();
        this.painel = painel;
    }

    public void atualizarTabuleiro() {

        ArrayList<MelhoriaVisual> melhorias = new ArrayList<>();

        // Percorre as quatro colunas
        for (int coluna = 0; coluna < 4; coluna++) {

            ArrayList<String> lista =
                    fachada.getMelhoriasTabuleiroJogador(coluna);

            // Cada posição da lista representa uma linha
            for (int linha = 0; linha < lista.size(); linha++) {

                melhorias.add(
                    new MelhoriaVisual(
                        lista.get(linha),
                        coluna,
                        linha
                    )
                );
            }
        }

        painel.atualizarMelhorias(melhorias);
    }
}