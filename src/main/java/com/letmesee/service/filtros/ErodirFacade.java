package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;

public class ErodirFacade {
	
	private static ErodirFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private ErodirFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized ErodirFacade getInstancia() {
		if(instancia == null) {
			instancia = new ErodirFacade();
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
		
		int w = this.parametros.get("w").asInt();
		int h = this.parametros.get("h").asInt();
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = Erodir(imagem, formato, w, h);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = Erodir(f, formato, w, h);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,
				  				  img.getNome().concat("+Erosão(")
								  .concat(String.valueOf(w)).concat("x").concat(String.valueOf(h)).concat(")"),
								  novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage Erodir(BufferedImage img, String formato, int w, int h){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
		Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(w,h));
        Imgproc.erode(mat, destination, element1);
		img = filtrosUtils.Mat2BufferedImage(destination, formato);
        return img;
	}

}
