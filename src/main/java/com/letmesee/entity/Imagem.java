package com.letmesee.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Imagem {
	@Id
	@GeneratedValue
	public int id;
	private String nome;
	private int largura;
	private int altura;
	private String tipo;
	@Column(name="conteudoBase64",columnDefinition="TEXT") 
	private String conteudoBase64;
	
	public int getLargura() {
		return largura;
	}
	public void setLargura(int largura) {
		this.largura = largura;
	}
	public int getAltura() {
		return altura;
	}
	public void setAltura(int altura) {
		this.altura = altura;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getConteudoBase64() {
		return conteudoBase64;
	}
	public void setConteudoBase64(String conteudoBase64) {
		this.conteudoBase64 = conteudoBase64;
	}
	
	public Imagem(int largura, int altura, String tipo, String nome, String conteudoBase64) {
		this.altura = altura;
		this.largura = largura;
		this.nome = nome;
		this.conteudoBase64 = conteudoBase64;
		this.tipo = tipo;
		if(this.tipo.equals("jpeg")) this.tipo = "jpg";
	}
	
	public Imagem() {}
}
