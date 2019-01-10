package com.letmesee.service;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.letmesee.entity.Imagem;
import com.letmesee.repositorio.ImagemRepositorio;
import com.letmesee.service.filtros.AjusteRGBFacade;
import com.letmesee.service.filtros.BlurFacade;
import com.letmesee.service.filtros.BrilhoFacade;
import com.letmesee.service.filtros.ContrasteFacade;
import com.letmesee.service.filtros.CortarFacade;
import com.letmesee.service.filtros.DilatarFacade;
import com.letmesee.service.filtros.EqualizarCanalFacade;
import com.letmesee.service.filtros.EqualizarHistogramaFacade;
import com.letmesee.service.filtros.ErodirFacade;
import com.letmesee.service.filtros.EscalaDeCinzaFacade;
import com.letmesee.service.filtros.EspelhoHorizontalFacade;
import com.letmesee.service.filtros.EspelhoVerticalFacade;
import com.letmesee.service.filtros.ExtrairCanalFacade;
import com.letmesee.service.filtros.GaussianBlurFacade;
import com.letmesee.service.filtros.LimiarFacade;
import com.letmesee.service.filtros.MedianaFacade;
import com.letmesee.service.filtros.NegativoFacade;
import com.letmesee.service.filtros.NitidezFacade;
import com.letmesee.service.filtros.PixelateFacade;
import com.letmesee.service.filtros.PrewittFacade;
import com.letmesee.service.filtros.RedimensionarFacade;
import com.letmesee.service.filtros.RobertsCrossFacade;
import com.letmesee.service.filtros.RotacaoAntiHorariaFacade;
import com.letmesee.service.filtros.RotacaoHorariaFacade;
import com.letmesee.service.filtros.SobelFacade;
import com.letmesee.service.filtros.TrocarCanaisFacade;

@Service
public class ImagemService {
	@Autowired
	private ImagemRepositorio imgRepositorio;

	public Imagem uploadImage(String nome, String formato, String conteudoBase64) {
		BufferedImage image = FiltrosUtils.getInstancia().base64toBufferedImage(conteudoBase64);
		Imagem imagemCarregada = new Imagem(image.getWidth(), image.getHeight(), formato, nome, conteudoBase64);
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
		switch (filtro) {
		
		//seção de filtros
		case "negativo":
			saida = NegativoFacade.getInstancia().Processar(img);
			break;
		case "escala_cinza":
			saida = EscalaDeCinzaFacade.getInstancia().Processar(img);
			break;
		case "limiar":
			saida = LimiarFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "dilatar":
			saida = DilatarFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "erodir":
			saida = ErodirFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "extrair_canal":
			saida = ExtrairCanalFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "trocar_canais":
			saida = TrocarCanaisFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "pixelate":
			saida = PixelateFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		//fim da seção de filtros
			
		//seção de ajuste
		case "contraste":
			saida = ContrasteFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "brilho":
			saida = BrilhoFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "ajusteRGB":
			saida = AjusteRGBFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "nitidez":
			saida = NitidezFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "mediana":
			saida = MedianaFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "blur":
			saida = BlurFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "gaussian_blur":
			saida = GaussianBlurFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "equalizar_histograma":
			saida = EqualizarHistogramaFacade.getInstancia().Processar(img);
			break;
		case "equalizar_canal":
			saida = EqualizarCanalFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		//fim da seção de ajuste
			
		//seção de transformar
		case "redimensionar":
			saida = RedimensionarFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		case "espelhoH":
			saida = EspelhoHorizontalFacade.getInstancia().Processar(img);
			break;
		case "espelhoV":
			saida = EspelhoVerticalFacade.getInstancia().Processar(img);
			break;
		case "girarH":
			saida = RotacaoHorariaFacade.getInstancia().Processar(img);
			break;
		case "girarAH":
			saida = RotacaoAntiHorariaFacade.getInstancia().Processar(img);
			break;
		case "cortar":
			saida = CortarFacade.getInstancia().Processar(img, parametrosFiltro);
			break;
		//fim da seção de transformar
		
		//seção bordas
		case "prewitt":
			saida = PrewittFacade.getInstancia().Processar(img);
			break;
		case "roberts":
			saida = RobertsCrossFacade.getInstancia().Processar(img);
			break;
		case "sobel":
			saida = SobelFacade.getInstancia().Processar(img);
			break;
		//fim da seção bordas
			
		default:
			break;
		}
		
		imgRepositorio.save(saida);
		return saida;
	}
}
