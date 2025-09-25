document.addEventListener('DOMContentLoaded', () => {

    // Seleciona os elementos com os quais vamos interagir
    const loginForm = document.querySelector('.login-form');
    const identificadorInput = document.querySelector('#identificador');
    const senhaInput = document.querySelector('#senha');

    loginForm.addEventListener('submit', (evento) => {
        evento.preventDefault(); // Evita que o form carregue ao clicar em submit.

        // Captura os valores digitados nos campos de input
        const identificador = identificadorInput.value;
        const senha = senhaInput.value;

        // Cria o objeto (payload) que será enviado para a API
        //    A estrutura deste objeto é idêntica à do LoginRequisicaoDTO no backend.
        const dadosLogin = {
            identificador: identificador,
            senha: senha
        };

        // Usa a API fetch para enviar os dados ao backend
        fetch('http://localhost:8080/login', {
            method: 'POST', 
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dadosLogin)
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Usuário ou senha inválidos.');
        })
        .then(data => {
            console.log('Login bem-sucedido:', data);
            alert(data.mensagem); // Mostra a mensagem de sucesso vinda do backend

            // Redireciona o usuário para a página principal
            window.location.href = '/principal.html';
        })
        .catch(error => {
            console.error('Erro na autenticação:', error);
            alert(error.message); 
        });
    });
});