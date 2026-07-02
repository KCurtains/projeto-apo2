package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.ClienteDao;
import dao.VeiculoDao;
import model.Cliente;
import model.Usuario;
import model.Veiculo;
import Enum.TipoVeiculoEnum;

@WebServlet("/veiculo")
public class VeiculoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private VeiculoDao veiculoDao = new VeiculoDao();
    private ClienteDao clienteDao = new ClienteDao();
    private Gson gson = new Gson();


    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("usuarioLogado") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"erro\": \"Usuário não autenticado.\"}");
            return;
        }

        String acao = req.getParameter("acao");
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        try {
            if ("motoristas".equals(acao)) {
                String veiculoIdStr = req.getParameter("veiculoId");
                if (veiculoIdStr == null || veiculoIdStr.isEmpty()) {
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"erro\": \"veiculoId é obrigatório.\"}");
                    return;
                }

                int veiculoId = Integer.parseInt(veiculoIdStr);
                Veiculo veiculo = veiculoDao.buscarPorId(veiculoId);
                if (veiculo == null || veiculo.getMotoristaPrincipal() == null
                        || veiculo.getMotoristaPrincipal() != usuario.getId()) {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"erro\": \"Este veículo não pertence a você.\"}");
                    return;
                }

                List<Cliente> motoristas = veiculoDao.listarMotoristasAutorizados(veiculoId);
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < motoristas.size(); i++) {
                    Cliente c = motoristas.get(i);
                    json.append("{")
                        .append("\"id\": ").append(c.getId()).append(",")
                        .append("\"nome\": \"").append(escapar(c.getNome())).append("\",")
                        .append("\"cpf\": \"").append(escapar(c.getCpf())).append("\",")
                        .append("\"email\": \"").append(escapar(c.getEmail())).append("\",")
                        .append("\"numero\": \"").append(escapar(c.getTelefone())).append("\"")
                        .append("}");
                    if (i < motoristas.size() - 1) json.append(",");
                }
                json.append("]");
                out.print(json.toString());
                return;
            }

            List<Veiculo> veiculos = veiculoDao.listarPorCliente(usuario.getId());
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < veiculos.size(); i++) {
                Veiculo v = veiculos.get(i);
                json.append("{")
                    .append("\"id\": ").append(v.getId()).append(",")
                    .append("\"placa\": \"").append(escapar(v.getPlaca())).append("\",")
                    .append("\"modelo\": \"").append(escapar(v.getModelo())).append("\",")
                    .append("\"cor\": \"").append(escapar(v.getCor())).append("\",")
                    .append("\"tipoVeiculo\": \"").append(v.getTipoVeiculo().name()).append("\"")
                    .append("}");
                if (i < veiculos.size() - 1) json.append(",");
            }
            json.append("]");
            out.print(json.toString());
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\": \"Erro ao buscar dados: " + e.getMessage() + "\"}");
        }
    }

    // cadastrar/atualizar/remover veículo, e autorizar/remover motoristas.
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

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        String acao = req.getParameter("acao");

        try {
            BufferedReader reader = req.getReader();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            if (json == null) json = new JsonObject();

            if ("atualizar".equals(acao)) {
                executarAtualizacao(json, usuario, res, out);
                return;
            }
            if ("remover".equals(acao)) {
                executarRemocao(json, usuario, res, out);
                return;
            }
            if ("adicionarMotorista".equals(acao)) {
                executarAdicaoMotorista(json, usuario, res, out);
                return;
            }
            if ("removerMotorista".equals(acao)) {
                executarRemocaoMotorista(json, usuario, res, out);
                return;
            }

            // cadastra um novo veículo
            Veiculo v = new Veiculo();
            v.setPlaca(json.get("placa").getAsString());
            v.setModelo(json.get("modelo").getAsString());
            v.setCor(json.get("cor").getAsString());
            v.setMotoristaPrincipal(usuario.getId());
            v.setTipoVeiculo(TipoVeiculoEnum.valueOf(json.get("tipoVeiculo").getAsString().toUpperCase()));

            veiculoDao.adicionarVeiculo(v);
            out.print("{\"sucesso\": true, \"mensagem\": \"Veículo cadastrado com sucesso!\"}");
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"sucesso\": false, \"mensagem\": \"Erro ao processar veículo: " + e.getMessage() + "\"}");
        }
    }

    private void executarAtualizacao(JsonObject json, Usuario usuario, HttpServletResponse res, PrintWriter out) throws IOException {
        int id = json.get("id").getAsInt();
        Veiculo atual = veiculoDao.buscarPorId(id);
        if (atual == null || atual.getMotoristaPrincipal() == null || atual.getMotoristaPrincipal() != usuario.getId()) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"sucesso\": false, \"mensagem\": \"Este veículo não pertence a você.\"}");
            return;
        }

        Veiculo v = new Veiculo();
        v.setId(id);
        v.setModelo(json.get("modelo").getAsString());
        v.setCor(json.get("cor").getAsString());
        v.setMotoristaPrincipal(atual.getMotoristaPrincipal()); // não muda o dono
        v.setTipoVeiculo(TipoVeiculoEnum.valueOf(json.get("tipoVeiculo").getAsString().toUpperCase()));

        veiculoDao.atualizarVeiculo(v);
        out.print("{\"sucesso\": true, \"mensagem\": \"Veículo atualizado com sucesso!\"}");
    }

    private void executarRemocao(JsonObject json, Usuario usuario, HttpServletResponse res, PrintWriter out) throws IOException {
        int id = json.get("id").getAsInt();
        Veiculo atual = veiculoDao.buscarPorId(id);
        if (atual == null || atual.getMotoristaPrincipal() == null || atual.getMotoristaPrincipal() != usuario.getId()) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"sucesso\": false, \"mensagem\": \"Este veículo não pertence a você.\"}");
            return;
        }

        Veiculo v = new Veiculo();
        v.setId(id);
        veiculoDao.removerVeiculo(v);
        out.print("{\"sucesso\": true, \"mensagem\": \"Veículo removido com sucesso!\"}");
    }

    private void executarAdicaoMotorista(JsonObject json, Usuario usuario, HttpServletResponse res, PrintWriter out) throws IOException {
        int veiculoId = json.get("veiculoId").getAsInt();
        String cpf = json.get("cpf").getAsString();

        Veiculo veiculo = veiculoDao.buscarPorId(veiculoId);
        if (veiculo == null || veiculo.getMotoristaPrincipal() == null || veiculo.getMotoristaPrincipal() != usuario.getId()) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"sucesso\": false, \"mensagem\": \"Este veículo não pertence a você.\"}");
            return;
        }

        Cliente motorista = clienteDao.buscarClientePorCpf(cpf);
        if (motorista == null) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"sucesso\": false, \"mensagem\": \"Nenhum cliente cadastrado com esse CPF.\"}");
            return;
        }

        veiculoDao.adicionarMotorista(motorista.getId(), veiculoId);
        out.print("{\"sucesso\": true, \"mensagem\": \"" + escapar(motorista.getNome()) + " foi autorizado a dirigir este veículo!\"}");
    }

    private void executarRemocaoMotorista(JsonObject json, Usuario usuario, HttpServletResponse res, PrintWriter out) throws IOException {
        int veiculoId = json.get("veiculoId").getAsInt();
        int clienteId = json.get("clienteId").getAsInt();

        Veiculo veiculo = veiculoDao.buscarPorId(veiculoId);
        if (veiculo == null || veiculo.getMotoristaPrincipal() == null || veiculo.getMotoristaPrincipal() != usuario.getId()) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"sucesso\": false, \"mensagem\": \"Este veículo não pertence a você.\"}");
            return;
        }

        veiculoDao.removerMotorista(clienteId, veiculoId);
        out.print("{\"sucesso\": true, \"mensagem\": \"Motorista removido com sucesso!\"}");
    }

    private String escapar(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
