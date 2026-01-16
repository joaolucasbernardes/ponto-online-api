// Justificar Ausência - JavaScript

let funcionarioId = null;

// Inicialização
document.addEventListener('DOMContentLoaded', async () => {
    await carregarDadosUsuario();
    configurarEventos();
    await carregarJustificativas();
});

async function carregarDadosUsuario() {
    try {
        const token = localStorage.getItem('jwt_token');
        const idFuncionario = localStorage.getItem('funcionario_id');

        if (!token || !idFuncionario) {
            window.location.href = '/login.html';
            return;
        }

        funcionarioId = parseInt(idFuncionario);
    } catch (error) {
        console.error('Erro ao carregar usuário:', error);
        toast.error('Erro ao carregar dados do usuário');
        setTimeout(() => window.location.href = '/login.html', 2000);
    }
}

function configurarEventos() {
    const form = document.getElementById('formJustificativa');
    const tipoSelect = document.getElementById('tipoJustificativa');
    const motivoTextarea = document.getElementById('motivo');

    // Evento de submit do formulário
    form.addEventListener('submit', enviarJustificativa);

    // Mostrar/ocultar campos de ajuste
    tipoSelect.addEventListener('change', (e) => {
        const camposAjuste = document.getElementById('camposAjuste');
        if (e.target.value === 'AJUSTE_PONTO') {
            camposAjuste.style.display = 'block';
            document.getElementById('horaSolicitada').required = true;
        } else {
            camposAjuste.style.display = 'none';
            document.getElementById('horaSolicitada').required = false;
            document.getElementById('horaOriginal').value = '';
            document.getElementById('horaSolicitada').value = '';
        }
    });

    // Contador de caracteres
    motivoTextarea.addEventListener('input', (e) => {
        const charCount = document.querySelector('.char-count');
        charCount.textContent = `${e.target.value.length}/1000 caracteres`;
    });

    // Botão sair
    document.getElementById('btn-sair').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('token');
        window.location.href = '/login.html';
    });
}

async function enviarJustificativa(e) {
    e.preventDefault();

    const tipo = document.getElementById('tipoJustificativa').value;
    const data = document.getElementById('dataJustificativa').value;
    const motivo = document.getElementById('motivo').value.trim();
    const horaOriginal = document.getElementById('horaOriginal').value || null;
    const horaSolicitada = document.getElementById('horaSolicitada').value || null;

    // Validações
    if (!tipo || !data || !motivo) {
        toast.error('Preencha todos os campos obrigatórios');
        return;
    }

    if (tipo === 'AJUSTE_PONTO' && !horaSolicitada) {
        toast.error('Para ajuste de ponto, informe a hora solicitada');
        return;
    }

    const justificativa = {
        funcionarioId: funcionarioId,
        data: data,
        tipo: tipo,
        motivo: motivo,
        horaOriginal: horaOriginal,
        horaSolicitada: horaSolicitada
    };

    try {
        const token = localStorage.getItem('jwt_token');
        const response = await fetch('/api/justificativas', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(justificativa)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.mensagem || 'Erro ao enviar justificativa');
        }

        toast.success('Justificativa enviada com sucesso! Aguarde aprovação.');
        limparFormulario();
        await carregarJustificativas();

    } catch (error) {
        console.error('Erro ao enviar justificativa:', error);
        toast.error(error.message);
    }
}

function limparFormulario() {
    document.getElementById('formJustificativa').reset();
    document.getElementById('camposAjuste').style.display = 'none';
    document.querySelector('.char-count').textContent = '0/1000 caracteres';
}

async function carregarJustificativas() {
    const listaContainer = document.getElementById('listaJustificativas');
    listaContainer.innerHTML = '<div class="loading">Carregando...</div>';

    try {
        const token = localStorage.getItem('jwt_token');
        const response = await fetch(`/api/justificativas/funcionario/${funcionarioId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao carregar justificativas');
        }

        const justificativas = await response.json();
        renderizarJustificativas(justificativas);

    } catch (error) {
        console.error('Erro ao carregar justificativas:', error);
        listaContainer.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">⚠️</div>
                <p>Erro ao carregar justificativas</p>
            </div>
        `;
    }
}

function renderizarJustificativas(justificativas) {
    const listaContainer = document.getElementById('listaJustificativas');

    if (justificativas.length === 0) {
        listaContainer.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📝</div>
                <p>Você ainda não possui justificativas</p>
            </div>
        `;
        return;
    }

    const html = justificativas.map(j => criarCardJustificativa(j)).join('');
    listaContainer.innerHTML = html;
}

function criarCardJustificativa(justificativa) {
    const statusClass = justificativa.status.toLowerCase();
    const tipoLabel = formatarTipo(justificativa.tipo);
    const statusLabel = formatarStatus(justificativa.status);

    let horasInfo = '';
    if (justificativa.horaOriginal && justificativa.horaSolicitada) {
        horasInfo = `
            <div class="justificativa-data">
                <strong>Hora Original:</strong> ${justificativa.horaOriginal} → 
                <strong>Hora Solicitada:</strong> ${justificativa.horaSolicitada}
            </div>
        `;
    }

    let aprovacaoInfo = '';
    if (justificativa.status !== 'PENDENTE') {
        aprovacaoInfo = `
            <div class="justificativa-footer">
                <strong>${justificativa.status === 'APROVADA' ? 'Aprovado' : 'Rejeitado'} por:</strong> 
                ${justificativa.aprovadorNome} em ${justificativa.dataAprovacao}
                ${justificativa.observacaoAprovador ? `<br><strong>Observação:</strong> ${justificativa.observacaoAprovador}` : ''}
            </div>
        `;
    }

    return `
        <div class="justificativa-card ${statusClass}">
            <div class="justificativa-header">
                <span class="justificativa-tipo">${tipoLabel}</span>
                <span class="justificativa-status status-${statusClass}">${statusLabel}</span>
            </div>
            <div class="justificativa-data">
                <strong>Data:</strong> ${justificativa.data}
            </div>
            ${horasInfo}
            <div class="justificativa-motivo">
                <strong>Motivo:</strong><br>
                ${justificativa.motivo}
            </div>
            ${aprovacaoInfo}
        </div>
    `;
}

function formatarTipo(tipo) {
    const tipos = {
        'AUSENCIA': '🚫 Ausência',
        'ATRASO': '⏰ Atraso',
        'AJUSTE_PONTO': '✏️ Ajuste de Ponto'
    };
    return tipos[tipo] || tipo;
}

function formatarStatus(status) {
    const statuses = {
        'PENDENTE': 'Pendente',
        'APROVADA': 'Aprovada',
        'REJEITADA': 'Rejeitada'
    };
    return statuses[status] || status;
}
