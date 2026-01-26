// ===== Gerenciar Escalas =====

let escalas = [];
let editandoId = null;

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
    verificarAutenticacao();
    carregarEscalas();
    inicializarEventos();
});

function verificarAutenticacao() {
    const token = localStorage.getItem('jwt_token');
    const role = localStorage.getItem('role');

    if (!token || role !== 'ADMIN') {
        window.location.href = '/login.html';
        return;
    }
}

// ===== Eventos =====
function inicializarEventos() {
    document.getElementById('btnNovaEscala').addEventListener('click', novaEscala);
    document.getElementById('btnCancelar').addEventListener('click', cancelarEdicao);
    document.getElementById('formEscala').addEventListener('submit', salvarEscala);
    document.getElementById('btnSair').addEventListener('click', () => {
        localStorage.clear();
        window.location.href = '/login.html';
    });
}

// ===== CRUD Escalas =====
async function carregarEscalas() {
    const token = localStorage.getItem('jwt_token');

    try {
        const response = await fetch('/api/escalas', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao carregar escalas');
        }

        escalas = await response.json();
        renderizarListaEscalas();

    } catch (error) {
        console.error('Erro:', error);
        toast.error('Erro ao carregar escalas');
        document.getElementById('listaEscalas').innerHTML =
            '<p style="text-align: center; color: var(--danger); padding: 2rem;">Erro ao carregar escalas</p>';
    }
}

function renderizarListaEscalas() {
    const container = document.getElementById('listaEscalas');

    if (escalas.length === 0) {
        container.innerHTML = `
            <p style="text-align: center; color: var(--text-muted); padding: 2rem;">
                Nenhuma escala cadastrada. Clique em "+ Nova Escala" para adicionar.
            </p>
        `;
        return;
    }

    container.innerHTML = escalas.map(escala => {
        const tipoBadgeClass = escala.tipo.toLowerCase().replace('_', '_');
        const diasAbrev = escala.diasSemana.map(d => d.substring(0, 3)).join(', ');

        return `
            <div class="escala-card">
                <h4>
                    ${escala.nome}
                    ${escala.escalaPadrao ? '<span class="badge-padrao">⭐ Padrão</span>' : ''}
                </h4>
                <p class="escala-info">⏰ ${escala.horaEntrada} - ${escala.horaSaida}</p>
                <p class="escala-info">📅 ${diasAbrev}</p>
                <p class="escala-info">⏱️ ${escala.cargaHorariaDiaria}h/dia | ${escala.cargaHorariaSemanal}h/semana</p>
                <p class="escala-info">☕ Intervalo: ${escala.intervaloMinutos} min</p>
                <p class="escala-info">🎯 Tolerância: ±${escala.toleranciaEntradaMinutos}/${escala.toleranciaSaidaMinutos} min</p>
                ${escala.descricao ? `<p class="escala-info" style="margin-top: 0.5rem;">${escala.descricao}</p>` : ''}
                <div style="margin-top: 0.5rem;">
                    <span class="badge-tipo ${tipoBadgeClass}">${formatarTipo(escala.tipo)}</span>
                    <span class="status-badge ${escala.ativo ? 'ativo' : 'inativo'}" style="margin-left: 0.5rem;">
                        ${escala.ativo ? 'Ativo' : 'Inativo'}
                    </span>
                </div>
                <div class="escala-actions">
                    <button class="btn-ponto btn-small" onclick="editarEscala(${escala.id})">Editar</button>
                    ${!escala.escalaPadrao ? `
                        <button class="btn-secondary btn-small" onclick="definirComoPadrao(${escala.id})">Definir Padrão</button>
                    ` : ''}
                    <button class="btn-secondary btn-small" onclick="alterarStatus(${escala.id}, ${!escala.ativo})">
                        ${escala.ativo ? 'Desativar' : 'Ativar'}
                    </button>
                    ${!escala.escalaPadrao ? `
                        <button class="btn-secondary btn-small" style="background: var(--danger); border-color: var(--danger);" 
                                onclick="excluirEscala(${escala.id}, '${escala.nome}')">Excluir</button>
                    ` : ''}
                </div>
            </div>
        `;
    }).join('');
}

function formatarTipo(tipo) {
    const tipos = {
        'FIXA': 'Fixa',
        'FLEXIVEL': 'Flexível',
        'ROTATIVA': 'Rotativa',
        'ESCALA_12X36': '12x36'
    };
    return tipos[tipo] || tipo;
}

function novaEscala() {
    editandoId = null;
    document.getElementById('formTitulo').textContent = 'Nova Escala';
    document.getElementById('formEscala').reset();
    document.getElementById('escalaId').value = '';

    // Marcar dias úteis por padrão
    ['seg', 'ter', 'qua', 'qui', 'sex'].forEach(id => {
        document.getElementById(id).checked = true;
    });
    ['sab', 'dom'].forEach(id => {
        document.getElementById(id).checked = false;
    });

    // Valores padrão
    document.getElementById('horaEntrada').value = '08:00';
    document.getElementById('horaSaida').value = '17:00';
    document.getElementById('intervaloMinutos').value = '60';
    document.getElementById('cargaHorariaDiaria').value = '8';
    document.getElementById('cargaHorariaSemanal').value = '40';
    document.getElementById('toleranciaEntrada').value = '10';
    document.getElementById('toleranciaSaida').value = '10';
    document.getElementById('ativo').checked = true;
    document.getElementById('escalaPadrao').checked = false;
}

async function editarEscala(id) {
    const escala = escalas.find(e => e.id === id);
    if (!escala) return;

    editandoId = id;
    document.getElementById('formTitulo').textContent = 'Editar Escala';
    document.getElementById('escalaId').value = id;
    document.getElementById('nome').value = escala.nome;
    document.getElementById('descricao').value = escala.descricao || '';
    document.getElementById('tipo').value = escala.tipo;
    document.getElementById('horaEntrada').value = escala.horaEntrada;
    document.getElementById('horaSaida').value = escala.horaSaida;
    document.getElementById('intervaloMinutos').value = escala.intervaloMinutos;
    document.getElementById('cargaHorariaDiaria').value = escala.cargaHorariaDiaria;
    document.getElementById('cargaHorariaSemanal').value = escala.cargaHorariaSemanal;
    document.getElementById('toleranciaEntrada').value = escala.toleranciaEntradaMinutos;
    document.getElementById('toleranciaSaida').value = escala.toleranciaSaidaMinutos;
    document.getElementById('ativo').checked = escala.ativo;
    document.getElementById('escalaPadrao').checked = escala.escalaPadrao;

    // Marcar dias da semana
    const diasMap = {
        'SEGUNDA': 'seg', 'TERCA': 'ter', 'QUARTA': 'qua',
        'QUINTA': 'qui', 'SEXTA': 'sex', 'SABADO': 'sab', 'DOMINGO': 'dom'
    };

    // Desmarcar todos
    Object.values(diasMap).forEach(id => {
        document.getElementById(id).checked = false;
    });

    // Marcar os dias da escala
    escala.diasSemana.forEach(dia => {
        const checkboxId = diasMap[dia];
        if (checkboxId) {
            document.getElementById(checkboxId).checked = true;
        }
    });

    // Scroll para o formulário
    document.querySelector('.escala-form').scrollIntoView({ behavior: 'smooth' });
}

async function salvarEscala(e) {
    e.preventDefault();

    const token = localStorage.getItem('jwt_token');

    // Coletar dias selecionados
    const diasSelecionados = [];
    ['seg', 'ter', 'qua', 'qui', 'sex', 'sab', 'dom'].forEach(id => {
        const checkbox = document.getElementById(id);
        if (checkbox.checked) {
            diasSelecionados.push(checkbox.value);
        }
    });

    if (diasSelecionados.length === 0) {
        toast.error('Selecione pelo menos um dia da semana');
        return;
    }

    const dados = {
        nome: document.getElementById('nome').value.trim(),
        descricao: document.getElementById('descricao').value.trim() || null,
        tipo: document.getElementById('tipo').value,
        horaEntrada: document.getElementById('horaEntrada').value,
        horaSaida: document.getElementById('horaSaida').value,
        intervaloMinutos: parseInt(document.getElementById('intervaloMinutos').value),
        cargaHorariaDiaria: parseFloat(document.getElementById('cargaHorariaDiaria').value),
        cargaHorariaSemanal: parseFloat(document.getElementById('cargaHorariaSemanal').value),
        diasSemana: diasSelecionados,
        toleranciaEntradaMinutos: parseInt(document.getElementById('toleranciaEntrada').value),
        toleranciaSaidaMinutos: parseInt(document.getElementById('toleranciaSaida').value),
        ativo: document.getElementById('ativo').checked,
        escalaPadrao: document.getElementById('escalaPadrao').checked
    };

    try {
        const url = editandoId
            ? `/api/escalas/${editandoId}`
            : '/api/escalas';

        const method = editandoId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(dados)
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Erro ao salvar escala');
        }

        toast.success(editandoId ? 'Escala atualizada com sucesso!' : 'Escala cadastrada com sucesso!');

        await carregarEscalas();
        novaEscala();

    } catch (error) {
        console.error('Erro:', error);
        toast.error(error.message || 'Erro ao salvar escala');
    }
}

function cancelarEdicao() {
    novaEscala();
}

async function alterarStatus(id, novoStatus) {
    const token = localStorage.getItem('jwt_token');

    try {
        const response = await fetch(`/api/escalas/${id}/status?ativo=${novoStatus}`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Erro ao alterar status');
        }

        toast.success(`Escala ${novoStatus ? 'ativada' : 'desativada'} com sucesso!`);
        await carregarEscalas();

    } catch (error) {
        console.error('Erro:', error);
        toast.error(error.message || 'Erro ao alterar status da escala');
    }
}

async function definirComoPadrao(id) {
    if (!confirm('Deseja definir esta escala como padrão?\n\nNovos funcionários receberão esta escala automaticamente.')) {
        return;
    }

    const token = localStorage.getItem('jwt_token');

    try {
        const response = await fetch(`/api/escalas/${id}/padrao`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao definir escala padrão');
        }

        toast.success('Escala definida como padrão com sucesso!');
        await carregarEscalas();

    } catch (error) {
        console.error('Erro:', error);
        toast.error('Erro ao definir escala padrão');
    }
}

async function excluirEscala(id, nome) {
    if (!confirm(`Deseja realmente excluir a escala "${nome}"?\n\nEsta ação não pode ser desfeita.`)) {
        return;
    }

    const token = localStorage.getItem('jwt_token');

    try {
        const response = await fetch(`/api/escalas/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Erro ao excluir escala');
        }

        toast.success('Escala excluída com sucesso!');
        await carregarEscalas();

        // Se estava editando esta escala, limpar formulário
        if (editandoId === id) {
            novaEscala();
        }

    } catch (error) {
        console.error('Erro:', error);
        toast.error(error.message || 'Erro ao excluir escala');
    }
}
