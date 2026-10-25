package view;

import javax.swing.JFrame;

import model.ModelAPI;

class JanelaTabuleiro extends JFrame{

	private static final long serialVersionUID = 1L;
	static final int STANDARD_WIDTH = 720;
	static final int STANDARD_HEIGHT = 480;
	private PainelTabuleiro painel;
	JanelaTabuleiro(ModelAPI modelAPI){
		painel = new PainelTabuleiro(modelAPI);
		setSize(STANDARD_WIDTH, STANDARD_HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(true);
		System.out.println(getWidth());
		System.out.println(getHeight());
		getContentPane().add(painel);
	}
	
	PainelTabuleiro getPainel() {
		return painel;
	}
	
	
}
