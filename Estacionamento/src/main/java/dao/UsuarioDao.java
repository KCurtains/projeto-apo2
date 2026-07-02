package dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import Enum.SexoEnum;
import model.Usuario;
import util.dbConnection;

public class UsuarioDao {

	// Cadastro: aplica o hash UMA vez, aqui no DAO. (O servlet deve mandar a senha PURA.)
	public boolean cadastrarUsuario(Usuario usuario) {
		String sql = "{CALL AdicionarUsuario(?,?,?,?,?,?,?)}";

		try (Connection conn = dbConnection.getConnection();
			CallableStatement stmt = conn.prepareCall(sql)) {

			stmt.setString(1, usuario.getCpf());
			stmt.setString(2, usuario.getNome());
			stmt.setString(3, usuario.getSexo().name());
			stmt.setObject(4, usuario.getDataNascimento());
			stmt.setString(5, usuario.getEmail());
			stmt.setString(6, usuario.getTelefone());
			stmt.setString(7, gerarHash(usuario.getSenha())); // hash aplicado só aqui

			stmt.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Login: SELECT direto no Usuario (não depende de procedure inexistente).
	// Compara o hash da senha digitada com o hash guardado no banco.
	public Usuario autenticarUsuario(String email, String senha) {
		String sql = "SELECT Id, CPF, Nome, Sexo, DataNascimento, Email, Telefone "
				   + "FROM Usuario WHERE Email = ? AND Senha = ?";

		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, email);
			stmt.setString(2, gerarHash(senha)); // hash simples, bate com o cadastro

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Usuario usuario = new Usuario(0, null, null, null, null, null, null, null);
					usuario.setId(rs.getInt("Id"));
					usuario.setCpf(rs.getString("CPF"));
					usuario.setNome(rs.getString("Nome"));

					String sexoStr = rs.getString("Sexo");
					if (sexoStr != null) {
						usuario.setSexo(SexoEnum.valueOf(sexoStr));
					}

					usuario.setDataNascimento(rs.getObject("DataNascimento", LocalDate.class));
					usuario.setEmail(rs.getString("Email"));
					usuario.setTelefone(rs.getString("Telefone"));
					return usuario;
				}
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String gerarHash(String texto) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Erro ao gerar hash SHA-256", e);
		}
	}

	// UpdateUsuario(Id, Nome, Sexo, DataNascimento, Email, Telefone, Senha)
	// CORRIGIDO: o 1º parâmetro é o Id (antes era enviado o CPF por engano).
	public void atualizarUsuario(Usuario usuario) {
		String sql = "{CALL UpdateUsuario(?,?,?,?,?,?,?)}";

		try (Connection conn = dbConnection.getConnection();
			 CallableStatement stmt = conn.prepareCall(sql)) {

			stmt.setInt(1, usuario.getId());
			stmt.setString(2, usuario.getNome());
			stmt.setString(3, usuario.getSexo().name());
			stmt.setObject(4, usuario.getDataNascimento());
			stmt.setString(5, usuario.getEmail());
			stmt.setString(6, usuario.getTelefone());
			stmt.setString(7, gerarHash(usuario.getSenha()));

			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// CORRIGIDO: a procedure no banco chama-se RemoveUsuario (sem "r").
	public void removerUsuario(Usuario usuario) {
		String sql = "{CALL RemoveUsuario(?)}";

		try (Connection conn = dbConnection.getConnection();
			 CallableStatement stmt = conn.prepareCall(sql)) {

			stmt.setInt(1, usuario.getId());
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Retorna o Id do usuário dono do e-mail (ou null se não existir).
	public Integer buscarIdPorEmail(String email) {
		String sql = "SELECT Id FROM Usuario WHERE Email = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) return rs.getInt("Id");
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Redefine a senha (aplica o hash aqui, mantendo a regra de hash único no DAO).
	public boolean atualizarSenha(int id, String novaSenha) {
		String sql = "UPDATE Usuario SET Senha = ? WHERE Id = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, gerarHash(novaSenha));
			stmt.setInt(2, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Atualiza apenas o e-mail (usado nas telas de "editar 1 campo").
	public boolean atualizarEmail(int id, String novoEmail) {
		String sql = "UPDATE Usuario SET Email = ? WHERE Id = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, novoEmail);
			stmt.setInt(2, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Atualiza um único campo simples (Nome ou Telefone). 'coluna' é sempre um valor
	// fixo escolhido pelo próprio servlet (whitelist), nunca texto vindo direto do usuário.
	public boolean atualizarCampoSimples(int id, String coluna, String valor) {
		if (!"Nome".equals(coluna) && !"Telefone".equals(coluna)) {
			throw new IllegalArgumentException("Campo não permitido: " + coluna);
		}
		String sql = "UPDATE Usuario SET " + coluna + " = ? WHERE Id = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, valor);
			stmt.setInt(2, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Confere se a senha informada bate com a senha atual do usuário (hash).
	public boolean confirmaSenhaAtual(int id, String senhaTentativa) {
		String sql = "SELECT Id FROM Usuario WHERE Id = ? AND Senha = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			stmt.setString(2, gerarHash(senhaTentativa));
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Confere se o e-mail informado é de fato o e-mail atual do usuário.
	public boolean confirmaEmailAtual(int id, String emailTentativa) {
		String sql = "SELECT Id FROM Usuario WHERE Id = ? AND Email = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			stmt.setString(2, emailTentativa);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
