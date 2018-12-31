package com.letmesee.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.inject.Singleton;

@Singleton
public class FiltrosUtils {
	
	private final String FORMATO_JPG  = "jpg";
	private final String FORMATO_BMP  = "bmp";
	private final String FORMATO_PNG  = "png";
	private final String FORMATO_GIF  = "gif";
	
	private static FiltrosUtils instancia;
	private FiltrosUtils() {}
	
	public static synchronized FiltrosUtils getInstancia() {
		if(instancia == null) {
			instancia = new FiltrosUtils();
		}
		return instancia;
	}
	
	public BufferedImage base64toBufferedImage(String conteudoBase64) {
		BufferedImage image = null;
		byte[] imageBytes = null;
		imageBytes = Base64.getDecoder().decode(conteudoBase64);
		System.out.println(imageBytes);
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
	
	public int getRed(BufferedImage img,int i, int j, String formato) {
		if(formato.equals(this.FORMATO_JPG) || formato.equals(this.FORMATO_GIF)) {
			Color c = new Color(img.getRGB(j,i));
			return c.getRed();
		}
		else if(formato.equals(this.FORMATO_PNG)) {
			int p = img.getRGB(j, i);
		    return (p>>16) & 0xff;
		}
		else if(formato.equals(this.FORMATO_BMP)) {
			Color c = new Color(img.getRGB(j,i));
			return c.getRed();
		}
		return -1;
	}
	
	public int getGreen(BufferedImage img, int i, int j, String formato) {
		if(formato.equals(this.FORMATO_JPG) || formato.equals(this.FORMATO_GIF)) {
			Color c = new Color(img.getRGB(j,i));
			return c.getGreen();
		}
		else if(formato.equals(this.FORMATO_PNG)) {
			int p = img.getRGB(j, i);
		    return (p>>8) & 0xff;
		}
		else if(formato.equals(this.FORMATO_BMP)) {
			Color c = new Color(img.getRGB(j,i));
			return c.getGreen();
		}
		return -1;
	}
	
	public int getBlue(BufferedImage img, int i, int j, String formato) {
		if(formato.equals(this.FORMATO_JPG) || formato.equals(this.FORMATO_GIF)) {
			Color c = new Color(img.getRGB(j,i));
			return c.getBlue();
		}
		else if(formato.equals(this.FORMATO_PNG)) {
			int p = img.getRGB(j, i);
		    return p & 0xff;
		}
		else if(formato.equals(this.FORMATO_BMP)) {
			Color c = new Color(img.getRGB(j,i));
			return c.getBlue();
		}
		return -1;
	}
	
	public int getAlpha(BufferedImage img, int i, int j, String formato) {
		if(formato.equals(this.FORMATO_JPG) || formato.equals(this.FORMATO_GIF)) {
			Color c = new Color(img.getRGB(j,i));
			return c.getAlpha();
		}
		else if(formato.equals(this.FORMATO_PNG)) {
			int p = img.getRGB(j, i);
		    return (p>>24) & 0xff;
		}
		else if(formato.equals(this.FORMATO_BMP)) {
			Color c = new Color(img.getRGB(j,i));
			return c.getAlpha();
		}
		return -1;
	}
	
	public void setPixel(BufferedImage img, int i, int j, int r, int g, int b, int a, String formato) {
		if(formato.equals(this.FORMATO_JPG) || formato.equals(this.FORMATO_GIF)) {
			Color c = new Color(r,g,b);
			img.setRGB(j,i,c.getRGB());
		}
		else if(formato.equals(this.FORMATO_PNG)) {
			int p = (a<<24) | (r<<16) | (g<<8) | b;
			img.setRGB(j, i, p);
		}
		else if(formato.equals(this.FORMATO_BMP)) {
			
		}
	}
	
	public Pixel getPixel(BufferedImage img, int i, int j, String formato) {
		if(formato.equals(this.FORMATO_JPG) || formato.equals(this.FORMATO_GIF)) {
			Color c = new Color(img.getRGB(j, i));
			return new Pixel(c.getRed(),c.getGreen(),c.getBlue());
		}
		else if(formato.equals(this.FORMATO_PNG)){
			int p = img.getRGB(j, i);
			int a = (p>>24) & 0xff;
		    int r = (p>>16) & 0xff;
		    int g = (p>>8) & 0xff;
		    int b = p & 0xff;
		    return new Pixel(r,g,b,a);
		}
		else if(formato.equals(this.FORMATO_BMP)) {
			Color c = new Color(img.getRGB(j, i));
			return new Pixel(c.getRed(),c.getGreen(),c.getBlue());
		}
		return null;
	}
	
	public void setPixel(BufferedImage img, int i, int j, Pixel p, String formato) {
		if(formato.equals(this.FORMATO_JPG) || formato.equals(this.FORMATO_GIF)) {
			Color c = new Color(p.getR(),p.getG(),p.getB());
			img.setRGB(j, i, c.getRGB());
		}
		else if(formato.equals(this.FORMATO_PNG)) {
			Color c = new Color(p.getR(),p.getG(),p.getB(),p.getAlpha());
			img.setRGB(j, i, c.getRGB());
		}
		else if(formato.equals(this.FORMATO_BMP)) {
			Color c = new Color(p.getR(),p.getG(),p.getB());
			img.setRGB(j, i, c.getRGB());
		}
	}
	
	public int TruncarValor(int v) {
		if(v < 0) return 0;
		if(v > 255) return 255;
		return v;
	}
	
	public ArrayList<BufferedImage> getGifFrames(String conteudoBase64, ArrayList<Integer> delays){
	    GifDecoder gd = new GifDecoder();
	    gd.read(new ByteArrayInputStream(Base64.getDecoder().decode(conteudoBase64)));
	    int n = gd.getFrameCount();
	    ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
	    for (int i = 0; i < n; i++) {
	       frames.add(gd.getFrame(i));
	       delays.add(gd.getDelay(i));
	    }
	    return frames;
	}
	
	public String gerarBase64GIF(ArrayList<BufferedImage> frames, ArrayList<Integer> delays) {
		GifCreator gc = new GifCreator();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		String imageString = null;
		try {
			imageString = Base64.getEncoder().encodeToString(gc.createGif(frames, delays, os));
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageString;
	}
	
	public class Pixel{
		int r;
		int g;
		int b;
		int a;
		
		public Pixel(int r, int g, int b, int a) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}
		
		public Pixel(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
		
		public Pixel() {}
		
		public int getR() {
			return this.r;
		}
		
		public int getG() {
			return this.g;
		}
		
		public int getB() {
			return this.b;
		}
		
		public int getAlpha() {
			return this.a;
		}
		
		public void setR(int r) {
			this.r = r;
		}
		
		public void setG(int g) {
			this.g = g;
		}
		
		public void setB(int b) {
			this.b = b;
		}
		
		public void setAlpha(int a) {
			this.a = a;
		}
		
		public void setRGB(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
		
		public void setRGBA(int r, int g, int b, int a) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}
	}
}
