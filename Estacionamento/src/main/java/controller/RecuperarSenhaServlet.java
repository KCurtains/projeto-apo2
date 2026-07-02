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
 * Recuperação de senha em 3 etapas, casando com o login.jsp:
 *   acao=enviar_codigo   -> valida e-mail, gera um código de 6 dígitos e guarda na sessão
 *   acao=verificar_codigo-> confere o código digitado
 *   acao=nova_senha      -> grava a nova senha (com hash) do e-mail em recuperação
 */
@WebServlet("/RecuperarSenhaServlet")
public class RecuperarSenhaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDao usuarioDao = new UsuarioDao();
    private static final SecureRandom RANDOM = new SecureRandom();

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String jsonBody = lerCorpoRequisicao(req);
        String acao = extrairCampoJson(jsonBody, "acao");

        try {
            switch (acao) {
                case "enviar_codigo":    enviarCodigo(req, res, jsonBody); break;
                case "verificar_codigo": verificarCodigo(req, res, jsonBody); break;
                case "nova_senha":       novaSenha(req, res, jsonBody); break;
                default:
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Ação inválida.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro interno no servidor.\"}");
        }
    }

    private void enviarCodigo(HttpServletRequest req, HttpServletResponse res, String body) throws IOException {
        String email = extrairCampoJson(body, "email");
        if (email.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"E-mail é obrigatório.\"}");
            return;
        }

        Integer id = usuarioDao.buscarIdPorEmail(email);
        // Por segurança respondemos sucesso mesmo se o e-mail não existir (não revela cadastro).
        if (id != null) {
            String codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
            HttpSession session = req.getSession();
            session.setAttribute("rec_email", email);
            session.setAttribute("rec_codigo", codigo);

            // Envia o código por e-mail (cai no seu inbox do Mailtrap).
            EmailService.enviarCodigo(email, codigo);
        }

        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Se o e-mail existir, um código foi enviado.\"}");
    }

    private void verificarCodigo(HttpServletRequest req, HttpServletResponse res, String body) throws IOException {
        String codigo = extrairCampoJson(body, "codigo");
        HttpSession session = req.getSession(false);
        String esperado = session == null ? null : (String) session.getAttribute("rec_codigo");

        if (esperado != null && esperado.equals(codigo)) {
            res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Código válido.\"}");
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Código inválido.\"}");
        }
    }

    private void novaSenha(HttpServletRequest req, HttpServletResponse res, String body) throws IOException {
        String senha = extrairCampoJson(body, "senha");
        HttpSession session = req.getSession(false);
        String email = session == null ? null : (String) session.getAttribute("rec_email");
        String codigo = session == null ? null : (String) session.getAttribute("rec_codigo");

        // Só permite trocar se passou pelas etapas anteriores (tem e-mail + código na sessão)
        if (email == null || codigo == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Fluxo de recuperação não iniciado.\"}");
            return;
        }
        if (senha.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Senha é obrigatória.\"}");
            return;
        }

        Integer id = usuarioDao.buscarIdPorEmail(email);
        boolean ok = id != null && usuarioDao.atualizarSenha(id, senha);

        if (ok) {
            session.removeAttribute("rec_email");
            session.removeAttribute("rec_codigo");
            res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Senha redefinida com sucesso!\"}");
        } else {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Não foi possível redefinir a senha.\"}");
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
