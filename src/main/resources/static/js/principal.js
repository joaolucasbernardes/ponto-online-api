document.addEventListener('DOMContentLoaded', () => {
    console.log('Página principal carregada e script principal.js executado.');

    const elementoSaudacao = document.querySelector('#saudacao');
    const elementoDataAtual = document.querySelector('#data-atual');

    const nomeUsuario = "Colaborador";
    elementoSaudacao.textContent = `Olá, ${nomeUsuario}!`;

    const hoje = new Date();
    const opcoesDeFormatacao = {
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric'    
    };

    let dataFormatada = hoje.toLocaleDateString('pt-BR', opcoesDeFormatacao);
    
    dataFormatada = dataFormatada.charAt(0).toUpperCase() + dataFormatada.slice(1);
    
    elementoDataAtual.textContent = dataFormatada;

});