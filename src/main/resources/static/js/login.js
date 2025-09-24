document.addEventListener('DOMContentLoaded', () => {

    // Seleciona os elementos com os quais vamos interagir
    const loginForm = document.querySelector('.login-form');
    const identificadorInput = document.querySelector('#identificador');
    const senhaInput = document.querySelector('#senha');

    // Adiciona um "ouvinte" de eventos ao formulário. Ele vai "escutar" pelo evento 'submit'
    loginForm.addEventListener('submit', (evento) => {

        // Evita que carregue a página ao enviar o form.
        evento.preventDefault();

        // Pega os valores digitados nos campos de input
        const identificador = identificadorInput.value;
        const senha = senhaInput.value;


        console.log('Tentativa de login submetida!');
        console.log('Identificador:', identificador);
        console.log('Senha:', senha);

        alert('Dados capturados! Verifique o console do navegador (F12).');

    });

});