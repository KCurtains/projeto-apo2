<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyParking - Reclamações</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="../CSS/global.css">
    <link rel="stylesheet" href="../CSS/cliente.css">
</head>
<body>

    <div class="app-container">

        <div class="header-title">
            Reclamações
        </div>

        <div class="content-area">
            <div id="empty-state-reclamacoes" class="text-center text-muted mt-5 d-none">
                <p>Você não tem reclamações listadas.<br>Clique no botão + para que possamos<br>te ajudar</p>
            </div>

            <div id="list-state-reclamacoes">
                </div>
        </div>

        <div class="fab" onclick="abrirModalCriarReclamacao()">+</div>

        <div class="bottom-nav">
            <a href="reserva.jsp" class="nav-item"><i class="bi bi-list-task"></i></a>
            <a href="reclamacao.jsp" class="nav-item active"><i class="bi bi-exclamation-circle"></i></a>
            <a href="veiculo.jsp" class="nav-item"><i class="bi bi-car-front"></i></a>
            <a href="perfil.jsp" class="nav-item"><i class="bi bi-person-circle"></i></a>
        </div>

    </div>

    <div class="modal fade" id="modalCriarReclamacao" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Reclamações</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="form-nova-reclamacao">
                        <label class="small text-muted">Estadia Relacionada</label>
                        <select class="form-select" id="recEstadia" required>
                            <option value="" disabled selected>Selecione a estadia...</option>
                        </select>

                        <label class="small text-muted">Reclamação</label>
                        <div class="position-relative">
                            <textarea class="form-control" id="recTexto" rows="6" placeholder="Conte-nos sobre seu problema" maxlength="2000" required></textarea>
                            <small class="text-muted position-absolute" style="bottom: 10px; right: 15px; font-size: 0.7rem;" id="charCount">0/2000</small>
                        </div>

                        <p class="text-muted small text-center mt-3" style="font-size: 0.75rem;">
                            Após enviado, a reclamação entrará em análise. Em breve entraremos em contato por email ou whatsapp.
                        </p>
                    </form>
                </div>
                <div class="modal-footer pb-4">
                    <button type="button" class="btn btn-orange" onclick="salvarReclamacao()">Enviar Reclamação</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="modalInfoReclamacao" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Reclamações</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <small class="text-muted d-block">Veículo</small>
                        <strong id="infoRecVeiculo"></strong>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Data da Estadia</small>
                        <strong id="infoRecData"></strong>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Reclamação</small>
                        <strong id="infoRecTexto"></strong>
                    </div>
                    <div class="mb-4">
                        <small class="text-muted d-block">Status</small>
                        <strong id="infoRecStatus"></strong>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        let reclamacoesCarregadas = [];

        $(document).ready(function() {
            carregarReclamacoes();

            $('#recTexto').on('input', function() {
                var currentLength = $(this).val().length;
                $('#charCount').text(currentLength + '/2000');
            });
        });

        function carregarReclamacoes() {
            $.ajax({
                url: '../reclamacao?acao=listarMinhas',
                type: 'GET',
                dataType: 'json',
                success: function(response) {
                    reclamacoesCarregadas = response;
                    renderizarListaReclamacoes(response);
                },
                error: function(xhr) {
                    console.error("Erro ao buscar as reclamações:", xhr.responseText);
                    $('#empty-state-reclamacoes').removeClass('d-none');
                    $('#list-state-reclamacoes').addClass('d-none');
                }
            });
        }

        function renderizarListaReclamacoes(reclamacoes) {
            const listDiv = $('#list-state-reclamacoes');
            listDiv.empty();

            if(reclamacoes.length === 0) {
                $('#empty-state-reclamacoes').removeClass('d-none');
                $('#list-state-reclamacoes').addClass('d-none');
            } else {
                $('#empty-state-reclamacoes').addClass('d-none');
                $('#list-state-reclamacoes').removeClass('d-none');

                reclamacoes.forEach(rec => {
                    const cardHtml = `
                        <div class="reclamacao-card">
                            <div>
                                <strong class="d-block">`+ rec.data +`</strong>
                                <span class="badge-analise">`+ rec.status +`</span>
                            </div>
                            <div>
                                <button class="btn btn-icon-orange btn-sm rounded-3" onclick="abrirModalInfoReclamacao(`+ rec.id +`)">
                                    <i class="bi bi-info-circle"></i>
                                </button>
                            </div>
                        </div>
                    `;
                    listDiv.append(cardHtml);
                });
            }
        }

        function abrirModalCriarReclamacao() {
            $('#form-nova-reclamacao')[0].reset();
            $('#charCount').text('0/2000');

            $.ajax({
                url: '../estadia?acao=listarMinhas',
                type: 'GET',
                dataType: 'json',
                success: function(estadias) {
                    const select = $('#recEstadia');
                    select.find('option:not(:first)').remove();
                    estadias.forEach(e => {
                        select.append('<option value="' + e.id + '">' + e.veiculo + ' - ' + e.data + '</option>');
                    });
                },
                error: function() {
                    alert("Não foi possível carregar suas estadias. Tente novamente mais tarde.");
                }
            });

            var myModal = new bootstrap.Modal(document.getElementById('modalCriarReclamacao'));
            myModal.show();
        }

        function abrirModalInfoReclamacao(id) {
            const rec = reclamacoesCarregadas.find(r => r.id === id);
            if (rec) {
                $('#infoRecVeiculo').text(rec.veiculo);
                $('#infoRecData').text(rec.data);
                $('#infoRecTexto').text('"' + rec.texto + '"');
                $('#infoRecStatus').text(rec.status);
            }
            var myModal = new bootstrap.Modal(document.getElementById('modalInfoReclamacao'));
            myModal.show();
        }

        function salvarReclamacao() {
            const dadosReclamacao = {
                idEstadia: $('#recEstadia').val(),
                texto: $('#recTexto').val()
            };

            if(!dadosReclamacao.idEstadia || !dadosReclamacao.texto) {
                alert("Por favor, preencha todos os campos.");
                return;
            }

            $.ajax({
            	url: '../reclamacao?acao=adicionar',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(dadosReclamacao),
                dataType: 'json',
                success: function(response) {
                    alert("Reclamação enviada com sucesso!");
                    $('#modalCriarReclamacao').modal('hide');
                    carregarReclamacoes();
                },
                error: function(xhr) {
                    alert("Não foi possível enviar a reclamação: " + (xhr.responseJSON ? xhr.responseJSON.mensagem : "erro no servidor."));
                }
            });
        }
    </script>
</body>
</html>
