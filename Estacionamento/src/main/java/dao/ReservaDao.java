package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
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
		
		try (Connection conn = dbConnection.getConnection();
	             CallableStatement stmt = conn.prepareCall(sql)) {

			stmt.setObject(1, reserva.getHorarioEntrada());
			stmt.setObject(2, reserva.getHorarioSaida());
			stmt.setFloat(3, reserva.getValor());
			stmt.setObject(4, reserva.getVeiculo().getId());
			stmt.setObject(5, reserva.getPatio().getId());
			stmt.setObject(6, reserva.getVaga().getId());

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
	
	// Adicione ou substitua na sua ReservaDao
    public List<Reserva> listarReservasPorCliente(int clienteId) {
        List<Reserva> lista = new ArrayList<>();
        // Chama a procedure que retorna as reservas vinculadas aos veículos do cliente
        String sql = "{CALL GetReservasCliente(?)}";
        
        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery(); // Executa e captura o resultado

            while (rs.next()) {
                Reserva r = new Reserva();
                r.setId(rs.getInt("Id"));
                // Converte DATETIME do banco para LocalDateTime do Java
                r.setHorarioEntrada(rs.getTimestamp("HorarioEntrada").toLocalDateTime());
                r.setHorarioSaida(rs.getTimestamp("HorarioSaida").toLocalDateTime());
                r.setValor(rs.getFloat("Valor"));
                r.setStatusReserva(StatusReservaEnum.valueOf(rs.getString("StatusReserva")));

                // Instancia as dependências apenas com o ID para compor o objeto.
                // (Para o nome real do Pátio e Veículo, seria necessário um JOIN no BD,
                // mas usaremos IDs por agora para fazer funcionar).
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
}
