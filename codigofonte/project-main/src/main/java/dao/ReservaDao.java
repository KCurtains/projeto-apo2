package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Enum.StatusReservaEnum;
import model.Patio;
import model.Reserva;
import model.Veiculo;
import util.dbConnection;

public class ReservaDao {
	
	public void criarReserva(Reserva reserva) {

		String sql = "{CALL adicionarReserva(?,?,?,?,?,?)}";

		try (Connection conn = dbConnection.getConnection()) {
			try (CallableStatement stmt = conn.prepareCall(sql)) {
				stmt.setObject(1, reserva.getHorarioEntrada());
				stmt.setObject(2, reserva.getHorarioSaida());
				stmt.setFloat(3, reserva.getValor());
				stmt.setObject(4, reserva.getVeiculo().getId());
				stmt.setObject(5, reserva.getPatio().getId());
				stmt.setObject(6, reserva.getVaga().getId());
				stmt.execute();
			}

			try (PreparedStatement stmt = conn.prepareStatement(
					"UPDATE Vaga SET StatusVaga = 'RESERVADA' WHERE Id = ?")) {
				stmt.setInt(1, reserva.getVaga().getId());
				stmt.executeUpdate();
			}
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}


	public void cancelarReserva(Reserva reserva) {

		String sql = "{CALL CancelarReserva(?)}";

		try (Connection conn = dbConnection.getConnection()) {
			try (CallableStatement stmt = conn.prepareCall(sql)) {
				stmt.setInt(1, reserva.getId());
				stmt.execute();
			}
			
			try (PreparedStatement stmt = conn.prepareStatement(
					"UPDATE Vaga v INNER JOIN Reserva r ON v.Id = r.VagaId "
					+ "SET v.StatusVaga = 'DISPONIVEL' WHERE r.Id = ?")) {
				stmt.setInt(1, reserva.getId());
				stmt.executeUpdate();
			}
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
	
	
    public List<Reserva> listarReservasPorCliente(int clienteId) {
        List<Reserva> lista = new ArrayList<>();
        String sql = "{CALL GetReservasCliente(?)}";
        
        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery(); 

            while (rs.next()) {
                Reserva r = new Reserva();
                r.setId(rs.getInt("Id"));
                r.setHorarioEntrada(rs.getTimestamp("HorarioEntrada").toLocalDateTime());
                r.setHorarioSaida(rs.getTimestamp("HorarioSaida").toLocalDateTime());
                r.setValor(rs.getFloat("Valor"));
                r.setStatusReserva(StatusReservaEnum.valueOf(rs.getString("StatusReserva")));

                Patio p = new Patio(clienteId, "", 3, 0, 0); 
                p.setId(rs.getInt("PatioId"));
                r.setPatio(p);

                Veiculo v = new Veiculo();
                v.setId(rs.getInt("VeiculoId"));
                r.setVeiculo(v);

                lista.add(r);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    public List<Reserva> listarReservasDoDia() {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT r.Id, r.HorarioEntrada, r.HorarioSaida, r.Valor, r.StatusReserva, "
                   + "       v.Id AS VeiculoId, v.Modelo, v.Placa, v.Cor, v.TipoVeiculo, "
                   + "       p.Id AS PatioId, p.Endereco "
                   + "FROM Reserva r "
                   + "INNER JOIN Veiculo v ON r.VeiculoId = v.Id "
                   + "INNER JOIN Patio p ON r.PatioId = p.Id "
                   + "WHERE DATE(r.HorarioEntrada) = CURDATE() AND r.StatusReserva = 'ATIVA' "
                   + "ORDER BY r.HorarioEntrada";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reserva r = new Reserva();
                r.setId(rs.getInt("Id"));
                r.setHorarioEntrada(rs.getTimestamp("HorarioEntrada").toLocalDateTime());
                r.setHorarioSaida(rs.getTimestamp("HorarioSaida").toLocalDateTime());
                r.setValor(rs.getFloat("Valor"));
                r.setStatusReserva(StatusReservaEnum.valueOf(rs.getString("StatusReserva")));

                Veiculo v = new Veiculo();
                v.setId(rs.getInt("VeiculoId"));
                v.setModelo(rs.getString("Modelo"));
                v.setPlaca(rs.getString("Placa"));
                v.setCor(rs.getString("Cor"));
                r.setVeiculo(v);

                Patio p = new Patio(rs.getInt("PatioId"), rs.getString("Endereco"), 0, 0, 0);
                r.setPatio(p);

                lista.add(r);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar reservas do dia", e);
        }
        return lista;
    }
}
