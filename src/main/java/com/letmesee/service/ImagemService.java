package com.letmesee.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
		BufferedImage image = FiltrosUtils.getInstancia().base64toBufferedImage(conteudoBase64);
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
			saida = FiltrosFacade.getInstancia().Negativo(img);
			break;
		case "limiar":
			ObjectMapper mapper = new ObjectMapper();
			JsonNode parametroJson;
			try {
				parametroJson = mapper.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().Limiar(img, parametroJson.get("valor").asInt());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "ajusteRGB":
			ObjectMapper mapperRGB = new ObjectMapper();
			JsonNode parametroJsonRGB;
			try {
				parametroJsonRGB = mapperRGB.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().AjusteRGB(img, parametroJsonRGB.get("valorR").asInt(),parametroJsonRGB.get("valorG").asInt(),parametroJsonRGB.get("valorB").asInt());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "espelhoH":
			saida = FiltrosFacade.getInstancia().EspelhoHorizontal(img);
			break;
		case "espelhoV":
			saida = FiltrosFacade.getInstancia().EspelhoVertical(img);
			break;
		case "girarH":
			saida = FiltrosFacade.getInstancia().GirarHorario(img);
			break;
		case "girarAH":
			saida = FiltrosFacade.getInstancia().GirarAntiHorario(img);
			break;
		default:
			break;
		}
		imgRepositorio.save(saida);
		return saida;
	}
}
