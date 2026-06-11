package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/relatorio")
public class RelatorioMensalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public RelatorioMensalServlet() {
        super();

    }


	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.getWriter().append("Served at: ").append(req.getContextPath());
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		doGet(req, res);
	}

}
