package com.letmesee.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.inject.Singleton;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import com.letmesee.service.FiltrosUtils.Pixel;

@Singleton
public class Filtros {
	private static Filtros instancia;
	private static FiltrosUtils filtrosUtils;
	private Filtros() {}
	
	public static synchronized Filtros getInstancia() {
		if(instancia == null) {
			instancia = new Filtros();
			filtrosUtils = FiltrosUtils.getInstancia();
			System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		}
		return instancia;
	}
	
	public BufferedImage Negativo(BufferedImage img, String formato) {
		int i,j,r,g,b;
		Pixel p;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				r = 255 - p.getR();
				g = 255 - p.getG();
				b = 255 - p.getB();
				p.setR(r);
				p.setG(g);
				p.setB(b);
				filtrosUtils.setPixel(img, i,j,p, formato);
			}
		}
		return img;
	}
	
	public BufferedImage Limiar(BufferedImage img, String formato, int valorLimiar) {
		int i,j,r,g,b,novaCor;
		Pixel p;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				r = (int) (p.getR() * 0.299);
				g = (int) (p.getG() * 0.587);
				b = (int) (p.getB() *0.114);
				novaCor = r + g + b;
				if(novaCor > valorLimiar) {
					novaCor = 255;
				}
				else {
					novaCor = 0;
				}
				p.setRGB(novaCor,novaCor,novaCor);
				filtrosUtils.setPixel(img, i, j, p, formato);
			}
		}
		return img;
	}
	
	public BufferedImage EscalaDeCinza(BufferedImage img, String formato) {
		int i,j,r,g,b,novaCor;
		Pixel p;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				r = (int) (p.getR() * 0.299);
				g = (int) (p.getG() * 0.587);
				b = (int) (p.getB() * 0.114);
				novaCor = r + g + b;
				p.setRGB(novaCor,novaCor,novaCor);
				filtrosUtils.setPixel(img, i, j, p, formato);
			}
		}
		return img;
	}
	
	public BufferedImage AjusteRGB(BufferedImage img, String formato, int valorR, int valorG, int valorB) {
		int i,j,r,g,b;
		Pixel p;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				r = p.getR() + valorR;
				g = p.getG() + valorG;
				b = p.getB() + valorB;
				r = filtrosUtils.TruncarValor(r);
				g = filtrosUtils.TruncarValor(g);
				b = filtrosUtils.TruncarValor(b);
				p.setRGB(r, g, b);
				filtrosUtils.setPixel(img, i, j, p, formato);
			}
		}
		return img;
	}
	
	public BufferedImage EspelhoHorizontal(BufferedImage img, String formato) {
		int i,j;
		Pixel p1,p2;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth() / 2; j++) {
				p1 = filtrosUtils.getPixel(img, i, j, formato);
				p2 = filtrosUtils.getPixel(img, i, img.getWidth() - 1 - j, formato);
				filtrosUtils.setPixel(img, i, j, p2, formato);
				filtrosUtils.setPixel(img, i, img.getWidth() - 1 - j, p1, formato);
			}
		}
		return img;
	}
	
	public BufferedImage EspelhoVertical(BufferedImage img, String formato) {
		int i,j;
		Pixel p1,p2;
		for(i = 0; i < img.getHeight() / 2; i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p1 = filtrosUtils.getPixel(img, i, j, formato);
				p2 = filtrosUtils.getPixel(img, img.getHeight() - 1 - i, j, formato);
				filtrosUtils.setPixel(img, i, j, p2, formato);
				filtrosUtils.setPixel(img, img.getHeight() - 1 - i, j, p1, formato);
			}
		}
		return img;
	}
	
	public BufferedImage GirarHorario(BufferedImage img, String formato) {
		int i,j;
		Pixel p;
		BufferedImage saida = new BufferedImage(img.getHeight(), img.getWidth(), img.getType());
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				filtrosUtils.setPixel(saida,j,img.getHeight() - 1 - i, p, formato);
			}
		}
		return saida;
	}
	
	public BufferedImage GirarAntiHorario(BufferedImage img, String formato) {
		int i,j;
		Pixel p;
		BufferedImage saida = new BufferedImage(img.getHeight(), img.getWidth(), img.getType());
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				filtrosUtils.setPixel(saida,img.getWidth() - 1 - j, i, p, formato);
			}
		}
		return saida;
	}
	
	public BufferedImage Dilatar(BufferedImage img, String formato, int w, int h){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
		Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(w,h));
        Imgproc.dilate(mat, destination, element1);
		img = filtrosUtils.Mat2BufferedImage(destination, formato);
        return img;
	}
	
	public BufferedImage Erodir(BufferedImage img, String formato, int w, int h){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
		Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(w,h));
        Imgproc.erode(mat, destination, element1);
		img = filtrosUtils.Mat2BufferedImage(destination, formato);
        return img;
	}
	
	public BufferedImage Blur(BufferedImage img, String formato, int w, int h){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
        Imgproc.blur(mat, destination, new Size(w,h));
		img = filtrosUtils.Mat2BufferedImage(destination, formato);
        return img;
	}
	
	public BufferedImage GaussianBlur(BufferedImage img, String formato, int w, int h, double sigmaX, double sigmaY){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
        Imgproc.GaussianBlur(mat, destination, new Size(w,h), sigmaX, sigmaY);
		img = filtrosUtils.Mat2BufferedImage(destination, formato);
        return img;
	}
	
	public BufferedImage Mediana(BufferedImage img, String formato, int tamanhoMascara){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
        Imgproc.medianBlur(mat, destination, tamanhoMascara);
		img = filtrosUtils.Mat2BufferedImage(destination, formato);
        return img;
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
	
	public BufferedImage Sobel(BufferedImage img, String formato){
		Mat src = filtrosUtils.BufferedImage2Mat(img, formato);
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
	  
		img = filtrosUtils.Mat2BufferedImage(sobel, formato);
        return img;
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
	    
		img = filtrosUtils.Mat2BufferedImage(rob, formato);
        return img;
	}
	
	public BufferedImage Pixelate(BufferedImage img, String formato){
		Mat src = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat saida = new Mat(src.rows(),src.cols(),src.type());
	    int depth = CvType.CV_32F;
	    
	    Mat Gx = new Mat(3,3, CvType.CV_64F){
	        {
	           put(0,0,1/9);
	           put(0,1,1/9);
	           put(0,2,1/9);
	           
	           put(1,0,1/9);
	           put(1,1,1/9);
	           put(1,2,1/9);
	           
	           put(2,0,1/9);
	           put(2,1,1/9);
	           put(2,2,1/9);
	          
	        }
	    };
	    
	    Imgproc.filter2D(src, saida, depth, Gx);
	    
		img = filtrosUtils.Mat2BufferedImage(src, formato);
        return img;
	}
	
	public BufferedImage Prewitt(BufferedImage img, String formato){
		Mat src = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat pwt = new Mat(src.rows(),src.cols(),src.type());
	    Mat gradX = new Mat(src.rows(),src.cols(),src.type());
	    Mat squareGradX = new Mat(src.rows(),src.cols(),src.type());
	    Mat gradY = new Mat(src.rows(),src.cols(),src.type());
	    Mat squareGradY = new Mat(src.rows(),src.cols(),src.type());
	    int depth = CvType.CV_32F;
	    
	    Mat Gx = new Mat(3,3, CvType.CV_32F){
	        {
	           put(0,0,1);
	           put(0,1,0);
	           put(0,2,-1);
	           
	           put(1,0,1);
	           put(1,1,0);
	           put(1,2,-1);
	           
	           put(2,0,1);
	           put(2,1,0);
	           put(2,2,-1);
	        }
	    };
	    
	    Mat Gy = new Mat(3,3, CvType.CV_32F){
	        {
	           put(0,0,1);
	           put(0,1,1);
	           put(0,2,1);
	           
	           put(1,0,0);
	           put(1,1,0);
	           put(1,2,0);
	           
	           put(2,0,-1);
	           put(2,1,-1);
	           put(2,2,-1);
	        }
	    }; 
	    
	    Imgproc.filter2D(src, gradX, depth, Gx);
	    Core.pow(gradX, 2, squareGradX);
	    
	    Imgproc.filter2D(src, gradY, depth, Gy);
	    Core.pow(gradY, 2, squareGradY);
	    
	    //Core.addWeighted(squareGradX, 0.5, squareGradY, 0.5, 1, pwt);
	    Core.add(squareGradX, squareGradY, pwt);
	    Core.sqrt(pwt,pwt);
	    
		img = filtrosUtils.Mat2BufferedImage(pwt, formato);
        return img;
	}
	
	public BufferedImage Redimensionar(BufferedImage img, String formato, int novaLargura, int novaAltura){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat destination = new Mat(novaAltura,novaLargura,mat.type());
		Imgproc.resize(mat, destination, new Size(novaLargura,novaAltura),Imgproc.INTER_AREA);
		BufferedImage saida = filtrosUtils.Mat2BufferedImage(destination, formato);
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
	
	public BufferedImage AjustarBrilho(BufferedImage img, String formato, int alpha) {
		int i,j,r,g,b;
		Pixel p;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				r = filtrosUtils.TruncarValor(p.getR() + alpha);
				g = filtrosUtils.TruncarValor(p.getG() + alpha);
				b = filtrosUtils.TruncarValor(p.getB() + alpha);
				p.setRGB(r, g, b);
				filtrosUtils.setPixel(img, i, j, p, formato);
			}
		}
		return img;
	}
	
	public BufferedImage AjustarContraste(BufferedImage img, String formato, double valor_gamma){
	    int i,j,r,g,b;
	    Pixel p;
	    for(i = 0; i < img.getHeight(); i++){
	        for(j = 0; j < img.getWidth(); j++){
	            p = filtrosUtils.getPixel(img, i, j, formato);
	            r = p.getR();
	            g = p.getG();
	            b = p.getB();
	            r = filtrosUtils.TruncarValor((int) (255 * Math.pow(((double)((double)r/255)), valor_gamma)));
	            g = filtrosUtils.TruncarValor((int) (255 * Math.pow(((double)((double)g/255)), valor_gamma)));
	            b = filtrosUtils.TruncarValor((int) (255 * Math.pow(((double)((double)b/255)), valor_gamma)));
	            p.setRGB(r, g, b);
	            filtrosUtils.setPixel(img, i, j, p, formato);
	        }
	    }
	    return img;
	}
	
	public BufferedImage TrocarCanais(BufferedImage img, String formato, String canais) {
		int i,j,r,g,b;
		Pixel p;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = filtrosUtils.getPixel(img, i, j, formato);
				r = p.getR();
				g = p.getG();
				b = p.getB();
				if(canais.equals("RG")) p.setRGB(g, r, b);
				else if(canais.equals("RB")) p.setRGB(b, g, r);
				else p.setRGB(r, b, g);
				filtrosUtils.setPixel(img, i, j, p, formato);
			}
		}
		return img;
	}
	
	public BufferedImage Mock(BufferedImage img, String formato) {
		File input = new File("textura.png");
		double alpha = 0.6;
		try {
			BufferedImage textura = ImageIO.read(input);
			Mat texturaMat = filtrosUtils.BufferedImage2Mat(textura, "png");
			Imgproc.resize(texturaMat, texturaMat, new Size(img.getWidth(),img.getHeight()),Imgproc.INTER_AREA);
			Mat src = filtrosUtils.BufferedImage2Mat(img, formato);
			Mat saida = new Mat(img.getWidth(),img.getHeight(),src.type());
			Core.addWeighted(texturaMat, alpha, src, 1, 0.0, saida);
			img = filtrosUtils.Mat2BufferedImage(saida, formato);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	//filtros interessante de se ver
    //Imgproc.applyColorMap(mat, rob, Imgproc.COLORMAP_WINTER);
    //Imgproc.threshold(mat, rob, 150, 255, Imgproc.THRESH_BINARY);
}
