package com.letmesee.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.letmesee.entity.Imagem;
import com.letmesee.service.ImagemService;

@RestController
@RequestMapping("/server")
@CrossOrigin(origins = "*",allowedHeaders = "*")
public class ControladorImagem {
	@Autowired
	private ImagemService imgService;
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ResponseEntity<Imagem> uploadFile(MultipartHttpServletRequest mrequest){
		Imagem imagemCarregada = imgService.uploadImage(mrequest.getParameter("nome"), mrequest.getParameter("formato"), mrequest.getParameter("conteudo").split(",")[1]);
		return new ResponseEntity<Imagem>(imagemCarregada, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/historico/obterHistorico", method = RequestMethod.GET)
	public ResponseEntity<List<Imagem>> getImagens(){
		return new ResponseEntity<List<Imagem>>(imgService.obterHistorico(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/historico/obterImagem/{id}", method = RequestMethod.GET)
	public ResponseEntity<Optional<Imagem>> getImagem(@PathVariable Integer id){
		Optional<Imagem> img = imgService.obterImagem(id);
		return new ResponseEntity<Optional<Imagem>>(img, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/historico/excluirImagem/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> excluirImagem(@PathVariable Integer id) {
		imgService.excluirImagem(id);
		return new ResponseEntity<String>("ok", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/historico/limparHistorico", method = RequestMethod.GET)
	public ResponseEntity<String> limparHistorico() {
		imgService.limparHistorico();
		return new ResponseEntity<String>("ok", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/aplicarFiltro/{filtro}/{parametros}", method = RequestMethod.POST,headers = "Accept=application/json")
	public ResponseEntity<Imagem> aplicarFiltro(@PathVariable("filtro") String filtro, @PathVariable("parametros") String parametros, @RequestBody Imagem img){
		Imagem saida = this.imgService.aplicarFiltro(filtro, parametros, img);
		return new ResponseEntity<Imagem>(saida, HttpStatus.OK);
	}
}