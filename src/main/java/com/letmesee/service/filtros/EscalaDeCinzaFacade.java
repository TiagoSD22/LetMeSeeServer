package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class EscalaDeCinzaFacade {
	
	private static EscalaDeCinzaFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private EscalaDeCinzaFacade() {}
	
	public static synchronized EscalaDeCinzaFacade getInstancia() {
		if(instancia == null) {
			instancia = new EscalaDeCinzaFacade();
			filtrosUtils = FiltrosUtils.getInstancia();
		}
		return instancia;
	}
	
	public Imagem Processar(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = EscalaDeCinza(imagem, formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = EscalaDeCinza(f,formato);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Escala_Cinza"),novoConteudoBase64);
		return saida;
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
}
