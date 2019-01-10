package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class ExtrairCanalFacade {
	private static ExtrairCanalFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private ExtrairCanalFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized ExtrairCanalFacade getInstancia() {
		if(instancia == null) {
			instancia = new ExtrairCanalFacade();
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
		boolean extrairR =  this.parametros.get("R").asBoolean();
		boolean extrairG = this.parametros.get("G").asBoolean();
		boolean extrairB = this.parametros.get("B").asBoolean();
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = ExtrairCanal(imagem, formato,extrairR, extrairG, extrairB);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = ExtrairCanal(f, formato, extrairR, extrairG, extrairB);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Extração_Canal")
								  .concat(extrairR || extrairG || extrairB ? "(" : "" )
								  .concat(extrairR? "R" : "")
								  .concat(extrairG? "G" : "")
								  .concat(extrairB? "B" : "").concat(")"),novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage ExtrairCanal(BufferedImage img, String formato, boolean extrair_r, boolean extrair_g, boolean extrair_b) {
		int i,j,r,g,b;
		Pixel p;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				r = 0;
				g = 0;
				b = 0;
				if(extrair_r)r = p.getR();
				if(extrair_g)g = p.getG();
				if(extrair_b)b = p.getB();
				p.setRGB(r, g, b);
				filtrosUtils.setPixel(img, i, j, p, formato);
			}
		}
		return img;
	}
}
