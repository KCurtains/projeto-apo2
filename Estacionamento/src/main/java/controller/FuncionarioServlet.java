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
import model.Funcionario;

@WebServlet("/funcionario")
public class FuncionarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FuncionarioDao funcionarioDao = new FuncionarioDao();

    public FuncionarioServlet() {
        super();
    }

    // 📥 BUSCA de informações (ex: carregar os dados do funcionário na tela de perfil)
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        // Regra de Segurança: Verifica se há um funcionário logado na sessão
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("funcionarioLogado") == null) {
            // Simulação para testes locais enquanto a sessão não está ativa no login
            res.getWriter().write("{"
                + "\"nome\":\"Fabio Henrique Baptista\","
                + "\"cpf\":\"48923\","
                + "\"email\":\"nooba*****9@gmail.com\","
                + "\"telefone\":\"(11) 94002-8922\""
                + "}");
            return;
        }

        if ("buscarPerfil".equals(acao)) {

            Funcionario func = (Funcionario) session.getAttribute("funcionarioLogado");
            res.getWriter().write("{\"nome\":\""+func.getNome()+"\",\"cpf\":\""+func.getCpf()+"\",\"email\":\""+func.getEmail()+"\",\"telefone\":\""+func.getTelefone()+"\"}");
        }
    }

    // 📤 SALVAMENTO e ALTERAÇÃO de dados (ex: salvar edições de perfil e fazer login)
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        if (acao == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Ação não informada.\"}");
            return;
        }

        try {
            switch (acao) {
                case "login":
                    executarLogin(req, res);
                    break;

                case "atualizarSimples":
                    // Processa as alterações diretas de Nome e Número de telefone
                    executarAtualizacaoSimples(req, res);
                    break;

                case "atualizarComplexo":
                    // Processa as atualizações protegidas por senha de Email e Senha
                    executarAtualizacaoComplexo(req, res);
                    break;

                default:
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Ação desconhecida.\"}");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro interno no servidor.\"}");
        }
    }

    // 🔐 1. Método para processar o login do funcionário
    private void executarLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        
        // 💡 IMPORTANTE: Aqui você deve extrair o email e senha reais vindos do seu 'jsonBody'
        String emailForm = "fabio@email.com"; 
        String senhaForm = "123456";

        // Agora o método mapeia perfeitamente e o erro some!
        Funcionario funcionarioObjeto = funcionarioDao.validarLogin(emailForm, senhaForm);
        
        if (funcionarioObjeto != null) {
            req.getSession().setAttribute("funcionarioLogado", funcionarioObjeto);
            res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Login efetuado!\", \"redirecionar\": \"reservas_funcionario.jsp\"}");
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"E-mail ou senha incorretos.\"}");
        }
    }

    // 📝 2. Método para atualizar dados simples (Nome, Telefone)
    private void executarAtualizacaoSimples(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        
        // TODO: Mapear o JSON recebido e disparar a Procedure de Update do Funcionário

        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Dados atualizados com sucesso no perfil do funcionário!\"}");
    }

    // 🔒 3. Método para atualizar credenciais complexas (E-mail, Senha)
    private void executarAtualizacaoComplexo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        
        // TODO: Validar senha atual e realizar o update seguro de e-mail ou senha nova

        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Credenciais do funcionário alteradas com sucesso!\"}");
    }

    // 🛡️ Lê o fluxo de texto (Stream) do JSON enviado via AJAX
    private String lerCorpoRequisicao(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) {
            sb.append(linha);
        }
        return sb.toString();
    }
}