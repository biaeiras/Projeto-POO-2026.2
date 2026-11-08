package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import model.CidadeID;
import model.FachadaJogo;
import model.KhanID;
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
    public void mouseReleased(MouseEvent evento) {
        if (!fachada.isTurnoEmAndamento() || fachada.isPartidaEncerrada()) {
            return;
        }

        if (evento.getButton() == MouseEvent.BUTTON2) {
            fachada.terminarTurnoJogadorAtual();
            return;
        }


        // Uma seleção executa uma única ação, na ordem dos elementos desenhados.
        if (evento.getButton() == MouseEvent.BUTTON1) {
            KhanID khan = viewAPI.getClickKhanID(evento.getX(), evento.getY());
            if (khan != null && fachada.getKhans().contains(khan)) {
                fachada.selecionarKhan(khan);
                return;
            }
            if (fachada.getAtivacaoKhan()) {
                ProvinciaID provincia = viewAPI.getClickProvinciaID(evento.getX(), evento.getY());
                if (provincia != null) fachada.selecionarTributo(provincia);
                return;
            }
            for (Map.Entry<CidadeID, Integer> entry : fachada.getCidadesAtacaveis().entrySet()) {
                int indice = viewAPI.getClickTesouro(evento.getX(), evento.getY(), entry.getKey(), entry.getValue());
                if (indice >= 0) {
                    fachada.atacarCidade(entry.getKey(), indice);
                    return;
                }
            }
            ParadaID parada = viewAPI.getClickParadaID(evento.getX(), evento.getY());
            if (parada != null) {
                fachada.moverPeao(parada);
                return;
            }
            ProvinciaID provincia = viewAPI.getClickProvinciaID(evento.getX(), evento.getY());
            if (provincia != null) fachada.selecionarTributo(provincia);
        } else if (evento.getButton() == MouseEvent.BUTTON3) {
            ParadaID parada = viewAPI.getClickParadaID(evento.getX(), evento.getY());
            if (parada != null) fachada.construirYurt(parada);
        }
    }
}
