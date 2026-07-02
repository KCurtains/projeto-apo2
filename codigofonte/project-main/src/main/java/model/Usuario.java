package model;

import java.time.LocalDate;

import Enum.SexoEnum;

public class Usuario {
	
	public int Id;
	public String Cpf;
	public String Nome;
	public SexoEnum Sexo;
	public LocalDate DataNascimento;
	public String Email;
	public String Telefone;
	public String Senha; //Hash
	
	public Usuario(int id, String cpf, String nome, SexoEnum sexo, LocalDate dataNascimento, String email,
			String telefone, String senha) {
		super();
		Id = id;
		Cpf = cpf;
		Nome = nome;
		Sexo = sexo;
		DataNascimento = dataNascimento;
		Email = email;
		Telefone = telefone;
		Senha = senha;
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getCpf() {
		return Cpf;
	}
	public void setCpf(String cpf) {
		Cpf = cpf;
	}
	public String getNome() {
		return Nome;
	}
	public void setNome(String nome) {
		Nome = nome;
	}
	public SexoEnum getSexo() {
		return Sexo;
	}
	public void setSexo(SexoEnum sexo) {
		Sexo = sexo;
	}
	public LocalDate getDataNascimento() {
		return DataNascimento;
	}
	public void setDataNascimento(LocalDate dataNascimento) {
		DataNascimento = dataNascimento;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getTelefone() {
		return Telefone;
	}
	public void setTelefone(String telefone) {
		Telefone = telefone;
	}
	public String getSenha() {
		return Senha;
	}
	public void setSenha(String senha) {
		Senha = senha;
	}
	
	
	
}
