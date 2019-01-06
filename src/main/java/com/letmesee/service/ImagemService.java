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
		case "escala_cinza":
			saida = FiltrosFacade.getInstancia().EscalaDeCinza(img);
			break;
		case "limiar":
			ObjectMapper mapper = new ObjectMapper();
			JsonNode parametroJson;
			try {
				parametroJson = mapper.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().Limiar(img, parametroJson.get("valor").asInt(),parametroJson.get("usarMedia").asBoolean(),parametroJson.get("referencia").asText());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "dilatar":
			ObjectMapper mapperDilatar = new ObjectMapper();
			JsonNode parametroDilatar;
			try {
				parametroDilatar = mapperDilatar.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().Dilatar(img, parametroDilatar.get("w").asInt(),parametroDilatar.get("h").asInt());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "erodir":
			ObjectMapper mapperErodir = new ObjectMapper();
			JsonNode parametroErodir;
			try {
				parametroErodir = mapperErodir.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().Erodir(img, parametroErodir.get("w").asInt(),parametroErodir.get("h").asInt());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "blur":
			ObjectMapper mapperBlur = new ObjectMapper();
			JsonNode parametroBlur;
			try {
				parametroBlur = mapperBlur.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().Blur(img, parametroBlur.get("w").asInt(),parametroBlur.get("h").asInt());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "gaussian_blur":
			ObjectMapper mapperGBlur = new ObjectMapper();
			JsonNode parametroGBlur;
			try {
				parametroGBlur = mapperGBlur.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().GaussianBlur(img, parametroGBlur.get("w").asInt(),parametroGBlur.get("h").asInt(), parametroGBlur.get("x").asDouble(), parametroGBlur.get("y").asDouble());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "mediana":
			ObjectMapper mapperMediana = new ObjectMapper();
			JsonNode parametroMediana;
			try {
				parametroMediana = mapperMediana.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().Mediana(img, parametroMediana.get("k").asInt());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "sobel":
			saida = FiltrosFacade.getInstancia().Sobel(img);
			break;
		case "roberts":
			saida = FiltrosFacade.getInstancia().Roberts_Cross(img);
			break;
		case "prewitt":
			saida = FiltrosFacade.getInstancia().Prewitt(img);
			break;
		case "contraste":
			ObjectMapper mapperContraste = new ObjectMapper();
			JsonNode parametroContraste;
			try {
				parametroContraste = mapperContraste.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().AjustarContraste(img, parametroContraste.get("gamma").asDouble());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "trocar_canais":
			ObjectMapper mapperTrocaCanais = new ObjectMapper();
			JsonNode parametroTrocaCanais;
			try {
				parametroTrocaCanais = mapperTrocaCanais.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().TrocarCanais(img, parametroTrocaCanais.get("canais").asText());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "brilho":
			ObjectMapper mapperBrilho = new ObjectMapper();
			JsonNode parametroBrilho;
			try {
				parametroBrilho = mapperBrilho.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().AjustarBrilho(img, parametroBrilho.get("gain").asInt());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "equalizar_histograma":
			saida = FiltrosFacade.getInstancia().EqualizarHistograma(img);
			break;
		case "equalizar_canal":
			ObjectMapper mapperEqCanal = new ObjectMapper();
			JsonNode parametroJsonEqCanal;
			try {
				parametroJsonEqCanal = mapperEqCanal.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().EqualizarCanal(img, parametroJsonEqCanal.get("equalizarR").asBoolean(),
																	parametroJsonEqCanal.get("equalizarG").asBoolean(),
																	parametroJsonEqCanal.get("equalizarB").asBoolean(),
																	parametroJsonEqCanal.get("minR").asInt(),
																	parametroJsonEqCanal.get("maxR").asInt(),
																	parametroJsonEqCanal.get("minG").asInt(),
																	parametroJsonEqCanal.get("maxG").asInt(),
																	parametroJsonEqCanal.get("minB").asInt(),
																	parametroJsonEqCanal.get("maxB").asInt());
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
		case "extrair_canal":
			ObjectMapper mapperExtrair = new ObjectMapper();
			JsonNode parametroJsonExtrair;
			try {
				parametroJsonExtrair = mapperExtrair.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().ExtrairCanal(img, parametroJsonExtrair.get("R").asBoolean(),parametroJsonExtrair.get("G").asBoolean(),parametroJsonExtrair.get("B").asBoolean());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "redimensionar":
			ObjectMapper mapperResize = new ObjectMapper();
			JsonNode parametroJsonResize;
			try {
				parametroJsonResize = mapperResize.readTree(parametrosFiltro);
				saida = FiltrosFacade.getInstancia().Redimensionar(img, parametroJsonResize.get("w").asInt(),parametroJsonResize.get("h").asInt());
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
