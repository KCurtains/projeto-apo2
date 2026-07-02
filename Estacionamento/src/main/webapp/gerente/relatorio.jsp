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
            <!-- Botão para o gerente gerar o relatório do mês corrente sob demanda -->
            <button class="btn btn-orange w-100 mb-3" onclick="gerarRelatorio()">
                <i class="bi bi-file-earmark-bar-graph"></i> Gerar relatório do mês
            </button>

            <div id="empty-state" class="text-center text-muted mt-5 d-none">
                <p>Nenhum relatório disponível no momento.</p>
            </div>

            <div id="list-reports"></div>
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

        // Busca a lista REAL de relatórios no backend (/relatorio?acao=listar)
        function carregarRelatorios() {
            $.ajax({
                url: '../relatorio?acao=listar',
                type: 'GET',
                dataType: 'json',
                success: function(relatorios) {
                    const listDiv = $('#list-reports');
                    listDiv.empty();

                    if (!relatorios || relatorios.length === 0) {
                        $('#empty-state').removeClass('d-none');
                        return;
                    }
                    $('#empty-state').addClass('d-none');

                    relatorios.forEach(function(r) {
                        const cardHtml =
                            '<div class="report-card d-flex justify-content-between align-items-center">' +
                                '<span class="report-month">' + r.gerado + '</span>' +
                                '<span class="text-muted small">R$ ' + Number(r.ganhos).toFixed(2) + '</span>' +
                                '<button class="btn-cloud" onclick="baixarRelatorio(' + r.id + ')">' +
                                    '<i class="bi bi-cloud-arrow-down"></i>' +
                                '</button>' +
                            '</div>';
                        listDiv.append(cardHtml);
                    });
                },
                error: function(err) {
                    if (err.status === 401) {
                        alert("Sessão de gerente expirada. Faça login novamente.");
                    } else {
                        alert("Erro ao carregar os relatórios.");
                    }
                }
            });
        }

        // Gera o relatório do mês corrente sob demanda
        function gerarRelatorio() {
            $.ajax({
                url: '../relatorio?acao=gerar',
                type: 'POST',
                success: function() {
                    alert("Relatório do mês gerado com sucesso!");
                    carregarRelatorios();
                },
                error: function() {
                    alert("Erro ao gerar o relatório.");
                }
            });
        }

        // Baixa o PDF: basta redirecionar para o endpoint que devolve o arquivo
        function baixarRelatorio(idRelatorio) {
            window.location.href = '../relatorio?acao=baixarPdf&id=' + idRelatorio;
        }
    </script>
</body>
</html>
