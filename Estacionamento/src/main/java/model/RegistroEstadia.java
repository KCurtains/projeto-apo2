package model;

import java.time.LocalDateTime;

public class RegistroEstadia {
	
	public int Id;
	public LocalDateTime HorarioEntradaReal;
	public LocalDateTime HorarioSaidaReal;
	public Reserva Reserva;
	
	public RegistroEstadia() {
        super();
    }
	public RegistroEstadia(int id, LocalDateTime horarioEntradaReal, LocalDateTime horarioSaidaReal,
			model.Reserva reserva) {
		super();
		Id = id;
		HorarioEntradaReal = horarioEntradaReal;
		HorarioSaidaReal = horarioSaidaReal;
		Reserva = reserva;
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public LocalDateTime getHorarioEntradaReal() {
		return HorarioEntradaReal;
	}
	public void setHorarioEntradaReal(LocalDateTime horarioEntradaReal) {
		HorarioEntradaReal = horarioEntradaReal;
	}
	public LocalDateTime getHorarioSaidaReal() {
		return HorarioSaidaReal;
	}
	public void setHorarioSaidaReal(LocalDateTime horarioSaidaReal) {
		HorarioSaidaReal = horarioSaidaReal;
	}
	public Reserva getReserva() {
		return Reserva;
	}
	public void setReserva(Reserva reserva) {
		Reserva = reserva;
	}
	
	

}
