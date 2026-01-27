document.addEventListener('DOMContentLoaded', () => {
    verificarAutenticacao();
    carregarMinhasSolicitacoes();
    configurarFormulario();
});

// Atualizar cálculo de dias ao mudar datas
function configurarFormulario() {
    const dataInicio = document.getElementById('dataInicio');
    const dataFim = document.getElementById('dataFim');
    const resumo = document.getElementById('resumoDias');

    function atualizarDias() {
        if (dataInicio.value && dataFim.value) {
            const inicio = new Date(dataInicio.value);
            const fim = new Date(dataFim.value);

            // Diferença em milissegundos
            const diffTime = fim - inicio;
            // Converter para dias (incluindo o dia final)
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

            if (diffDays > 0) {
                resumo.textContent = `Total: ${diffDays} dia(s)`;
            } else {
                resumo.textContent = 'Data final deve ser posterior ou igual à inicial.';
            }
        } else {
            resumo.textContent = '';
        }
    }

    dataInicio.addEventListener('change', atualizarDias);
    dataFim.addEventListener('change', atualizarDias);

    // Envio do formulário
    document.getElementById('formFerias').addEventListener('submit', async (e) => {
        e.preventDefault();

        const tipo = document.getElementById('tipo').value;
        const inicio = dataInicio.value;
        const fim = dataFim.value;
        const obs = document.getElementById('observacao').value;

        if (new Date(fim) < new Date(inicio)) {
            toast.error('Data final inválida!');
            return;
        }

        try {
            const token = getToken(); // auth.js
            const funcionarioId = getUserId(); // auth.js

            const response = await fetch('/api/ferias', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    funcionarioId: parseInt(funcionarioId),
                    dataInicio: inicio,
                    dataFim: fim,
                    tipo: tipo,
                    observacao: obs
                })
            });

            if (!response.ok) {
                const erro = await response.json();
                throw new Error(erro.mensagem || 'Erro ao solicitar férias');
            }

            toast.success('Solicitação enviada com sucesso!');
            document.getElementById('formFerias').reset();
            document.getElementById('resumoDias').textContent = '';
            carregarMinhasSolicitacoes();

        } catch (error) {
            console.error(error);
            toast.error(error.message);
        }
    });
}

async function carregarMinhasSolicitacoes() {
    const container = document.getElementById('listaHistorico');
    container.innerHTML = '<div class="loading-spinner"></div>';

    try {
        const token = getToken();
        const funcionarioId = getUserId();

        const response = await fetch(`/api/ferias/funcionario/${funcionarioId}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error('Erro ao carregar histórico');

        const lista = await response.json();

        if (lista.length === 0) {
            container.innerHTML = '<p class="text-secondary">Nenhuma solicitação encontrada.</p>';
            return;
        }

        container.innerHTML = lista.map(item => `
            <div class="historico-item">
                <div>
                    <div style="font-weight: bold;">${formatarTipo(item.tipo)}</div>
                    <div style="font-size: 0.9rem;">${formatarData(item.dataInicio)} até ${formatarData(item.dataFim)} (${item.dias} dias)</div>
                    <div style="font-size: 0.8rem; color: #aaa;">Solicitado em: ${formatarData(item.dataSolicitacao)}</div>
                </div>
                <div style="text-align: right;">
                    <span class="status-badge status-${item.status.toLowerCase()}">${item.status}</span>
                    ${item.status === 'SOLICITADA' ? `<br><button onclick="cancelarSolicitacao(${item.id})" style="font-size: 0.7rem; color: #ff6b6b; background: none; border: none; cursor: pointer; text-decoration: underline; margin-top: 5px;">Cancelar</button>` : ''}
                </div>
            </div>
        `).join('');

    } catch (error) {
        container.innerHTML = '<p class="text-danger">Erro ao carregar.</p>';
        console.error(error);
    }
}

async function cancelarSolicitacao(id) {
    if (!confirm('Deseja realmente cancelar esta solicitação?')) return;

    try {
        const token = getToken();
        const funcionarioId = getUserId();

        const response = await fetch(`/api/ferias/${id}?solicitanteId=${funcionarioId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) {
            const erro = await response.json();
            throw new Error(erro.mensagem || 'Erro ao cancelar');
        }

        toast.info('Solicitação cancelada.');
        carregarMinhasSolicitacoes();

    } catch (error) {
        toast.error(error.message);
    }
}

function formatarTipo(tipo) {
    const map = {
        'FERIAS': '🏖️ Férias',
        'LICENCA_MEDICA': '🏥 Licença Médica',
        'LICENCA_MATERNIDADE': '🤰 Maternidade',
        'LICENCA_PATERNIDADE': '👨‍🍼 Paternidade',
        'FOLGA_COMPENSATORIA': '💤 Folga',
        'CASAMENTO': '💍 Casamento',
        'LUTO': '🖤 Luto',
        'OUTROS': 'Outros'
    };
    return map[tipo] || tipo;
}

function formatarData(dataStr) {
    if (!dataStr) return '-';
    // Se vier com hora (LocalDateTime), corta
    const datePart = dataStr.split('T')[0];
    const [ano, mes, dia] = datePart.split('-');
    return `${dia}/${mes}/${ano}`;
}
