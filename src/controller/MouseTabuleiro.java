package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.FachadaJogo;
import model.ParadaID;
import model.ProvinciaID;
import view.ViewAPI;

class MouseTabuleiro extends MouseAdapter {

    private final ViewAPI viewAPI;
    private final FachadaJogo fachada;

    MouseTabuleiro(ViewAPI viewAPI, FachadaJogo fachada) {
        this.viewAPI = viewAPI;
        this.fachada = fachada;
    }

    @Override
    public void mouseClicked(MouseEvent evento) {
        if (!fachada.isTurnoEmAndamento() || fachada.isPartidaEncerrada()) {
            return;
        }

        if (evento.getButton() == MouseEvent.BUTTON2) {
            fachada.terminarTurnoJogadorAtual();
            return;
        }

        ParadaID parada = viewAPI.getClickParadaID(evento.getX(), evento.getY());
        ProvinciaID provincia = viewAPI.getClickProvinciaID(evento.getX(), evento.getY());

        if (evento.getButton() == MouseEvent.BUTTON1) {
            if (parada != null) {
                fachada.moverPeao(parada);
            } else if (provincia != null) {
                fachada.pegarTributo(provincia);
            }
        } else if (evento.getButton() == MouseEvent.BUTTON3 && parada != null) {
            fachada.construirYurt(parada);
        }
    }
}
