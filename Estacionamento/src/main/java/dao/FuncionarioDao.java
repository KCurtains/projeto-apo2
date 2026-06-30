package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Funcionario;
import util.dbConnection;

public class FuncionarioDao {

    // 🔐 1. Método para validar o Login do Funcionário
    public Funcionario validarLogin(String email, String senha) {
        String sql = "SELECT id, cpf, nome, email, telefone, senha FROM Funcionario WHERE email = ? AND senha = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, senha);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Monta o objeto Funcionario com os dados vindos do banco
                    // (Ajuste os parâmetros de acordo com o construtor da sua model Funcionario)
                    return new Funcionario(
                        rs.getInt("id"),
                        rs.getString("cpf"),
                        rs.getString("nome"),
                        null, // SexoEnum (se não usar no funcionário, passe null ou remova se o construtor não pedir)
                        null, // LocalDate dataNascimento
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getString("senha")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao validar login do funcionário", e);
        }
        return null; // Retorna null se o e-mail ou a senha estiverem errados
    }

    // 📝 2. Método para atualizar dados simples (Nome e Telefone) do Perfil
    public boolean atualizarPerfilSimples(int id, String nome, String telefone) {
        String sql = "UPDATE Funcionario SET nome = ?, telefone = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            stmt.setString(2, telefone);
            stmt.setInt(3, id);
            
            int linhasAlteradas = stmt.executeUpdate();
            return linhasAlteradas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar perfil simples do funcionário", e);
        }
    }

    // 🔒 3. Método para atualizar credenciais complexas (Email e Senha) do Perfil
    public boolean atualizarPerfilComplexo(int id, String email, String novaSenha) {
        String sql = "UPDATE Funcionario SET email = ?, senha = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, novaSenha);
            stmt.setInt(3, id);
            
            int linhasAlteradas = stmt.executeUpdate();
            return linhasAlteradas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar credenciais do funcionário", e);
        }
    }
}