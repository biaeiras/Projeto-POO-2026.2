package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import model.Cor;
import model.ModelAPI;
import model.ParadaID;
import model.ProvinciaID;

class PainelTabuleiro extends JPanel{
	private static final String IMAGE_PATH = "res//tabuleiro.jpg";
	private static final long serialVersionUID = 1L;
	private static final double RAIO_PARADA = 0.021;
	private static final double RAIO_PEAO = 0.015;
	private static final double RAIO_YURT = 0.024;
	private static final double ESPACAMENTO_PEAO = 0.010;
	private static final double ESPACAMENTO_YURT = 0.005;
	
	
	private Image imagemTabuleiro;
	private HashMap<ParadaID, Point2D.Double> posicoesParadas = new HashMap<ParadaID, Point2D.Double>();
	private HashMap<ProvinciaID, Point2D.Double> posicoesProvincias = new HashMap<ProvinciaID, Point2D.Double>();
	private ModelAPI modelAPI;
	
	private void initImages() {
		try {
			File img = new File(IMAGE_PATH);
			imagemTabuleiro = ImageIO.read(img);
		}catch(IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
			
		}
		return;
	}
	
	private void initParadas() {
		posicoesParadas.put(ParadaID.KARAKORUM, new Point2D.Double(0.44, 0.52));
		posicoesParadas.put(ParadaID.KIEV1, new Point2D.Double(0.1687, 0.0891));
		posicoesParadas.put(ParadaID.KIEV2, new Point2D.Double(0.1619, 0.1854));
		posicoesParadas.put(ParadaID.KIEV3, new Point2D.Double(0.0441, 0.1854));
		posicoesParadas.put(ParadaID.MOSCOU1, new Point2D.Double(0.4371, 0.1701));
		posicoesParadas.put(ParadaID.MOSCOU2, new Point2D.Double(0.3731, 0.2893));
		posicoesParadas.put(ParadaID.MOSCOU3, new Point2D.Double(0.2949, 0.1911));
		posicoesParadas.put(ParadaID.SARAI1, new Point2D.Double(0.6048, 0.0772));
		posicoesParadas.put(ParadaID.SARAI2, new Point2D.Double(0.6517, 0.2031));
		posicoesParadas.put(ParadaID.SARAI3, new Point2D.Double(0.5380, 0.2301));
		posicoesParadas.put(ParadaID.KAESONG1, new Point2D.Double(0.8606, 0.2271));
		posicoesParadas.put(ParadaID.KAESONG2, new Point2D.Double(0.9523, 0.3035));
		posicoesParadas.put(ParadaID.KAESONG3, new Point2D.Double(0.8685, 0.3688));
		posicoesParadas.put(ParadaID.SAMARCANDA1, new Point2D.Double(0.2906, 0.4088));
		posicoesParadas.put(ParadaID.SAMARCANDA2, new Point2D.Double(0.3291, 0.5345));
		posicoesParadas.put(ParadaID.SAMARCANDA3, new Point2D.Double(0.1970, 0.4875));
		posicoesParadas.put(ParadaID.PEQUIM1, new Point2D.Double(0.7378, 0.4316));
		posicoesParadas.put(ParadaID.PEQUIM2, new Point2D.Double(0.7485, 0.5757));
		posicoesParadas.put(ParadaID.PEQUIM3, new Point2D.Double(0.6529, 0.5507));
		posicoesParadas.put(ParadaID.BAGDA1, new Point2D.Double(0.0753, 0.5116));
		posicoesParadas.put(ParadaID.BAGDA2, new Point2D.Double(0.1560, 0.5642));
		posicoesParadas.put(ParadaID.BAGDA3, new Point2D.Double(0.0615, 0.6547));
		posicoesParadas.put(ParadaID.CABUL1, new Point2D.Double(0.4387, 0.6226));
		posicoesParadas.put(ParadaID.CABUL2, new Point2D.Double(0.5080, 0.7059));
		posicoesParadas.put(ParadaID.CABUL3, new Point2D.Double(0.4196, 0.7656));
		posicoesParadas.put(ParadaID.CANTAO1, new Point2D.Double(0.8702, 0.6978));
		posicoesParadas.put(ParadaID.CANTAO2, new Point2D.Double(0.8906, 0.8255));
		posicoesParadas.put(ParadaID.CANTAO3, new Point2D.Double(0.7692, 0.7642));
		posicoesParadas.put(ParadaID.KIEV_MOSCOU, new Point2D.Double(0.3073, 0.0736));
		posicoesParadas.put(ParadaID.SARAI_KAESONG_KARAKORUM, new Point2D.Double(0.7710, 0.1703));
		posicoesParadas.put(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA, new Point2D.Double(0.1581, 0.3090));
		posicoesParadas.put(ParadaID.BAGDA_CABUL, new Point2D.Double(0.25, 0.6489));
		posicoesParadas.put(ParadaID.KAESONG_CANTAO, new Point2D.Double(0.8794, 0.5332));
		posicoesParadas.put(ParadaID.PEQUIM_CABUL_CANTAO, new Point2D.Double(0.6326, 0.7630));
		return;
	}
	
	private void initProvincias() {
		posicoesProvincias.put(ProvinciaID.TL_YURT, new Point2D.Double(0.2175, 0.1480));
		posicoesProvincias.put(ProvinciaID.TL_ESPADA, new Point2D.Double(0.2969, 0.1264));
		posicoesProvincias.put(ProvinciaID.TL_MOEDA, new Point2D.Double(0.2752, 0.3228));
		posicoesProvincias.put(ProvinciaID.BL_YURT, new Point2D.Double(0.5244, 0.5587));
		posicoesProvincias.put(ProvinciaID.BL_ESPADA, new Point2D.Double(0.1803, 0.4187));
		posicoesProvincias.put(ProvinciaID.BL_MOEDA, new Point2D.Double(0.2092, 0.5696));
		posicoesProvincias.put(ProvinciaID.TR_YURT, new Point2D.Double(0.4287, 0.3186));
		posicoesProvincias.put(ProvinciaID.TR_ESPADA, new Point2D.Double(0.7419, 0.3607));
		posicoesProvincias.put(ProvinciaID.TR_MOEDA, new Point2D.Double(0.6759, 0.2591));
		posicoesProvincias.put(ProvinciaID.BR_YURT, new Point2D.Double(0.7114, 0.6299));
		posicoesProvincias.put(ProvinciaID.BR_ESPADA, new Point2D.Double(0.5787, 0.6611));
		posicoesProvincias.put(ProvinciaID.BR_MOEDA, new Point2D.Double(0.8255, 0.5363));
	}
	
 	private void drawParadas(Graphics2D g, HashSet<ParadaID> paradas, Color color) {
		g.setColor(color);
		g.setStroke(new BasicStroke((float)(ESPACAMENTO_YURT*(getWidth() + getHeight())/4)));
		for(ParadaID parada: paradas) {
			Point2D.Double p = posicoesParadas.get(parada);
			int x = (int)((p.getX()-RAIO_PARADA)*getWidth());
			int y = (int)((p.getY()-RAIO_PARADA)*getHeight());
			int w = (int) (2*RAIO_PARADA*getWidth());
			int h = (int) (2*RAIO_PARADA*getHeight());
			g.drawOval(x, y, w, h);
		}
		return;
	}
	
 	private void drawParadas(Graphics2D g, HashSet<ParadaID> vizinhos, HashSet<ParadaID> possiveis) {
 		drawParadas(g, possiveis, Color.white);
 		drawParadas(g, vizinhos, Color.magenta);
 	}
 	
 	private void drawProvincias(Graphics2D g, HashMap<ProvinciaID, Integer> provincias, HashSet<ProvinciaID> interagiveis) {
 		g.setStroke(new BasicStroke((float)(ESPACAMENTO_YURT*(getWidth() + getHeight())/4)));
 		for(Map.Entry<ProvinciaID, Integer> entry: provincias.entrySet()) {
 			if(interagiveis.contains(entry.getKey()))
 				g.setColor(Color.red);
 			else
 				g.setColor(Color.cyan);
 			Point2D.Double p = posicoesProvincias.get(entry.getKey());
 			for(int i = 0; i<entry.getValue(); ++i) { 				
 				int x = (int)((p.getX()-(RAIO_PARADA+ESPACAMENTO_YURT*i))*getWidth());
 				int y = (int)((p.getY()-(RAIO_PARADA+ESPACAMENTO_YURT*i))*getHeight());
 				int w = (int) (2*(RAIO_PARADA+ESPACAMENTO_YURT*i)*getWidth());
 				int h = (int) (2*(RAIO_PARADA+ESPACAMENTO_YURT*i)*getHeight());
 				g.drawOval(x, y, w, h);
 				g.setColor(Color.cyan);
 			}
 		}
 	}
 	
	PainelTabuleiro(ModelAPI modelAPI){
		this.modelAPI = modelAPI;
		initImages();
		initParadas();
		initProvincias();
	}
	
	private void drawPeao(Graphics2D g, ParadaID id, Cor cor, int i) {
		double px = posicoesParadas.get(id).getX() - RAIO_PEAO;
		double py = posicoesParadas.get(id).getY() - RAIO_PEAO - ESPACAMENTO_PEAO*i;
		int x = (int) (px*getWidth());
		int y = (int) (py*getHeight());
		int w = (int) (2*RAIO_PEAO*getWidth());
		int h = (int) (2*RAIO_PEAO*getHeight());
		g.setColor(ViewAPI.getColor(cor));
		g.fillOval(x, y, w, h);
		return;
	}
	
	private void drawYurt(Graphics2D g, ParadaID id, Cor cor, int i) {
		double px = posicoesParadas.get(id).getX() - RAIO_YURT - ESPACAMENTO_YURT*i;
		double py = posicoesParadas.get(id).getY() - RAIO_YURT - ESPACAMENTO_YURT*i;
		int x = (int) (px*getWidth());
		int y = (int) (py*getHeight());
		int w = (int) (2*(RAIO_YURT + ESPACAMENTO_YURT*i)*getWidth());
		int h = (int) (2*(RAIO_YURT + ESPACAMENTO_YURT*i)*getHeight());
		g.setStroke(new BasicStroke((float)(ESPACAMENTO_YURT*(getWidth() + getHeight())/4)));
		g.setColor(ViewAPI.getColor(cor));
		g.drawOval(x, y, w, h);
	}
	
	private void drawPeoes(Graphics2D g, HashMap<ParadaID, ArrayList<Cor>> cores) {
		for(Map.Entry<ParadaID, ArrayList<Cor>> entry: cores.entrySet()) {
			for(int i = 0; i<entry.getValue().size(); ++i) drawPeao(g, entry.getKey(), entry.getValue().get(i), i);
		}
		return;
	}

	private void drawYurts(Graphics2D g, HashMap<ParadaID, ArrayList<Cor>> cores) {
		for(Map.Entry<ParadaID, ArrayList<Cor>> entry: cores.entrySet()) {
			for(int i = 0; i<entry.getValue().size(); ++i) drawYurt(g, entry.getKey(), entry.getValue().get(i), i);
		}
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(imagemTabuleiro, 0, 0, getWidth(), getHeight(), null);
		drawParadas(g2d, modelAPI.getVizinhosPossiveis(), modelAPI.getAlcancaveisTotais());
		drawProvincias(g2d, modelAPI.getCargasProvincias(), modelAPI.getProvincias());
		drawYurts(g2d, modelAPI.getYurts());
		drawPeoes(g2d, modelAPI.getPosicoesPeoes());
	}
	
	ParadaID getCollisionParada(int x, int y) {
		for(ParadaID p: posicoesParadas.keySet()) {
			Point2D.Double pp = posicoesParadas.get(p);
			double x0 = (double) x / getWidth();
			double y0 = (double) y / getHeight();
			double px = pp.getX() - x0;
			double py = pp.getY() - y0;
			double dist;
			px*=px;
			py*=py;
			dist = px+py;
			
			if(dist<=RAIO_PARADA*RAIO_PARADA) return p;
		}
		return null;
	}
	
	ProvinciaID getCollisionProvincia(int x, int y) {
		for(ProvinciaID p: posicoesProvincias.keySet()) {
			Point2D.Double pp = posicoesProvincias.get(p);
			double x0 = (double) x / getWidth();
			double y0 = (double) y / getHeight();
			double px = pp.getX() - x0;
			double py = pp.getY() - y0;
			double dist;
			px*=px;
			py*=py;
			dist = px+py;
			
			if(dist<=RAIO_PARADA*RAIO_PARADA) return p;
		}
		return null;
	}
}
