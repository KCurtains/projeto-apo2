package model;

import Enum.StatusMultaEnum;

public class Multa {

	
	public int Id;
	public Float Valor;
	public String Motivo;
	public StatusMultaEnum StatusMulta;
	public RegistroEstadia EstadiaRelacionada;
	
	public Multa(int id, Float valor, String motivo, StatusMultaEnum statusMulta, RegistroEstadia estadiaRelacionada) {
		super();
		Id = id;
		Valor = valor;
		Motivo = motivo;
		StatusMulta = statusMulta;
		EstadiaRelacionada = estadiaRelacionada;
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public Float getValor() {
		return Valor;
	}
	public void setValor(Float valor) {
		Valor = valor;
	}
	public String getMotivo() {
		return Motivo;
	}
	public void setMotivo(String motivo) {
		Motivo = motivo;
	}
	public StatusMultaEnum getStatusMulta() {
		return StatusMulta;
	}
	public void setStatusMulta(StatusMultaEnum statusMulta) {
		StatusMulta = statusMulta;
	}
	public RegistroEstadia getEstadiaRelacionada() {
		return EstadiaRelacionada;
	}
	public void setEstadiaRelacionada(RegistroEstadia estadiaRelacionada) {
		EstadiaRelacionada = estadiaRelacionada;
	}
	
	
	
}
