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

public class GaussianBlurFacade {
	
	private static GaussianBlurFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private GaussianBlurFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized GaussianBlurFacade getInstancia() {
		if(instancia == null) {
			instancia = new GaussianBlurFacade();
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
		double sigmaX = this.parametros.get("x").asDouble(); 
		double sigmaY = this.parametros.get("y").asDouble();
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = GaussianBlur(imagem, formato, w, h, sigmaX, sigmaY);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = GaussianBlur(f, formato, w, h, sigmaX, sigmaY);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,
				  				  img.getNome().concat("+Gaussian_Blur(")
								  .concat(String.valueOf(w)).concat("x").concat(String.valueOf(h))
								  .concat(",sx: ").concat(String.valueOf(sigmaX))
								  .concat(", sy: ").concat(String.valueOf(sigmaY)).concat(")"),
								  novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage GaussianBlur(BufferedImage img, String formato, int w, int h, double sigmaX, double sigmaY){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
        Imgproc.GaussianBlur(mat, destination, new Size(w,h), sigmaX, sigmaY);
		img = filtrosUtils.Mat2BufferedImage(destination, formato);
        return img;
	}

}
