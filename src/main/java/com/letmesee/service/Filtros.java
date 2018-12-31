package com.letmesee.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.inject.Singleton;
import aj.org.objectweb.asm.Type;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import com.letmesee.entity.Imagem;
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
		}
		return instancia;
	}
	
	public static Mat BufferedImage2Mat(BufferedImage image, String formato) throws IOException {
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    ImageIO.write(image, formato, byteArrayOutputStream);
	    byteArrayOutputStream.flush();
	    return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
	}
	
	public static BufferedImage Mat2BufferedImage(Mat matrix, String formato)throws IOException {
	    MatOfByte mob=new MatOfByte();
	    Imgcodecs.imencode(".png", matrix, mob);
	    return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
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
	
	public BufferedImage Limiar(BufferedImage img, int valorLimiar, String formato) {
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
	
	public BufferedImage AjusteRGB(BufferedImage img, int valorR, int valorG, int valorB,String formato) {
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
	
	/*public Imagem EscalaCinza(Imagem img) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		BufferedImage image = base64toBufferedImage(img.getConteudoBase64());
		byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC4);
        
        mat.put(0, 0, data);
		
		/*Mat mat = null;
		try {
			mat = BufferedImage2Mat(image, img.getTipo());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        Mat mat1 = new Mat(image.getHeight(),image.getWidth(),CvType.CV_8UC4);
        Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_RGBA2GRAY);

        byte[] data1 = new byte[mat1.rows() * mat1.cols() * (int)(mat1.elemSize())];
        mat1.get(0, 0, data1);
        BufferedImage image1 = new BufferedImage(mat1.cols(),mat1.rows(), BufferedImage.TYPE_4BYTE_ABGR);
        image1.getRaster().setDataElements(0, 0, mat1.cols(), mat1.rows(), data1);
        
        /*BufferedImage image1 = null;
		try {
			image1 = Mat2BufferedImage(mat1, img.getTipo());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /*File ouptut = new File("grayscale.png");
        try {
			ImageIO.write(image1, "png", ouptut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Imagem saida = new Imagem(img.getLargura(),img.getAltura(),img.getTipo(),img.getNome().concat("+Escala_Cinza"),BufferedImageToBase64(image1,img.getTipo()));
        return saida;
	}*/
}
