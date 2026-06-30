package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ClienteDao;
import model.Cliente;
import Enum.SexoEnum;

@WebServlet("/cliente")
public class ClienteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ClienteDao clienteDao = new ClienteDao();

    // 📥 O doGet serve para BUSCAR e carregar dados na tela (ex: carregar o Perfil)
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        // Segurança: Pegamos o usuário logado direto da sessão do servidor
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            // Se não tiver sessão (simulação), vamos criar um cliente mock para teste
            Cliente mock = new Cliente(1, "48923", "Fabio Henrique Baptista", SexoEnum.MASCULINO, LocalDate.of(2000, 4, 23), "nooba@gmail.com", "(11) 94002-8922", "******", false, null);
            res.getWriter().write("{\"nome\":\""+mock.getNome()+"\",\"cpf\":\""+mock.getCpf()+"\",\"email\":\""+mock.getEmail()+"\",\"telefone\":\""+mock.getTelefone()+"\"}");
            return;
        }

        if ("buscarPerfil".equals(acao)) {
            Cliente clienteLogado = (Cliente) session.getAttribute("usuarioLogado");
            // Devolve os dados do cliente logado em formato JSON para o perfil.jsp ler
            res.getWriter().write("{\"nome\":\""+clienteLogado.getNome()+"\",\"cpf\":\""+clienteLogado.getCpf()+"\",\"email\":\""+clienteLogado.getEmail()+"\",\"telefone\":\""+clienteLogado.getTelefone()+"\"}");
        }
    }

    // 📤 O doPost serve para SALVAR, CRIAR ou ALTERAR dados no banco
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
                case "cadastrar":
                    executarCadastro(req, res);
                    break;
                    
                case "atualizarSimples":
                    // Trata as edições diretas de Nome e Telefone do perfil.jsp
                    executarAtualizacaoSimples(req, res);
                    break;
                    
                case "atualizarComplexo":
                    // Trata as alterações seguras de Email e Senha do perfil.jsp
                    executarAtualizacaoComplexo(req, res);
                    break;
                    
                case "mudarMensalista":
                    executarMudancaMensalista(req, res);
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

    private void executarCadastro(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        // Lógica de salvar o cliente (Dispara a Stored Procedure do DAO)
        Cliente novoCliente = new Cliente(0, "111.222.333-44", "Fabio Henrique Baptista", SexoEnum.MASCULINO, LocalDate.of(2000, 4, 23), "fabio@email.com", "(11) 99999-8888", "senhaForte123", false, null);
        
        boolean sucesso = clienteDao.cadastrarCliente(novoCliente);
        if (sucesso) {
            res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Cliente cadastrado com sucesso!\"}");
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro ao salvar no banco.\"}");
        }
    }

    private void executarAtualizacaoSimples(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        // Aqui você fará o update da Procedure para Nome ou Telefone
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Perfil atualizado!\"}");
    }

    private void executarAtualizacaoComplexo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        // Aqui você validará a senha antiga e trocará por e-mail/senha nova
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Credenciais alteradas com sucesso!\"}");
    }

    private void executarMudancaMensalista(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Cliente clienteAtual = new Cliente(1, null, null, null, null, null, null, null, false, null);
        boolean alterado = clienteDao.mudarStatusMensalista(clienteAtual);
        if (alterado) {
            res.getWriter().write("{\"sucesso\": true, \"novoStatus\": " + clienteAtual.getMensalista() + "}");
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro ao alterar status.\"}");
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
}