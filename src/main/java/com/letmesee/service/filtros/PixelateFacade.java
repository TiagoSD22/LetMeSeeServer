package com.letmesee.service.filtros;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.service.FiltrosUtils;

public class PixelateFacade {
	
	private static PixelateFacade instancia;
	private static FiltrosUtils filtrosUtils;
	private PixelateFacade() {}
	
	private static ObjectMapper mapper;
	private JsonNode parametros;
	
	public static synchronized PixelateFacade getInstancia() {
		if(instancia == null) {
			instancia = new PixelateFacade();
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
		int k = this.parametros.get("k").asInt();
		
		String formato = img.getTipo();
		String novoConteudoBase64 = "";
		if(formato.equals("jpg") || formato.equals("png") || formato.equals("bmp")) {
			BufferedImage imagem = filtrosUtils.base64toBufferedImage(img.getConteudoBase64());
			imagem = Pixelate(imagem, formato,k);
			novoConteudoBase64 = filtrosUtils.BufferedImageToBase64(imagem,formato);
			imagem = null;
		}
		else if(formato.equals("gif")) {
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<BufferedImage> frames = filtrosUtils.getGifFrames(img.getConteudoBase64(),delays);
			ArrayList<BufferedImage> framesProcessados = new ArrayList<BufferedImage>();
			for(BufferedImage f : frames) {
				f = Pixelate(f, formato,k);
				framesProcessados.add(f);
			}
			novoConteudoBase64 = filtrosUtils.gerarBase64GIF(framesProcessados,delays);
			frames = null;
			framesProcessados = null;
		}
		
		Imagem saida = new Imagem(img.getLargura(),img.getAltura(),formato,img.getNome().concat("+Pixelizar(").concat(String.valueOf(k)).concat(")"),novoConteudoBase64);
		return saida;
	}
	
	public BufferedImage Pixelate(BufferedImage img, String formato, int k) {
		BufferedImage saida = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
		int[] pixels = new int[k * k];
        for ( int y = 0; y < img.getHeight(); y += k) {
            for ( int x = 0; x < img.getWidth(); x += k ) {
                int w = Math.min( k, img.getWidth()-x );
                int h = Math.min( k, img.getHeight()-y );
                int t = w*h;
                filtrosUtils.getRGB( img, x, y, w, h, pixels );
                int r = 0, g = 0, b = 0;
                int argb;
                int i = 0;
                for ( int by = 0; by < h; by++ ) {
                    for ( int bx = 0; bx < w; bx++ ) {
                        argb = pixels[i];
                        r += (argb >> 16) & 0xff;
                        g += (argb >> 8) & 0xff;
                        b += argb & 0xff;
                        i++;
                    }
                }
                argb = ((r/t) << 16) | ((g/t) << 8) | (b/t);
                i = 0;
                for ( int by = 0; by < h; by++ ) {
                    for ( int bx = 0; bx < w; bx++ ) {
                        pixels[i] = (pixels[i] & 0xff000000) | argb;
                        i++;
                    }
                }
                filtrosUtils.setRGB( saida, x, y, w, h, pixels );
            }
        }
        return saida;
	}
}
