package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class EqualizarCanalFacade {
	
	private static EqualizarCanalFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private EqualizarCanalFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized EqualizarCanalFacade getInstancia() {
		if(instancia == null) {
			instancia = new EqualizarCanalFacade();
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
		boolean equalizarR = this.parametros.get("equalizarR").asBoolean();
		boolean equalizarG = this.parametros.get("equalizarG").asBoolean();
		boolean equalizarB = this.parametros.get("equalizarB").asBoolean();
		int minR = this.parametros.get("minR").asInt();
		int maxR = this.parametros.get("maxR").asInt();
		int minG = this.parametros.get("minG").asInt();
		int maxG = this.parametros.get("maxG").asInt(); 
		int minB = this.parametros.get("minB").asInt();
		int maxB = this.parametros.get("maxB").asInt();
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = EqualizarCanal(imagem, formato, equalizarR, equalizarG, equalizarB, minR, maxR, minG, maxG, minB, maxB);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = EqualizarCanal(f, formato, equalizarR, equalizarG, equalizarB, minR, maxR, minG, maxG, minB, maxB);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Equalização_Canal")
								 .concat("(")
								 .concat(equalizarR ? "R" : "")
								 .concat(minR != 0 || maxR != 255 ? String.valueOf("[" + minR + ":" + maxR + "]") : "")
								 .concat(equalizarG ? "G" : "")
								 .concat(minG != 0 || maxG != 255 ? String.valueOf("[" + minG + ":" + maxG + "]") : "")
								 .concat(equalizarB ? "B" : "")
								 .concat(minB != 0 || maxB != 255 ? String.valueOf("[" + minB + ":" + maxB + "]") : "")
								 .concat(")")
								 ,novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage EqualizarCanal(BufferedImage img, String formato, boolean equalizarR, boolean equalizarG, boolean equalizarB, int minR, int maxR, int minG, int maxG, int minB, int maxB) {
		int i,j,r,g,b;
		Pixel p;
		BufferedImage saida = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		int [] niveisR = new int[256];
		double [] probabilidadesR = new double[256];
		double acumuladorR;
		int [] mapeadorR = new int[256];
		int [] niveisG = new int[256];
		double [] probabilidadesG = new double[256];
		double acumuladorG;
		int [] mapeadorG = new int[256];
		int [] niveisB = new int[256];
		double [] probabilidadesB = new double[256];
		double acumuladorB;
		int [] mapeadorB = new int[256];
		int pixelsValidos = 0;
		
		for(i = 0; i < 256; i++) {
			niveisR[i] = 0;
			niveisG[i] = 0;
			niveisB[i] = 0;
		}
		
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				if(formato.equals(filtrosUtils.FORMATO_PNG)) {
					if(p.getAlpha() != 0) {
						pixelsValidos++;
						niveisR[p.getR()]++;
						niveisG[p.getG()]++;
						niveisB[p.getB()]++;
					}
				}
				else {
					niveisR[p.getR()]++;
					niveisG[p.getG()]++;
					niveisB[p.getB()]++;
				}
			}
		}
		
		acumuladorR = 0;
		acumuladorG = 0;
		acumuladorB = 0;
		for(i = 0; i < 256; i++) {
			if(formato.equals(filtrosUtils.FORMATO_PNG)) {
				probabilidadesR[i] = ((double) niveisR[i] / (double) pixelsValidos);
				probabilidadesG[i] = ((double) niveisG[i] / (double) pixelsValidos);
				probabilidadesB[i] = ((double) niveisB[i] / (double) pixelsValidos);
			}
			else {
				probabilidadesR[i] = ((double) niveisR[i] / (img.getHeight() * img.getWidth()));
				probabilidadesG[i] = ((double) niveisG[i] / (img.getHeight() * img.getWidth()));
				probabilidadesB[i] = ((double) niveisB[i] / (img.getHeight() * img.getWidth()));
			}
			acumuladorR += probabilidadesR[i];
			acumuladorG += probabilidadesG[i];
			acumuladorB += probabilidadesB[i];
			mapeadorR[i] = (int) (255 * acumuladorR);
			mapeadorG[i] = (int) (255 * acumuladorG);
			mapeadorB[i] = (int) (255 * acumuladorB);
		}
		
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				if(equalizarR) {
					r = p.getR();
					if(r >= minR && r <= maxR) {
						p.setR(mapeadorR[r]);
					}
				}
				if(equalizarG) {
					g = p.getG();
					if(g >= minG && g <= maxG) {
						p.setG(mapeadorG[g]);
					}
				}
				if(equalizarB) {
					b = p.getB();
					if(b >= minB && b <= maxB) {
						p.setB(mapeadorB[b]);
					}
				}
				filtrosUtils.setPixel(saida, i, j, p, formato);
			}
		}
		
		return saida;
	}

}
