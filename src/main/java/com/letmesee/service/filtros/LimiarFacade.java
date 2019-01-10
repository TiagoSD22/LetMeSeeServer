package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class LimiarFacade {
	
	private static LimiarFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private LimiarFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized LimiarFacade getInstancia() {
		if(instancia == null) {
			instancia = new LimiarFacade();
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
		int valorLimiar = this.parametros.get("valor").asInt();
		boolean usarMedia = this.parametros.get("usarMedia").asBoolean();
		String referencia = this.parametros.get("referencia").asText();
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			if(usarMedia) {
				valorLimiar = filtrosUtils.encontrarIntensidadeMedia(imagem, formato, referencia);
			}
			imagem = Limiar(imagem, formato, valorLimiar);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				if(usarMedia) {
					valorLimiar = filtrosUtils.encontrarIntensidadeMedia(f, formato, referencia);
				}
				f = Limiar(f, formato, valorLimiar);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		String nomeSaida = img.getNome().concat("+Limiar(").concat(String.valueOf(valorLimiar))
										.concat(usarMedia ? String.valueOf(" - Média{" + referencia + "}") : "").concat(")");
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,nomeSaida,novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage Limiar(BufferedImage img, String formato, int valorLimiar) {
		int i,j,r,g,b,novaCor;
		Pixel p;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				r = (int) (p.getR() * 0.299);
				g = (int) (p.getG() * 0.587);
				b = (int) (p.getB() *0.114);
				novaCor = r + g + b;
				if(novaCor > valorLimiar) {
					novaCor = 255;
				}
				else {
					novaCor = 0;
				}
				p.setRGB(novaCor,novaCor,novaCor);
				filtrosUtils.setPixel(img, i, j, p, formato);
			}
		}
		return img;
	}
}
