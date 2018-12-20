package com.letmesee.service;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.inject.Singleton;

import com.letmesee.entity.Imagem;

import aj.org.objectweb.asm.Type;

@Singleton
public class Filtros {
	private static Filtros instancia;
	
	private Filtros() {}
	
	public static synchronized Filtros getInstancia() {
		if(instancia == null) {
			instancia = new Filtros();
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
	
	public void setPixel(BufferedImage img, int i, int j, int r, int g, int b) {
		Color c = new Color(r,g,b);
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
	
	/*public Imagem Negativo(Imagem img) {
		BufferedImage imagem = this.base64toBufferedImage(img.getConteudoBase64());
		BufferedImage saidaBF = imagem;
		Imagem saida = null;
		Color c,novoPixel;
		int i,j,r,g,b,a;
		for(i = 0; i < imagem.getHeight(); i++) {
			for(j = 0; j < imagem.getWidth(); j++) {
				c = new Color(imagem.getRGB(j, i));
				a = c.getAlpha();
				r = 255 - c.getRed();
				g = 255 - c.getGreen();
				b = 255 - c.getBlue();
				if(r < 0) {
					r = 0;
				}
				if(g < 0) {
					g = 0;
				}
				if(b < 0) {
					b = 0;
				}
				novoPixel = new Color(r,g,b,a);
				saidaBF.setRGB(j, i,novoPixel.getRGB());
			}
		}
		String novoConteudoBase64 = this.BufferedImageToBase64(saidaBF,img.getTipo());
		saida = new Imagem(img.getAltura(),img.getAltura(),img.getTipo(),img.getNome().concat("+Negativo"),novoConteudoBase64);
		return saida;
	}*/
	
	public Imagem Negativo(Imagem img) {
		BufferedImage imagem = this.base64toBufferedImage(img.getConteudoBase64());
		BufferedImage saidaBF = imagem;
		Imagem saida = null;
		int i,j,r,g,b;
		for(i = 0; i < imagem.getHeight(); i++) {
			for(j = 0; j < imagem.getWidth(); j++) {
				r = 255 - getCanalR(imagem,i,j);
				g = 255 - getCanalG(imagem,i,j);
				b = 255 - getCanalB(imagem,i,j);
				if(r < 0) {
					r = 0;
				}
				if(g < 0) {
					g = 0;
				}
				if(b < 0) {
					b = 0;
				}
				setPixel(saidaBF,i,j,r,g,b);
			}
		}
		String novoConteudoBase64 = this.BufferedImageToBase64(saidaBF,img.getTipo());
		saida = new Imagem(img.getLargura(),img.getAltura(),img.getTipo(),img.getNome().concat("+Negativo"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem Limiar(Imagem img, int valorLimiar) {
		BufferedImage imagem = this.base64toBufferedImage(img.getConteudoBase64());
		BufferedImage saidaBF = imagem;
		Imagem saida = null;
		Color c,novoPixel;
		int i,j,r,g,b,a,novaCor;
		for(i = 0; i < imagem.getHeight(); i++) {
			for(j = 0; j < imagem.getWidth(); j++) {
				c = new Color(imagem.getRGB(j, i));
				a = c.getAlpha();
				r = (int) (c.getRed() * 0.299);
				g = (int) (c.getGreen() * 0.587);
				b = (int) (c.getBlue() *0.114);
				novaCor = r + g + b;
				if(novaCor > valorLimiar) {
					novaCor = 255;
				}
				else {
					novaCor = 0;
				}
				novoPixel = new Color(novaCor,novaCor,novaCor,a);
				saidaBF.setRGB(j, i,novoPixel.getRGB());
			}
		}
		String novoConteudoBase64 = this.BufferedImageToBase64(saidaBF,img.getTipo());
		saida = new Imagem(img.getLargura(),img.getAltura(),img.getTipo(),img.getNome().concat("+Limiar(").concat(String.valueOf(valorLimiar)).concat(")"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem AjusteRGB(Imagem img, int valoR, int valorG, int valorB) {
		BufferedImage imagem = this.base64toBufferedImage(img.getConteudoBase64());
		BufferedImage saidaBF = imagem;
		Imagem saida = null;
		Color c,novoPixel;
		int i,j,r,g,b,a;
		for(i = 0; i < imagem.getHeight(); i++) {
			for(j = 0; j < imagem.getWidth(); j++) {
				c = new Color(imagem.getRGB(j, i));
				a = c.getAlpha();
				r = c.getRed() + valoR;
				g = c.getGreen() + valorG;
				b = c.getBlue() + valorB;
				if(r > 255) {
					r = 255;
				}
				if(r < 0) {
					r = 0;
				}
				if(g > 255) {
					g = 255;
				}
				if(g < 0) {
					g = 0;
				}
				if(b > 255) {
					b = 255;
				}
				if(b < 0) {
					b = 0;
				}
				novoPixel = new Color(r,g,b,a);
				saidaBF.setRGB(j, i,novoPixel.getRGB());
			}
		}
		String novoConteudoBase64 = this.BufferedImageToBase64(saidaBF,img.getTipo());
		saida = new Imagem(img.getLargura(),img.getAltura(),img.getTipo(),img.getNome().concat("+AjusteRGB"),novoConteudoBase64);
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
}
