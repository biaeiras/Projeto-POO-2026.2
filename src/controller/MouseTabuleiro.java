package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.FachadaJogo;
import model.ParadaID;
import model.ProvinciaID;
import view.ViewAPI;

class MouseTabuleiro extends MouseAdapter{
	
	private ViewAPI viewAPI;
	private FachadaJogo fachada;
	
	MouseTabuleiro(ViewAPI viewAPI, FachadaJogo fachada){
		this.viewAPI = viewAPI;
		this.fachada = fachada;
	}
	
	public void mouseClicked(MouseEvent e) {
		ParadaID idParada = viewAPI.getClickParadaID(e.getX(), e.getY());
		ProvinciaID idProvincia = viewAPI.getClickProvinciaID(e.getX(), e.getY());
		if(e.getButton() == MouseEvent.BUTTON2) {
			fachada.avancarTurno();
			viewAPI.render();
			return;
		}
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(idParada != null) fachada.moverPeao(idParada);
			if(idProvincia != null) fachada.pegarTributo(idProvincia);
		}
		if(e.getButton() == MouseEvent.BUTTON3) {
			if(idParada != null) fachada.construirYurt(idParada);
			
		}
		viewAPI.render();
	}
}
