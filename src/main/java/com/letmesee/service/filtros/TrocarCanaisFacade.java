package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class TrocarCanaisFacade {
	
	private static TrocarCanaisFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private TrocarCanaisFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized TrocarCanaisFacade getInstancia() {
		if(instancia == null) {
			instancia = new TrocarCanaisFacade();
			filtrosUtils = FiltrosUtils.getInstancia();
			mapper = new ObjectMapper();
		}
		return instancia;
	}
	
	public Imagem Processar(Imagem img, String parametros) {
		try {
			this.parametros = mapper.readTree(parametros);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String canais = this.parametros.get("canais").asText();
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = TrocarCanais(imagem, formato, canais);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = TrocarCanais(f, formato, canais);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,
				  				  img.getNome().concat("+Troca_de_canais(")
				  				  .concat(canais.equals("RG") ? "R<->G" : "")
				  				  .concat(canais.equals("RB") ? "R<->B" : "")
				  				  .concat(canais.equals("GB") ? "G<->B" : "")
				  				  .concat(")"),
								  novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage TrocarCanais(BufferedImage img, String formato, String canais) {
		int i,j,r,g,b;
		Pixel p;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				r = p.getR();
				g = p.getG();
				b = p.getB();
				if(canais.equals("RG")) p.setRGB(g, r, b);
				else if(canais.equals("RB")) p.setRGB(b, g, r);
				else p.setRGB(r, b, g);
				filtrosUtils.setPixel(img, i, j, p, formato);
			}
		}
		return img;
	}

}
