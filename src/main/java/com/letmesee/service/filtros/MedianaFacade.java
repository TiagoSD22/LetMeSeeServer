package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;

public class MedianaFacade {
	
	private static MedianaFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private MedianaFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized MedianaFacade getInstancia() {
		if(instancia == null) {
			instancia = new MedianaFacade();
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
		int tamanhoMascara = this.parametros.get("k").asInt();
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = Mediana(imagem, formato, tamanhoMascara);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = Mediana(f, formato, tamanhoMascara);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,
				  				  img.getNome().concat("+Mediana(")
								  .concat(String.valueOf(tamanhoMascara)).concat(")"),
								  novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage Mediana(BufferedImage img, String formato, int tamanhoMascara){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
        Imgproc.medianBlur(mat, destination, tamanhoMascara);
		img = filtrosUtils.Mat2BufferedImage(destination, formato);
        return img;
	}
	
}
