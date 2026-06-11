package model;

import java.time.LocalDateTime;

public class RelatorioMensal {
	
	public int Id;
	public LocalDateTime HorarioGerado;
	public Float Ganhos;
	public int QntdClientesCarro;
	public int QntdClientesMoto;
	public int QntdClientesCaminhao;
	public Float TempoMedioEstadia;
	public int ReclamacoesRegistradas;
	public int MultasAplicadas;
	
	public RelatorioMensal(int id, LocalDateTime horarioGerado, Float ganhos, int qntdClientesCarro,
			int qntdClientesMoto, int qntdClientesCaminhao, Float tempoMedioEstadia, int reclamacoesRegistradas,
			int multasAplicadas) {
		super();
		Id = id;
		HorarioGerado = horarioGerado;
		Ganhos = ganhos;
		QntdClientesCarro = qntdClientesCarro;
		QntdClientesMoto = qntdClientesMoto;
		QntdClientesCaminhao = qntdClientesCaminhao;
		TempoMedioEstadia = tempoMedioEstadia;
		ReclamacoesRegistradas = reclamacoesRegistradas;
		MultasAplicadas = multasAplicadas;
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public LocalDateTime getHorarioGerado() {
		return HorarioGerado;
	}
	public void setHorarioGerado(LocalDateTime horarioGerado) {
		HorarioGerado = horarioGerado;
	}
	public Float getGanhos() {
		return Ganhos;
	}
	public void setGanhos(Float ganhos) {
		Ganhos = ganhos;
	}
	public int getQntdClientesCarro() {
		return QntdClientesCarro;
	}
	public void setQntdClientesCarro(int qntdClientesCarro) {
		QntdClientesCarro = qntdClientesCarro;
	}
	public int getQntdClientesMoto() {
		return QntdClientesMoto;
	}
	public void setQntdClientesMoto(int qntdClientesMoto) {
		QntdClientesMoto = qntdClientesMoto;
	}
	public int getQntdClientesCaminhao() {
		return QntdClientesCaminhao;
	}
	public void setQntdClientesCaminhao(int qntdClientesCaminhao) {
		QntdClientesCaminhao = qntdClientesCaminhao;
	}
	public Float getTempoMedioEstadia() {
		return TempoMedioEstadia;
	}
	public void setTempoMedioEstadia(Float tempoMedioEstadia) {
		TempoMedioEstadia = tempoMedioEstadia;
	}
	public int getReclamacoesRegistradas() {
		return ReclamacoesRegistradas;
	}
	public void setReclamacoesRegistradas(int reclamacoesRegistradas) {
		ReclamacoesRegistradas = reclamacoesRegistradas;
	}
	public int getMultasAplicadas() {
		return MultasAplicadas;
	}
	public void setMultasAplicadas(int multasAplicadas) {
		MultasAplicadas = multasAplicadas;
	}

}
