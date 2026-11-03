package controller;

import model.FachadaJogo;
import view.ViewAPI;

public class ControllerAPI {
	private MouseTabuleiro mouseTabuleiro;
	public ControllerAPI(ViewAPI viewAPI, FachadaJogo fachada) {
		mouseTabuleiro = new MouseTabuleiro(viewAPI, fachada);
		viewAPI.getTabulerioPanel().addMouseListener(mouseTabuleiro);
		
	}
}
