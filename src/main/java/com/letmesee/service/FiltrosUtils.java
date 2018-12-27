package com.letmesee.service;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.inject.Singleton;

@Singleton
public class FiltrosUtils {
	private static FiltrosUtils instancia;
	private FiltrosUtils() {}
	
	public static synchronized FiltrosUtils getInstancia() {
		if(instancia == null) {
			instancia = new FiltrosUtils();
		}
		return instancia;
	}
	
	public int getRed(BufferedImage img,int i, int j, String formato) {
		if(formato.equals(".jpg")) {
			Color c = new Color(img.getRGB(j,i));
			return c.getRed();
		}
		else if(formato.equals(".png")) {
			int p = img.getRGB(j, i);
		    return (p>>16) & 0xff;
		}
		else if(formato.equals(".bmp")) {
			Color c = new Color(img.getRGB(j,i));
			return c.getRed();
		}
		return -1;
	}
	
	public int getGreen(BufferedImage img, int i, int j, String formato) {
		if(formato.equals(".jpg")) {
			Color c = new Color(img.getRGB(j,i));
			return c.getGreen();
		}
		else if(formato.equals(".png")) {
			int p = img.getRGB(j, i);
		    return (p>>8) & 0xff;
		}
		else if(formato.equals(".bmp")) {
			Color c = new Color(img.getRGB(j,i));
			return c.getGreen();
		}
		return -1;
	}
	
	public void setPixel(BufferedImage img, int i, int j, int r, int g, int b, int a, String formato) {
		if(formato.equals(".jpg")) {
			Color c = new Color(r,g,b);
			img.setRGB(j,i,c.getRGB());
		}
		else if(formato.equals(".png")) {
			int p = (a<<24) | (r<<16) | (g<<8) | b;
			img.setRGB(j, i, p);
		}
		else if(formato.equals(".bmp")) {
			
		}
	}
}
