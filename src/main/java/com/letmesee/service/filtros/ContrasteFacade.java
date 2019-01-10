package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;
import com.letmesee.service.FiltrosUtils.Pixel;

public class ContrasteFacade {
	
	private static ContrasteFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private ContrasteFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized ContrasteFacade getInstancia() {
		if(instancia == null) {
			instancia = new ContrasteFacade();
			filtrosUtils = FiltrosUtils.getInstancia();
			mapper = new ObjectMapper();
		}
		return instancia;
	}
	
	public Imagem Processar(Imagem img, String parametros) {
		try {
			this.parametros = mapper.readTree(parametros);
		} catch (IOException e) {
			e.printStackTrace();
		}
		double gamma =  this.parametros.get("gamma").asDouble();
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = Contraste(imagem, formato, gamma);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = Contraste(f, formato, gamma);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,
				  				  img.getNome().concat("+Contraste(")
				  				  .concat(String.valueOf(Math.round((gamma / 10.0) * 100))).concat("%)"),
								  novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage Contraste(BufferedImage img, String formato, double valor_gamma){
	    int i,j,r,g,b;
	    Pixel p;
	    for(i = 0; i < img.getHeight(); i++){
	        for(j = 0; j < img.getWidth(); j++){
	            p = filtrosUtils.getPixel(img, i, j, formato);
	            r = p.getR();
	            g = p.getG();
	            b = p.getB();
	            r = filtrosUtils.TruncarValor((int) (255 * Math.pow(((double)((double)r/255)), valor_gamma)));
	            g = filtrosUtils.TruncarValor((int) (255 * Math.pow(((double)((double)g/255)), valor_gamma)));
	            b = filtrosUtils.TruncarValor((int) (255 * Math.pow(((double)((double)b/255)), valor_gamma)));
	            p.setRGB(r, g, b);
	            filtrosUtils.setPixel(img, i, j, p, formato);
	        }
	    }
	    return img;
	}

}
