<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyParking - Gestão de Clientes</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="../CSS/global.css">
    <link rel="stylesheet" href="../CSS/funcionario.css">
    
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
            padding: 20px;
            font-weight: bold;
            font-size: 1.5rem;
            position: relative;
        }

        .btn-back-header {
            position: absolute;
            left: 20px;
            top: 25px;
            cursor: pointer;
            font-size: 1.2rem;
            color: #333;
        }

        .content-area {
            flex-grow: 1;
            padding: 0 20px 20px 20px;
            overflow-y: auto;
            padding-bottom: 90px;
        }

        /* Input de Pesquisa */
        .search-label {
            font-weight: bold;
            color: #999;
            font-size: 1.1rem;
            margin-bottom: 5px;
            display: block;
        }
        .input-group-search .form-control { border: 1px solid #333; border-radius: 5px 0 0 5px; }
        .input-group-search .btn { border: 1px solid #333; border-radius: 0 5px 5px 0; border-left: none; }

        /* Card do Resultado da Pesquisa */
        .item-card {
            background-color: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 10px;
            padding: 12px 15px;
            margin-bottom: 15px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .item-card-text { font-weight: bold; color: #333; font-size: 1rem; display: flex; align-items: center; gap: 15px; }

        /* Estilos do Perfil do Cliente */
        .profile-block { margin-bottom: 20px; }
        .profile-label { font-size: 0.8rem; color: #888; margin-bottom: 2px; display: block; }
        .profile-value { font-weight: bold; font-size: 1rem; color: #000; }
        .edit-icon { color: #333; cursor: pointer; font-size: 1.1rem; }
        
        .form-control-edit { border-radius: 5px; border: 1px solid #666; width: 100%; padding: 5px 10px;}
        .btn-check-orange { background-color: #f98825; color: white; border: none; border-radius: 3px; padding: 5px 10px; display: flex; align-items: center; justify-content: center; }
        .btn-check-orange:hover { background-color: #e07a21; color: white; }

        /* Botões Gerais */
        .btn-orange { background-color: #f98825; color: white; border: none; }
        .btn-orange:hover { background-color: #e07a21; color: white; }
        .btn-orange-rounded { background-color: #f98825; color: white; font-weight: bold; border-radius: 20px; border: none; }
        .btn-red-rounded { background-color: #dc3545; color: white; font-weight: bold; border-radius: 20px; border: none; }

        /* Barra de Navegação Inferior */
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
    </style>
</head>
<body>

    <div class="app-container">
        
        <div id="view-consulta" class="d-flex flex-column h-100">
            <div class="header-title container-md">Clientes</div>

            <div class="content-area container-md">
                <label class="search-label">Consultar:</label>
                <div class="input-group input-group-search mb-4">
                    <input type="text" class="form-control" id="input-pesquisa" placeholder="Nome do cliente">
                    <button class="btn btn-orange" type="button" onclick="pesquisarCliente()">
                        <i class="bi bi-search text-dark" style="font-weight: bold;"></i>
                    </button>
                </div>

                <div id="lista-resultados">
                    </div>
            </div>
        </div>

        <div id="view-perfil" class="d-none flex-column h-100">
            <div class="header-title container-md text-center">
                <i class="bi bi-arrow-left btn-back-header" onclick="voltarParaConsulta()"></i>
                Clientes
            </div>

            <div class="content-area container-md">
                <input type="hidden" id="clienteIdEditando">

                <div class="profile-block" id="block-nome">
                    <span class="profile-label">Nome</span>
                    <div class="d-flex align-items-center view-mode">
                        <span class="profile-value" id="val-nome"></span>
                        <i class="bi bi-pencil-square ms-2 edit-icon" onclick="toggleEdit('nome')"></i>
                    </div>
                    <div class="edit-mode d-none d-flex align-items-center mt-1">
                        <input type="text" class="form-control-edit me-2" id="input-nome">
                        <button class="btn-check-orange" onclick="salvarSimples('nome')"><i class="bi bi-check-lg"></i></button>
                    </div>
                </div>

                <div class="profile-block">
                    <span class="profile-label">CPF</span>
                    <div class="d-flex align-items-center">
                        <span class="profile-value" id="val-cpf"></span>
                    </div>
                </div>

                <div class="profile-block" id="block-email">
                    <span class="profile-label">Email</span>
                    <div class="d-flex align-items-center view-mode">
                        <span class="profile-value" id="val-email"></span>
                        <i class="bi bi-pencil-square ms-2 edit-icon" onclick="toggleEdit('email')"></i>
                    </div>
                    <div class="edit-mode d-none mt-2">
                        <input type="email" class="form-control-edit mb-2" id="input-email-atual" placeholder="Email atual">
                        <input type="email" class="form-control-edit mb-2" id="input-email-novo" placeholder="Novo email">
                        <input type="email" class="form-control-edit mb-3" id="input-email-confirma" placeholder="Confirmar novo email">
                        
                        <div class="d-flex gap-2">
                            <button class="btn btn-orange-rounded btn-sm w-50 py-2" onclick="salvarComplexo('email')">Salvar</button>
                            <button class="btn btn-red-rounded btn-sm w-50 py-2" onclick="cancelarEdit('email')">Cancelar</button>
                        </div>
                    </div>
                </div>

                <div class="profile-block" id="block-numero">
                    <span class="profile-label">Número</span>
                    <div class="d-flex align-items-center view-mode">
                        <span class="profile-value" id="val-numero"></span>
                        <i class="bi bi-pencil-square ms-2 edit-icon" onclick="toggleEdit('numero')"></i>
                    </div>
                    <div class="edit-mode d-none d-flex align-items-center mt-1">
                        <input type="text" class="form-control-edit me-2" id="input-numero">
                        <button class="btn-check-orange" onclick="salvarSimples('numero')"><i class="bi bi-check-lg"></i></button>
                    </div>
                </div>

                <div class="profile-block" id="block-senha">
                    <span class="profile-label">Senha</span>
                    <div class="d-flex align-items-center view-mode">
                        <span class="profile-value">**********</span>
                        <i class="bi bi-pencil-square ms-2 edit-icon" onclick="toggleEdit('senha')"></i>
                    </div>
                    <div class="edit-mode d-none mt-2">
                        <input type="password" class="form-control-edit mb-2" id="input-senha-atual" placeholder="Senha atual do cliente (se necessário)">
                        <input type="password" class="form-control-edit mb-2" id="input-senha-nova" placeholder="Nova senha">
                        <input type="password" class="form-control-edit mb-3" id="input-senha-confirma" placeholder="Confirmar nova senha">
                        
                        <div class="d-flex gap-2">
                            <button class="btn btn-orange-rounded btn-sm w-50 py-2" onclick="salvarComplexo('senha')">Salvar</button>
                            <button class="btn btn-red-rounded btn-sm w-50 py-2" onclick="cancelarEdit('senha')">Cancelar</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="bottom-nav">
            <a href="reservas_funcionario.jsp" class="nav-item"><i class="bi bi-list-task"></i></a>
            <a href="andamento_funcionario.jsp" class="nav-item"><i class="bi bi-exclamation-circle"></i></a>
            <a href="pesquisa_funcionario.jsp" class="nav-item active"><i class="bi bi-car-front"></i></a>
            <a href="perfil_funcionario.jsp" class="nav-item"><i class="bi bi-person-circle"></i></a>
        </div>

    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

    <script>

        function pesquisarCliente() {
            const termo = $('#input-pesquisa').val();
            
            if(!termo) {
                alert("Digite um nome para pesquisar.");
                return;
            }

            // Exemplo de requisição AJAX GET
            /*
            $.ajax({
                url: 'PesquisarClienteServlet?termo=' + termo,
                type: 'GET',
                dataType: 'json',
                success: function(clientes) { renderizarResultados(clientes); }
            });
            */

            // Mock simulando o retorno do banco
            const resultadosMock = [
                { id: 1, nome: "Fabio Henrique Baptista", cpf: "489******-23", email: "nooba*****9@gmail.com", numero: "(11) 94002-8922" }
            ];
            renderizarResultados(resultadosMock);
        }

        function renderizarResultados(clientes) {
            const lista = $('#lista-resultados');
            lista.empty();

            if(clientes.length === 0) {
                lista.append('<p class="text-muted mt-3">Nenhum cliente encontrado.</p>');
                return;
            }

            clientes.forEach(cli => {
                const cliDataString = encodeURIComponent(JSON.stringify(cli));
                lista.append(`
                    <div class="item-card mt-3">
                        <div class="item-card-text">
                            <i class="bi bi-person-fill fs-3"></i> ` + cli.nome.split(' ')[0] + ` ` + (cli.nome.split(' ')[1] || '') + `
                        </div>
                        <button class="btn btn-orange rounded-3 px-3 py-2" onclick="abrirPerfilCliente('`+ cliDataString +`')">
                            <i class="bi bi-pencil-square fs-5"></i>
                        </button>
                    </div>
                `);
            });
        }

        function abrirPerfilCliente(clienteDataEncoded) {

            const cli = JSON.parse(decodeURIComponent(clienteDataEncoded));

            $('#clienteIdEditando').val(cli.id);
            $('#val-nome').text(cli.nome); $('#input-nome').val(cli.nome);
            $('#val-cpf').text(cli.cpf);
            $('#val-email').text(cli.email);
            $('#val-numero').text(cli.numero); $('#input-numero').val(cli.numero);
            $('#view-consulta').removeClass('d-flex').addClass('d-none');
            $('#view-perfil').removeClass('d-none').addClass('d-flex');
        }

        function voltarParaConsulta() {

            cancelarTodosEdits();
            $('#view-perfil').removeClass('d-flex').addClass('d-none');
            $('#view-consulta').removeClass('d-none').addClass('d-flex');
        }

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
            const idCli = $('#clienteIdEditando').val();
            const novoValor = $('#input-' + campo).val();
            
            if(!novoValor) return;

            $.ajax({
                url: 'AtualizarClienteServlet',
                type: 'POST',
                data: JSON.stringify({ id: idCli, campo: campo, valor: novoValor }),
                contentType: 'application/json',
                success: function(response) {
                    $('#val-' + campo).text(novoValor);
                    cancelarEdit(campo);
                },
                error: function() {
                    alert("Simulação: " + campo + " do cliente ID " + idCli + " atualizado.");
                    $('#val-' + campo).text(novoValor);
                    cancelarEdit(campo);
                }
            });
        }

        function salvarComplexo(campo) {
            const idCli = $('#clienteIdEditando').val();
            const atual = $('#input-' + campo + '-atual').val();
            const novo = $('#input-' + campo + '-novo').val();
            const confirma = $('#input-' + campo + '-confirma').val();

            if(!novo || novo !== confirma) {
                alert("Os novos valores não coincidem ou estão vazios.");
                return;
            }

            $.ajax({
                url: 'AtualizarClienteCredenciaisServlet',
                type: 'POST',
                data: JSON.stringify({ id: idCli, tipo: campo, valorAtual: atual, novoValor: novo }),
                contentType: 'application/json',
                success: function() {
                    alert(campo + " atualizado com sucesso!");
                    if(campo === 'email') $('#val-email').text(novo);
                    $('#block-' + campo).find('input').val('');
                    cancelarEdit(campo);
                },
                error: function() {
                    alert("Simulação: " + campo + " do cliente ID " + idCli + " atualizado.");
                    if(campo === 'email') $('#val-email').text(novo);
                    $('#block-' + campo).find('input').val('');
                    cancelarEdit(campo);
                }
            });
        }
    </script>
</body>
</html>