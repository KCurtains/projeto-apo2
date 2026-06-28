package model;

import Enum.StatusVagaEnum;
import Enum.TipoVagaEnum;

public class Vaga {

	public int Id;
	public TipoVagaEnum Tipo;
	public StatusVagaEnum StatusVaga;
	public Patio Patio;
	
	public Vaga() {
		super();
	}
	
	public Vaga(int id, TipoVagaEnum tipo, StatusVagaEnum statusVaga, model.Patio patio) {
		super();
		Id = id;
		Tipo = tipo;
		StatusVaga = statusVaga;
		Patio = patio;
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public TipoVagaEnum getTipo() {
		return Tipo;
	}
	public void setTipo(TipoVagaEnum tipo) {
		Tipo = tipo;
	}
	public StatusVagaEnum getStatusVaga() {
		return StatusVaga;
	}
	public void setStatusVaga(StatusVagaEnum statusVaga) {
		StatusVaga = statusVaga;
	}
	public Patio getPatio() {
		return Patio;
	}
	public void setPatio(Patio patio) {
		Patio = patio;
	}
	
	
	
}
