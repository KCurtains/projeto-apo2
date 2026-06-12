package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import model.Veiculo;
import util.dbConnection;

public class VeiculoDao {
	
	public void adicionarVeiculo(Veiculo veiculo) {
		
		String sql = "{CALL AdicionarVeiculo(?,?,?,?,?}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)) {
			
			stmt.setString(1, veiculo.getPlaca());
			stmt.setString(2, veiculo.getModelo());
			stmt.setString(3, veiculo.getCor());
			stmt.setObject(4, veiculo.getMotoristaPrincipal());
			stmt.setString(5, veiculo.getTipoVeiculo().name());
			
			stmt.execute();
			
		}catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar veículo: " + e.getMessage(), e);
        }
	}
	
	public void removerVeiculo(Veiculo veiculo) {
		
		String sql = "{CALL RemoverVeiculo(?)}";
		
		try (Connection conn = dbConnection.getConnection();
		         CallableStatement stmt = conn.prepareCall(sql)) {
				
				stmt.setInt(1, veiculo.getId());
				
				stmt.execute();
				
			}catch (SQLException e) {
	            throw new RuntimeException("Erro ao adicionar veículo: " + e.getMessage(), e);
	        }
	}
	
	public void atualizarVeiculo(Veiculo veiculo) {
		
		String sql = "{CALL UpdateVeiculo(?,?,?,?,?)}";
		
		try (Connection conn = dbConnection.getConnection();
		         CallableStatement stmt = conn.prepareCall(sql)) {
				
				stmt.setInt(1, veiculo.getId());
				stmt.setString(2, veiculo.getModelo());
				stmt.setString(3, veiculo.getCor());
				stmt.setInt(4, veiculo.getMotoristaPrincipal().getId());
				stmt.setString(5, veiculo.getTipoVeiculo().name());
				
				stmt.execute();
				
			}catch (SQLException e) {
	            throw new RuntimeException("Erro ao adicionar veículo: " + e.getMessage(), e);
	        }
	}

}
