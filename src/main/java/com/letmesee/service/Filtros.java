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
	
	public Imagem Negativo(Imagem img) {
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
		saida = new Imagem(img.getAltura(),img.getAltura(),img.getTipo(),img.getNome().concat("+Limiar(").concat(String.valueOf(valorLimiar)).concat(")"),novoConteudoBase64);
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
		saida = new Imagem(img.getAltura(),img.getAltura(),img.getTipo(),img.getNome().concat("+AjusteRGB"),novoConteudoBase64);
		return saida;
	}
}
