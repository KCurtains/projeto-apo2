package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.PatioDao;
import model.Patio;

@WebServlet("/patio")
public class PatioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PatioDao patioDao = new PatioDao();

    public PatioServlet() {
        super();
    }


    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        //AÇÃO: LISTAR PÁTIOS
        if ("listar".equals(acao)) {
            try {
                List<Patio> patios = patioDao.listarPatios();
                StringBuilder json = new StringBuilder("[");
                
                for (int i = 0; i < patios.size(); i++) {
                    Patio p = patios.get(i);
                    
                    // remove quebras de linha e escapa aspas
                    String enderecoSeguro = p.getEndereco() != null ? 
                            p.getEndereco().replace("\"", "\\\"").replace("\n", " ") : "";
                    
                    json.append(String.format(
                        "{\"id\":%d,\"nome\":\"%s\",\"vagasCarro\":%d,\"vagasMoto\":%d,\"vagasCaminhao\":%d}",
                        p.getId(), enderecoSeguro, p.getCapacidadeCarro(), p.getCapacidadeMoto(), p.getCapacidadeCaminhao()
                    ));
                    
                    if (i < patios.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");
                
                res.setStatus(HttpServletResponse.SC_OK);
                res.getWriter().write(json.toString());
            } catch (Exception e) {
                e.printStackTrace();
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro ao listar pátios.\"}");
            }
            return;
        }

        // AÇÃO: VERIFICAR DISPONIBILIDADE
        if ("verificarDisponibilidade".equals(acao)) {
            try {
                String idStr = req.getParameter("id");
                String tipoVeiculo = req.getParameter("tipoVeiculo");

                if (idStr == null || tipoVeiculo == null) {
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"ID do pátio e tipo de veículo são obrigatórios.\"}");
                    return;
                }

                int id = Integer.parseInt(idStr);
                Patio patio = new Patio(id, "", 0, 0, 0);
                int vagasDisponiveis = patioDao.verificarDisponibilidadeVaga(patio, tipoVeiculo);

                res.setStatus(HttpServletResponse.SC_OK);
                res.getWriter().write("{\"sucesso\": true, \"vagasDisponiveis\": " + vagasDisponiveis + "}");
            } catch (Exception e) {
                e.printStackTrace();
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro ao verificar disponibilidade de vagas.\"}");
            }
            return;
        }
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");
        String jsonBody = lerCorpoRequisicao(req);

        // * talvez fique mais limpo com um switch case. Talvez seja melhor a gente mudar depois.
        try {
            //AÇÃO: ADICIONAR PÁTIO
            if ("adicionar".equals(acao)) {
                String endereco = extrairCampoJson(jsonBody, "endereco");
                int capCarro = Integer.parseInt(extrairCampoJson(jsonBody, "capacidadeCarro"));
                int capMoto = Integer.parseInt(extrairCampoJson(jsonBody, "capacidadeMoto"));
                int capCaminhao = Integer.parseInt(extrairCampoJson(jsonBody, "capacidadeCaminhao"));

                Patio novoPatio = new Patio(0, endereco, capCarro, capMoto, capCaminhao);
                patioDao.adicionarPatio(novoPatio);

                res.setStatus(HttpServletResponse.SC_CREATED);
                res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Pátio cadastrado com sucesso!\"}");
                return;
            }

            //AÇÃO: ATUALIZAR PÁTIO
            if ("atualizar".equals(acao)) {
                int id = Integer.parseInt(extrairCampoJson(jsonBody, "id"));
                String endereco = extrairCampoJson(jsonBody, "endereco");
                int capCarro = Integer.parseInt(extrairCampoJson(jsonBody, "capacidadeCarro"));
                int capMoto = Integer.parseInt(extrairCampoJson(jsonBody, "capacidadeMoto"));
                int capCaminhao = Integer.parseInt(extrairCampoJson(jsonBody, "capacidadeCaminhao"));

                Patio patioAtualizar = new Patio(id, endereco, capCarro, capMoto, capCaminhao);
                patioDao.atualizarPatio(patioAtualizar);

                res.setStatus(HttpServletResponse.SC_OK);
                res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Pátio atualizado com sucesso!\"}");
                return;
            }

            //AÇÃO: REMOVER PÁTIO
            if ("remover".equals(acao)) {
                int id = Integer.parseInt(extrairCampoJson(jsonBody, "id"));

                Patio patioRemover = new Patio(id, "", 0, 0, 0);
                patioDao.removerPatio(patioRemover);

                res.setStatus(HttpServletResponse.SC_OK);
                res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Pátio removido do sistema!\"}");
                return;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro ao processar operação no pátio.\"}");
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
            inicio = json.indexOf(":", inicio) + 1;
            
            // Pula espaços em branco após os dois pontos
            while(json.charAt(inicio) == ' ') inicio++;
            
            int fim;
            if (json.charAt(inicio) == '"') {
                // Se for texto tem aspas, procura a aspa final
                inicio++; 
                fim = json.indexOf("\"", inicio);
            } else {
                // Se for número, procura a próxima vírgula ou fechamento de chaves
                fim = json.indexOf(",", inicio);
                if (fim == -1 || json.indexOf("}", inicio) < fim) {
                    fim = json.indexOf("}", inicio);
                }
            }
            
            return json.substring(inicio, fim).trim();
        } catch (Exception e) {
            return "";
        }
    }
}