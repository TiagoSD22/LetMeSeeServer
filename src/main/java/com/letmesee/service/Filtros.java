package com.letmesee.service;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.inject.Singleton;

import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils.Pixel;

import aj.org.objectweb.asm.Type;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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
	
	public BufferedImage base64toBufferedImage(String conteudoBase64) {
		BufferedImage image = null;
		byte[] imageBytes = null;
		imageBytes = Base64.getDecoder().decode(conteudoBase64);
		try {
			image = ImageIO.read(new ByteArrayInputStream(imageBytes));
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String BufferedImageToBase64(BufferedImage img, String formato) {
		String imageString = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		 try {
			ImageIO.write(img,formato, os);
			imageString = Base64.getEncoder().encodeToString(os.toByteArray());
			return imageString;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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

	
	public int getCanalR(BufferedImage img, int i, int j) {
		Color c = new Color(img.getRGB(j, i));
		return c.getRed();
	}
	
	public int getCanalG(BufferedImage img, int i, int j) {
		Color c = new Color(img.getRGB(j, i));
		return c.getGreen();
	}
	
	public int getCanalB(BufferedImage img, int i, int j) {
		Color c = new Color(img.getRGB(j, i));
		return c.getBlue();
	}
	
	public int getAlpha(BufferedImage img, int i, int j) {
		Color c = new Color(img.getRGB(j, i));
		return c.getAlpha();
	}
	
	public void setPixel(BufferedImage img, int i, int j, int r, int g, int b) {
		Color c = new Color(r,g,b);
		img.setRGB(j,i,c.getRGB());
	}
	
	public void setPixel(BufferedImage img, int i, int j, int r, int g, int b,int a) {
		Color c = new Color(r,g,b,a);
		img.setRGB(j,i,c.getRGB());
	}
	
	public BufferedImage AdicionarPadding(BufferedImage entrada) {
		BufferedImage saida = new BufferedImage(entrada.getWidth() + 2, entrada.getHeight() + 2,BufferedImage.TYPE_INT_RGB);
		int i,j,r,g,b;
		for(i = 0; i < entrada.getHeight() + 2; i++) {
			for(j = 0; j < entrada.getWidth() + 2; j++) {
				if(i == 0 || j == 0 || i == entrada.getHeight() + 1 || j == entrada.getWidth() + 1) {
					setPixel(saida,i,j,0,0,0);
				}
				else {
					r = getCanalR(entrada,i - 1,j - 1);
					g = getCanalG(entrada,i - 1,j - 1);
					b = getCanalB(entrada,i - 1,j - 1);
					setPixel(saida,i,j,r,g,b);
				}
			}
		}
		return saida;
	}
	
	public Imagem Negativo(Imagem img) {
		BufferedImage imagem = this.base64toBufferedImage(img.getConteudoBase64());
		int i,j,r,g,b;
		Pixel p;
		String formato = img.getTipo();
		for(i = 0; i < imagem.getHeight(); i++) {
			for(j = 0; j < imagem.getWidth(); j++) {
				p = filtrosUtils.getPixel(imagem, i, j, formato);
				r = 255 - p.getR();
				g = 255 - p.getG();
				b = 255 - p.getB();
				p.setR(r);
				p.setG(g);
				p.setB(b);
				filtrosUtils.setPixel(imagem, i,j,p, formato);
			}
		}
		String novoConteudoBase64 = this.BufferedImageToBase64(imagem,formato);
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Negativo"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem Negativo2(Imagem img) {
		BufferedImage imagem = this.base64toBufferedImage(img.getConteudoBase64());
		int i,j,p,r,g,b,a;
		for(i = 0; i < imagem.getHeight(); i++) {
			for(j = 0; j < imagem.getWidth(); j++) {
				p = imagem.getRGB(j, i);
			    a = (p>>24) & 0xff;
			    r = (p>>16) & 0xff;
			    g = (p>>8) & 0xff;
			    b = p & 0xff;
			    r = 255 - r;
			    g = 255 - g;
			    b = 255 - b;
			    p = (a<<24) | (r<<16) | (g<<8) | b;
				imagem.setRGB(j, i, p);
			}
		}
		BufferedImage saidaBF = new BufferedImage(imagem.getWidth(),imagem.getHeight(),BufferedImage.TYPE_INT_ARGB);
		saidaBF.createGraphics().drawImage(imagem, 0, 0, null);
		String novoConteudoBase64 = this.BufferedImageToBase64(saidaBF,img.getTipo());
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),img.getTipo(),img.getNome().concat("+Negativo"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem Limiar(Imagem img, int valorLimiar) {
		BufferedImage imagem = this.base64toBufferedImage(img.getConteudoBase64());
		int i,j,r,g,b,novaCor;
		String formato = img.getTipo();
		Pixel p;
		for(i = 0; i < imagem.getHeight(); i++) {
			for(j = 0; j < imagem.getWidth(); j++) {
				p = filtrosUtils.getPixel(imagem, i, j, formato);
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
				filtrosUtils.setPixel(imagem, i, j, p, formato);
			}
		}
		String novoConteudoBase64 = this.BufferedImageToBase64(imagem,formato);
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Limiar(").concat(String.valueOf(valorLimiar)).concat(")"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem AjusteRGB(Imagem img, int valoR, int valorG, int valorB) {
		BufferedImage imagem = this.base64toBufferedImage(img.getConteudoBase64());
		int i,j,r,g,b;
		Pixel p;
		String formato = img.getTipo();
		for(i = 0; i < imagem.getHeight(); i++) {
			for(j = 0; j < imagem.getWidth(); j++) {
				p = filtrosUtils.getPixel(imagem, i, j, formato);
				r = p.getR() + valoR;
				g = p.getG() + valorG;
				b = p.getB() + valorB;
				r = filtrosUtils.TruncarValor(r);
				g = filtrosUtils.TruncarValor(g);
				b = filtrosUtils.TruncarValor(b);
				p.setRGB(r, g, b);
				filtrosUtils.setPixel(imagem, i, j, p, formato);
			}
		}
		String novoConteudoBase64 = this.BufferedImageToBase64(imagem,formato);
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+AjusteRGB"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem RobertsCross(Imagem img) {
		BufferedImage imagem = this.base64toBufferedImage(img.getConteudoBase64());
		BufferedImage padd = AdicionarPadding(imagem);
		BufferedImage saidaBF = imagem;
		Imagem saida = null;
		int i,j,r,g,b,GxR,GyR,GxG,GyG,GxB,GyB,x,y,novoR,novoG,novoB;
		int[][] robertX = {{1,0},{0,-1}};
	    int[][] robertY = {{0,1},{-1,0}};
		for(i = 0; i < imagem.getHeight(); i++) {
			for(j = 0; j < imagem.getWidth(); j++) {
				GxR = 0;
				GyR = 0;
				GxG = 0;
				GyG = 0;
				GxB = 0;
				GyB = 0;
				for(x = 0; x < 2; x++){
	                for(y = 0; y < 2; y++){
	                    if(i > 0 || j > 0){
	                    	r = getCanalR(padd,i + x,j + y);
		                	g = getCanalG(padd,i + x,j + y);
		    				b = getCanalB(padd,i + x,j + y);
	                        GxR += r * robertX[x][y];
	                        GyR += r * robertY[x][y];
	                        GxG += g * robertX[x][y];
	                        GyG += g * robertY[x][y];
	                        GxB += b * robertX[x][y];
	                        GyB += b * robertY[x][y];
	                    }
	                    else{
	                    	try {
	                    	r = getCanalR(imagem,i + x,j + y);
		                	g = getCanalG(imagem,i + x,j + y);
		    				b = getCanalB(imagem,i + x,j + y);
	                        GxR += r * robertX[x][y];
	                        GyR += r * robertY[x][y];
	                        GxG += g * robertX[x][y];
	                        GyG += g * robertY[x][y];
	                        GxB += b * robertX[x][y];
	                        GyB += b * robertY[x][y];
	                    	}
	                    	catch(Exception e) {
	                    		System.out.println("Erro! " + "i: " + i + "j : " + j + "x : " + x + "y: " + y);
	                    	}
	                    }
	                }
	            }
				novoR = (int) Math.sqrt((GxR * GxR) + (GyR * GyR));
				novoG = (int) Math.sqrt((GxG * GxG) + (GyG * GyG));
				novoB = (int) Math.sqrt((GxB * GxB) + (GyB * GyB));
				if(novoR > 255) {
					novoR = 255;
				}
				if(novoG > 255) {
					novoG = 255;
				}
				if(novoB > 255) {
					novoB = 255;
				}
				if(novoR < 0) {
					novoR = 0;
				}
				if(novoG < 0) {
					novoG = 0;
				}
				if(novoB < 0) {
					novoB = 0;
				}
				setPixel(saidaBF,i,j,novoR,novoG,novoB);
			}
		}
		String novoConteudoBase64 = this.BufferedImageToBase64(saidaBF,img.getTipo());
		saida = new Imagem(img.getLargura(),img.getAltura(),img.getTipo(),img.getNome().concat("+RobertsCross"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem EscalaCinza(Imagem img) {
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
		}*/

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
		}*/
        
        /*File ouptut = new File("grayscale.png");
        try {
			ImageIO.write(image1, "png", ouptut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        
        Imagem saida = new Imagem(img.getLargura(),img.getAltura(),img.getTipo(),img.getNome().concat("+Escala_Cinza"),BufferedImageToBase64(image1,img.getTipo()));
        return saida;
	}
}
