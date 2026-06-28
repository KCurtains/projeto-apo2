package model;

import java.time.LocalDateTime;

import Enum.StatusReservaEnum;

public class Reserva {
	
	public int Id;
	public LocalDateTime HorarioEntrada;
	public LocalDateTime HorarioSaida;
	public Float Valor;
	public StatusReservaEnum StatusReserva;
	public Patio Patio;
	public Veiculo Veiculo;
	public Vaga Vaga;
	
	public Reserva() {
		super();

	}
	
	public Reserva(int id, LocalDateTime horarioEntrada, LocalDateTime horarioSaida, Float valor,
			StatusReservaEnum statusReserva, model.Patio patio, model.Veiculo veiculo, model.Vaga vaga) {
		super();
		Id = id;
		HorarioEntrada = horarioEntrada;
		HorarioSaida = horarioSaida;
		Valor = valor;
		StatusReserva = statusReserva;
		Patio = patio;
		Veiculo = veiculo;
		Vaga = vaga;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public LocalDateTime getHorarioEntrada() {
		return HorarioEntrada;
	}

	public void setHorarioEntrada(LocalDateTime horarioEntrada) {
		HorarioEntrada = horarioEntrada;
	}

	public LocalDateTime getHorarioSaida() {
		return HorarioSaida;
	}

	public void setHorarioSaida(LocalDateTime horarioSaida) {
		HorarioSaida = horarioSaida;
	}

	public Float getValor() {
		return Valor;
	}

	public void setValor(Float valor) {
		Valor = valor;
	}

	public StatusReservaEnum getStatusReserva() {
		return StatusReserva;
	}

	public void setStatusReserva(StatusReservaEnum statusReserva) {
		StatusReserva = statusReserva;
	}

	public Patio getPatio() {
		return Patio;
	}

	public void setPatio(Patio patio) {
		Patio = patio;
	}

	public Veiculo getVeiculo() {
		return Veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		Veiculo = veiculo;
	}

	public Vaga getVaga() {
		return Vaga;
	}

	public void setVaga(Vaga vaga) {
		Vaga = vaga;
	}
	
	
	
}
