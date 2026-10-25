package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.ModelAPI;
import model.ParadaID;
import model.ProvinciaID;
import view.ViewAPI;

class MouseTabuleiro extends MouseAdapter{
	
	private ViewAPI viewAPI;
	private ModelAPI modelAPI;
	
	MouseTabuleiro(ViewAPI viewAPI, ModelAPI modelAPI){
		this.viewAPI = viewAPI;
		this.modelAPI = modelAPI;
	}
	
	public void mouseClicked(MouseEvent e) {
		ParadaID idParada = viewAPI.getClickParadaID(e.getX(), e.getY());
		ProvinciaID idProvincia = viewAPI.getClickProvinciaID(e.getX(), e.getY());
		if(e.getButton() == MouseEvent.BUTTON2) {
			modelAPI.avancarTurno();
			viewAPI.render();
			return;
		}
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(idParada != null) modelAPI.moverPeao(idParada);
			if(idProvincia != null) modelAPI.pegarTributo(idProvincia);
		}
		if(e.getButton() == MouseEvent.BUTTON3) {
			if(idParada != null) modelAPI.construirYurt(idParada);
			
		}
		viewAPI.render();
	}
}
