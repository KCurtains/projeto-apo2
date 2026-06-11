package model;

import java.util.List;
import Enum.TipoVeiculoEnum;

public class Veiculo {

	public int Id;
	public String Placa;
	public String Modelo;
	public String Cor;
	public Cliente MotoristaPrincipal;
	public TipoVeiculoEnum TipoVeiculo;
	
	public List<Cliente> MotoristasAutorizados;

	public Veiculo(int id, String placa, String modelo, String cor, Cliente motoristaPrincipal,
			TipoVeiculoEnum tipoVeiculo, List<Cliente> motoristasAutorizados) {
		super();
		Id = id;
		Placa = placa;
		Modelo = modelo;
		Cor = cor;
		MotoristaPrincipal = motoristaPrincipal;
		TipoVeiculo = tipoVeiculo;
		MotoristasAutorizados = motoristasAutorizados;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getPlaca() {
		return Placa;
	}

	public void setPlaca(String placa) {
		Placa = placa;
	}

	public String getModelo() {
		return Modelo;
	}

	public void setModelo(String modelo) {
		Modelo = modelo;
	}

	public String getCor() {
		return Cor;
	}

	public void setCor(String cor) {
		Cor = cor;
	}

	public Cliente getMotoristaPrincipal() {
		return MotoristaPrincipal;
	}

	public void setMotoristaPrincipal(Cliente motoristaPrincipal) {
		MotoristaPrincipal = motoristaPrincipal;
	}

	public TipoVeiculoEnum getTipoVeiculo() {
		return TipoVeiculo;
	}

	public void setTipoVeiculo(TipoVeiculoEnum tipoVeiculo) {
		TipoVeiculo = tipoVeiculo;
	}

	public List<Cliente> getMotoristasAutorizados() {
		return MotoristasAutorizados;
	}

	public void setMotoristasAutorizados(List<Cliente> motoristasAutorizados) {
		MotoristasAutorizados = motoristasAutorizados;
	}
	
	
}
