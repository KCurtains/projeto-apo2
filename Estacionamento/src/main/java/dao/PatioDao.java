package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import model.Patio;
import util.dbConnection;

public class PatioDao {
	
	public void adicionarPatio(Patio patio) {
		String sql = "{CALL AdicionarPatio(?,?,?)}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)) {
	            stmt.setInt(1, patio.getCapacidadeCarro());
	            stmt.setInt(2, patio.getCapacidadeMoto());
	            stmt.setInt(3, patio.getCapacidadeCaminhao());
				stmt.execute();
	            
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}
	
	public void atualizarPatio(Patio patio) {
		String sql = "{CALL AtualizarPatio(?,?,?,?)}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)) {
            	stmt.setInt(1, patio.getId());
	            stmt.setInt(2, patio.getCapacidadeCarro());
	            stmt.setInt(3, patio.getCapacidadeMoto());
	            stmt.setInt(4, patio.getCapacidadeCaminhao());
				stmt.execute();
	            
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}
	
	public void removerPatio(Patio patio) {
		String sql = "{CALL RemoverPatio(?)}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)) {
            	stmt.setInt(1, patio.getId());

				stmt.execute();
	            
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
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
