package model;

public class Patio {

	public int Id;
	public String Endereco;
	public int CapacidadeCarro;
	public int CapacidadeMoto;
	public int CapacidadeCaminhao;
	
	public Patio(int id, String endereco, int capacidadeCarro, int capacidadeMoto, int capacidadeCaminhao) {
		super();
		Id = id;
		Endereco = endereco;
		CapacidadeCarro = capacidadeCarro;
		CapacidadeMoto = capacidadeMoto;
		CapacidadeCaminhao = capacidadeCaminhao;
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	
	public String getEndereco() {
		return Endereco;
	}

	public void setEndereco(String endereco) {
		Endereco = endereco;
	}

	public int getCapacidadeCarro() {
		return CapacidadeCarro;
	}
	public void setCapacidadeCarro(int capacidadeCarro) {
		CapacidadeCarro = capacidadeCarro;
	}
	public int getCapacidadeMoto() {
		return CapacidadeMoto;
	}
	public void setCapacidadeMoto(int capacidadeMoto) {
		CapacidadeMoto = capacidadeMoto;
	}
	public int getCapacidadeCaminhao() {
		return CapacidadeCaminhao;
	}
	public void setCapacidadeCaminhao(int capacidadeCaminhao) {
		CapacidadeCaminhao = capacidadeCaminhao;
	}
}
