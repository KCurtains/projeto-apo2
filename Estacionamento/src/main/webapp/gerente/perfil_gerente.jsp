<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyParking - Perfil</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="../CSS/global.css">
    <link rel="stylesheet" href="../CSS/gerente.css">

</head>
<body>

    <div class="app-container">
        
        <div class="header-title container-md">Perfil</div>

        <div class="content-area container-md" id="perfil-container">
            
            <div class="profile-block" id="block-nome">
                <span class="profile-label">Nome</span>
                <div class="d-flex align-items-center view-mode">
                    <span class="profile-value" id="val-nome">Fabio Henrique Baptista</span>
                    <i class="bi bi-pencil-square ms-2 edit-icon" onclick="toggleEdit('nome')"></i>
                </div>
                <div class="edit-mode d-none d-flex align-items-center mt-1">
                    <input type="text" class="form-control form-control-sm me-2" id="input-nome" value="Fabio Henrique Baptista">
                    <button class="btn-check-orange" onclick="salvarSimples('nome')"><i class="bi bi-check-lg"></i></button>
                </div>
            </div>

            <div class="profile-block">
                <span class="profile-label">CPF</span>
                <div class="d-flex align-items-center">
                    <span class="profile-value">48923</span>
                </div>
            </div>

            <div class="profile-block" id="block-email">
                <span class="profile-label">Email</span>
                <div class="d-flex align-items-center view-mode">
                    <span class="profile-value" id="val-email">nooba*****9@gmail.com</span>
                    <i class="bi bi-pencil-square ms-2 edit-icon" onclick="toggleEdit('email')"></i>
                </div>
                <div class="edit-mode d-none mt-2">
                    <input type="email" class="form-control form-control-sm mb-2" id="input-email-atual" placeholder="Email atual">
                    <input type="email" class="form-control form-control-sm mb-2" id="input-email-novo" placeholder="Novo email">
                    <input type="email" class="form-control form-control-sm mb-3" id="input-email-confirma" placeholder="Confirmar novo email">
                    
                    <div class="d-flex gap-2">
                        <button class="btn btn-orange btn-sm w-50 py-2" onclick="salvarComplexo('email')">Salvar</button>
                        <button class="btn btn-red btn-sm w-50 py-2" onclick="cancelarEdit('email')">Cancelar</button>
                    </div>
                </div>
            </div>

            <div class="profile-block" id="block-numero">
                <span class="profile-label">Número</span>
                <div class="d-flex align-items-center view-mode">
                    <span class="profile-value" id="val-numero">(11) 94002-8922</span>
                    <i class="bi bi-pencil-square ms-2 edit-icon" onclick="toggleEdit('numero')"></i>
                </div>
                <div class="edit-mode d-none d-flex align-items-center mt-1">
                    <input type="text" class="form-control form-control-sm me-2" id="input-numero" value="(11) 94002-8922">
                    <button class="btn-check-orange" onclick="salvarSimples('numero')"><i class="bi bi-check-lg"></i></button>
                </div>
            </div>

            <div class="profile-block" id="block-senha">
                <span class="profile-label">Senha</span>
                <div class="d-flex align-items-center view-mode">
                    <span class="profile-value" id="val-senha">**********</span>
                    <i class="bi bi-pencil-square ms-2 edit-icon" onclick="toggleEdit('senha')"></i>
                </div>
                <div class="edit-mode d-none mt-2">
                    <input type="password" class="form-control form-control-sm mb-2" id="input-senha-atual" placeholder="Senha atual">
                    <input type="password" class="form-control form-control-sm mb-2" id="input-senha-nova" placeholder="Nova senha">
                    <input type="password" class="form-control form-control-sm mb-3" id="input-senha-confirma" placeholder="Confirmar nova senha">
                    
                    <div class="d-flex gap-2">
                        <button class="btn btn-orange btn-sm w-50 py-2" onclick="salvarComplexo('senha')">Salvar</button>
                        <button class="btn btn-red btn-sm w-50 py-2" onclick="cancelarEdit('senha')">Cancelar</button>
                    </div>
                </div>
            </div>

        </div>

        <div class="bottom-nav">
            <a href="relatorio.jsp" class="nav-item"><i class="bi bi-list-task"></i></a>
            <a href="patio.jsp" class="nav-item"><i class="bi bi-exclamation-circle"></i></a>
            <a href="reclamacao_gerente.jsp" class="nav-item"><i class="bi bi-car-front"></i></a>
            <a href="perfil_gerente.jsp" class="nav-item active"><i class="bi bi-person-circle"></i></a>
        </div>

    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        
        function toggleEdit(campo) {          
            cancelarTodosEdits();
            
            const block = $('#block-' + campo);
            block.find('.view-mode').addClass('d-none');
            block.find('.edit-mode').removeClass('d-none');
        }

        function cancelarEdit(campo) {
            const block = $('#block-' + campo);
            block.find('.edit-mode').addClass('d-none');
            block.find('.view-mode').removeClass('d-none');
        }

        function cancelarTodosEdits() {
            $('.profile-block').each(function() {
                $(this).find('.edit-mode').addClass('d-none');
                $(this).find('.view-mode').removeClass('d-none');
            });
        }

        function salvarSimples(campo) {
            const novoValor = $('#input-' + campo).val();
            
            if(!novoValor) {
                alert("O campo não pode ficar vazio.");
                return;
            }

            $.ajax({
            	url: '../gerente?acao=atualizarSimples',
                type: 'POST',
                data: JSON.stringify({ campo: campo, valor: novoValor }),
                contentType: 'application/json',
                success: function(response) {
                    $('#val-' + campo).text(novoValor);
                    cancelarEdit(campo);
                },
                error: function() {
                    alert("Simulação: " + campo + " atualizado com sucesso!");
                    $('#val-' + campo).text(novoValor);
                    cancelarEdit(campo);
                }
            });
        }

        function salvarComplexo(campo) {
            const atual = $('#input-' + campo + '-atual').val();
            const novo = $('#input-' + campo + '-novo').val();
            const confirma = $('#input-' + campo + '-confirma').val();

            if(!atual || !novo || !confirma) {
                alert("Preencha todos os campos.");
                return;
            }

            if(novo !== confirma) {
                alert("Os novos valores não coincidem.");
                return;
            }

            $.ajax({
            	url: '../gerente?acao=atualizarComplexo',
                type: 'POST',
                data: JSON.stringify({ 
                    tipo: campo, 
                    valorAtual: atual, 
                    novoValor: novo 
                }),
                contentType: 'application/json',
                success: function(response) {
                    alert(campo + " atualizado com sucesso!");
                    
                    if(campo === 'email') {
                        $('#val-email').text(novo); 
                    }

                    $('#block-' + campo).find('input').val('');
                    cancelarEdit(campo);
                },
                error: function() {
                    alert("Simulação: " + campo + " atualizado com sucesso!");
                    if(campo === 'email') $('#val-email').text(novo);
                    $('#block-' + campo).find('input').val('');
                    cancelarEdit(campo);
                }
            });
        }
    </script>
</body>
</html>