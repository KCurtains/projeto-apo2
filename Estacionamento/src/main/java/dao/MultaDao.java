package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import model.Multa;
import util.dbConnection;

public class MultaDao {

	public void adicionarMulta(Multa multa) {
		
		String sql = "{CALL AdicionarMulta(?,?,?)}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)) {

				stmt.setFloat(1, multa.getValor());
				stmt.setString(2, multa.getMotivo());
				stmt.setFloat(3, multa.getEstadiaRelacionada().getId());
				stmt.execute();
				
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}
	
	public void updateMulta(Multa multa) {
		
		String sql = "{CALL UpdateMulta(?,?)}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)) {

				stmt.setInt(1, multa.getId());
				stmt.setString(2, multa.getStatusMulta().name());
				stmt.execute();
				
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}
}
