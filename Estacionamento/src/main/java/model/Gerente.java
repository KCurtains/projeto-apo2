package model;

import java.time.LocalDate;

import Enum.SexoEnum;

public class Gerente extends Usuario{

	public Gerente(int id, String cpf, String nome, SexoEnum sexo, LocalDate dataNascimento, String email,
			String telefone, String senha) {
		super(id, cpf, nome, sexo, dataNascimento, email, telefone, senha);
		// TODO Auto-generated constructor stub
	}

}
