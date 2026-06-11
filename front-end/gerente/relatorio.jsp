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
    
    <style>
        body {
            background-color: #f4f4f4;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            display: flex;
            justify-content: center;
            min-height: 100vh;
            margin: 0;
        }
        
        .app-container {
            width: 100%;
            background-color: #ffffff;
            position: relative;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
            overflow-x: hidden;
        }

        .header-title {
            padding: 30px 20px 20px 20px;
            font-weight: 900;
            font-size: 1.6rem;
        }

        .content-area {
            flex-grow: 1;
            padding: 0 20px 20px 20px;
            overflow-y: auto;
            padding-bottom: 90px; 
        }

        .report-card {
            background-color: #f8f9fa; 
            border: 1px solid #e9ecef;
            border-radius: 10px;
            padding: 10px 10px 10px 20px;
            margin-bottom: 15px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .report-month {
            font-weight: bold;
            font-size: 1.1rem;
            color: #000;
        }

        .btn-cloud {
            background-color: #f98825;
            color: white;
            border: none;
            border-radius: 8px;
            padding: 8px 15px;
            font-size: 1.2rem;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: background-color 0.2s;
        }

        .btn-cloud:hover {
            background-color: #e07a21;
        }

        .bottom-nav {
            background-color: #ffffff;
            border-top: 1px solid #ddd;
            display: flex;
            justify-content: space-around;
            align-items: center;
            padding: 10px 0;
            position: absolute;
            bottom: 0;
            width: 100%;
            z-index: 1000;
        }

        .nav-item {
            color: #000;
            font-size: 1.5rem;
            padding: 5px 20px;
            border-radius: 8px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .nav-item.active {
            background-color: #f98825;
            color: white;
        }
    </style>
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