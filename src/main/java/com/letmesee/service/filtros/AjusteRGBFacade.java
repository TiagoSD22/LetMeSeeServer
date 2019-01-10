package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class AjusteRGBFacade {
	
	private static AjusteRGBFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private AjusteRGBFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized AjusteRGBFacade getInstancia() {
		if(instancia == null) {
			instancia = new AjusteRGBFacade();
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
		int valorR = this.parametros.get("valorR").asInt();
		int valorG = this.parametros.get("valorG").asInt(); 
		int valorB = this.parametros.get("valorB").asInt();;
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = AjusteRGB(imagem, formato, valorR, valorG, valorB);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = AjusteRGB(f, formato, valorR, valorG, valorB);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome()
								  .concat("+RGB(R").concat(valorR >= 0 ? "+" : "").concat(String.valueOf(valorR))
					              .concat(",G").concat(valorG >= 0 ? "+" : "").concat(String.valueOf(valorG))
					              .concat(",B").concat(valorB >= 0 ? "+" : "").concat(String.valueOf(valorB))
					              .concat(")"),novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage AjusteRGB(BufferedImage img, String formato, int valorR, int valorG, int valorB) {
		int i,j,r,g,b;
		Pixel p;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				r = p.getR() + valorR;
				g = p.getG() + valorG;
				b = p.getB() + valorB;
				r = filtrosUtils.TruncarValor(r);
				g = filtrosUtils.TruncarValor(g);
				b = filtrosUtils.TruncarValor(b);
				p.setRGB(r, g, b);
				filtrosUtils.setPixel(img, i, j, p, formato);
			}
		}
		return img;
	}

}
