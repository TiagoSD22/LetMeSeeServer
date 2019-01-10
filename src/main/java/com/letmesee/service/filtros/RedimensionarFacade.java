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

public class RedimensionarFacade {
	
	private static RedimensionarFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private RedimensionarFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized RedimensionarFacade getInstancia() {
		if(instancia == null) {
			instancia = new RedimensionarFacade();
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
		int novaLargura = this.parametros.get("w").asInt();
		int novaAltura = this.parametros.get("h").asInt();
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			BufferedImage nova = new BufferedImage(novaLargura, novaAltura, imagem.getType());
			nova = Redimensionar(imagem, formato, novaLargura, novaAltura);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(nova,formato);
			imagem = null;
			nova = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				BufferedImage nf = new BufferedImage(novaLargura, novaAltura, f.getType());
				nf = Redimensionar(f, formato, novaLargura, novaAltura);
				framesProcessados.add(nf);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(novaLargura,novaAltura,formato,
				  				  img.getNome().concat("+Redimensionamento(")
								  .concat(String.valueOf(novaLargura)).concat("x").concat(String.valueOf(novaAltura)).concat(")"),
								  novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage Redimensionar(BufferedImage img, String formato, int novaLargura, int novaAltura){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat destination = new Mat(novaAltura,novaLargura,mat.type());
		Imgproc.resize(mat, destination, new Size(novaLargura,novaAltura),Imgproc.INTER_AREA);
		BufferedImage saida = filtrosUtils.Mat2BufferedImage(destination, formato);
        return saida;
	}

}
