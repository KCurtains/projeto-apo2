package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ValidacaoDao;
import model.Usuario;
import model.ValidacaoToken;

@WebServlet("/validacao")
public class ValidacaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ValidacaoDao validacaoDao = new ValidacaoDao();

    // Gera um novo token de validação para o usuário logado
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("usuarioLogado") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"erro\": \"Usuário não autenticado.\"}");
            return;
        }

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
            String token = validacaoDao.gerarToken(usuario.getId());
            out.print("{\"mensagem\": \"Token gerado com sucesso!\", \"token\": \"" + token + "\"}");

        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\": \"Erro ao gerar token: " + e.getMessage() + "\"}");
        }
    }

    // Valida um token recebido por link de e-mail
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();

        String token = req.getParameter("token");

        if (token == null || token.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"erro\": \"Token não informado.\"}");
            return;
        }

        try {
            ValidacaoToken vt = validacaoDao.buscarTokenValido(token);

            if (vt == null) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\": \"Token inválido, expirado ou já utilizado.\"}");
                return;
            }

            validacaoDao.marcarTokenUtilizado(token);
            validacaoDao.marcarEmailVerificado(vt.getClienteId());

            out.print("{\"mensagem\": \"E-mail validado com sucesso!\"}");

        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\": \"Erro ao validar token: " + e.getMessage() + "\"}");
        }
    }
}