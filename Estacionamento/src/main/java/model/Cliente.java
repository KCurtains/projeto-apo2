package model;

import java.time.LocalDateTime;
import java.util.List;

import Enum.SexoEnum;

public class Cliente extends Usuario{

	public Boolean Mensalista;
	public List<Veiculo> Veiculos;

	public Cliente(int id, String cpf, String nome, SexoEnum sexo, LocalDateTime dataNascimento, String email,
			String telefone, String senha, Boolean mensalista, List<Veiculo> veiculo) {
		super(id, cpf, nome, sexo, dataNascimento, email, telefone, senha);
		Mensalista = mensalista;
	}

	public Boolean getMensalista() {
		return Mensalista;
	}

	public void setMensalista(Boolean mensalista) {
		Mensalista = mensalista;
	}

	public List<Veiculo> getVeiculos() {
		return Veiculos;
	}

	public void setVeiculos(List<Veiculo> veiculos) {
		Veiculos = veiculos;
	}
	
	
	
}
