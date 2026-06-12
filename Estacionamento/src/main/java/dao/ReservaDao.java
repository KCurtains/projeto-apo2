package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import model.Reserva;
import util.dbConnection;

public class ReservaDao {
	
	public void criarReserva(Reserva reserva) {
		
		String sql = "{CALL adicionarReserva(?,?,?,?,?,?)}";
		
		try (Connection conn = dbConnection.getConnection();
	             CallableStatement stmt = conn.prepareCall(sql)) {

			stmt.setObject(1, reserva.getHorarioEntrada());
			stmt.setObject(1, reserva.getHorarioSaida());
			stmt.setFloat(1, reserva.getValor());
			stmt.setObject(1, reserva.getVeiculo().getId());
			stmt.setObject(1, reserva.getPatio().getId());
			stmt.setObject(1, reserva.getVaga().getId());

	            stmt.execute();
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}

	
	public void cancelarReserva(Reserva reserva) {
		
		String sql = "{CALL CancelarReserva(?)}";
		
		try (Connection conn = dbConnection.getConnection();
	             CallableStatement stmt = conn.prepareCall(sql)) {

			stmt.setInt(1, reserva.getId());


	            stmt.execute();
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}
	
	public void listarReserva(Reserva reserva) {
		
		String sql = "{CALL ListarReserva(?)}";
		
		try (Connection conn = dbConnection.getConnection();
	             CallableStatement stmt = conn.prepareCall(sql)) {

			stmt.setInt(1, reserva.getId());


	            stmt.execute();
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}
}
