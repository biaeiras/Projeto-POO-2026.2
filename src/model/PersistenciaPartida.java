
package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;

class PersistenciaPartida {

    private PersistenciaPartida() {
    }

    static void salvarPartida(Partida partida, File arquivo) throws IOException {

        if (partida == null || arquivo == null) {
            throw new IllegalArgumentException(
                    "Partida ou arquivo inválido."
            );
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {

            TabuleiroPrincipal tabuleiro = partida.getTabuleiroPrincipal();

            escrever(writer, "VERSAO", "1");
            
            escrever(writer, "JOGADOR_ATUAL",partida.getIndiceJogadorAtual());
            
            escrever(writer, "LIMITE_VOTOS",partida.getLimiteVotos());
            
            escrever(writer, "VOTOS_FICHA_NEUTRA", partida.getVotosFichaNeutra());
            
            escrever(writer, "ENCERRADA",partida.isEncerrada());

            escrever(writer, "FIM_DISPARADO", partida.isFimDisparado());

            escrever(writer, "RODADAS_EXTRAS", partida.getRodadasExtras());

            escrever(writer, "TURNOS_RESTANTES",partida.getTurnosRestantesAposFim());

            escrever(writer, "PONTUACAO_FINAL_CALCULADA",partida.isPontuacaoFinalCalculada());
            
            ArrayList<Jogador> jogadores = partida.getJogadores();

            escrever(writer, "QUANTIDADE_JOGADORES",jogadores.size());
            
         // Conselheiros abertos e estado de cumprimento
            ArrayList<ConselheiroAberto> conselheiros = partida.getConselheirosAbertos();

            for (int i = 0; i < conselheiros.size(); i++) {
                ConselheiroAberto conselheiro = conselheiros.get(i);

                escrever(writer, "CONSELHEIRO_ABERTO_" + i,conselheiro.getTipo().name());

                Jogador cumpridoPor = conselheiro.getCumpridaPor();

                escrever(writer, "CONSELHEIRO_ABERTO_CUMPRIDO_POR_" + i,cumpridoPor == null ? "NENHUM" : cumpridoPor.getTabuleiroJogador() .getPersonagem().getCor().name());
            }

            // Dados dos jogadores
            for (int i = 0; i < jogadores.size(); i++) {

                Jogador jogador = jogadores.get(i);
                String prefixo = "JOGADOR_" + i + "_";

                Cor cor = jogador.getTabuleiroJogador()
                        .getPersonagem().getCor();

                escrever(writer, prefixo + "COR", cor.name());
                escrever(writer, prefixo + "YURTS",jogador.getYurtsDisponiveis());
                escrever(writer, prefixo + "VOTOS",jogador.getVotos());

                ConselheiroSecreto conselheiro = jogador.getConselheiroSecreto();

                escrever(writer, prefixo + "CONSELHEIRO",conselheiro == null? "NENHUM" : conselheiro.getTipo().name());

                escrever(writer, prefixo + "OPCOES_CONSELHEIRO",juntarConselheiros(jogador.getOpcoesConselheiro()));

                for (TipoTributo tipo : TipoTributo.values()) {
                    escrever(writer,prefixo + "TRIBUTO_" + tipo.name(),jogador.getQuantidadeTributo(tipo));
                }

                for (TipoTesouro tipo : TipoTesouro.values()) {
                    escrever(writer, prefixo + "TESOURO_" + tipo.name(),jogador.getQuantidadeTesouro(tipo));
                }

                // Posições dos peões
                escrever(writer, prefixo + "QUANTIDADE_PEOES",
                        jogador.getQuantidadePeoes());

                for (int p = 0; p < jogador.getQuantidadePeoes(); p++) {
                    Parada posicao = jogador.getPeao(p).getPosicao();

                    escrever(writer, prefixo + "PEAO_" + p, posicao == null ? "NENHUMA": posicao.getId().name());
                }

                // Melhorias do tabuleiro individual
                TabuleiroJogador tabuleiroJogador =jogador.getTabuleiroJogador();

                for (Regiao regiao : Regiao.values()) {
                    escrever(writer,prefixo + "MELHORIAS_" + regiao.name(),juntarMelhorias(tabuleiroJogador.exportarMelhoriasRegiao().get(regiao)));
                }

                escrever(writer, prefixo + "MELHORIAS_BRANCAS", juntarMelhorias( tabuleiroJogador.exportarMelhoriasBrancas()));
                
             // Estado das ativações das colunas
                escrever(writer, prefixo + "FICHAS_ATIVACAO",tabuleiroJogador.getFichasAtivacaoDisponiveis());

                for (Regiao regiao : Regiao.values()) {
                    escrever(writer,  prefixo + "COLUNA_ATIVADA_" + regiao.name(),tabuleiroJogador.colunaEstaAtivada(regiao));
                }

                escrever(writer, prefixo + "COLUNA_BRANCA_ATIVADA",tabuleiroJogador.colunaBrancaEstaAtivada());
                
             // Peças bônus do jogador
                ArrayList<TipoBonus> pecasBonus = jogador.getOrdemPecasBonus();

                escrever(writer, prefixo + "QUANTIDADE_PECAS_BONUS", pecasBonus.size());

                for (int b = 0; b < pecasBonus.size(); b++) {
                    TipoBonus tipo = pecasBonus.get(b);

                    escrever(writer, prefixo + "PECA_BONUS_" + b, tipo.name());

                    escrever(writer,prefixo + "PECA_BONUS_USADA_" + b,jogador.isPecaBonusUsada(tipo));
                }
            }

            // Estado das províncias
            for (Map.Entry<ProvinciaID, Integer> entrada :
                    tabuleiro.getCargasProvincias().entrySet()) {

                escrever(writer,"PROVINCIA_" + entrada.getKey().name(),entrada.getValue());
            }

            // Estado das cidades
            for (CidadeID id : CidadeID.values()) {

                Cidade cidade = tabuleiro.getCidade(id);

                escrever(writer,"CIDADE_" + id.name() + "_TESOUROS",juntarTesouros(cidade.getTesouros()));

                Jogador conquistador = cidade.getConquistador();

                escrever(writer,"CIDADE_" + id.name() + "_CONQUISTADOR",conquistador == null ? "NENHUM": conquistador.getTabuleiroJogador().getPersonagem().getCor().name());
            }

            // Pilhas restantes
            escrever(writer, "PILHA_CIDADES", juntarEnums(tabuleiro.getPilhaCidades()));

            escrever(writer, "PILHA_TESOUROS",juntarEnums(tabuleiro.getPilhaTesouros()));

            escrever(writer, "PILHA_MELHORIAS",juntarMelhorias(tabuleiro.getPilhaMelhorias()));

            // Melhorias disponíveis no tabuleiro principal
            escrever(writer, "MELHORIAS_TABULEIRO", juntarMelhorias(tabuleiro.getMelhorias()));

            // Khan
            escrever(writer, "KHAN",tabuleiro.getKhan() == null? "NENHUM" : tabuleiro.getKhan().name());

            // Yurts colocadas no mapa
            for (Map.Entry<ParadaID, ArrayList<Cor>> entrada :
                    tabuleiro.getYurts().entrySet()) {

                escrever(writer, "YURTS_" + entrada.getKey().name(),juntarEnums(entrada.getValue()));
            }

            // Bônus ainda presentes nas paradas
            for (Map.Entry<ParadaID, TipoBonus> entrada :
                    tabuleiro.getBonus().entrySet()) {

                escrever(writer, "BONUS_" + entrada.getKey().name(),entrada.getValue().name());
            }
        }
    }

    private static void escrever(BufferedWriter writer, String chave, Object valor)  throws IOException {

        writer.write(chave + "=" + valor);
        writer.newLine();
    }

    private static String juntarEnums(
            Iterable<? extends Enum<?>> valores) {

        StringBuilder resultado = new StringBuilder();

        for (Enum<?> valor : valores) {
            if (resultado.length() > 0) {
                resultado.append(",");
            }

            resultado.append(valor.name());
        }

        return resultado.toString();
    }

    private static String juntarTesouros(
            ArrayList<TipoTesouro> tesouros) {
        return juntarEnums(tesouros);
    }

    private static String juntarConselheiros(
            ArrayList<ConselheiroSecreto> conselheiros) {

        StringBuilder resultado = new StringBuilder();

        for (ConselheiroSecreto conselheiro : conselheiros) {
            if (resultado.length() > 0) {
                resultado.append(",");
            }

            resultado.append(conselheiro.getTipo().name());
        }

        return resultado.toString();
    }

    private static String juntarMelhorias(
            Iterable<Melhoria> melhorias) {

        StringBuilder resultado = new StringBuilder();

        for (Melhoria melhoria : melhorias) {

            if (resultado.length() > 0) {
                resultado.append(",");
            }

            if (melhoria == null) {
                resultado.append("VAZIO");
                continue;
            }

            resultado.append(melhoria.getTipo().name())
                    .append(":")
                    .append(melhoria.getRegiao() == null
                            ? "BRANCA"
                            : melhoria.getRegiao().name());
        }

        return resultado.toString();
    }
    
    private static HashMap<String, String> lerDados(File arquivo) throws IOException {

        if (arquivo == null || !arquivo.isFile()) {
            throw new IllegalArgumentException("Arquivo de partida inválido.");
        }

        HashMap<String, String> dados = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            int numeroLinha = 0;

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;

                if (linha.trim().isEmpty()) {
                    continue;
                }

                int separador = linha.indexOf('=');

                if (separador <= 0) {
                    throw new IOException(
                            "Linha inválida no arquivo: " + numeroLinha
                    );
                }

                String chave = linha.substring(0, separador).trim();
                String valor = linha.substring(separador + 1).trim();

                if (dados.containsKey(chave)) {
                    throw new IOException("Chave duplicada: " + chave);
                }

                dados.put(chave, valor);
            }
        }

        if (!"1".equals(dados.get("VERSAO"))) {
            throw new IOException("Versão do arquivo incompatível.");
        }

        return dados;
    }

    private static String obter(HashMap<String, String> dados, String chave) throws IOException {

        String valor = dados.get(chave);

        if (valor == null) {
            throw new IOException("Campo ausente no arquivo: " + chave);
        }

        return valor;
    }

    private static int obterInteiro( HashMap<String, String> dados, String chave) throws IOException {

        try {
            return Integer.parseInt(obter(dados, chave));
        } catch (NumberFormatException e) {
            throw new IOException("Valor inteiro inválido: " + chave, e);
        }
    }

    private static boolean obterBooleano(HashMap<String, String> dados, String chave) throws IOException {

        String valor = obter(dados, chave);

        if (!valor.equals("true") && !valor.equals("false")) {
            throw new IOException("Valor booleano inválido: " + chave);
        }

        return Boolean.parseBoolean(valor);
    }

    private static <T extends Enum<T>> T obterEnum(String valor, Class<T> tipo) throws IOException {

        try {
            return Enum.valueOf(tipo, valor);
        } catch (IllegalArgumentException e) {
            throw new IOException("Valor inválido para " + tipo.getSimpleName() + ": " + valor, e);
        }
    }
}
