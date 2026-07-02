package dao;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Cliente;
import util.dbConnection;

public class ClienteDao {

	// CORRIGIDO: usa a procedure AdicionaCliente (cria Usuario + Cliente, 8 parâmetros)
	// e aplica o hash na senha, igual ao restante do sistema.
	public boolean cadastrarCliente(Cliente cliente) {
		String sql = "{CALL AdicionaCliente(?,?,?,?,?,?,?,?)}";

		try (Connection conn = dbConnection.getConnection();
			CallableStatement stmt = conn.prepareCall(sql)) {

			stmt.setString(1, cliente.getCpf());
			stmt.setString(2, cliente.getNome());
			stmt.setString(3, cliente.getSexo().name());
			stmt.setObject(4, cliente.getDataNascimento());
			stmt.setString(5, cliente.getEmail());
			stmt.setString(6, cliente.getTelefone());
			stmt.setString(7, UsuarioDao.gerarHash(cliente.getSenha())); // hash consistente
			stmt.setBoolean(8, cliente.getMensalista());

			stmt.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean mudarStatusMensalista(Cliente cliente) {
		var statusMensalista = !cliente.getMensalista();
		String sql = "{CALL UpdateCliente(?,?)}";

		try (Connection conn = dbConnection.getConnection();
			 CallableStatement stmt = conn.prepareCall(sql)) {

			stmt.setInt(1, cliente.getId());
			stmt.setBoolean(2, statusMensalista);
			int linhasAlteradas = stmt.executeUpdate();
			cliente.setMensalista(statusMensalista);
			return linhasAlteradas > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// (Removi o antigo listarReservasCliente: ele chamava UpdateCliente por engano.
	//  Para listar reservas do cliente, use ReservaDao.listarReservasPorCliente.)

	// Atualiza Nome e Telefone (dados ficam na tabela Usuario, Id compartilhado).
	public boolean atualizarPerfilSimples(int id, String nome, String telefone) {
		String sql = "UPDATE Usuario SET Nome = ?, Telefone = ? WHERE Id = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, nome);
			stmt.setString(2, telefone);
			stmt.setInt(3, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao atualizar perfil do cliente", e);
		}
	}

	// Atualiza E-mail e Senha (senha com hash).
	public boolean atualizarPerfilComplexo(int id, String email, String novaSenha) {
		String sql = "UPDATE Usuario SET Email = ?, Senha = ? WHERE Id = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);
			stmt.setString(2, UsuarioDao.gerarHash(novaSenha));
			stmt.setInt(3, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao atualizar credenciais do cliente", e);
		}
	}

	// Lê o status atual de mensalista, inverte e grava. Retorna o novo status.
	public boolean alternarMensalista(int clienteId) {
		boolean atual = false;
		String select = "SELECT Mensalista FROM Cliente WHERE Id = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(select)) {
			stmt.setInt(1, clienteId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) atual = rs.getBoolean("Mensalista");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao ler status de mensalista", e);
		}

		boolean novo = !atual;
		String sql = "{CALL UpdateCliente(?,?)}";
		try (Connection conn = dbConnection.getConnection();
			 CallableStatement stmt = conn.prepareCall(sql)) {
			stmt.setInt(1, clienteId);
			stmt.setBoolean(2, novo);
			stmt.executeUpdate();
			return novo;
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao alterar status de mensalista", e);
		}
	}

	// Busca um cliente já cadastrado pelo CPF (usado para autorizar um motorista em um veículo).
	public Cliente buscarClientePorCpf(String cpf) {
		String sql = "SELECT u.Id, u.CPF, u.Nome, u.Email, u.Telefone "
				   + "FROM Usuario u INNER JOIN Cliente c ON u.Id = c.Id "
				   + "WHERE u.CPF = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, cpf);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return new Cliente(rs.getInt("Id"), rs.getString("CPF"), rs.getString("Nome"),
							null, null, rs.getString("Email"), rs.getString("Telefone"), null, null, null);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao buscar cliente por CPF", e);
		}
		return null;
	}

	// Pesquisa clientes pelo nome (usado pela tela de "Clientes" do funcionário).
	public List<Cliente> pesquisarPorNome(String termo) {
		List<Cliente> lista = new ArrayList<>();
		String sql = "SELECT u.Id, u.CPF, u.Nome, u.Email, u.Telefone "
				   + "FROM Usuario u INNER JOIN Cliente c ON u.Id = c.Id "
				   + "WHERE u.Nome LIKE ? ORDER BY u.Nome";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, "%" + termo + "%");
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					lista.add(new Cliente(rs.getInt("Id"), rs.getString("CPF"), rs.getString("Nome"),
							null, null, rs.getString("Email"), rs.getString("Telefone"), null, null, null));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao pesquisar clientes", e);
		}
		return lista;
	}
}
