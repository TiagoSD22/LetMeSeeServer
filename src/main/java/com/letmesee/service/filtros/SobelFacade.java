package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;

public class SobelFacade {
	
	private static SobelFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private SobelFacade() {}
	
	public static synchronized SobelFacade getInstancia() {
		if(instancia == null) {
			instancia = new SobelFacade();
			filtrosUtils = FiltrosUtils.getInstancia();
		}
		return instancia;
	}
	
	public Imagem Processar(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = Sobel(imagem, formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = Sobel(f, formato);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Sobel"),novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage Sobel(BufferedImage img, String formato){
		Mat src = filtrosUtils.BufferedImage2Mat(img, formato);
		if(formato.equals(filtrosUtils.FORMATO_PNG)) 
			Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2BGR);
		Mat sobel = new Mat();
	    Mat gradX = new Mat();
	    Mat squareGradX = new Mat();
	    Mat gradY = new Mat();
	    Mat squareGradY = new Mat();
	    int type = CvType.CV_32F;
	    
	    Imgproc.Sobel(src, gradX, type, 1, 0);
	    Imgproc.Sobel(src, gradY, type, 0, 1);
	    
	    Core.pow(gradX,2, squareGradX);
	    Core.pow(gradY,2, squareGradY);
	    
	    //Core.addWeighted(squareGradX, 0.5, squareGradY, 0.5, 1, sobel);
	    Core.add(squareGradX, squareGradY, sobel);
	    Core.sqrt(sobel, sobel);
	    Core.convertScaleAbs(sobel,sobel);
	  
	    if(formato.equals(filtrosUtils.FORMATO_PNG)) {
	    	img = filtrosUtils.devolverTransparencia(img, filtrosUtils.Mat2BufferedImage(sobel, formato));
	    	return img;
	    }
		img = filtrosUtils.Mat2BufferedImage(sobel, formato);
        return img;
	}

}
