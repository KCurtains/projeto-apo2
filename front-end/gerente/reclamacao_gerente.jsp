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