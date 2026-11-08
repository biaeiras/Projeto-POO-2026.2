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

        
        ParadaID parada = viewAPI.getClickParadaID(evento.getX(), evento.getY());
        ProvinciaID provincia = viewAPI.getClickProvinciaID(evento.getX(), evento.getY());
        KhanID khan = viewAPI.getClickKhanID(evento.getX(), evento.getY());
        

        if (evento.getButton() == MouseEvent.BUTTON1) {
        	for(Map.Entry<CidadeID, Integer> entry: fachada.getCidadesAtacaveis().entrySet()) {
            	int i = viewAPI.getClickTesouro(evento.getX(), evento.getY(), entry.getKey(), entry.getValue());
            	if(i<0) continue;
            	
            	fachada.atacarCidade(entry.getKey(), i);
            	return;
            }
            if (parada != null) 
                fachada.moverPeao(parada);
            if (provincia != null)
                fachada.selecionarTributo(provincia);
            if (khan != null)
            	fachada.selecionarKhan(khan);
            
            	
        } else if (evento.getButton() == MouseEvent.BUTTON3 && parada != null) {
            fachada.construirYurt(parada);
        }
    }
}
