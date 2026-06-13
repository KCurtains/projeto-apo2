package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import model.Patio;
import model.Veiculo;
import model.Cliente;
import model.Vaga;
import util.dbConnection;

public class VagaDao {
	
	public void DefiniVagaMensalista(Vaga vaga) {
		
		String sql = "{CALL DefinirVagasMensalistas(?)}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)){
			
			stmt.setInt(1, vaga.getId());
			
			stmt.execute();
		}catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar veículo: " + e.getMessage(), e);
        }
	}
	
	public void AlugarVagaMensal(Cliente cliente, Veiculo veiculo, Patio patio, Vaga vaga) {
		
		String sql = "{CALL AlugarVagaMensal(?,?,?,?,?)}";
		
		double valorMensalidade = 400.00;
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)){
			
			stmt.setInt(1, cliente.getId());
			stmt.setInt(2, veiculo.getId());
			stmt.setInt(3, patio.getId());
			stmt.setInt(4, vaga.getId());
			stmt.setDouble(5, valorMensalidade);
			
			stmt.execute();
		}catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar veículo: " + e.getMessage(), e);
        }
	}
}
