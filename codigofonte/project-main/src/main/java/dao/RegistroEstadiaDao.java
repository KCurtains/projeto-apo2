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
import model.RegistroEstadia;
import model.Reserva;
import model.Veiculo;
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
        try (Connection conn = dbConnection.getConnection()) {
			try (CallableStatement stmt = conn.prepareCall(sql)) {
				stmt.setInt(1, estadia.getReserva().getId());
				stmt.execute();
			}

			try (PreparedStatement stmt = conn.prepareStatement(
					"UPDATE Vaga v INNER JOIN Reserva r ON v.Id = r.VagaId "
					+ "SET v.StatusVaga = 'DISPONIVEL' WHERE r.Id = ?")) {
				stmt.setInt(1, estadia.getReserva().getId());
				stmt.executeUpdate();
			}
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

	public List<RegistroEstadia> listarEmAndamento() {
		List<RegistroEstadia> lista = new ArrayList<>();
		String sql = "SELECT re.Id AS EstadiaId, re.HorarioEntradaReal, re.HorarioSaidaReal, "
				   + "       r.Id AS ReservaId, r.HorarioEntrada, r.HorarioSaida, r.Valor, "
				   + "       v.Id AS VeiculoId, v.Modelo, v.Placa, v.Cor, "
				   + "       p.Id AS PatioId, p.Endereco "
				   + "FROM RegistroEstadia re "
				   + "INNER JOIN Reserva r ON re.ReservaId = r.Id "
				   + "INNER JOIN Veiculo v ON r.VeiculoId = v.Id "
				   + "INNER JOIN Patio p ON r.PatioId = p.Id "
				   + "WHERE re.HorarioSaidaReal IS NULL "
				   + "ORDER BY re.HorarioEntradaReal";

		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				RegistroEstadia estadia = new RegistroEstadia();
				estadia.setId(rs.getInt("EstadiaId"));
				estadia.setHorarioEntradaReal(rs.getTimestamp("HorarioEntradaReal").toLocalDateTime());

				Reserva r = new Reserva();
				r.setId(rs.getInt("ReservaId"));
				r.setValor(rs.getFloat("Valor"));

				Veiculo v = new Veiculo();
				v.setId(rs.getInt("VeiculoId"));
				v.setModelo(rs.getString("Modelo"));
				v.setPlaca(rs.getString("Placa"));
				v.setCor(rs.getString("Cor"));
				r.setVeiculo(v);

				Patio p = new Patio(rs.getInt("PatioId"), rs.getString("Endereco"), 0, 0, 0);
				r.setPatio(p);

				estadia.setReserva(r);
				lista.add(estadia);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao listar estadias em andamento", e);
		}
		return lista;
	}

	public List<RegistroEstadia> listarEstadiasPorCliente(int clienteId) {
		List<RegistroEstadia> lista = new ArrayList<>();
		String sql = "SELECT re.Id AS EstadiaId, re.HorarioEntradaReal, re.HorarioSaidaReal, "
				   + "       v.Modelo, v.Placa, v.Cor "
				   + "FROM RegistroEstadia re "
				   + "INNER JOIN Reserva r ON re.ReservaId = r.Id "
				   + "INNER JOIN Veiculo v ON r.VeiculoId = v.Id "
				   + "WHERE v.MotoristaPrincipal = ? "
				   + "ORDER BY re.HorarioEntradaReal DESC";

		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, clienteId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					RegistroEstadia estadia = new RegistroEstadia();
					estadia.setId(rs.getInt("EstadiaId"));
					estadia.setHorarioEntradaReal(rs.getTimestamp("HorarioEntradaReal").toLocalDateTime());

					Veiculo v = new Veiculo();
					v.setModelo(rs.getString("Modelo"));
					v.setPlaca(rs.getString("Placa"));
					v.setCor(rs.getString("Cor"));

					Reserva r = new Reserva();
					r.setVeiculo(v);
					estadia.setReserva(r);

					lista.add(estadia);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao listar estadias do cliente", e);
		}
		return lista;
	}
}
