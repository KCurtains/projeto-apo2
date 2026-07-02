package controller;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.FuncionarioDao;
import dao.GerenteDao;
import dao.UsuarioDao;
import model.Funcionario;
import model.Gerente;
import model.Usuario;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDao usuarioDao = new UsuarioDao();

    private FuncionarioDao funcionarioDao = new FuncionarioDao();
    private GerenteDao gerenteDao = new GerenteDao();

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
            Usuario usuarioAutenticado = usuarioDao.autenticarUsuario(email, senha);

            if (usuarioAutenticado != null) {
                HttpSession session = req.getSession();

                // descobre o papel/cardo do usuario
                Gerente gerente = gerenteDao.validarLogin(email, senha);
                Funcionario funcionario = (gerente == null) ? funcionarioDao.validarLogin(email, senha) : null;

                String redirecionar;
                if (gerente != null) {
                    session.setAttribute("gerenteLogado", gerente);
                    redirecionar = "gerente/reclamacao_gerente.jsp";
                } else if (funcionario != null) {
                    session.setAttribute("funcionarioLogado", funcionario);
                    redirecionar = "funcionario/reservas_funcionario.jsp";
                } else {
                    session.setAttribute("usuarioLogado", usuarioAutenticado);
                    redirecionar = "cliente/reserva.jsp";
                }

                res.setStatus(HttpServletResponse.SC_OK);
                res.getWriter().write("{\"sucesso\": true, \"redirecionar\": \"" + redirecionar + "\"}");
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
        while ((linha = reader.readLine()) != null) sb.append(linha);
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
