package view;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import model.Cor;
import model.ModelAPI;
import model.ParadaID;
import model.ProvinciaID;

public class ViewAPI {
	
	private static final Map<Cor, Color> colorMap = initColorMap();
	
	private static Map<Cor, Color> initColorMap() {
		Map<Cor, Color> temp = new HashMap<Cor, Color>();
		temp.put(Cor.AMARELO, Color.yellow);
		temp.put(Cor.AZUL, Color.blue);
		temp.put(Cor.CINZA, Color.gray);
		temp.put(Cor.VERDE, Color.green);
		temp.put(Cor.VERMELHO, Color.red);
		return Collections.unmodifiableMap(temp);
	}
	
	public static Color getColor(Cor c) {
		return colorMap.get(c);
	}
	
	JanelaTabuleiro janela;
	public ViewAPI(ModelAPI modelAPI) {
		janela = new JanelaTabuleiro(modelAPI);
		janela.setTitle("Tabuleiro");
		janela.setVisible(true);	
	}
	
	public ParadaID getClickParadaID(int x, int y) {
		return janela.getPainel().getCollisionParada(x, y);
	}
	
	public ProvinciaID getClickProvinciaID(int x, int y) {
		return janela.getPainel().getCollisionProvincia(x, y);
	}
	
	public JPanel getTabulerioPanel() {
		return janela.getPainel();
	}
	
	public void render() {
		janela.repaint();
		return;
	}

}
