package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class BrilhoFacade {
	
	private static BrilhoFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private BrilhoFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized BrilhoFacade getInstancia() {
		if(instancia == null) {
			instancia = new BrilhoFacade();
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
		int gain = this.parametros.get("gain").asInt();
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = Brilho(imagem, formato, gain);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = Brilho(f, formato, gain);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,
				  				  img.getNome().concat("+Brilho(")
				  				  .concat(gain > 0? "+" : "")
				  				  .concat(String.valueOf(Math.round(((double)gain / 255) * 100))).concat("%)"),
								  novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage Brilho(BufferedImage img, String formato, int alpha) {
		int i,j,r,g,b;
		Pixel p;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				r = filtrosUtils.TruncarValor(p.getR() + alpha);
				g = filtrosUtils.TruncarValor(p.getG() + alpha);
				b = filtrosUtils.TruncarValor(p.getB() + alpha);
				p.setRGB(r, g, b);
				filtrosUtils.setPixel(img, i, j, p, formato);
			}
		}
		return img;
	}

}
