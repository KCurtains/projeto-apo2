package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Enum.TipoVeiculoEnum;
import model.Cliente;
import model.Veiculo;
import util.dbConnection;

public class VeiculoDao {
	
	public void adicionarVeiculo(Veiculo veiculo) {
		
		String sql = "{CALL AdicionarVeiculo(?,?,?,?,?)}";
		
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
				stmt.setObject(4, veiculo.getMotoristaPrincipal()); // setObject trata null com segurança
				stmt.setString(5, veiculo.getTipoVeiculo().name());
				
				stmt.execute();
				
			}catch (SQLException e) {
	            throw new RuntimeException("Erro ao adicionar veículo: " + e.getMessage(), e);
	        }
	}
	
	public List<Veiculo> listarPorCliente(int idCliente) throws SQLException {
        List<Veiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM Veiculo WHERE MotoristaPrincipal = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Veiculo v = new Veiculo();
                    v.setId(rs.getInt("Id"));
                    v.setPlaca(rs.getString("Placa"));
                    v.setModelo(rs.getString("Modelo"));
                    v.setCor(rs.getString("Cor"));
                    v.setMotoristaPrincipal(rs.getInt("MotoristaPrincipal"));
                    v.setTipoVeiculo(TipoVeiculoEnum.valueOf(rs.getString("TipoVeiculo")));
                    lista.add(v);
                }
            }
        }
        return lista;
    }

	// Busca um único veículo pelo Id (usado para conferir o dono antes de editar/remover).
	public Veiculo buscarPorId(int id) {
		String sql = "SELECT * FROM Veiculo WHERE Id = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Veiculo v = new Veiculo();
					v.setId(rs.getInt("Id"));
					v.setPlaca(rs.getString("Placa"));
					v.setModelo(rs.getString("Modelo"));
					v.setCor(rs.getString("Cor"));
					v.setMotoristaPrincipal(rs.getInt("MotoristaPrincipal"));
					v.setTipoVeiculo(TipoVeiculoEnum.valueOf(rs.getString("TipoVeiculo")));
					return v;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao buscar veículo", e);
		}
		return null;
	}

	// Autoriza um cliente já cadastrado a dirigir um veículo (tabela associativa VeiculoCliente).
	public void adicionarMotorista(int clienteId, int veiculoId) {
		String sql = "{CALL AdicionaMotorista(?,?)}";
		try (Connection conn = dbConnection.getConnection();
			 CallableStatement stmt = conn.prepareCall(sql)) {
			stmt.setInt(1, clienteId);
			stmt.setInt(2, veiculoId);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao autorizar motorista", e);
		}
	}

	// Remove a autorização de um motorista para um veículo.
	public void removerMotorista(int clienteId, int veiculoId) {
		String sql = "DELETE FROM VeiculoCliente WHERE ClienteId = ? AND VeiculoId = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, clienteId);
			stmt.setInt(2, veiculoId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao remover motorista", e);
		}
	}

	// Lista os motoristas autorizados (além do principal) de um veículo.
	public List<Cliente> listarMotoristasAutorizados(int veiculoId) {
		List<Cliente> lista = new ArrayList<>();
		String sql = "SELECT u.Id, u.CPF, u.Nome, u.Email, u.Telefone "
				   + "FROM VeiculoCliente vc "
				   + "INNER JOIN Cliente c ON vc.ClienteId = c.Id "
				   + "INNER JOIN Usuario u ON c.Id = u.Id "
				   + "WHERE vc.VeiculoId = ?";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, veiculoId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					lista.add(new Cliente(rs.getInt("Id"), rs.getString("CPF"), rs.getString("Nome"),
							null, null, rs.getString("Email"), rs.getString("Telefone"), null, null, null));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao listar motoristas autorizados", e);
		}
		return lista;
	}

	// Busca um Id de vaga DISPONÍVEL em um pátio para um tipo de veículo (usada ao criar reservas).
	public Integer buscarVagaDisponivel(int patioId, String tipo) {
		String sql = "SELECT Id FROM Vaga WHERE PatioId = ? AND Tipo = ? AND StatusVaga = 'DISPONIVEL' LIMIT 1";
		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, patioId);
			stmt.setString(2, tipo);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) return rs.getInt("Id");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao buscar vaga disponível", e);
		}
		return null;
	}

}
