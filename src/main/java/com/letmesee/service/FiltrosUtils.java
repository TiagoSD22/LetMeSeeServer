package com.letmesee.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.inject.Singleton;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

@Singleton
public class FiltrosUtils {
	
	public final String FORMATO_JPG  = "jpg";
	public final String FORMATO_BMP  = "bmp";
	public final String FORMATO_PNG  = "png";
	public final String FORMATO_GIF  = "gif";
	
	private static FiltrosUtils instancia;
	private FiltrosUtils() {}
	
	public static synchronized FiltrosUtils getInstancia() {
		if(instancia == null) {
			instancia = new FiltrosUtils();
		}
		return instancia;
	}
	
	public void printPixelMap(BufferedImage img, String formato, String nome) {
		int i,j,r,g,b;
		Pixel p;
		FileWriter fileWriter = null;
		String fnome = "pixels" + nome + ".txt";
		File f = new File(fnome);
		try {
			f.createNewFile();
			fileWriter = new FileWriter(f);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    PrintWriter printWriter = new PrintWriter(fileWriter);
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = getPixel(img, i, j, formato);
				r = p.getR();
				g = p.getG(); 
				b = p.getB();
				printWriter.printf(r + "," + g + "," + b + " ");
			}
			printWriter.printf("\n");
		}
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
	
	public Mat BufferedImage2Mat(BufferedImage image, String formato)  {
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    try {
	    	if(formato.equals("gif")) {
	    		ImageIO.write(image, "jpg", byteArrayOutputStream);
	    	}
	    	else {
	    		ImageIO.write(image, formato, byteArrayOutputStream);
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    try {
			byteArrayOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
	}
	
	public BufferedImage Mat2BufferedImage(Mat matrix, String formato) {
	    MatOfByte mob=new MatOfByte();
	    if(formato.equals("gif")) {
	    	Imgcodecs.imencode(".jpg", matrix, mob);
	    }
	    else {
	    	Imgcodecs.imencode(".".concat(formato), matrix, mob);
	    }
	    try {
			return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
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
	
	public int getIntensidade(BufferedImage img, int i, int j, String formato) {
		int r,g,b;
		Pixel p = getPixel(img, i, j, formato);
		r = p.getR();
		g = p.getG();
		b = p.getB();
		return ((int) (r * 0.299 + g * 0.587 + b* 0.114));
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
		return v > 255 ? 255 : (v < 0 ? 0 : v);
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
	
	public int[] getRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
		int type = image.getType();
		if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
			return (int [])image.getRaster().getDataElements( x, y, width, height, pixels );
		return image.getRGB( x, y, width, height, pixels, 0, width );
    }
	
	public void setRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
		int type = image.getType();
		if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
			image.getRaster().setDataElements( x, y, width, height, pixels );
		else
			image.setRGB( x, y, width, height, pixels, 0, width );
    }
	
	public int encontrarValorMedioR(BufferedImage img, String formato) {
		int i,j,mediaR;
		mediaR = 0;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				mediaR += getRed(img, i, j, formato);
			}
		}
		return mediaR / (img.getHeight() * img.getWidth());
	}
	
	public int encontrarValorMedioG(BufferedImage img, String formato) {
		int i,j,mediaG;
		mediaG = 0;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				mediaG += getGreen(img, i, j, formato);
			}
		}
		return mediaG / (img.getHeight() * img.getWidth());
	}
	
	public int encontrarValorMedioB(BufferedImage img, String formato) {
		int i,j,mediaB;
		mediaB = 0;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				mediaB += getBlue(img, i, j, formato);
			}
		}
		return mediaB / (img.getHeight() * img.getWidth());
	}
	
	public int encontrarValorMedioRG(BufferedImage img, String formato) {
		int i,j,r,g,media;
		Pixel p;
		media = 0;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = getPixel(img, i, j, formato);
				r = p.getR();
				g = p.getG();
				media += ((int) (r * 0.299 + g * 0.587));
			}
		}
		return media / (img.getHeight() * img.getWidth());
	}
	
	public int encontrarValorMedioRB(BufferedImage img, String formato) {
		int i,j,r,b,media;
		Pixel p;
		media = 0;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = getPixel(img, i, j, formato);
				r = p.getR();
				b = p.getB();
				media += ((int) (r * 0.299 + b* 0.114));
			}
		}
		return media / (img.getHeight() * img.getWidth());
	}
	
	public int encontrarValorMedioGB(BufferedImage img, String formato) {
		int i,j,g,b,media;
		Pixel p;
		media = 0;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = getPixel(img, i, j, formato);
				g = p.getG();
				b = p.getB();
				media += ((int) (g * 0.587 + b* 0.114));
			}
		}
		return media / (img.getHeight() * img.getWidth());
	}
	
	public int encontrarValorMedioRGB(BufferedImage img, String formato) {
		int i,j,r,g,b,media;
		Pixel p;
		media = 0;
		for(i = 0; i < img.getHeight(); i++) {
			for(j = 0; j < img.getWidth(); j++) {
				p = getPixel(img, i, j, formato);
				r = p.getR();
				g = p.getG();
				b = p.getB();
				media += ((int) (r * 0.299 + g * 0.587 + b* 0.114));
			}
		}
		return media / (img.getHeight() * img.getWidth());
	}
	
	public int encontrarIntensidadeMedia(BufferedImage img, String formato, String canais) {
		int media;
		switch(canais) {
		case "R":
			media = encontrarValorMedioR(img, formato);
			break;
		case "G":
			media = encontrarValorMedioG(img, formato);
			break;
		case "B":
			media = encontrarValorMedioB(img, formato);
			break;
		case "RG":
			media = encontrarValorMedioRG(img, formato);
			break;
		case "RB":
			media = encontrarValorMedioRB(img, formato);
			break;
		case "GB":
			media = encontrarValorMedioGB(img, formato);
			break;
		case "RGB":
			media = encontrarValorMedioRGB(img, formato);
			break;
		default:
			media = -1;
			break;
		}
		return media;
	}
	
	public BufferedImage devolverTransparencia(BufferedImage src, BufferedImage dst) {
		int i,j,a;
		Pixel p;
		for(i = 0; i < src.getHeight(); i++) {
			for(j = 0; j < src.getWidth(); j++) {
				p = getPixel(dst, i, j, FORMATO_PNG);
				a = getAlpha(src, i, j, FORMATO_PNG);
				p.setAlpha(a);
				setPixel(src, i, j, p, FORMATO_PNG);
			}
		}
		return src;
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
