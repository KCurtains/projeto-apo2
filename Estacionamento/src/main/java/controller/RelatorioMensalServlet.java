package controller;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import dao.RelatorioMensalDao;
import model.RelatorioMensal;

@WebServlet("/relatorio")
public class RelatorioMensalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RelatorioMensalDao relatorioDao = new RelatorioMensalDao();

    // Só o gerente logado pode acessar os relatórios.
    private boolean gerenteAutorizado(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("gerenteLogado") != null;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!gerenteAutorizado(req)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Acesso restrito ao gerente.\"}");
            return;
        }

        String acao = req.getParameter("acao");

        if ("baixarPdf".equals(acao)) {
            baixarPdf(req, res);
            return;
        }

        // Padrão: lista os relatórios em JSON (para a tabela do relatorio.jsp)
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            List<RelatorioMensal> relatorios = relatorioDao.listarTodos();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < relatorios.size(); i++) {
                RelatorioMensal r = relatorios.get(i);
                json.append("{")
                    .append("\"id\":").append(r.getId()).append(",")
                    .append("\"gerado\":\"").append(r.getHorarioGerado().format(fmt)).append("\",")
                    .append("\"ganhos\":").append(r.getGanhos()).append(",")
                    .append("\"carros\":").append(r.getQntdClientesCarro()).append(",")
                    .append("\"motos\":").append(r.getQntdClientesMoto()).append(",")
                    .append("\"caminhoes\":").append(r.getQntdClientesCaminhao()).append(",")
                    .append("\"tempoMedio\":").append(r.getTempoMedioEstadia()).append(",")
                    .append("\"reclamacoes\":").append(r.getReclamacoesRegistradas()).append(",")
                    .append("\"multas\":").append(r.getMultasAplicadas())
                    .append("}");
                if (i < relatorios.size() - 1) json.append(",");
            }
            json.append("]");
            res.getWriter().write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro ao listar relatórios.\"}");
        }
    }

    // Geração manual sob demanda (botão "Gerar relatório do mês" na tela do gerente).
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        if (!gerenteAutorizado(req)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Acesso restrito ao gerente.\"}");
            return;
        }

        String acao = req.getParameter("acao");
        try {
            if ("gerar".equals(acao)) {
                relatorioDao.gerarRelatorioMesAtual();
                res.setStatus(HttpServletResponse.SC_CREATED);
                res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Relatório do mês gerado com sucesso!\"}");
            } else {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Ação desconhecida.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro ao gerar relatório.\"}");
        }
    }

    // Monta e envia o PDF do relatório escolhido.
    private void baixarPdf(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        RelatorioMensal r = relatorioDao.buscarPorId(Integer.parseInt(idStr));
        if (r == null) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        res.setContentType("application/pdf");
        res.setHeader("Content-Disposition", "attachment; filename=relatorio_mensal_" + r.getId() + ".pdf");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try (OutputStream os = res.getOutputStream()) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, os);
            doc.open();

            Font fTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font fSub = FontFactory.getFont(FontFactory.HELVETICA, 11);

            doc.add(new Paragraph("EasyParking — Relatório Mensal", fTitulo));
            doc.add(new Paragraph("Relatório #" + r.getId() + "  |  Gerado em: " + r.getHorarioGerado().format(fmt), fSub));
            doc.add(new Paragraph(" "));

            PdfPTable tabela = new PdfPTable(2);
            tabela.setWidthPercentage(100);
            addLinha(tabela, "Ganhos no mês (R$)", String.format("%.2f", r.getGanhos()));
            addLinha(tabela, "Reservas de carros", String.valueOf(r.getQntdClientesCarro()));
            addLinha(tabela, "Reservas de motos", String.valueOf(r.getQntdClientesMoto()));
            addLinha(tabela, "Reservas de caminhões", String.valueOf(r.getQntdClientesCaminhao()));
            addLinha(tabela, "Tempo médio de estadia (h)", String.format("%.2f", r.getTempoMedioEstadia()));
            addLinha(tabela, "Reclamações registradas", String.valueOf(r.getReclamacoesRegistradas()));
            addLinha(tabela, "Multas aplicadas", String.valueOf(r.getMultasAplicadas()));
            doc.add(tabela);

            doc.close();
        } catch (DocumentException e) {
            throw new IOException("Falha ao gerar o PDF do relatório", e);
        }
    }

    private void addLinha(PdfPTable tabela, String rotulo, String valor) {
        tabela.addCell(rotulo);
        tabela.addCell(valor);
    }
}
