package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class EspelhoVerticalFacade {
	
	private static EspelhoVerticalFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private EspelhoVerticalFacade() {}
	
	public static synchronized EspelhoVerticalFacade getInstancia() {
		if(instancia == null) {
			instancia = new EspelhoVerticalFacade();
			filtrosUtils = FiltrosUtils.getInstancia();
		}
		return instancia;
	}
	
	public Imagem Processar(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = EspelhoVertical(imagem,formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = EspelhoVertical(f,formato);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Espelho_Vertical"),novoConteudoBase64);
		return saida;
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

}
