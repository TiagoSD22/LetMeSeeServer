package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class RotacaoAntiHorariaFacade {
	
	private static RotacaoAntiHorariaFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private RotacaoAntiHorariaFacade() {}
	
	public static synchronized RotacaoAntiHorariaFacade getInstancia() {
		if(instancia == null) {
			instancia = new RotacaoAntiHorariaFacade();
			filtrosUtils = FiltrosUtils.getInstancia();
		}
		return instancia;
	}
	
	public Imagem Processar(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			BufferedImage saida = new BufferedImage(imagem.getHeight(), imagem.getWidth(), imagem.getType());
			saida = RotacaoAntiHoraria(imagem,formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(saida,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				BufferedImage fSaida = new BufferedImage(f.getHeight(), f.getWidth(), f.getType());
				fSaida = RotacaoAntiHoraria(f,formato);
				framesProcessados.add(fSaida);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getAltura(),img.getLargura(),formato,img.getNome().concat("+Rotação_Anti-horária"),novoConteudoBase64);
		return saida;
	}

	public BufferedImage RotacaoAntiHoraria(BufferedImage img, String formato) {
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

}
