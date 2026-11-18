package view;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import model.Melhoria;
import model.Regiao;
import model.TipoMelhoria;

class ImagensMelhorias {

	private static final Map<Melhoria, String> enderecos = initEnderecos();

	private static Map<Melhoria, String> initEnderecos(){
	    Map<Melhoria, String> temp = new HashMap<>();
	    temp.put(new Melhoria(TipoMelhoria.MOEDA_VIRTUAL, Regiao.CHINA), "moeda_virtual_china.jpg");
	    temp.put(new Melhoria(TipoMelhoria.ESPADA_VIRTUAL, Regiao.CHINA), "espada_virtual_china.jpg");
	    temp.put(new Melhoria(TipoMelhoria.YURT_VIRTUAL, Regiao.CHINA), "yurt_virtual_china.jpg");
	    temp.put(new Melhoria(TipoMelhoria.PEGAR_TRIBUTO_EXTRA, Regiao.CHINA), "pegar_tributo_extra_china.jpg");
	    temp.put(new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, Regiao.CHINA), "movimento_extra_china.jpg");
	    temp.put(new Melhoria(TipoMelhoria.FERRO_VIRTUAL, Regiao.CHINA), "ferro_virtual_china.jpg");
	    
	    temp.put(new Melhoria(TipoMelhoria.MOEDA_VIRTUAL, Regiao.RUSSIA), "moeda_virtual_russia.jpg");
	    temp.put(new Melhoria(TipoMelhoria.ESPADA_VIRTUAL, Regiao.RUSSIA), "espada_virtual_russia.jpg");
	    temp.put(new Melhoria(TipoMelhoria.YURT_VIRTUAL, Regiao.RUSSIA), "yurt_virtual_russia.jpg");
	    temp.put(new Melhoria(TipoMelhoria.PEGAR_TRIBUTO_EXTRA, Regiao.RUSSIA), "pegar_tributo_extra_russia.jpg");
	    temp.put(new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, Regiao.RUSSIA), "movimento_extra_russia.jpg");
	    temp.put(new Melhoria(TipoMelhoria.BOI_VIRTUAL, Regiao.RUSSIA), "boi_virtual_russia.jpg");
	    temp.put(new Melhoria(TipoMelhoria.ALIMENTO_VIRTUAL, Regiao.RUSSIA), "alimento_virtual_russia.jpg");
	    
	    temp.put(new Melhoria(TipoMelhoria.MOEDA_VIRTUAL, Regiao.PERSIA), "moeda_virtual_persia.jpg");
	    temp.put(new Melhoria(TipoMelhoria.ESPADA_VIRTUAL, Regiao.PERSIA), "espada_virtual_persia.jpg");
	    temp.put(new Melhoria(TipoMelhoria.YURT_VIRTUAL, Regiao.PERSIA), "yurt_virtual_persia.jpg");
	    temp.put(new Melhoria(TipoMelhoria.PEGAR_TRIBUTO_EXTRA, Regiao.PERSIA), "pegar_tributo_extra_persia.jpg");
	    temp.put(new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, Regiao.PERSIA), "movimento_extra_persia.jpg");
	    temp.put(new Melhoria(TipoMelhoria.PELE_VIRTUAL, Regiao.PERSIA), "pele_virtual_china");
	    temp.put(new Melhoria(TipoMelhoria.TECIDO_VIRTUAL, Regiao.PERSIA), "tecido_virtual_persia.jpg");
	    
	    return Collections.unmodifiableMap(temp);
	}
	
	static HashMap<String, BufferedImage> imagens = new HashMap<>();
	
	static BufferedImage loadImage(String nomeArquivo) {
		if (imagens.containsKey(nomeArquivo)) {
            return imagens.get(nomeArquivo);
        }

        String caminho = "/melhorias/" + nomeArquivo;

        java.net.URL recurso = ImagensMelhorias.class.getResource(caminho);

        if (recurso == null) {
            throw new IllegalArgumentException(
                    "Imagem não encontrada: " + caminho
            );
        }

        try {
            BufferedImage imagem = ImageIO.read(recurso);

            imagens.put(nomeArquivo, imagem);

            return imagem;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao carregar melhoria: " + caminho, e
            );
        }
	}
	
	static BufferedImage loadImage(Melhoria melhoria) {
		String endereco = enderecos.get(melhoria);
		System.out.println(endereco);
		return loadImage(endereco);
	}
	
}
