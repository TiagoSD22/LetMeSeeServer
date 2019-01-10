package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;

public class NitidezFacade {
	
	private static NitidezFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private NitidezFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized NitidezFacade getInstancia() {
		if(instancia == null) {
			instancia = new NitidezFacade();
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
		double fator = this.parametros.get("fator").asDouble() ;
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = Nitidez(imagem, formato, fator);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = Nitidez(f, formato,fator);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,
								  img.getNome().concat("+Nitidez(").concat(String.valueOf((fator / 5.0 ) * 100))
								  .concat(")"),
							      novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage Nitidez(BufferedImage img, String formato, double fator) {
		Mat src = filtrosUtils.BufferedImage2Mat(img, formato);
		int k = (int)(2 * fator) + 3;
		if(k % 2 != 1) {
			k = k + 1;
		}
		img = GaussianBlurFacade.getInstancia().GaussianBlur(img, formato, k, k, 0, 0);
		Mat srcBlur = filtrosUtils.BufferedImage2Mat(img, formato);
		Core.subtract(src, srcBlur, srcBlur);
		Core.addWeighted(src, 1, srcBlur, fator, 0, src);
		img = filtrosUtils.Mat2BufferedImage(src, formato);
		return img;
	}

}
