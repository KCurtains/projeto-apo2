package model;

import Enum.StatusReclamacaoEnum;

public class Reclamacao {
	
	public int Id;
	public String Conteudo;
	public StatusReclamacaoEnum StatusReclamacao;
	public RegistroEstadia EstadiaRelacionada;
	
	public Reclamacao(int id, String conteudo, StatusReclamacaoEnum statusReclamacao,
			RegistroEstadia estadiaRelacionada) {
		super();
		Id = id;
		Conteudo = conteudo;
		StatusReclamacao = statusReclamacao;
		EstadiaRelacionada = estadiaRelacionada;
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getConteudo() {
		return Conteudo;
	}
	public void setConteudo(String conteudo) {
		Conteudo = conteudo;
	}
	public StatusReclamacaoEnum getStatusReclamacao() {
		return StatusReclamacao;
	}
	public void setStatusReclamacao(StatusReclamacaoEnum statusReclamacao) {
		StatusReclamacao = statusReclamacao;
	}
	public RegistroEstadia getEstadiaRelacionada() {
		return EstadiaRelacionada;
	}
	public void setEstadiaRelacionada(RegistroEstadia estadiaRelacionada) {
		EstadiaRelacionada = estadiaRelacionada;
	}
}
