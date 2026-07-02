package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Gerente;
import util.dbConnection;

public class GerenteDao {

	// CORRIGIDO: dados do gerente vivem em Usuario. GERENTE só guarda o Id.
	public Gerente validarLogin(String email, String senha) {
		String sql = "SELECT u.Id, u.CPF, u.Nome, u.Email, u.Telefone "
				   + "FROM Usuario u INNER JOIN GERENTE g ON u.Id = g.Id "
				   + "WHERE u.Email = ? AND u.Senha = ?";

		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, email);
			stmt.setString(2, UsuarioDao.gerarHash(senha));

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return new Gerente(
						rs.getInt("Id"),
						rs.getString("CPF"),
						rs.getString("Nome"),
						null,
						null,
						rs.getString("Email"),
						rs.getString("Telefone"),
						null
					);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao validar login do gerente", e);
		}
		return null;
	}

	public boolean atualizarPerfilSimples(int id, String nome, String telefone) {
		String sql = "UPDATE Usuario SET Nome = ?, Telefone = ? WHERE Id = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, nome);
			stmt.setString(2, telefone);
			stmt.setInt(3, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao atualizar perfil do gerente", e);
		}
	}

	public boolean atualizarPerfilComplexo(int id, String email, String novaSenha) {
		String sql = "UPDATE Usuario SET Email = ?, Senha = ? WHERE Id = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);
			stmt.setString(2, UsuarioDao.gerarHash(novaSenha));
			stmt.setInt(3, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao atualizar credenciais do gerente", e);
		}
	}
}
