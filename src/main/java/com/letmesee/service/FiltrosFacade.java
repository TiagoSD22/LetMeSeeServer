package com.letmesee.service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.inject.Singleton;
import com.letmesee.entity.Imagem;

@Singleton
public class FiltrosFacade {
	private static FiltrosFacade instancia;
	private static Filtros filtros;
	private static FiltrosUtils filtrosUtils;
	
	private FiltrosFacade() {}
	
	public static synchronized FiltrosFacade getInstancia() {
		if(instancia == null) {
			instancia = new FiltrosFacade();
			filtros = Filtros.getInstancia();
			filtrosUtils = FiltrosUtils.getInstancia();
		}
		return instancia;
	}
	
	public Imagem Negativo(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			filtros.Negativo(imagem,formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = filtros.Negativo(f,formato);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Negativo"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem Limiar(Imagem img, int valorLimiar) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			filtros.Limiar(imagem,valorLimiar,formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = filtros.Limiar(f,valorLimiar,formato);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Limiar(").concat(String.valueOf(valorLimiar)).concat(")"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem AjusteRGB(Imagem img, int valorR, int valorG, int valorB) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			filtros.AjusteRGB(imagem, valorR, valorG, valorB,formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = filtros.AjusteRGB(f, valorR, valorG, valorB, formato);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome()
								  .concat("+RGB(R").concat(valorR >= 0 ? "+" : "").concat(String.valueOf(valorR))
					              .concat(",G").concat(valorG >= 0 ? "+" : "").concat(String.valueOf(valorG))
					              .concat(",B").concat(valorB >= 0 ? "+" : "").concat(String.valueOf(valorB))
					              .concat(")"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem EspelhoHorizontal(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			filtros.EspelhoHorizontal(imagem,formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = filtros.EspelhoHorizontal(f,formato);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Espelho_Horizontal"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem EspelhoVertical(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			filtros.EspelhoVertical(imagem,formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = filtros.EspelhoVertical(f,formato);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Espelho_Vertical"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem GirarHorario(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			BufferedImage saida = new BufferedImage(imagem.getHeight(), imagem.getWidth(), imagem.getType());
			saida = filtros.GirarHorario(imagem,formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(saida,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				BufferedImage fSaida = new BufferedImage(f.getHeight(), f.getWidth(), f.getType());
				fSaida = filtros.GirarHorario(f,formato);
				framesProcessados.add(fSaida);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getAltura(),img.getLargura(),formato,img.getNome().concat("+Rotação_Horária"),novoConteudoBase64);
		return saida;
	}
	
	public Imagem GirarAntiHorario(Imagem img) {
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			BufferedImage saida = new BufferedImage(imagem.getHeight(), imagem.getWidth(), imagem.getType());
			saida = filtros.GirarAntiHorario(imagem,formato);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(saida,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				BufferedImage fSaida = new BufferedImage(f.getHeight(), f.getWidth(), f.getType());
				fSaida = filtros.GirarAntiHorario(f,formato);
				framesProcessados.add(fSaida);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		Imagem saida = new Imagem(img.getAltura(),img.getLargura(),formato,img.getNome().concat("+Rotação_Anti-horária"),novoConteudoBase64);
		return saida;
	}
}
