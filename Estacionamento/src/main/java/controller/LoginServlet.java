package controller;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ClienteDao;
import dao.FuncionarioDao;
import dao.GerenteDao;
import model.Cliente;
import model.Funcionario;
import model.Gerente;

@WebServlet("/LoginServlet") // 👈 Tem que ser exatamente o mesmo nome que está no AJAX do seu login.jsp!
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private ClienteDao clienteDao = new ClienteDao();
    private FuncionarioDao funcionarioDao = new FuncionarioDao();
    private GerenteDao gerenteDao = new GerenteDao();

    public LoginServlet() {
        super();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.getWriter().append("Use POST para realizar o login.");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        try {
            // 1. Captura o JSON enviado pelo formulário do login.jsp
            String jsonBody = lerCorpoRequisicao(req);
            
            // 2. Extrai o email e a senha de dentro do texto JSON
            String emailForm = extrairCampoJson(jsonBody, "email");
            String senhaForm = extrairCampoJson(jsonBody, "senha");

            if (emailForm.isEmpty() || !jsonBody.contains("senha")) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"E-mail e senha são obrigatórios.\"}");
                return;
            }

            // 🔍 TESTE 1: É o Gerente? (Checa primeiro o cargo mais alto)
            Gerente gerente = gerenteDao.validarLogin(emailForm, senhaForm);
            if (gerente != null) {
                req.getSession().setAttribute("gerenteLogado", gerente);
                res.getWriter().write("{\"sucesso\": true, \"redirecionar\": \"reclamacao_gerente.jsp\"}");
                return;
            }

            // 🔍 TESTE 2: É um Funcionário?
            Funcionario funcionario = funcionarioDao.validarLogin(emailForm, senhaForm);
            if (funcionario != null) {
                req.getSession().setAttribute("funcionarioLogado", funcionario);
                res.getWriter().write("{\"sucesso\": true, \"redirecionar\": \"funcionario/reservas_funcionario.jsp\"}");
                return;
            }

            // 🔍 TESTE 3: É um Cliente?
            // (Nota: Se o seu ClienteDao ainda não tiver o método validarLogin, passe um mock ou crie-o lá)
            // Cliente cliente = clienteDao.validarLogin(emailForm, senhaForm);
            Cliente clienteMock = null; 
            if (emailForm.equals("cliente@teste.com") && senhaForm.equals("123")) {
                clienteMock = new Cliente(1, "123", "Fabio", null, null, emailForm, null, senhaForm, false, null);
            }
            
            if (clienteMock != null) {
                req.getSession().setAttribute("usuarioLogado", clienteMock);
                res.getWriter().write("{\"sucesso\": true, \"redirecionar\": \"cliente/reserva.jsp\"}");
                return;
            }

            // 🛑 Se chegou até aqui, as credenciais estão erradas em todas as tabelas
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"E-mail ou senha incorretos.\"}");

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro interno ao processar o login.\"}");
        }
    }

    // 🛡️ Método auxiliar para ler o JSON vindo do front-end
    private String lerCorpoRequisicao(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) {
            sb.append(linha);
        }
        return sb.toString();
    }

    // 🔍 Método auxiliar para pegar os valores do JSON manualmente
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