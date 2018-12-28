package com.letmesee.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmesee.entity.Imagem;
import com.letmesee.repositorio.ImagemRepositorio;

@Service
public class ImagemService {
	@Autowired
	private ImagemRepositorio imgRepositorio;
	
	public Imagem uploadImage(String nome, String formato, String conteudoBase64) {
		BufferedImage image = Filtros.getInstancia().base64toBufferedImage(conteudoBase64);
		Imagem imagemCarregada = new Imagem(image.getWidth(),image.getHeight(),formato,nome,conteudoBase64);
		imgRepositorio.save(imagemCarregada);
		return imagemCarregada;
	}
	
	public List<Imagem> obterHistorico() {
		return imgRepositorio.findAll();
	}
	
	public Optional<Imagem> obterImagem(int id) {
		return imgRepositorio.findById(id);
	}
	
	public void salvarImagem(Imagem img) {
		imgRepositorio.save(img);
	}
	
	public void excluirImagem(int id) {
		imgRepositorio.deleteById(id);
	}
	
	public void limparHistorico() {
		imgRepositorio.deleteAll();
	}
	
	public Imagem aplicarFiltro(String filtro, String parametrosFiltro, Imagem img) {
		Imagem saida = null;
		switch(filtro) {
		case "negativo":
			saida = Filtros.getInstancia().Negativo(img);
			break;
		case "limiar":
			ObjectMapper mapper = new ObjectMapper();
			JsonNode parametroJson;
			try {
				parametroJson = mapper.readTree(parametrosFiltro);
				saida = Filtros.getInstancia().Limiar(img, parametroJson.get("valor").asInt());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "ajusteRGB":
			ObjectMapper mapperRGB = new ObjectMapper();
			JsonNode parametroJsonRGB;
			try {
				parametroJsonRGB = mapperRGB.readTree(parametrosFiltro);
				saida = Filtros.getInstancia().AjusteRGB(img, parametroJsonRGB.get("valorR").asInt(),parametroJsonRGB.get("valorG").asInt(),parametroJsonRGB.get("valorB").asInt());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "roberts":
			saida = Filtros.getInstancia().RobertsCross(img);
			break;
		default:
			break;
		}
		imgRepositorio.save(saida);
		return saida;
	}
}
