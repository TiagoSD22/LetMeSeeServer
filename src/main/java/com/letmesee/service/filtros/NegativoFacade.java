package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.inject.Singleton;

import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

@Singleton
public class NegativoFacade {
	
	private static NegativoFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private NegativoFacade() {}
	
	public static synchronized NegativoFacade getInstancia() {
		if(instancia == null) {
			instancia = new NegativoFacade();
			filtrosUtils = FiltrosUtils.getInstancia();
		}
		return instancia;
	}
	
	public Imagem Processar(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if (formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = Negativo(imagem, formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem, formato);
			imagem = null;
		} else if (formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(), delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for (BufferedImage f : frames) {
				f = Negativo(f, formato);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados, delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getLargura(), img.getAltura(), formato, img.getNome().concat("+Negativo"),
				novoConteudoBase64);
		return saida;
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
}
