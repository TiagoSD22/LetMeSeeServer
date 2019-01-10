package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class EspelhoHorizontalFacade {

	private static EspelhoHorizontalFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private EspelhoHorizontalFacade() {}
	
	public static synchronized EspelhoHorizontalFacade getInstancia() {
		if(instancia == null) {
			instancia = new EspelhoHorizontalFacade();
			filtrosUtils = FiltrosUtils.getInstancia();
		}
		return instancia;
	}
	
	public Imagem Processar(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			EspelhoHorizontal(imagem,formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = EspelhoHorizontal(f,formato);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Espelho_Horizontal"),novoConteudoBase64);
		return saida;
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
	
}
