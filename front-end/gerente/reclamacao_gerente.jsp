<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyParking - Gestão de Reclamações</title>
    
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

        .reclamacao-card {
            background-color: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 15px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .badge-status {
            color: #333;
            font-weight: bold;
            font-size: 0.75rem;
            padding: 5px 10px;
            border-radius: 15px;
            display: inline-block;
            margin-top: 5px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .status-analise { background-color: #fce83a; }
        .status-resolvido { background-color: #4caf50; color: white; }
        .status-nao-resolvido { background-color: #f44336; color: white; }

        .btn-icon-orange { background-color: #f98825; color: white; border: none; }
        .btn-icon-orange:hover { background-color: #e07a21; color: white; }

        .bottom-nav {
            background-color: #ffffff;
            border-top: 1px solid #ddd;
            display: flex;
            justify-content: space-around;
            padding: 10px 0;
            position: absolute;
            bottom: 0;
            width: 100%;
            z-index: 1000;
        }

        .nav-item { color: #333; font-size: 1.5rem; padding: 5px 15px; border-radius: 8px; cursor: pointer; }
        .nav-item.active { background-color: #f98825; color: white; }

        .modal-content { border-radius: 15px; border: none; }
        .modal-header { border-bottom: none; padding-bottom: 0; }
        .modal-footer { border-top: none; }
        
        .form-select { border-radius: 10px; border: 1px solid #ccc; font-weight: bold; }
        
        .btn-orange { background-color: #f98825; color: white; font-weight: bold; border-radius: 20px; width: 100%; padding: 10px; border: none; }
        .btn-orange:hover { background-color: #e07a21; color: white; }
    </style>
</head>
<body>

    <div class="app-container">
        
        <div class="header-title container-md">
            Reclamações
        </div>

        <div class="content-area container-md">
            
            <div id="empty-state" class="text-center text-muted mt-5 d-none">
                <p>Não há reclamações no momento.</p>
            </div>

            <div id="list-state">
                </div>

        </div>

        <div class="bottom-nav">
            <a href="relatorio.jsp" class="nav-item"><i class="bi bi-list-task"></i></a>
            <a href="patio.jsp" class="nav-item"><i class="bi bi-exclamation-circle"></i></a>
            <a href="reclamacao_gerente.jsp" class="nav-item active"><i class="bi bi-car-front"></i></a>
            <a href="perfil_gerente.jsp" class="nav-item"><i class="bi bi-person-circle"></i></a>
        </div>

    </div>

    <div class="modal fade" id="modalAtualizarReclamacao" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Reclamações</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="infoRecId">

                    <div class="mb-3">
                        <small class="text-muted d-block">Veículo</small>
                        <strong id="infoRecVeiculo"></strong>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Data da Reserva</small>
                        <strong id="infoRecData"></strong>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Reclamação</small>
                        <strong id="infoRecTexto"></strong>
                    </div>
                    
                    <div class="mb-4 mt-4">
                        <label class="small text-muted d-block mb-1">Status</label>
                        <select class="form-select" id="editRecStatus">
                            <option value="Em análise">Em análise</option>
                            <option value="Resolvido">Resolvido</option>
                            <option value="Não resolvido">Não resolvido</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer pb-4">
                    <button type="button" class="btn btn-orange" onclick="atualizarStatus()">Atualizar Status</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        $(document).ready(function() {
            carregarReclamacoes();
        });

        function carregarReclamacoes() {
            const reclamacoesMock = [
                { 
                    id: 1, 
                    veiculo: "Ford Ka 2012 Vermelho - AVW4G37", 
                    data: "23/04/2026", 
                    texto: "O pátio estava sem luz e tive dificuldade de encontrar meu veículo.",
                    status: "Em análise" 
                },
                { 
                    id: 2, 
                    veiculo: "Honda Civic 2020 Prata - ABC1D23", 
                    data: "20/04/2026", 
                    texto: "Fui cobrado duas vezes no cartão de crédito.",
                    status: "Não resolvido" 
                }
            ];
            
            const listDiv = $('#list-state');
            listDiv.empty();

            if(reclamacoesMock.length === 0) {
                $('#empty-state').removeClass('d-none');
                $('#list-state').addClass('d-none');
            } else {
                $('#empty-state').addClass('d-none');
                $('#list-state').removeClass('d-none');

                reclamacoesMock.forEach(rec => {
                    let classeCor = 'status-analise';
                    if(rec.status === 'Resolvido') classeCor = 'status-resolvido';
                    else if(rec.status === 'Não resolvido') classeCor = 'status-nao-resolvido';

                    const recDataString = encodeURIComponent(JSON.stringify(rec));

                    const cardHtml = `
                        <div class="reclamacao-card">
                            <div>
                                <strong class="d-block">`+ rec.data +`</strong>
                                <span class="badge-status `+ classeCor +`">`+ rec.status +`</span>
                            </div>
                            <div>
                                <button class="btn btn-icon-orange btn-sm rounded-3" onclick="abrirModalAtualizar('`+ recDataString +`')">
                                    <i class="bi bi-info-circle"></i>
                                </button>
                            </div>
                        </div>
                    `;
                    listDiv.append(cardHtml);
                });
            }
        }

        function abrirModalAtualizar(recDataEncoded) {
            const rec = JSON.parse(decodeURIComponent(recDataEncoded));

            $('#infoRecId').val(rec.id);
            $('#infoRecVeiculo').text(rec.veiculo);
            $('#infoRecData').text(rec.data);
            $('#infoRecTexto').text('"' + rec.texto + '"');
            $('#editRecStatus').val(rec.status);

            var myModal = new bootstrap.Modal(document.getElementById('modalAtualizarReclamacao'));
            myModal.show();
        }

        function atualizarStatus() {
            const idRec = $('#infoRecId').val();
            const novoStatus = $('#editRecStatus').val();

            $.ajax({
                url: 'AtualizarReclamacaoServlet', 
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ id: idRec, status: novoStatus }),
                dataType: 'json',
                success: function(response) {
                    alert("Status atualizado com sucesso!");
                    $('#modalAtualizarReclamacao').modal('hide');
                    carregarReclamacoes(); 
                },
                error: function() {
                    alert("Simulação: Status da reclamação " + idRec + " atualizado para '" + novoStatus + "'.");
                    $('#modalAtualizarReclamacao').modal('hide');
                    carregarReclamacoes();
                }
            });
        }
    </script>
</body>
</html>