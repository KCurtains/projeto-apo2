package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import model.Patio;
import util.dbConnection;

public class PatioDao {
	
	public void adicionarPatio(Patio patio) {
		String sql = "{CALL AdicionarPatio(?,?,?,?)}";

		try (Connection conn = dbConnection.getConnection()) {
			try (CallableStatement stmt = conn.prepareCall(sql)) {
				stmt.setString(1,  patio.getEndereco());
	            stmt.setInt(2, patio.getCapacidadeCarro());
	            stmt.setInt(3, patio.getCapacidadeMoto());
	            stmt.setInt(4, patio.getCapacidadeCaminhao());
				stmt.execute();
			}

			int novoId;
			try (PreparedStatement idStmt = conn.prepareStatement("SELECT LAST_INSERT_ID()");
				 ResultSet rs = idStmt.executeQuery()) {
				rs.next();
				novoId = rs.getInt(1);
			}
			sincronizarVagas(conn, novoId);
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}

	public void atualizarPatio(Patio patio) {
		String sql = "{CALL AtualizarPatio(?,?,?,?,?)}";

		try (Connection conn = dbConnection.getConnection()) {
			try (CallableStatement stmt = conn.prepareCall(sql)) {
            	stmt.setInt(1, patio.getId());
            	stmt.setString(2, patio.getEndereco());
	            stmt.setInt(3, patio.getCapacidadeCarro());
	            stmt.setInt(4, patio.getCapacidadeMoto());
	            stmt.setInt(5, patio.getCapacidadeCaminhao());
				stmt.execute();
			}


			sincronizarVagas(conn, patio.getId());
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}

	private void sincronizarVagas(Connection conn, int patioId) throws SQLException {
		int capCarro = 0, capMoto = 0, capCaminhao = 0;
		try (PreparedStatement stmt = conn.prepareStatement(
				"SELECT CapacidadeCarro, CapacidadeMoto, CapacidadeCaminhao FROM Patio WHERE Id = ?")) {
			stmt.setInt(1, patioId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					capCarro = rs.getInt("CapacidadeCarro");
					capMoto = rs.getInt("CapacidadeMoto");
					capCaminhao = rs.getInt("CapacidadeCaminhao");
				}
			}
		}
		criarVagasFaltantes(conn, patioId, "CARRO", capCarro);
		criarVagasFaltantes(conn, patioId, "MOTO", capMoto);
		criarVagasFaltantes(conn, patioId, "CAMINHAO", capCaminhao);
	}

	private void criarVagasFaltantes(Connection conn, int patioId, String tipo, int capacidade) throws SQLException {
		int existentes = 0;
		try (PreparedStatement stmt = conn.prepareStatement(
				"SELECT COUNT(*) FROM Vaga WHERE PatioId = ? AND Tipo = ?")) {
			stmt.setInt(1, patioId);
			stmt.setString(2, tipo);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) existentes = rs.getInt(1);
			}
		}

		int faltam = capacidade - existentes;
		if (faltam <= 0) return;

		try (PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO Vaga (Tipo, StatusVaga, PatioId) VALUES (?, 'DISPONIVEL', ?)")) {
			for (int i = 0; i < faltam; i++) {
				stmt.setString(1, tipo);
				stmt.setInt(2, patioId);
				stmt.addBatch();
			}
			stmt.executeBatch();
		}
	}
	
	public void removerPatio(Patio patio) {
		String sql = "{CALL RemoverPatio(?)}";

		try (Connection conn = dbConnection.getConnection()) {

			try (PreparedStatement del = conn.prepareStatement("DELETE FROM Vaga WHERE PatioId = ?")) {
				del.setInt(1, patio.getId());
				del.executeUpdate();
			}
			try (CallableStatement stmt = conn.prepareCall(sql)) {
            	stmt.setInt(1, patio.getId());
				stmt.execute();
			}
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}

	public List<Patio> listarPatios() {
	    List<Patio> lista = new ArrayList<>();
	    String sql = "SELECT * FROM Patio"; 
	    
	    try (Connection conn = dbConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	         
	        while (rs.next()) {
	            Patio p = new Patio(
	                rs.getInt("Id"),
	                rs.getString("Endereco"),
	                rs.getInt("CapacidadeCarro"),
	                rs.getInt("CapacidadeMoto"),
	                rs.getInt("CapacidadeCaminhao")
	            );
	            lista.add(p);
	        }
	    } catch (SQLException e) {
	        throw new RuntimeException(e);
	    }
	    return lista;
	}

	
	public int verificarDisponibilidadeVaga(Patio patio, String tipoVeiculo) {
        String sql = "{CALL VerificarDisponibilidadeVaga(?, ?, ?)}";
        int vagasDisponiveis = 0;
        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, patio.getId());
            stmt.setString(2, tipoVeiculo);
            stmt.registerOutParameter(3, Types.INTEGER); // Lendo o OUT 
            
            stmt.execute();
            vagasDisponiveis = stmt.getInt(3);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return vagasDisponiveis;
    }
}
