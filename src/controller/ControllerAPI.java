package controller;

import model.ModelAPI;
import view.ViewAPI;

public class ControllerAPI {
	private MouseTabuleiro mouseTabuleiro;
	public ControllerAPI(ViewAPI viewAPI, ModelAPI modelAPI) {
		mouseTabuleiro = new MouseTabuleiro(viewAPI, modelAPI);
		viewAPI.getTabulerioPanel().addMouseListener(mouseTabuleiro);
		
	}
}
