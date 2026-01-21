document.addEventListener('DOMContentLoaded', () => {
    console.log('Página principal carregada e script principal.js executado.');

    const elementoSaudacao = document.querySelector('#saudacao');
    const elementoDataAtual = document.querySelector('#data-atual');
    const btnBaterPonto = document.querySelector('#btnBaterPonto');
    const spansDeHora = {
        entrada: document.querySelector('#hora-entrada'),
        saidaAlmoco: document.querySelector('#hora-saida-almoco'),
        retornoAlmoco: document.querySelector('#hora-retorno-almoco'),
        saida: document.querySelector('#hora-saida')
    };

    function configurarCabecalho() {
        const nomeUsuario = localStorage.getItem('funcionario_nome');
        if (nomeUsuario) {
            elementoSaudacao.textContent = `Olá, ${nomeUsuario}!`;
        } else {
            elementoSaudacao.textContent = 'Olá!';
            toast.error('Não foi possível identificar o usuário. Faça o login novamente.');
            window.location.href = '/login.html';
        }

        const hoje = new Date();
        const opcoesDeFormatacao = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        let dataFormatada = hoje.toLocaleDateString('pt-BR', opcoesDeFormatacao);
        elementoDataAtual.textContent = dataFormatada.charAt(0).toUpperCase() + dataFormatada.slice(1);
    }

    function atualizarQuadroDeHoras(registros = []) {
        spansDeHora.entrada.textContent = '--:--';
        spansDeHora.saidaAlmoco.textContent = '--:--';
        spansDeHora.retornoAlmoco.textContent = '--:--';
        spansDeHora.saida.textContent = '--:--';

        if (registros.length > 0) spansDeHora.entrada.textContent = registros[0].dataHora.split(' ')[1];
        if (registros.length > 1) spansDeHora.saidaAlmoco.textContent = registros[1].dataHora.split(' ')[1];
        if (registros.length > 2) spansDeHora.retornoAlmoco.textContent = registros[2].dataHora.split(' ')[1];
        if (registros.length > 3) spansDeHora.saida.textContent = registros[3].dataHora.split(' ')[1];
    }

    function verificarLimiteEAtualizarBotao(totalDeRegistros) {
        if (totalDeRegistros >= 4) {
            btnBaterPonto.disabled = true;
            btnBaterPonto.textContent = 'Limite Atingido';
            btnBaterPonto.style.backgroundColor = '#6c757d';
            btnBaterPonto.style.cursor = 'not-allowed';
        } else {
            btnBaterPonto.disabled = false;
            btnBaterPonto.textContent = 'Bater Ponto';
            btnBaterPonto.style.backgroundColor = '#536dfe';
            btnBaterPonto.style.cursor = 'pointer';
        }
    }

    async function carregarRegistrosDoDia() {
        const token = localStorage.getItem('jwt_token');
        const funcionarioId = localStorage.getItem('funcionario_id');

        if (!token || !funcionarioId) {
            toast.error('Sessão expirada. Por favor, faça o login novamente.');
            window.location.href = '/login.html';
            return;
        }

        try {
            const response = await fetch(`/registros-ponto/funcionario/${funcionarioId}/hoje`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!response.ok) throw new Error('Falha ao carregar registros do dia.');

            const registros = await response.json();
            console.log('Registros de hoje carregados:', registros);

            atualizarQuadroDeHoras(registros);
            verificarLimiteEAtualizarBotao(registros.length);

        } catch (error) {
            console.error('Erro ao carregar registros:', error);
            toast.error(error.message);
        }
    }

    btnBaterPonto.addEventListener('click', async () => {
        const token = localStorage.getItem('jwt_token');
        const funcionarioId = localStorage.getItem('funcionario_id');

        if (!token || !funcionarioId) {
            toast.error('Sessão expirada. Faça login.');
            window.location.href = '/login.html';
            return;
        }

        try {
            const response = await fetch('/registros-ponto', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ funcionarioId })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensagem || 'Erro ao registrar o ponto.');
            }

            toast.success('Ponto registrado com sucesso!');
            await carregarRegistrosDoDia();

        } catch (error) {
            console.error('Erro no registro de ponto:', error);
            toast.error(error.message);
        }
    });

    async function carregarResumoHoras() {
        const token = localStorage.getItem('jwt_token');
        const funcionarioId = localStorage.getItem('funcionario_id');

        if (!token || !funcionarioId) return;

        try {
            // Buscar cálculo do dia
            const responseDia = await fetch(`/registros-ponto/funcionario/${funcionarioId}/horas/dia`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (responseDia.ok) {
                const calculoDia = await responseDia.json();
                atualizarResumoVisual(calculoDia);
            }

            // Buscar resumo mensal
            const responseMes = await fetch(`/registros-ponto/funcionario/${funcionarioId}/horas/mes`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (responseMes.ok) {
                const resumoMes = await responseMes.json();
                document.querySelector('#banco-horas').textContent = resumoMes.bancoDeHoras;
                aplicarCorSaldo(document.querySelector('#banco-horas'), resumoMes.bancoDeHoras);
            }

        } catch (error) {
            console.error('Erro ao carregar resumo de horas:', error);
        }
    }

    function atualizarResumoVisual(calculoDTO) {
        document.querySelector('#horas-trabalhadas').textContent = calculoDTO.horasTrabalhadas;
        document.querySelector('#intervalo-almoco').textContent = calculoDTO.intervaloAlmoco;
        document.querySelector('#saldo-dia').textContent = calculoDTO.saldo;

        aplicarCorSaldo(document.querySelector('#saldo-dia'), calculoDTO.saldo);
    }

    function aplicarCorSaldo(elemento, saldo) {
        elemento.classList.remove('positivo', 'negativo', 'neutro');

        if (saldo.startsWith('+') && saldo !== '+00:00') {
            elemento.classList.add('positivo');
        } else if (saldo.startsWith('-')) {
            elemento.classList.add('negativo');
        } else {
            elemento.classList.add('neutro');
        }
    }

    // ===== GEOLOCALIZAÇÃO =====
    let coordenadasAtuais = null;

    const btnObterLocalizacao = document.querySelector('#btnObterLocalizacao');
    const geoStatus = document.querySelector('#geoStatus');
    const geoInfo = document.querySelector('#geoInfo');

    btnObterLocalizacao.addEventListener('click', obterLocalizacao);

    async function obterLocalizacao() {
        if (!navigator.geolocation) {
            exibirStatusGeo('error', 'Geolocalização não suportada pelo navegador');
            return;
        }

        exibirStatusGeo('info', 'Obtendo localização...');
        btnObterLocalizacao.disabled = true;

        try {
            const position = await new Promise((resolve, reject) => {
                navigator.geolocation.getCurrentPosition(resolve, reject, {
                    enableHighAccuracy: true,
                    timeout: 10000,
                    maximumAge: 0
                });
            });

            coordenadasAtuais = {
                latitude: position.coords.latitude,
                longitude: position.coords.longitude,
                precisaoMetros: position.coords.accuracy
            };

            // Validar localização com o backend
            await validarLocalizacao(coordenadasAtuais);

        } catch (error) {
            let mensagem = 'Erro ao obter localização';
            if (error.code === 1) {
                mensagem = 'Permissão de localização negada';
            } else if (error.code === 2) {
                mensagem = 'Localização indisponível';
            } else if (error.code === 3) {
                mensagem = 'Tempo esgotado ao obter localização';
            }
            exibirStatusGeo('error', mensagem);
            console.error('Erro de geolocalização:', error);
        } finally {
            btnObterLocalizacao.disabled = false;
        }
    }

    async function validarLocalizacao(coords) {
        const token = localStorage.getItem('jwt_token');

        try {
            const response = await fetch('/api/geolocalizacao/validar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(coords)
            });

            if (!response.ok) {
                throw new Error('Erro ao validar localização');
            }

            const resultado = await response.json();
            exibirResultadoValidacao(resultado, coords);

        } catch (error) {
            console.error('Erro ao validar localização:', error);
            exibirStatusGeo('error', 'Erro ao validar localização com o servidor');
        }
    }

    function exibirResultadoValidacao(resultado, coords) {
        // Atualizar status
        if (resultado.valido) {
            exibirStatusGeo('success', resultado.mensagem);
        } else {
            exibirStatusGeo('warning', resultado.mensagem);
        }

        // Exibir informações
        document.querySelector('#coordenadas').textContent =
            `${coords.latitude.toFixed(6)}, ${coords.longitude.toFixed(6)}`;

        document.querySelector('#precisao').textContent =
            `±${Math.round(coords.precisaoMetros)}m`;

        document.querySelector('#localPermitido').textContent =
            resultado.localPermitido ? resultado.localPermitido.nome : 'Nenhum';

        const statusElement = document.querySelector('#statusValidacao');
        if (resultado.valido) {
            statusElement.textContent = '✓ Válido';
            statusElement.className = 'info-value status valido';
        } else {
            statusElement.textContent = '✗ Fora do raio';
            statusElement.className = 'info-value status invalido';
        }

        geoInfo.style.display = 'block';
    }

    function exibirStatusGeo(tipo, mensagem) {
        geoStatus.className = 'geo-status ' + tipo;
        geoStatus.querySelector('.geo-message').textContent = mensagem;
    }

    // Atualizar função de bater ponto para incluir coordenadas
    const btnBaterPontoOriginal = btnBaterPonto.onclick;
    btnBaterPonto.onclick = null;

    btnBaterPonto.addEventListener('click', async () => {
        const token = localStorage.getItem('jwt_token');
        const funcionarioId = localStorage.getItem('funcionario_id');

        if (!token || !funcionarioId) {
            toast.error('Sessão expirada. Faça login.');
            window.location.href = '/login.html';
            return;
        }

        // Preparar dados do registro
        const dadosRegistro = { funcionarioId };

        // Incluir coordenadas se disponíveis
        if (coordenadasAtuais) {
            dadosRegistro.latitude = coordenadasAtuais.latitude;
            dadosRegistro.longitude = coordenadasAtuais.longitude;
            dadosRegistro.precisaoMetros = coordenadasAtuais.precisaoMetros;
        }

        try {
            const response = await fetch('/registros-ponto', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(dadosRegistro)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensagem || 'Erro ao registrar o ponto.');
            }

            toast.success('Ponto registrado com sucesso!');
            await carregarRegistrosDoDia();

            // Limpar coordenadas após registro
            coordenadasAtuais = null;
            geoInfo.style.display = 'none';
            exibirStatusGeo('info', 'Clique no botão abaixo para obter sua localização');

        } catch (error) {
            console.error('Erro no registro de ponto:', error);
            toast.error(error.message);
        }
    });

    configurarCabecalho();
    carregarRegistrosDoDia().then(() => carregarResumoHoras());
});