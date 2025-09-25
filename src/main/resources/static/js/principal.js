document.addEventListener('DOMContentLoaded', () => {
    console.log('Página principal carregada e script principal.js executado.');

    // 1. Seleciona os elementos da página
    const elementoSaudacao = document.querySelector('#saudacao');
    const elementoDataAtual = document.querySelector('#data-atual');
    const btnBaterPonto = document.querySelector('#btnBaterPonto');
    
    // Seleciona os spans onde as horas serão exibidas
    const horaEntradaSpan = document.querySelector('#hora-entrada');
    const horaSaidaAlmocoSpan = document.querySelector('#hora-saida-almoco');
    const horaRetornoAlmocoSpan = document.querySelector('#hora-retorno-almoco');
    const horaSaidaSpan = document.querySelector('#hora-saida');

    const nomeUsuario = "Colaborador Padrão";
    elementoSaudacao.textContent = `Olá, ${nomeUsuario}!`;

    const hoje = new Date();
    const opcoesDeFormatacao = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    let dataFormatada = hoje.toLocaleDateString('pt-BR', opcoesDeFormatacao);
    elementoDataAtual.textContent = dataFormatada.charAt(0).toUpperCase() + dataFormatada.slice(1);

    btnBaterPonto.addEventListener('click', () => {
        console.log('Botão "Bater Ponto" clicado!');

        const dados = {
            funcionarioId: 1 
        };

        fetch('http://localhost:8080/registros-ponto', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dados)
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            // Se o backend retornar um erro (ex: funcionário não encontrado), ele cairá aqui.
            throw new Error('Ocorreu um erro ao registrar o ponto.');
        })
        .then(data => {
            // 'data' aqui é o nosso RegistroPontoRespostaDTO
            console.log('Ponto registrado com sucesso:', data);
            
            // Atualiza o quadro de registros na tela
            atualizarQuadroDeHoras(data.dataHora);
        })
        .catch(error => {
            console.error('Erro:', error);
            alert(error.message);
        });
    });

    function atualizarQuadroDeHoras(dataHoraCompleta) {
        const hora = dataHoraCompleta.split(' ')[1];

        // Lógica sequencial simples para preencher o próximo campo vazio
        if (horaEntradaSpan.textContent === '--:--') {
            horaEntradaSpan.textContent = hora;
        } else if (horaSaidaAlmocoSpan.textContent === '--:--') {
            horaSaidaAlmocoSpan.textContent = hora;
        } else if (horaRetornoAlmocoSpan.textContent === '--:--') {
            horaRetornoAlmocoSpan.textContent = hora;
        } else if (horaSaidaSpan.textContent === '--:--') {
            horaSaidaSpan.textContent = hora;
        } else {
            alert('Todos os pontos do dia já foram registrados!');
        }
    }
});