package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import model.Reclamacao;
import util.dbConnection;

public class ReclamacaoDao {

	
	public void adicionarReclamacao(Reclamacao reclamacao) {
		
		String sql = "{CALL AdicionarReclamacao(?,?)}";
		
		try(Connection conn = dbConnection.getConnection();
			CallableStatement stmt = conn.prepareCall(sql)){
			
			stmt.setString(1,  reclamacao.getConteudo());
			stmt.setInt(2,  reclamacao.getEstadiaRelacionada().getId());
			stmt.execute();
			
		}catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
	
	public void listarReclamacao(Reclamacao reclamacao) {
		
		String sql = "{CALL ListarReclamacoes(?)}";
		
		try(Connection conn = dbConnection.getConnection();
			CallableStatement stmt = conn.prepareCall(sql)){
			
			stmt.setInt(1,  reclamacao.getId());
			stmt.execute();
			
		}catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
	
	public void updateStatusReclamacao(Reclamacao reclamacao) {
		
		String sql = "{CALL UpdateReclamacaoStatus(?,?)}";
		
		try(Connection conn = dbConnection.getConnection();
			CallableStatement stmt = conn.prepareCall(sql)){
			
			stmt.setInt(1,  reclamacao.getId());
			stmt.setString(2,  reclamacao.getStatusReclamacao().name());
			stmt.execute();
			
		}catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
}
