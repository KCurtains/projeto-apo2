package controller;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UsuarioDao;
import model.Usuario;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDao usuarioDao = new UsuarioDao();

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String jsonBody = lerCorpoRequisicao(req);
        String email = extrairCampoJson(jsonBody, "email");
        String senha = extrairCampoJson(jsonBody, "senha");

        if (email.isEmpty() || senha.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"E-mail e senha são obrigatórios.\"}");
            return;
        }

        try {
            // Chama a SUA DAO com o método que você já criou
            Usuario usuarioAutenticado = usuarioDao.autenticarUsuario(email, senha);

            if (usuarioAutenticado != null) {
                // 🔐 Cria e vincula a sessão do usuário
                HttpSession session = req.getSession(); 
                session.setAttribute("usuarioLogadoId", usuarioAutenticado.getId());
                session.setAttribute("usuarioLogadoNome", usuarioAutenticado.getNome());
                session.setAttribute("usuarioLogadoEmail", usuarioAutenticado.getEmail());

                res.setStatus(HttpServletResponse.SC_OK);
                // Direciona para a tela de reservas
                res.getWriter().write("{\"sucesso\": true, \"redirecionar\": \"reserva.jsp\"}");
                
            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
                res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"E-mail ou senha incorretos!\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro interno no servidor.\"}");
        }
    }

    private String lerCorpoRequisicao(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) {
            sb.append(linha);
        }
        return sb.toString();
    }

    private String extrairCampoJson(String json, String campo) {
        try {
            String chave = "\"" + campo + "\"";
            if (!json.contains(chave)) return "";
            int inicio = json.indexOf(chave) + chave.length();
            inicio = json.indexOf("\"", inicio) + 1;
            int fim = json.indexOf("\"", inicio);
            return json.substring(inicio, fim);
        } catch (Exception e) {
            return "";
        }
    }
}