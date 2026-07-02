package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.SecureRandom;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UsuarioDao;
import util.EmailService;

/**
 * Verificação de e-mail durante o CADASTRO (antes de a conta ser criada).
 * Casa com o login.jsp (tela "código de verificação"):
 *   acao=enviar    -> valida e-mail, gera código de 6 dígitos, envia via Mailtrap e guarda na sessão
 *   acao=verificar -> confere o código digitado
 */
@WebServlet("/verificacao-cadastro")
public class VerificacaoCadastroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDao usuarioDao = new UsuarioDao();
    private static final SecureRandom RANDOM = new SecureRandom();

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String body = lerCorpoRequisicao(req);
        String acao = extrairCampoJson(body, "acao");

        try {
            if ("enviar".equals(acao)) {
                enviar(req, res, body);
            } else if ("verificar".equals(acao)) {
                verificar(req, res, body);
            } else {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Ação inválida.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Não foi possível enviar o código. Tente novamente.\"}");
        }
    }

    private void enviar(HttpServletRequest req, HttpServletResponse res, String body) throws IOException {
        String email = extrairCampoJson(body, "email");
        if (email.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"E-mail é obrigatório.\"}");
            return;
        }

        // Evita cadastro duplicado: se o e-mail já existe, avisa.
        if (usuarioDao.buscarIdPorEmail(email) != null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Este e-mail já está cadastrado.\"}");
            return;
        }

        String codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        HttpSession session = req.getSession();
        session.setAttribute("cad_email", email);
        session.setAttribute("cad_codigo", codigo);

        // Envia de verdade — cai no seu inbox do Mailtrap.
        EmailService.enviarCodigo(email, codigo);

        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Código enviado para o e-mail.\"}");
    }

    private void verificar(HttpServletRequest req, HttpServletResponse res, String body) throws IOException {
        String codigo = extrairCampoJson(body, "codigo");
        HttpSession session = req.getSession(false);
        String esperado = session == null ? null : (String) session.getAttribute("cad_codigo");

        if (esperado != null && esperado.equals(codigo)) {
            session.setAttribute("cad_verificado", Boolean.TRUE);
            res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"E-mail verificado.\"}");
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Código inválido.\"}");
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
