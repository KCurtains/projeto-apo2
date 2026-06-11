<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyParking - Home Gerente</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="../CSS/global.css">
    <link rel="stylesheet" href="../CSS/gerente.css">

</head>
<body>

    <div class="app-container">
        
        <div class="header-title container-md">
            Relatórios Mensais
        </div>

        <div class="content-area container-md">
            <div id="empty-state" class="text-center text-muted mt-5 d-none">
                <p>Nenhum relatório disponível no momento.</p>
            </div>

            <div id="list-reports">
                </div>
        </div>

        <div class="bottom-nav">
            <a href="relatorio.jsp" class="nav-item active"><i class="bi bi-list-task"></i></a>
            <a href="patio.jsp" class="nav-item"><i class="bi bi-exclamation-circle"></i></a>
            <a href="reclamacao_gerente.jsp" class="nav-item"><i class="bi bi-car-front"></i></a>
            <a href="perfil_gerente.jsp" class="nav-item"><i class="bi bi-person-circle"></i></a>
        </div>

    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <script>
        $(document).ready(function() {
            carregarRelatorios();
        });

        function carregarRelatorios() {
            const relatoriosMock = [
                { id: "05-2026", rotulo: "05/26" },
                { id: "04-2026", rotulo: "04/26" }
            ];
            
            const listDiv = $('#list-reports');
            listDiv.empty();

            if(relatoriosMock.length === 0) {
                $('#empty-state').removeClass('d-none');
            } else {
                relatoriosMock.forEach(relatorio => {
                    const cardHtml = `
                        <div class="report-card">
                            <span class="report-month">` + relatorio.rotulo + `</span>
                            <button class="btn-cloud" onclick="baixarRelatorio('` + relatorio.id + `')">
                                <i class="bi bi-cloud-arrow-down"></i>
                            </button>
                        </div>
                    `;
                    listDiv.append(cardHtml);
                });
            }
        }

        function baixarRelatorio(idRelatorio) {
            /* A melhor forma de baixar um arquivo em uma aplicação Web 
               é redirecionar a janela para o Endpoint que cospe o arquivo. 
               Seu Servlet deve retornar um cabeçalho assim:
               response.setHeader("Content-Disposition", "attachment; filename=relatorio.pdf");
            */
            
            alert("Iniciando download do relatório: " + idRelatorio);
            
            // Quando tiver o backend em Java, descomente a linha abaixo:
            // window.location.href = 'BaixarRelatorioServlet?id=' + idRelatorio;
        }
    </script>
</body>
</html>