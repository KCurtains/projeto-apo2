package model;

import java.time.LocalDateTime;

import Enum.SexoEnum;

public class Funcionario extends Usuario{

	public Funcionario(int id, String cpf, String nome, SexoEnum sexo, LocalDateTime dataNascimento, String email,
			String telefone, String senha) {
		super(id, cpf, nome, sexo, dataNascimento, email, telefone, senha);
	}

	
}
