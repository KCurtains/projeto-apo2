package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import model.RegistroEstadia;
import util.dbConnection;

public class RegistroEstadiaDao {

	
	public void validarEntrada(RegistroEstadia estadia) {
		String sql = "{CALL ValidarEntrada(?)}";
		
		try (Connection conn = dbConnection.getConnection();
	             CallableStatement stmt = conn.prepareCall(sql)) {
	            stmt.setInt(1, estadia.getReserva().getId());
	            stmt.execute();
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}
	
	public void registrarSaida(RegistroEstadia estadia) {
		String sql = "{CALL RegistrarSaida(?)}";
		
		try (Connection conn = dbConnection.getConnection();
	             CallableStatement stmt = conn.prepareCall(sql)) {
	            stmt.setInt(1, estadia.getId());
	            stmt.execute();
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}
	
	public double calcularHoras(RegistroEstadia estadia) {
        String sql = "{CALL CalcularHoras(?, ?)}";
        double totalHoras = 0.0;
        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, estadia.getId());
            stmt.registerOutParameter(2, Types.DECIMAL); // Resgata parâmetro OUT 
            
            stmt.execute();
            totalHoras = stmt.getDouble(2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return totalHoras;
    }
	
	public void processarPagamento(RegistroEstadia estadia) {
        String sql = "{CALL ProcessarPagamento(?)}";
        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, estadia.getReserva().getId());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
