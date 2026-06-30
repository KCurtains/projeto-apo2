package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Gerente;
import util.dbConnection;

public class GerenteDao {

    // 🔐 1. Validar o login do Gerente
    public Gerente validarLogin(String email, String senha) {
        String sql = "SELECT id, cpf, nome, email, telefone, senha FROM Gerente WHERE email = ? AND senha = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, senha);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    return new Gerente(
                        rs.getInt("id"),
                        rs.getString("cpf"),
                        rs.getString("nome"),
                        null, 
                        null, 
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getString("senha")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao validar login do gerente", e);
        }
        return null;
    }

    // 📝 2. Atualizar perfil simples (Nome e Telefone)
    public boolean atualizarPerfilSimples(int id, String nome, String telefone) {
        String sql = "UPDATE Gerente SET nome = ?, telefone = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            stmt.setString(2, telefone);
            stmt.setInt(3, id);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar perfil do gerente", e);
        }
    }

    // 🔒 3. Atualizar credenciais complexas (Email e Senha)
    public boolean atualizarPerfilComplexo(int id, String email, String novaSenha) {
        String sql = "UPDATE Gerente SET email = ?, senha = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, novaSenha);
            stmt.setInt(3, id);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar credenciais do gerente", e);
        }
    }
}
