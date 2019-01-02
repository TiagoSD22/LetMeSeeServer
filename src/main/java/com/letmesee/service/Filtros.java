package com.letmesee.service;

import java.awt.image.BufferedImage;
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
				b = (int) (p.getB() *0.114);
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
		Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
        Imgproc.equalizeHist(mat, destination);
		img = filtrosUtils.Mat2BufferedImage(destination, formato);
        return img;
	}
	
	public BufferedImage Sobel(BufferedImage img, String formato){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat sobel = new Mat();
	    Mat grad_x = new Mat();
	    Mat abs_grad_x = new Mat();
	    Mat grad_y = new Mat();
	    Mat abs_grad_y = new Mat();
	    int type = CvType.CV_16S;
	    Imgproc.Sobel(mat, grad_x, type, 1, 0, 3, 1, 0);
	    
	    //Calculating gradient in vertical direction
	    Imgproc.Sobel(mat, grad_y, type, 0, 1, 3, 1, 0);

	    //Calculating absolute value of gradients in both the direction
	    Core.convertScaleAbs(grad_x, abs_grad_x);
	    Core.convertScaleAbs(grad_y, abs_grad_y);

	    //Calculating the resultant gradient
	    //Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1, sobel);
	    Imgproc.Laplacian(mat, sobel, type, 5);
		img = filtrosUtils.Mat2BufferedImage(sobel, formato);
        return img;
	}
	
	public BufferedImage Contraste(BufferedImage img, String formato, int bias){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
		mat.convertTo(destination, -1, 1, bias);
		img = filtrosUtils.Mat2BufferedImage(destination, formato);
        return img;
	}
	
	public BufferedImage Brilho(BufferedImage img, String formato, double gain){
		Mat mat = filtrosUtils.BufferedImage2Mat(img, formato);
		Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
		mat.convertTo(destination, -1, gain, 0);
		img = filtrosUtils.Mat2BufferedImage(destination, formato);
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
}
