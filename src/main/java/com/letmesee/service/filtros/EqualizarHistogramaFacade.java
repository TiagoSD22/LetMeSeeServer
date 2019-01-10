package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class EqualizarHistogramaFacade {
	
	private static EqualizarHistogramaFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private EqualizarHistogramaFacade() {}
	
	public static synchronized EqualizarHistogramaFacade getInstancia() {
		if(instancia == null) {
			instancia = new EqualizarHistogramaFacade();
			filtrosUtils = FiltrosUtils.getInstancia();
		}
		return instancia;
	}
	
	public Imagem Processar(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = EqualizarHistograma(imagem, formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = EqualizarHistograma(f, formato);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Equalização_Histograma"),novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage EqualizarHistograma(BufferedImage img, String formato){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		if(mat.type() == 16) {
			Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
	        Imgproc.cvtColor(mat, destination,Imgproc.COLOR_BGR2YCrCb);
	        List<Mat> canais = new ArrayList<Mat>();
	        Core.split(destination, canais);
	        Imgproc.equalizeHist(canais.get(0), canais.get(0));
	        Core.merge(canais, destination);
	        Imgproc.cvtColor(destination, destination, Imgproc.COLOR_YCrCb2BGR);
			img = filtrosUtils.Mat2BufferedImage(destination, formato);
		}
		else if(mat.type() == 24) {
			Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
	        Imgproc.cvtColor(mat, destination,Imgproc.COLOR_BGR2YCrCb);
	        List<Mat> canais = new ArrayList<Mat>();
	        Core.split(destination, canais);
	        Imgproc.equalizeHist(canais.get(0), canais.get(0));
	        Core.merge(canais, destination);
	        Imgproc.cvtColor(destination, destination, Imgproc.COLOR_YCrCb2BGR);
			BufferedImage imgRGB = filtrosUtils.Mat2BufferedImage(destination, formato);
			Pixel pRGB;
			Pixel pRGBA;
			for(int i = 0; i < img.getHeight(); i++) {
				for(int j = 0; j < img.getWidth(); j++) {
					pRGB = filtrosUtils.getPixel(imgRGB, i, j, "jpg");
					pRGBA = filtrosUtils.getPixel(img, i, j, formato);
					pRGBA.setRGBA(pRGB.getR(), pRGB.getG(), pRGB.getB(), pRGBA.getAlpha());
					filtrosUtils.setPixel(img, i, j, pRGBA, formato);
				}
			}
		}
		else {
			Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
			Imgproc.equalizeHist(mat, destination);
			img = filtrosUtils.Mat2BufferedImage(destination, formato);
		}
        return img;
	}

}
