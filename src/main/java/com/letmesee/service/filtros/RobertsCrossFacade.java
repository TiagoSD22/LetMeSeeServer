package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;

public class RobertsCrossFacade {
	
	private static RobertsCrossFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private RobertsCrossFacade() {}
	
	public static synchronized RobertsCrossFacade getInstancia() {
		if(instancia == null) {
			instancia = new RobertsCrossFacade();
			filtrosUtils = FiltrosUtils.getInstancia();
		}
		return instancia;
	}
	
	public Imagem Processar(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = Roberts_Cross(imagem, formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = Roberts_Cross(f, formato);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Roberts_Cross"),novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage Roberts_Cross(BufferedImage img, String formato){
		Mat src = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat rob = new Mat(src.rows(),src.cols(),src.type());
	    Mat gradX = new Mat(src.rows(),src.cols(),src.type());
	    Mat squareGradX = new Mat(src.rows(),src.cols(),src.type());
	    Mat gradY = new Mat(src.rows(),src.cols(),src.type());
	    Mat squareGradY = new Mat(src.rows(),src.cols(),src.type());
	    int depth = CvType.CV_32F;
	    
	    Mat Gx = new Mat(2,2, CvType.CV_32F){
	        {
	           put(0,0,1);
	           put(0,1,0);
	           
	           put(1,0,0);
	           put(1,1,-1);
	        }
	    };
	    
	    Mat Gy = new Mat(2,2, CvType.CV_32F){
	        {
	           put(0,0,0);
	           put(0,1,1);
	           
	           put(1,0,-1);
	           put(1,1,0);
	        }
	    }; 
	    
	    Imgproc.filter2D(src, gradX, depth, Gx);
	    Core.pow(gradX, 2, squareGradX);
	    
	    Imgproc.filter2D(src, gradY, depth, Gy);
	    Core.pow(gradY, 2, squareGradY);
	    
	    //Core.addWeighted(squareGradX, 0.5, squareGradY, 0.5, 1, rob);
	    Core.add(squareGradX, squareGradY, rob);
	    Core.sqrt(rob, rob);
	    Core.convertScaleAbs(rob, rob);
		img = filtrosUtils.Mat2BufferedImage(rob, formato);
        return img;
	}

}
