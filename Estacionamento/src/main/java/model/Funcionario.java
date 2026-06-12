package model;

import java.time.LocalDate;

import Enum.SexoEnum;

public class Funcionario extends Usuario{

	public Funcionario(int id, String cpf, String nome, SexoEnum sexo, LocalDate dataNascimento, String email,
			String telefone, String senha) {
		super(id, cpf, nome, sexo, dataNascimento, email, telefone, senha);
	}

	
}
