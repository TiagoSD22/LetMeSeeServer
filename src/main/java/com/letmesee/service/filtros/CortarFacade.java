package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class CortarFacade {
	
	private static CortarFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private CortarFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized CortarFacade getInstancia() {
		if(instancia == null) {
			instancia = new CortarFacade();
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
		
		String tipoCorte = this.parametros.get("tipoCorte").asText();
		String modo = this.parametros.get("modo").asText();
		int x1 = this.parametros.get("x1").asInt();
		int y1 = this.parametros.get("y1").asInt();
		int larguraRegiao = this.parametros.get("w").asInt();
		int alturaRegiao = this.parametros.get("h").asInt();
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			if(tipoCorte.equals("retangular")) {
				imagem = CortarRetangular(imagem, formato, modo, x1, y1, larguraRegiao, alturaRegiao);
				novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,modo.equals("cortar")? formato : "png");
				imagem = null;
			}
			else if(tipoCorte.equals("circular")) {
				imagem = CortarCircular(imagem, modo, x1, y1, larguraRegiao, alturaRegiao);
				novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,"png");
				imagem = null;
			}
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				if(tipoCorte.equals("retangular")) {
					f = CortarRetangular(f, formato, modo, x1, y1, larguraRegiao, alturaRegiao);
				}
				else if(tipoCorte.equals("circular")) {
					f = CortarCircular(f, modo, x1, y1, larguraRegiao, alturaRegiao);
				}
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(modo.equals("cortar")? larguraRegiao : img.getLargura(), 
								  modo.equals("cortar")? alturaRegiao : img.getAltura(),
								  tipoCorte.equals("retangular")?
										  modo.equals("cortar")? 
												  formato 
												  : formato.equals("gif")? 
														  "gif" 
														  : "png"
										  : formato.equals("gif")?
												  "gif"
												  : "png",
				  				  img.getNome().concat("+Corte"),
								  novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage CortarRetangular(BufferedImage img, String formato, String modo, int x1, int y1, int larguraRegiao, int alturaRegiao) {
		if(modo.equals("cortar")) {
			Mat src = filtrosUtils.BufferedImage2Mat(img, formato);
			Rect rectCrop = new Rect(x1, y1 , larguraRegiao, alturaRegiao);
			Mat dst = src.submat(rectCrop);
			img = filtrosUtils.Mat2BufferedImage(dst, formato);
			return img;
		}
		else {
			int i,j;
			Pixel p;
			BufferedImage saida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
			for(i = 0; i < img.getHeight(); i++) {
				for(j = 0; j < img.getWidth(); j++) {
					p = filtrosUtils.getPixel(img, i, j, "png");
					if(i >= y1 && i < y1 + alturaRegiao && j >= x1 && j < x1 + larguraRegiao) {
						p.setRGBA(0, 0, 0, 0);
					}
					filtrosUtils.setPixel(saida, i, j, p, "png");
				}
			}
			return saida;
		}
	}
	
	public BufferedImage CortarCircular(BufferedImage img, String modo, int x1, int y1, int larguraRegiao, int alturaRegiao) {
		int h = (x1 + x1 + larguraRegiao) / 2;
		int k = (y1 + y1 + alturaRegiao) / 2;
		int maiorRaio = larguraRegiao > alturaRegiao? larguraRegiao / 2 : alturaRegiao / 2;
		int menorRaio = larguraRegiao < alturaRegiao? larguraRegiao / 2 : alturaRegiao / 2;
		int i,j;
		Pixel p;
		if(modo.equals("cortar")) {
			BufferedImage saida = new BufferedImage(larguraRegiao, alturaRegiao, BufferedImage.TYPE_INT_ARGB);
			for(i = y1; i < y1 + alturaRegiao; i++) {
				for (j = x1; j < x1 + larguraRegiao; j++) {
					p = filtrosUtils.getPixel(img, i, j, "png");
					if (checarRegiao(j, i, h, k, maiorRaio, menorRaio) > 1.0) {
						p.setRGBA(0, 0, 0, 0);
					}
					filtrosUtils.setPixel(saida, i - y1, j - x1, p, "png");
				}
			}
			return saida;
		}
		else {
			BufferedImage saida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
			for(i = 0; i < img.getHeight(); i++) {
				for(j = 0; j < img.getWidth(); j++) {
					p = filtrosUtils.getPixel(img, i, j, "png");
					if(i >= y1 && i < y1 + alturaRegiao && j >= x1 && j < x1 + larguraRegiao) {
						if(checarRegiao(j, i, h, k, maiorRaio, menorRaio) <= 1) {
							p.setRGBA(0, 0, 0, 0);
						}
					}
					filtrosUtils.setPixel(saida, i, j, p, "png");
				}
			}
			return saida;
		}
	}
	
	public double checarRegiao(int x, int y, int h, int k, int maiorRaio, int menorRaio) { 
	    double p = (Math.pow((x - h), 2) / Math.pow(maiorRaio, 2)) 
	            + (Math.pow((y - k), 2) / Math.pow(menorRaio, 2)); 
	  
	    return p; 
	} 

}
