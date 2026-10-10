package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

class TabuleiroPrincipal {
	private final static int CIDADES_INICIAIS = 4;
	private class Cidade {
		private final static int MAX_TESOUROS = 4;
		private TipoTesouro[] tesouros = new TipoTesouro[4];
		private int contadorDeTesouros = 0; 
		private Jogador conquistador = null;
		
		protected void revelar(Stack<TipoTesouro> pilhaTesouros) {
			for(int i = 0; i<MAX_TESOUROS; ++i) {
				tesouros[i] = pilhaTesouros.pop();		
			}
			return;
		}
		
		public void atacar(Jogador jogador, TipoTesouro tesouro) {
			tesouro = null;
			this.contadorDeTesouros++;
			if(this.contadorDeTesouros >= MAX_TESOUROS) conquistador = jogador;
			return;
		}
		
		public TipoTesouro[] getTesouros() {
			return this.tesouros;
		}
		
		public int getContadorDeTesouros() {
			return this.contadorDeTesouros;
		}
		
		public Jogador getConquistador() {
			return this.conquistador;
		}
		
	}
	private class Parada {
		private HashMap<ParadaID, Parada> vizinhos = new HashMap<ParadaID, Parada>();
		private HashMap<ProvinciaID, Provincia> provincias = new HashMap<ProvinciaID, Provincia>();
		private byte yurt = -1;
		private int multiplicidade = 1;
		private Cidade cidade = null;
		private HashSet<Personagem> personagens = new HashSet<Personagem>();
		private TipoBonus bonus = null;
		
		public void insertVizinhos(ParadaID id, Parada parada) {
			this.vizinhos.put(id, parada);
			return;
		}
		public void insertProvincia(ProvinciaID nome, Provincia provincia) {
			this.provincias.put(nome, provincia);
		}
		public void setYurt(byte yurt) {
			this.yurt = yurt;
			return;
		}
		public void setMultiplicidade(int multiplicidade) {
			this.multiplicidade = multiplicidade;
			return;
		}
		public void setCidade(Cidade cidade) {
			this.cidade = cidade;
			return;
		}
		public void setBonus(TipoBonus bonus) {
			this.bonus = bonus;
			return;
		}
		public HashMap<ParadaID, Parada> getVizinhos(){
			return this.vizinhos;
		}
		public HashMap<ProvinciaID, Provincia> getProvincias(){
			return this.provincias;
		}
		public byte getYurt() {
			return this.yurt;
		}
		public int getMultiplicidade() {
			return this.multiplicidade;
		}
		public Cidade getCidade() {
			return this.cidade;
		}
		public boolean isMoveble() {
			return (personagens.size() < multiplicidade);
		}
		public TipoBonus getBonus() {
			return this.bonus;
		}
		
	}
	private class Provincia {
		private TipoTributo tributo;
		private int cargas = 1;
		public Provincia(TipoTributo tributo) {
			this.tributo = tributo;
		}
		public TipoTributo getTipoTributo() {
			return this.tributo;
		}
		public int carregar() {
			this.cargas++;
			return this.cargas;
		}
		public int descarregar(int c) {
			this.cargas -= c;
			return this.cargas;
		}
	}
	private class ProvinciaKhan{

		private Provincia espada = new Provincia(TipoTributo.ESPADA),
				moeda = new Provincia(TipoTributo.MOEDA),
				yurt = new Provincia(TipoTributo.YURT);
		public Provincia getEspada() {
			return this.espada;
		}
		public Provincia getMoeda() {
			return this.moeda;
		}
		public Provincia getYurt() {
			return this.yurt;
		}
		
	}
	
	private HashMap<ParadaID, Parada> mapa = new HashMap<ParadaID, Parada>();
	private HashMap<CidadeID, Cidade> cidades = new HashMap<CidadeID, Cidade>();
	private HashMap<ProvinciaKhanID, ProvinciaKhan> provinciasKhan = new HashMap<ProvinciaKhanID, ProvinciaKhan>();
	private Stack<CidadeID> pilhaCidades = new Stack<CidadeID>();
	private Stack<TipoTesouro> pilhaTesouros = new Stack<TipoTesouro>();
	
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
		for(TipoTesouro id : TipoTesouro.values()) pilhaTesouros.push(id);
		Collections.shuffle(pilhaTesouros);
		return;
	}
	private void initProvincias() {
		for(ProvinciaKhanID id : ProvinciaKhanID.values()) provinciasKhan.put(id, new ProvinciaKhan());
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
		paradaTemp.setBonus(pilhaBonus.pop());
		paradaTemp.setCidade(cidades.get(CidadeID.KARAKORUM));
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
		paradaTemp.setCidade(cidades.get(CidadeID.KIEV));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.MOSCOU3, mapa.get(ParadaID.MOSCOU3));
		paradaTemp.insertVizinhos(ParadaID.KIEV_MOSCOU, mapa.get(ParadaID.KIEV_MOSCOU));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TL_ESPADA, provinciasKhan.get(ProvinciaKhanID.TL).getEspada());
		paradaTemp.insertProvincia(ProvinciaID.TL_YURT, provinciasKhan.get(ProvinciaKhanID.TL).getYurt());
		
			//Kiev2
		paradaTemp = mapa.get(ParadaID.KIEV2);
		paradaTemp.setCidade(cidades.get(CidadeID.KIEV));
				//Paradas Vizinhas
		
		paradaTemp.insertVizinhos(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA, mapa.get(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TL_YURT, provinciasKhan.get(ProvinciaKhanID.TL).getYurt());
		
			//Kiev3
		paradaTemp = mapa.get(ParadaID.KIEV3);
		paradaTemp.setCidade(cidades.get(CidadeID.KIEV));
		
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
		paradaTemp.setCidade(cidades.get(CidadeID.MOSCOU));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KIEV_MOSCOU, mapa.get(ParadaID.KIEV_MOSCOU));
		paradaTemp.insertVizinhos(ParadaID.SARAI3, mapa.get(ParadaID.SARAI3));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TL_ESPADA, provinciasKhan.get(ProvinciaKhanID.TL).getEspada());
		paradaTemp.insertProvincia(ProvinciaID.TR_YURT, provinciasKhan.get(ProvinciaKhanID.TR).getYurt());
		
			//Moscou2
		paradaTemp = mapa.get(ParadaID.MOSCOU2);
		paradaTemp.setCidade(cidades.get(CidadeID.MOSCOU));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA1, mapa.get(ParadaID.SAMARCANDA1));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TL_MOEDA, provinciasKhan.get(ProvinciaKhanID.TL).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.TR_YURT, provinciasKhan.get(ProvinciaKhanID.TR).getYurt());
		
			//Moscou3
		paradaTemp = mapa.get(ParadaID.MOSCOU3);
		paradaTemp.setCidade(cidades.get(CidadeID.MOSCOU));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KIEV1, mapa.get(ParadaID.KIEV1));
		paradaTemp.insertVizinhos(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA, mapa.get(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TL_YURT, provinciasKhan.get(ProvinciaKhanID.TL).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.TL_MOEDA, provinciasKhan.get(ProvinciaKhanID.TL).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.TL_ESPADA, provinciasKhan.get(ProvinciaKhanID.TL).getEspada());
		
			//Sarai1
		paradaTemp = mapa.get(ParadaID.SARAI1);
		paradaTemp.setCidade(cidades.get(CidadeID.SARAI));
		
			//Sarai2
		paradaTemp = mapa.get(ParadaID.SARAI2);
		paradaTemp.setCidade(cidades.get(CidadeID.SARAI));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.SARAI_KAESONG_KARAKORUM, mapa.get(ParadaID.SARAI_KAESONG_KARAKORUM));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TR_MOEDA, provinciasKhan.get(ProvinciaKhanID.TR).getMoeda());
		
			//Sarai3
		paradaTemp = mapa.get(ParadaID.SARAI3);
		paradaTemp.setCidade(cidades.get(CidadeID.SARAI));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.MOSCOU1, mapa.get(ParadaID.MOSCOU1));
		paradaTemp.insertVizinhos(ParadaID.KARAKORUM, mapa.get(ParadaID.KARAKORUM));
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
		paradaTemp.setCidade(cidades.get(CidadeID.KAESONG));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.SARAI_KAESONG_KARAKORUM, mapa.get(ParadaID.SARAI_KAESONG_KARAKORUM));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TR_ESPADA, provinciasKhan.get(ProvinciaKhanID.TR).getEspada());
		
			//Kaesong2
		paradaTemp = mapa.get(ParadaID.KAESONG2);
		paradaTemp.setCidade(cidades.get(CidadeID.KAESONG));
		
			//Kaesong3
		paradaTemp = mapa.get(ParadaID.KAESONG3);
		paradaTemp.setCidade(cidades.get(CidadeID.KAESONG));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.PEQUIM1, mapa.get(ParadaID.PEQUIM1));
		paradaTemp.insertVizinhos(ParadaID.KAESONG_CANTAO, mapa.get(ParadaID.KAESONG_CANTAO));
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
		paradaTemp.setCidade(cidades.get(CidadeID.SAMARCANDA));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.MOSCOU2, mapa.get(ParadaID.MOSCOU2));
		paradaTemp.insertVizinhos(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA, mapa.get(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TR_YURT, provinciasKhan.get(ProvinciaKhanID.TR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BL_ESPADA, provinciasKhan.get(ProvinciaKhanID.BL).getEspada());
		paradaTemp.insertProvincia(ProvinciaID.TL_MOEDA, provinciasKhan.get(ProvinciaKhanID.TL).getMoeda());
		
			//Samarcanda2
		paradaTemp = mapa.get(ParadaID.SAMARCANDA2);
		paradaTemp.setCidade(cidades.get(CidadeID.SAMARCANDA));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KARAKORUM, mapa.get(ParadaID.KARAKORUM));
		paradaTemp.insertVizinhos(ParadaID.CABUL1, mapa.get(ParadaID.CABUL1));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.TR_YURT, provinciasKhan.get(ProvinciaKhanID.TR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BL_YURT, provinciasKhan.get(ProvinciaKhanID.BL).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BL_MOEDA, provinciasKhan.get(ProvinciaKhanID.BL).getMoeda());
		
			//Samarcanda3
		paradaTemp = mapa.get(ParadaID.SAMARCANDA3);
		paradaTemp.setCidade(cidades.get(CidadeID.SAMARCANDA));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.BAGDA1, mapa.get(ParadaID.BAGDA1));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_MOEDA, provinciasKhan.get(ProvinciaKhanID.BL).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.BL_ESPADA, provinciasKhan.get(ProvinciaKhanID.BL).getEspada());
		
			//Bagda1
		paradaTemp = mapa.get(ParadaID.BAGDA1);
		paradaTemp.setCidade(cidades.get(CidadeID.BAGDA));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA, mapa.get(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA));
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA3, mapa.get(ParadaID.SAMARCANDA3));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_MOEDA, provinciasKhan.get(ProvinciaKhanID.BL).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.BL_ESPADA, provinciasKhan.get(ProvinciaKhanID.BL).getEspada());
		
			//Bagda2
		paradaTemp = mapa.get(ParadaID.BAGDA2);
		paradaTemp.setCidade(cidades.get(CidadeID.BAGDA));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.BAGDA_CABUL, mapa.get(ParadaID.BAGDA_CABUL));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_MOEDA, provinciasKhan.get(ProvinciaKhanID.BL).getMoeda());
		
			//Bagda3
		paradaTemp = mapa.get(ParadaID.BAGDA3);
		paradaTemp.setCidade(cidades.get(CidadeID.BAGDA));
		
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
		paradaTemp.setCidade(cidades.get(CidadeID.CABUL));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.SAMARCANDA2, mapa.get(ParadaID.SAMARCANDA2));
		paradaTemp.insertVizinhos(ParadaID.PEQUIM3, mapa.get(ParadaID.PEQUIM3));
		paradaTemp.insertVizinhos(ParadaID.BAGDA_CABUL, mapa.get(ParadaID.BAGDA_CABUL));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_YURT, provinciasKhan.get(ProvinciaKhanID.BL).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BL_MOEDA, provinciasKhan.get(ProvinciaKhanID.BL).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.BR_ESPADA, provinciasKhan.get(ProvinciaKhanID.BR).getEspada());
		
			//Cabul2
		paradaTemp = mapa.get(ParadaID.CABUL2);
		paradaTemp.setCidade(cidades.get(CidadeID.CABUL));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.PEQUIM_CABUL_CANTAO, mapa.get(ParadaID.PEQUIM_CABUL_CANTAO));
				//Provincias

		paradaTemp.insertProvincia(ProvinciaID.BR_ESPADA, provinciasKhan.get(ProvinciaKhanID.BR).getEspada());
		
			//Cabul3
		paradaTemp = mapa.get(ParadaID.CABUL3);
		paradaTemp.setCidade(cidades.get(CidadeID.CABUL));
		
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
		paradaTemp.setCidade(cidades.get(CidadeID.PEQUIM));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.KARAKORUM, mapa.get(ParadaID.KARAKORUM));
		paradaTemp.insertVizinhos(ParadaID.KAESONG3, mapa.get(ParadaID.KAESONG3));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_YURT, provinciasKhan.get(ProvinciaKhanID.BL).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BR_MOEDA, provinciasKhan.get(ProvinciaKhanID.BR).getMoeda());
		paradaTemp.insertProvincia(ProvinciaID.TR_ESPADA, provinciasKhan.get(ProvinciaKhanID.TR).getEspada());
		
			//Pequim2
		paradaTemp = mapa.get(ParadaID.PEQUIM2);
		paradaTemp.setCidade(cidades.get(CidadeID.PEQUIM));
						//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.CANTAO1, mapa.get(ParadaID.CANTAO1));
						//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BR_YURT, provinciasKhan.get(ProvinciaKhanID.BR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BR_MOEDA, provinciasKhan.get(ProvinciaKhanID.BR).getMoeda());
		
			//Pequim3
		paradaTemp = mapa.get(ParadaID.PEQUIM3);
		paradaTemp.setCidade(cidades.get(CidadeID.PEQUIM));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.CABUL1, mapa.get(ParadaID.CABUL1));
		paradaTemp.insertVizinhos(ParadaID.PEQUIM_CABUL_CANTAO, mapa.get(ParadaID.PEQUIM_CABUL_CANTAO));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BL_YURT, provinciasKhan.get(ProvinciaKhanID.BL).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BR_YURT, provinciasKhan.get(ProvinciaKhanID.BR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BR_ESPADA, provinciasKhan.get(ProvinciaKhanID.BR).getEspada());
			
			//Cantao1
		paradaTemp = mapa.get(ParadaID.CANTAO1);
		paradaTemp.setCidade(cidades.get(CidadeID.CANTAO));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.PEQUIM2, mapa.get(ParadaID.PEQUIM2));
		paradaTemp.insertVizinhos(ParadaID.KAESONG_CANTAO, mapa.get(ParadaID.KAESONG_CANTAO));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BR_YURT, provinciasKhan.get(ProvinciaKhanID.BR).getYurt());
		paradaTemp.insertProvincia(ProvinciaID.BR_MOEDA, provinciasKhan.get(ProvinciaKhanID.BR).getMoeda());
		
			//Cantao2
		paradaTemp = mapa.get(ParadaID.CANTAO2);
		paradaTemp.setCidade(cidades.get(CidadeID.CANTAO));
		
			//Cantao3
		paradaTemp = mapa.get(ParadaID.CANTAO3);
		paradaTemp.setCidade(cidades.get(CidadeID.CANTAO));
				//Paradas Vizinhas
		paradaTemp.insertVizinhos(ParadaID.PEQUIM_CABUL_CANTAO, mapa.get(ParadaID.PEQUIM_CABUL_CANTAO));
				//Provincias
		paradaTemp.insertProvincia(ProvinciaID.BR_YURT, provinciasKhan.get(ProvinciaKhanID.BR).getYurt());
		return;
	}
	
	
	public TabuleiroPrincipal() {
		initTesouros();
		initCidades();
		initProvincias();
		initParadas();
	}
	
}
