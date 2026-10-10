package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

class TabuleiroPrincipal {
	private final static int CIDADES_INICIAIS = 3;
	private final static int MELHORIAS = 5;
	private final static int MELHORIAS_MOEDAS = 8;
	private final static int MELHORIAS_ESPADAS = 8;
	private final static int MELHORIAS_PEGAR_TRIBUTO = 6;
	private final static int MELHORIAS_TESOURO = 1;
	private final static int MELHORIAS_YURT = 8;
	private final static int MELHORIAS_MOVIMENTO = 9;
	private final static int MULTIPLICIDADE_TESOURO = 8;
	private final static TipoMelhoria[] MELHORIAS_EXTRAS= {null, TipoMelhoria.MOEDA_VIRTUAL, null, TipoMelhoria.ESPADA_VIRTUAL, null};
	
	private class ProvinciaKhan{

		private Provincia espada = new Provincia(TipoTributo.ESPADA),
				moeda = new Provincia(TipoTributo.MOEDA),
				yurt = new Provincia(TipoTributo.YURT);
		private HashMap<TipoTributo, Provincia> provincias = new HashMap<TipoTributo, Provincia>();
		
		public ProvinciaKhan() {
			provincias.put(TipoTributo.ESPADA, espada);
			provincias.put(TipoTributo.MOEDA, moeda);
			provincias.put(TipoTributo.YURT, yurt);
			espada.carregar();
			moeda.carregar();
			yurt.carregar();
		}
		
		public Provincia getEspada() {
			return this.espada;
		}
		public Provincia getMoeda() {
			return this.moeda;
		}
		public Provincia getYurt() {
			return this.yurt;
		}
		public void activate(TipoTributo tributo1, TipoTributo tributo2) {
			provincias.get(tributo1).carregar();
			provincias.get(tributo2).carregar();
			
		}
	}
	private class SlotMelhoria{
		private TipoMelhoria melhoria = null;
		private TipoMelhoria melhoriaExtra;
		
		public SlotMelhoria(TipoMelhoria melhoriaExtra) {
			this.melhoriaExtra = melhoriaExtra;
		}
		
		public TipoMelhoria getMelhoriaExtra() {
			return this.melhoriaExtra;
		}
		
		public void setMelhoria(TipoMelhoria melhoria) {
			this.melhoria = melhoria;
			return;
		}
		
		public TipoMelhoria getMelhoria() {
			return this.melhoria;
		}
	}
	
	private HashMap<ProvinciaID, Provincia> provincias = new HashMap<ProvinciaID, Provincia>();
	private HashMap<Peao, Jogador> peoes;
	private HashMap<ParadaID, Parada> mapa = new HashMap<ParadaID, Parada>();
	private HashMap<CidadeID, Cidade> cidades = new HashMap<CidadeID, Cidade>();
	private HashMap<ProvinciaKhanID, ProvinciaKhan> provinciasKhan = new HashMap<ProvinciaKhanID, ProvinciaKhan>();
	private Stack<CidadeID> pilhaCidades = new Stack<CidadeID>();
	private Stack<TipoTesouro> pilhaTesouros = new Stack<TipoTesouro>();
	private Stack<TipoMelhoria> pilhaMelhorias = new Stack<TipoMelhoria>();
	private ArrayList<SlotMelhoria> melhorias = new ArrayList<SlotMelhoria>();
	private ProvinciaKhanID khan = null;
	
	private void initCidades() {
		for(CidadeID id : CidadeID.values()) cidades.put(id, new Cidade());
		for(CidadeID id : CidadeID.values()) pilhaCidades.push(id);
		Collections.shuffle(pilhaCidades);
		for(int i = 0; i < CIDADES_INICIAIS; i++) {
			CidadeID id = pilhaCidades.pop();
			Cidade tempCidade = cidades.get(id);
			tempCidade.revelar(pilhaTesouros);
		}
		return;
	}
	private void initTesouros() {
		for(TipoTesouro id : TipoTesouro.values()) {
			for(int i = 0; i<MULTIPLICIDADE_TESOURO; i++) pilhaTesouros.push(id);
		}
		Collections.shuffle(pilhaTesouros);
		return;
	}
	private void initProvincias() {
		for(ProvinciaKhanID id : ProvinciaKhanID.values()) provinciasKhan.put(id, new ProvinciaKhan());
		provincias.put(ProvinciaID.TL_ESPADA, provinciasKhan.get(ProvinciaKhanID.TL).getEspada());
		provincias.put(ProvinciaID.TL_MOEDA, provinciasKhan.get(ProvinciaKhanID.TL).getMoeda());
		provincias.put(ProvinciaID.TL_YURT, provinciasKhan.get(ProvinciaKhanID.TL).getYurt());
		provincias.put(ProvinciaID.TR_ESPADA, provinciasKhan.get(ProvinciaKhanID.TR).getEspada());
		provincias.put(ProvinciaID.TR_MOEDA, provinciasKhan.get(ProvinciaKhanID.TR).getMoeda());
		provincias.put(ProvinciaID.TR_YURT, provinciasKhan.get(ProvinciaKhanID.TR).getYurt());
		provincias.put(ProvinciaID.BL_ESPADA, provinciasKhan.get(ProvinciaKhanID.BL).getEspada());
		provincias.put(ProvinciaID.BL_MOEDA, provinciasKhan.get(ProvinciaKhanID.BL).getMoeda());
		provincias.put(ProvinciaID.BL_YURT, provinciasKhan.get(ProvinciaKhanID.BL).getYurt());
		provincias.put(ProvinciaID.BR_ESPADA, provinciasKhan.get(ProvinciaKhanID.BR).getEspada());
		provincias.put(ProvinciaID.BR_MOEDA, provinciasKhan.get(ProvinciaKhanID.BR).getMoeda());
		provincias.put(ProvinciaID.BR_YURT, provinciasKhan.get(ProvinciaKhanID.BR).getYurt());
		return;
	}
	private void initParadas() {
		Stack<TipoBonus> pilhaBonus = new Stack<TipoBonus>();
		for(TipoBonus id: TipoBonus.values()) pilhaBonus.push(id);
		Collections.shuffle(pilhaBonus);
		
		//Gerando paradas
		
		for(ParadaID id : ParadaID.values()) mapa.put(id, new Parada());
			
		//Caracterizando paradas
			//Karakorum
		Parada paradaTemp = mapa.get(ParadaID.KARAKORUM);
		Cidade cidadeTemp = cidades.get(CidadeID.KARAKORUM);
		cidadeTemp.setRegiao(null);
		paradaTemp.setBonus(pilhaBonus.pop());
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.SARAI3, mapa.get(ParadaID.SARAI3));
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA2, mapa.get(ParadaID.SAMARCANDA2));
		paradaTemp.insertVizinhos(ParadaID.PEQUIM1, mapa.get(ParadaID.PEQUIM1));
		paradaTemp.insertVizinhos(ParadaID.SARAI_KAESONG_KARAKORUM, mapa.get(ParadaID.SARAI_KAESONG_KARAKORUM));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TR_YURT, provinciasKhan.get(ProvinciaKhanID.TR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.TR_MOEDA, provinciasKhan.get(ProvinciaKhanID.TR).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.TR_ESPADA, provinciasKhan.get(ProvinciaKhanID.TR).getEspada());
		paradaTemp.insertProvincia(ProvinciaID.TR_YURT, provinciasKhan.get(ProvinciaKhanID.BL).getYurt());
		
			//Kiev1
		paradaTemp = mapa.get(ParadaID.KIEV1);
		cidadeTemp = cidades.get(CidadeID.KIEV);
		paradaTemp.setCidade(cidadeTemp);
		cidadeTemp.setRegiao(Regiao.RUSSIA);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.MOSCOU3, mapa.get(ParadaID.MOSCOU3));
		paradaTemp.insertVizinhos(ParadaID.KIEV_MOSCOU, mapa.get(ParadaID.KIEV_MOSCOU));
		paradaTemp.insertVizinhos(ParadaID.KIEV3, mapa.get(ParadaID.KIEV3));
		paradaTemp.insertVizinhos(ParadaID.KIEV2, mapa.get(ParadaID.KIEV2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TL_ESPADA, provinciasKhan.get(ProvinciaKhanID.TL).getEspada());
		paradaTemp.insertProvincia(ProvinciaID.TL_YURT, provinciasKhan.get(ProvinciaKhanID.TL).getYurt());
		
			//Kiev2
		paradaTemp = mapa.get(ParadaID.KIEV2);
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		
		paradaTemp.insertVizinhos(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA, mapa.get(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA));
		paradaTemp.insertVizinhos(ParadaID.KIEV1, mapa.get(ParadaID.KIEV1));
		paradaTemp.insertVizinhos(ParadaID.KIEV3, mapa.get(ParadaID.KIEV3));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TL_YURT, provinciasKhan.get(ProvinciaKhanID.TL).getYurt());
		
			//Kiev3
		paradaTemp = mapa.get(ParadaID.KIEV3);
		paradaTemp.setCidade(cidadeTemp);
		paradaTemp.insertVizinhos(ParadaID.KIEV1, mapa.get(ParadaID.KIEV1));
		paradaTemp.insertVizinhos(ParadaID.KIEV2, mapa.get(ParadaID.KIEV2));
		
			//Kiev_Moscou
		paradaTemp = mapa.get(ParadaID.KIEV_MOSCOU);
		paradaTemp.setMultiplicidade(2);
		paradaTemp.setBonus(pilhaBonus.pop());
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KIEV1, mapa.get(ParadaID.KIEV1));
		paradaTemp.insertVizinhos(ParadaID.MOSCOU1, mapa.get(ParadaID.MOSCOU1));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TL_ESPADA, provinciasKhan.get(ProvinciaKhanID.TL).getEspada());
		
			//Kiev_Moscou_Samarcanda_bagda
		paradaTemp = mapa.get(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA);
		paradaTemp.setMultiplicidade(2);
		paradaTemp.setBonus(pilhaBonus.pop());
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KIEV2, mapa.get(ParadaID.KIEV2));
		paradaTemp.insertVizinhos(ParadaID.MOSCOU3, mapa.get(ParadaID.MOSCOU3));
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA1, mapa.get(ParadaID.SAMARCANDA1));
		paradaTemp.insertVizinhos(ParadaID.BAGDA1, mapa.get(ParadaID.BAGDA1));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TL_YURT, provinciasKhan.get(ProvinciaKhanID.TL).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.TL_MOEDA, provinciasKhan.get(ProvinciaKhanID.TL).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.BL_ESPADA, provinciasKhan.get(ProvinciaKhanID.BL).getEspada());
		
			//Moscou1
		paradaTemp = mapa.get(ParadaID.MOSCOU1);
		cidadeTemp = cidades.get(CidadeID.MOSCOU);
		paradaTemp.setCidade(cidadeTemp);
		cidadeTemp.setRegiao(Regiao.RUSSIA);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KIEV_MOSCOU, mapa.get(ParadaID.KIEV_MOSCOU));
		paradaTemp.insertVizinhos(ParadaID.SARAI3, mapa.get(ParadaID.SARAI3));
		paradaTemp.insertVizinhos(ParadaID.MOSCOU3, mapa.get(ParadaID.MOSCOU3));
		paradaTemp.insertVizinhos(ParadaID.MOSCOU2, mapa.get(ParadaID.MOSCOU2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TL_ESPADA, provinciasKhan.get(ProvinciaKhanID.TL).getEspada());
		paradaTemp.insertProvincia(ProvinciaID.TR_YURT, provinciasKhan.get(ProvinciaKhanID.TR).getYurt());
		
			//Moscou2
		paradaTemp = mapa.get(ParadaID.MOSCOU2);
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA1, mapa.get(ParadaID.SAMARCANDA1));
		paradaTemp.insertVizinhos(ParadaID.MOSCOU1, mapa.get(ParadaID.MOSCOU1));
		paradaTemp.insertVizinhos(ParadaID.MOSCOU3, mapa.get(ParadaID.MOSCOU3));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TL_MOEDA, provinciasKhan.get(ProvinciaKhanID.TL).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.TR_YURT, provinciasKhan.get(ProvinciaKhanID.TR).getYurt());
		
			//Moscou3
		paradaTemp = mapa.get(ParadaID.MOSCOU3);
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KIEV1, mapa.get(ParadaID.KIEV1));
		paradaTemp.insertVizinhos(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA, mapa.get(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA));
		paradaTemp.insertVizinhos(ParadaID.MOSCOU1, mapa.get(ParadaID.MOSCOU1));
		paradaTemp.insertVizinhos(ParadaID.MOSCOU2, mapa.get(ParadaID.MOSCOU2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TL_YURT, provinciasKhan.get(ProvinciaKhanID.TL).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.TL_MOEDA, provinciasKhan.get(ProvinciaKhanID.TL).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.TL_ESPADA, provinciasKhan.get(ProvinciaKhanID.TL).getEspada());
		
			//Sarai1
		paradaTemp = mapa.get(ParadaID.SARAI1);
		cidadeTemp = cidades.get(CidadeID.SARAI);
		paradaTemp.setCidade(cidadeTemp);
		cidadeTemp.setRegiao(Regiao.RUSSIA);
		paradaTemp.insertVizinhos(ParadaID.SARAI3, mapa.get(ParadaID.SARAI3));
		paradaTemp.insertVizinhos(ParadaID.SARAI2, mapa.get(ParadaID.SARAI2));
		
			//Sarai2
		paradaTemp = mapa.get(ParadaID.SARAI2);
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.SARAI_KAESONG_KARAKORUM, mapa.get(ParadaID.SARAI_KAESONG_KARAKORUM));
		paradaTemp.insertVizinhos(ParadaID.SARAI1, mapa.get(ParadaID.SARAI1));
		paradaTemp.insertVizinhos(ParadaID.SARAI3, mapa.get(ParadaID.SARAI3));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TR_MOEDA, provinciasKhan.get(ProvinciaKhanID.TR).getMoeda());
		
			//Sarai3
		paradaTemp = mapa.get(ParadaID.SARAI3);
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.MOSCOU1, mapa.get(ParadaID.MOSCOU1));
		paradaTemp.insertVizinhos(ParadaID.KARAKORUM, mapa.get(ParadaID.KARAKORUM));
		paradaTemp.insertVizinhos(ParadaID.SARAI1, mapa.get(ParadaID.SARAI1));
		paradaTemp.insertVizinhos(ParadaID.SARAI2, mapa.get(ParadaID.SARAI2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TR_YURT, provinciasKhan.get(ProvinciaKhanID.TR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.TR_MOEDA, provinciasKhan.get(ProvinciaKhanID.TR).getMoeda());
		
			//Sarai_Kaesong_Karakorum
		paradaTemp = mapa.get(ParadaID.SARAI_KAESONG_KARAKORUM);
		paradaTemp.setMultiplicidade(2);
		paradaTemp.setBonus(pilhaBonus.pop());
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.SARAI2, mapa.get(ParadaID.SARAI2));
		paradaTemp.insertVizinhos(ParadaID.KAESONG1, mapa.get(ParadaID.KAESONG1));
		paradaTemp.insertVizinhos(ParadaID.KARAKORUM, mapa.get(ParadaID.KARAKORUM));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TR_MOEDA, provinciasKhan.get(ProvinciaKhanID.TR).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.TR_ESPADA, provinciasKhan.get(ProvinciaKhanID.TR).getEspada());
		
			//Kaesong1
		paradaTemp = mapa.get(ParadaID.KAESONG1);
		cidadeTemp = cidades.get(CidadeID.KAESONG);
		paradaTemp.setCidade(cidadeTemp);
		cidadeTemp.setRegiao(Regiao.CHINA);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.SARAI_KAESONG_KARAKORUM, mapa.get(ParadaID.SARAI_KAESONG_KARAKORUM));
		paradaTemp.insertVizinhos(ParadaID.KAESONG3, mapa.get(ParadaID.KAESONG3));
		paradaTemp.insertVizinhos(ParadaID.KAESONG2, mapa.get(ParadaID.KAESONG2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TR_ESPADA, provinciasKhan.get(ProvinciaKhanID.TR).getEspada());
		
			//Kaesong2
		paradaTemp = mapa.get(ParadaID.KAESONG2);
		paradaTemp.setCidade(cidadeTemp);
		paradaTemp.insertVizinhos(ParadaID.KAESONG1, mapa.get(ParadaID.KAESONG1));
		paradaTemp.insertVizinhos(ParadaID.KAESONG3, mapa.get(ParadaID.KAESONG3));
		
			//Kaesong3
		paradaTemp = mapa.get(ParadaID.KAESONG3);
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.PEQUIM1, mapa.get(ParadaID.PEQUIM1));
		paradaTemp.insertVizinhos(ParadaID.KAESONG_CANTAO, mapa.get(ParadaID.KAESONG_CANTAO));
		paradaTemp.insertVizinhos(ParadaID.KAESONG1, mapa.get(ParadaID.KAESONG1));
		paradaTemp.insertVizinhos(ParadaID.KAESONG2, mapa.get(ParadaID.KAESONG2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TR_ESPADA, provinciasKhan.get(ProvinciaKhanID.TR).getEspada());
		paradaTemp.insertProvincia(ProvinciaID.BR_MOEDA, provinciasKhan.get(ProvinciaKhanID.BR).getMoeda());
		
			//Kaesong_Cantao
		paradaTemp = mapa.get(ParadaID.KAESONG_CANTAO);
		paradaTemp.setMultiplicidade(2);
		paradaTemp.setBonus(pilhaBonus.pop());
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KAESONG3, mapa.get(ParadaID.KAESONG3));
		paradaTemp.insertVizinhos(ParadaID.CANTAO1, mapa.get(ParadaID.CANTAO1));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BR_MOEDA, provinciasKhan.get(ProvinciaKhanID.BR).getMoeda());
		
			//Samarcanda1
		paradaTemp = mapa.get(ParadaID.SAMARCANDA1);
		cidadeTemp = cidades.get(CidadeID.SAMARCANDA);
		paradaTemp.setCidade(cidadeTemp);
		cidadeTemp.setRegiao(Regiao.PERSIA);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.MOSCOU2, mapa.get(ParadaID.MOSCOU2));
		paradaTemp.insertVizinhos(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA, mapa.get(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA));
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA3, mapa.get(ParadaID.SAMARCANDA3));
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA2, mapa.get(ParadaID.SAMARCANDA2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TR_YURT, provinciasKhan.get(ProvinciaKhanID.TR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BL_ESPADA, provinciasKhan.get(ProvinciaKhanID.BL).getEspada());
		paradaTemp.insertProvincia(ProvinciaID.TL_MOEDA, provinciasKhan.get(ProvinciaKhanID.TL).getMoeda());
		
			//Samarcanda2
		paradaTemp = mapa.get(ParadaID.SAMARCANDA2);
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KARAKORUM, mapa.get(ParadaID.KARAKORUM));
		paradaTemp.insertVizinhos(ParadaID.CABUL1, mapa.get(ParadaID.CABUL1));
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA1, mapa.get(ParadaID.SAMARCANDA1));
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA3, mapa.get(ParadaID.SAMARCANDA3));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TR_YURT, provinciasKhan.get(ProvinciaKhanID.TR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BL_YURT, provinciasKhan.get(ProvinciaKhanID.BL).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BL_MOEDA, provinciasKhan.get(ProvinciaKhanID.BL).getMoeda());
		
			//Samarcanda3
		paradaTemp = mapa.get(ParadaID.SAMARCANDA3);
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.BAGDA1, mapa.get(ParadaID.BAGDA1));
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA1, mapa.get(ParadaID.SAMARCANDA1));
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA2, mapa.get(ParadaID.SAMARCANDA2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_MOEDA, provinciasKhan.get(ProvinciaKhanID.BL).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.BL_ESPADA, provinciasKhan.get(ProvinciaKhanID.BL).getEspada());
		
			//Bagda1
		paradaTemp = mapa.get(ParadaID.BAGDA1);
		cidadeTemp = cidades.get(CidadeID.BAGDA);
		paradaTemp.setCidade(cidadeTemp);
		cidadeTemp.setRegiao(Regiao.PERSIA);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA, mapa.get(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA));
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA3, mapa.get(ParadaID.SAMARCANDA3));
		paradaTemp.insertVizinhos(ParadaID.BAGDA3, mapa.get(ParadaID.BAGDA3));
		paradaTemp.insertVizinhos(ParadaID.BAGDA2, mapa.get(ParadaID.BAGDA2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_MOEDA, provinciasKhan.get(ProvinciaKhanID.BL).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.BL_ESPADA, provinciasKhan.get(ProvinciaKhanID.BL).getEspada());
		
			//Bagda2
		paradaTemp = mapa.get(ParadaID.BAGDA2);
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.BAGDA_CABUL, mapa.get(ParadaID.BAGDA_CABUL));
		paradaTemp.insertVizinhos(ParadaID.BAGDA1, mapa.get(ParadaID.BAGDA1));
		paradaTemp.insertVizinhos(ParadaID.BAGDA3, mapa.get(ParadaID.BAGDA3));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_MOEDA, provinciasKhan.get(ProvinciaKhanID.BL).getMoeda());
		
			//Bagda3
		paradaTemp = mapa.get(ParadaID.BAGDA3);
		paradaTemp.setCidade(cidadeTemp);
		paradaTemp.insertVizinhos(ParadaID.BAGDA1, mapa.get(ParadaID.BAGDA1));
		paradaTemp.insertVizinhos(ParadaID.BAGDA2, mapa.get(ParadaID.BAGDA2));
		
			//Bagda_Cabul
		paradaTemp = mapa.get(ParadaID.BAGDA_CABUL);
		paradaTemp.setMultiplicidade(2);
		paradaTemp.setBonus(pilhaBonus.pop());
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.BAGDA2, mapa.get(ParadaID.BAGDA2));
		paradaTemp.insertVizinhos(ParadaID.CABUL1, mapa.get(ParadaID.CABUL1));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_MOEDA, provinciasKhan.get(ProvinciaKhanID.BL).getMoeda());
		
			//Cabul1
		paradaTemp = mapa.get(ParadaID.CABUL1);
		cidadeTemp = cidades.get(CidadeID.CABUL);
		paradaTemp.setCidade(cidadeTemp);
		cidadeTemp.setRegiao(Regiao.PERSIA);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA2, mapa.get(ParadaID.SAMARCANDA2));
		paradaTemp.insertVizinhos(ParadaID.PEQUIM3, mapa.get(ParadaID.PEQUIM3));
		paradaTemp.insertVizinhos(ParadaID.BAGDA_CABUL, mapa.get(ParadaID.BAGDA_CABUL));
		paradaTemp.insertVizinhos(ParadaID.CABUL3, mapa.get(ParadaID.CABUL3));
		paradaTemp.insertVizinhos(ParadaID.CABUL2, mapa.get(ParadaID.CABUL2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_YURT, provinciasKhan.get(ProvinciaKhanID.BL).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BL_MOEDA, provinciasKhan.get(ProvinciaKhanID.BL).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.BR_ESPADA, provinciasKhan.get(ProvinciaKhanID.BR).getEspada());
		
			//Cabul2
		paradaTemp = mapa.get(ParadaID.CABUL2);
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.PEQUIM_CABUL_CANTAO, mapa.get(ParadaID.PEQUIM_CABUL_CANTAO));
		paradaTemp.insertVizinhos(ParadaID.CABUL1, mapa.get(ParadaID.CABUL1));
		paradaTemp.insertVizinhos(ParadaID.CABUL3, mapa.get(ParadaID.CABUL3));
				//Provincias

		paradaTemp.insertProvincia(ProvinciaID.BR_ESPADA, provinciasKhan.get(ProvinciaKhanID.BR).getEspada());
		
			//Cabul3
		paradaTemp = mapa.get(ParadaID.CABUL3);
		paradaTemp.setCidade(cidadeTemp);
		paradaTemp.insertVizinhos(ParadaID.CABUL1, mapa.get(ParadaID.CABUL1));
		paradaTemp.insertVizinhos(ParadaID.CABUL2, mapa.get(ParadaID.CABUL2));
		
			//Pequim_Cabul_Cantao
		paradaTemp = mapa.get(ParadaID.PEQUIM_CABUL_CANTAO);
		paradaTemp.setMultiplicidade(2);
		paradaTemp.setBonus(pilhaBonus.pop());
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.CABUL2, mapa.get(ParadaID.CABUL2));
		paradaTemp.insertVizinhos(ParadaID.CANTAO3, mapa.get(ParadaID.CANTAO3));
		paradaTemp.insertVizinhos(ParadaID.PEQUIM3, mapa.get(ParadaID.PEQUIM3));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BR_YURT, provinciasKhan.get(ProvinciaKhanID.BR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BR_ESPADA, provinciasKhan.get(ProvinciaKhanID.BR).getEspada());
		
			//Pequim1
		paradaTemp = mapa.get(ParadaID.PEQUIM1);
		cidadeTemp = cidades.get(CidadeID.PEQUIM);
		paradaTemp.setCidade(cidadeTemp);
		cidadeTemp.setRegiao(Regiao.CHINA);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KARAKORUM, mapa.get(ParadaID.KARAKORUM));
		paradaTemp.insertVizinhos(ParadaID.KAESONG3, mapa.get(ParadaID.KAESONG3));
		paradaTemp.insertVizinhos(ParadaID.PEQUIM1, mapa.get(ParadaID.PEQUIM3));
		paradaTemp.insertVizinhos(ParadaID.PEQUIM2, mapa.get(ParadaID.PEQUIM2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_YURT, provinciasKhan.get(ProvinciaKhanID.BL).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BR_MOEDA, provinciasKhan.get(ProvinciaKhanID.BR).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.TR_ESPADA, provinciasKhan.get(ProvinciaKhanID.TR).getEspada());
		
			//Pequim2
		paradaTemp = mapa.get(ParadaID.PEQUIM2);
		paradaTemp.setCidade(cidadeTemp);
						//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.CANTAO1, mapa.get(ParadaID.CANTAO1));
		paradaTemp.insertVizinhos(ParadaID.PEQUIM1, mapa.get(ParadaID.PEQUIM1));
		paradaTemp.insertVizinhos(ParadaID.PEQUIM3, mapa.get(ParadaID.PEQUIM3));
						//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BR_YURT, provinciasKhan.get(ProvinciaKhanID.BR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BR_MOEDA, provinciasKhan.get(ProvinciaKhanID.BR).getMoeda());
		
			//Pequim3
		paradaTemp = mapa.get(ParadaID.PEQUIM3);
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.CABUL1, mapa.get(ParadaID.CABUL1));
		paradaTemp.insertVizinhos(ParadaID.PEQUIM_CABUL_CANTAO, mapa.get(ParadaID.PEQUIM_CABUL_CANTAO));
		paradaTemp.insertVizinhos(ParadaID.PEQUIM1, mapa.get(ParadaID.PEQUIM1));
		paradaTemp.insertVizinhos(ParadaID.PEQUIM2, mapa.get(ParadaID.PEQUIM2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_YURT, provinciasKhan.get(ProvinciaKhanID.BL).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BR_YURT, provinciasKhan.get(ProvinciaKhanID.BR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BR_ESPADA, provinciasKhan.get(ProvinciaKhanID.BR).getEspada());
			
			//Cantao1
		paradaTemp = mapa.get(ParadaID.CANTAO1);
		cidadeTemp = cidades.get(CidadeID.CANTAO);
		paradaTemp.setCidade(cidadeTemp);
		cidadeTemp.setRegiao(Regiao.CHINA);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.PEQUIM2, mapa.get(ParadaID.PEQUIM2));
		paradaTemp.insertVizinhos(ParadaID.KAESONG_CANTAO, mapa.get(ParadaID.KAESONG_CANTAO));
		paradaTemp.insertVizinhos(ParadaID.CANTAO2, mapa.get(ParadaID.CANTAO2));
		paradaTemp.insertVizinhos(ParadaID.CANTAO3, mapa.get(ParadaID.CANTAO3));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BR_YURT, provinciasKhan.get(ProvinciaKhanID.BR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BR_MOEDA, provinciasKhan.get(ProvinciaKhanID.BR).getMoeda());
		
			//Cantao2
		paradaTemp = mapa.get(ParadaID.CANTAO2);
		paradaTemp.setCidade(cidadeTemp);
		paradaTemp.insertVizinhos(ParadaID.CANTAO1, mapa.get(ParadaID.CANTAO1));
		paradaTemp.insertVizinhos(ParadaID.CANTAO3, mapa.get(ParadaID.CANTAO3));
		
			//Cantao3
		paradaTemp = mapa.get(ParadaID.CANTAO3);
		paradaTemp.setCidade(cidadeTemp);
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.PEQUIM_CABUL_CANTAO, mapa.get(ParadaID.PEQUIM_CABUL_CANTAO));
		paradaTemp.insertVizinhos(ParadaID.CANTAO1, mapa.get(ParadaID.CANTAO1));
		paradaTemp.insertVizinhos(ParadaID.CANTAO2, mapa.get(ParadaID.CANTAO2));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BR_YURT, provinciasKhan.get(ProvinciaKhanID.BR).getYurt());
		return;
	}
	private void initMelhorias() {
		for(int i = 0; i<MELHORIAS_MOEDAS; i++) pilhaMelhorias.push(TipoMelhoria.MOEDA_VIRTUAL);
		for(int i = 0; i<MELHORIAS_ESPADAS; i++) pilhaMelhorias.push(TipoMelhoria.ESPADA_VIRTUAL);
		for(int i = 0; i<MELHORIAS_PEGAR_TRIBUTO; i++) pilhaMelhorias.push(TipoMelhoria.PEGAR_TRIBUTO_EXTRA);
		for(int i = 0; i<MELHORIAS_TESOURO; i++) pilhaMelhorias.push(TipoMelhoria.TESOURO_VIRTUAL);
		for(int i = 0; i<MELHORIAS_YURT; i++) pilhaMelhorias.push(TipoMelhoria.YURT_VIRTUAL);
		for(int i = 0; i<MELHORIAS_MOVIMENTO; i++) pilhaMelhorias.push(TipoMelhoria.MOVIMENTO_EXTRA);
		for(int i = 0; i<MELHORIAS; i++) { 
			melhorias.add(i, new SlotMelhoria(MELHORIAS_EXTRAS[i]));
			melhorias.get(i).setMelhoria(pilhaMelhorias.pop());
		}
		
		
	}
	private void initPeoes() {
		Parada karakorum = mapa.get(ParadaID.KARAKORUM);
		for(Peao p: peoes.keySet()) {
			p.setPosicao(karakorum);
			karakorum.insertPeao(p);
		}
		
	}
	public TabuleiroPrincipal(HashMap<Peao, Jogador> peoes) {
		this.peoes = peoes;
		initTesouros();
		initCidades();
		initProvincias();
		initParadas();
		initMelhorias();
		initPeoes();
	}
	
	public HashSet<Provincia> moverPeao(Peao peao, Parada parada){
		HashSet<Provincia> provincias = new HashSet<Provincia>();
		peao.getPosicao().removePeao(peao);
		peao.setPosicao(parada);
		parada.insertPeao(peao);
		for(Provincia p: parada.getProvincias().values()) provincias.add(p);
		return provincias;
	}
	public HashSet<Provincia> moverPeao(Peao peao, ParadaID id){
		HashSet<Provincia> provincias = new HashSet<Provincia>();
		Parada parada = mapa.get(id);
		peao.getPosicao().removePeao(peao);
		peao.setPosicao(parada);
		parada.insertPeao(peao);
		for(Provincia p: parada.getProvincias().values()) provincias.add(p);
		return provincias;
	}
	public HashMap<Parada, Integer> getMovimentosPossiveis(Peao peao, int movimentos){
		HashMap<Parada, Integer> ret = new HashMap<Parada, Integer>();
		Queue<Parada> fila = new LinkedList<Parada>();
		ret.put(peao.getPosicao(), 0);
		fila.add(peao.getPosicao());
		while (!fila.isEmpty()) {
			Parada atual = fila.poll();
			int i = ret.get(atual);
			int custo = (peao.getJogador() == atual.getYurt())? 0:1;
			if(i+custo>movimentos) continue;
			for(Parada p: atual.getVizinhos().values()) {
				int novoPasso = i + custo;
				
				if(!ret.containsKey(p)||novoPasso<ret.get(p)) {
					ret.put(p, novoPasso);
					fila.add(p);
				}
			}
			
		}
		HashSet<Parada> ocupados = new HashSet<Parada>();
		
		for(Parada p: ret.keySet()) {
			if(p.isMoveble()) continue;
			ocupados.add(p);
		}
		ret.keySet().removeAll(ocupados);
		return ret;
	}
	public HashMap<ParadaID, Parada> getMapa(){
		return this.mapa;
	}
	public HashMap<ProvinciaID, Provincia> getProvincias(){
		return this.provincias;
	}
	public ProvinciaKhanID getKhan() {
		return this.khan;
	}
	public void ativarProvinciaKhan(ProvinciaKhanID id, TipoTributo tributo1, TipoTributo tributo2) {
		this.khan = id;
		provinciasKhan.get(id).activate(tributo1, tributo2);
	}
	public void ativarMelhoriasKhan() {
		this.khan = null;
		
	}
}
