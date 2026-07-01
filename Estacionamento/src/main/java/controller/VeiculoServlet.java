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
import dao.VeiculoDao;
import model.Usuario;
import model.Veiculo;
import Enum.TipoVeiculoEnum;

@WebServlet("/veiculo")
public class VeiculoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private VeiculoDao veiculoDao = new VeiculoDao();
    private Gson gson = new Gson();

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
            BufferedReader reader = req.getReader();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

            Veiculo v = new Veiculo();
            v.setPlaca(json.get("placa").getAsString());
            v.setModelo(json.get("modelo").getAsString());
            v.setCor(json.get("cor").getAsString());
            v.setMotoristaPrincipal(usuario.getId());
            v.setTipoVeiculo(TipoVeiculoEnum.valueOf(json.get("tipoVeiculo").getAsString().toUpperCase()));

            veiculoDao.adicionarVeiculo(v);
            out.print("{\"mensagem\": \"Veículo cadastrado com sucesso!\"}");
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\": \"Erro ao cadastrar veículo: " + e.getMessage() + "\"}");
        }
    }

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

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
            List<Veiculo> veiculos = veiculoDao.listarPorCliente(usuario.getId());
            out.print(gson.toJson(veiculos));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\": \"Erro ao buscar veículos: " + e.getMessage() + "\"}");
        }
    }
}