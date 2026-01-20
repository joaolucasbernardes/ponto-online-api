// ===== Configurações da Empresa - JavaScript =====

// Variáveis globais
let configuracaoId = null;
let feriadoEditandoId = null;

// ===== Inicialização =====
document.addEventListener('DOMContentLoaded', () => {
    verificarAutenticacao();
    carregarConfiguracoes();
    carregarFeriados();
    inicializarEventos();
});

// ===== Autenticação =====
function verificarAutenticacao() {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    if (!token || role !== 'ADMIN') {
        window.location.href = '/login.html';
        return;
    }
}

// ===== Eventos =====
function inicializarEventos() {
    // Botão Salvar Configurações
    document.getElementById('btnSalvar').addEventListener('click', salvarConfiguracoes);

    // Botão Sair
    document.getElementById('btnSair').addEventListener('click', () => {
        localStorage.clear();
        window.location.href = '/login.html';
    });

    // Toggle de Horas Extras
    document.getElementById('permiteHorasExtras').addEventListener('change', (e) => {
        const config = document.getElementById('horasExtrasConfig');
        config.style.display = e.target.checked ? 'grid' : 'none';
    });

    // Botão Novo Feriado
    document.getElementById('btnNovoFeriado').addEventListener('click', abrirModalNovoFeriado);

    // Botão Salvar Feriado
    document.getElementById('btnSalvarFeriado').addEventListener('click', salvarFeriado);
}

// ===== Carregar Configurações =====
async function carregarConfiguracoes() {
    const token = localStorage.getItem('token');

    try {
        const response = await fetch('/api/configuracoes', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao carregar configurações');
        }

        const config = await response.json();
        preencherFormulario(config);

    } catch (error) {
        console.error('Erro:', error);
        toast.error('Erro ao carregar configurações da empresa');
    }
}

function preencherFormulario(config) {
    configuracaoId = config.id;

    // Horários
    document.getElementById('horaEntrada').value = config.horaEntradaPadrao;
    document.getElementById('horaSaidaAlmoco').value = config.horaSaidaAlmocoPadrao;
    document.getElementById('horaRetornoAlmoco').value = config.horaRetornoAlmocoPadrao;
    document.getElementById('horaSaida').value = config.horaSaidaPadrao;

    // Carga horária (converter minutos para horas)
    document.getElementById('cargaHorariaDiaria').value = config.cargaHorariaDiaria / 60;
    document.getElementById('cargaHorariaSemanal').value = config.cargaHorariaSemanal / 60;

    // Dias úteis
    document.getElementById('diaSegunda').checked = config.diasUteis.segunda;
    document.getElementById('diaTerca').checked = config.diasUteis.terca;
    document.getElementById('diaQuarta').checked = config.diasUteis.quarta;
    document.getElementById('diaQuinta').checked = config.diasUteis.quinta;
    document.getElementById('diaSexta').checked = config.diasUteis.sexta;
    document.getElementById('diaSabado').checked = config.diasUteis.sabado;
    document.getElementById('diaDomingo').checked = config.diasUteis.domingo;

    // Tolerâncias
    document.getElementById('toleranciaEntrada').value = config.tolerancias.entradaMinutos;
    document.getElementById('toleranciaRetorno').value = config.tolerancias.retornoAlmocoMinutos;

    // Intervalo de almoço
    document.getElementById('intervaloMinimo').value = config.intervaloAlmoco.minimoMinutos;
    document.getElementById('intervaloMaximo').value = config.intervaloAlmoco.maximoMinutos;

    // Horas extras
    document.getElementById('permiteHorasExtras').checked = config.horasExtras.permite;
    document.getElementById('limiteDiario').value = config.horasExtras.limiteDiarioMinutos / 60;
    document.getElementById('limiteMensal').value = config.horasExtras.limiteMensalMinutos / 60;
    document.getElementById('percentualHoraExtra').value = config.horasExtras.percentual;

    // Atualizar visibilidade de horas extras
    const horasExtrasConfig = document.getElementById('horasExtrasConfig');
    horasExtrasConfig.style.display = config.horasExtras.permite ? 'grid' : 'none';
}

// ===== Salvar Configurações =====
async function salvarConfiguracoes() {
    const token = localStorage.getItem('token');

    // Validar formulário
    if (!validarFormulario()) {
        return;
    }

    // Montar DTO
    const dto = {
        id: configuracaoId,
        horaEntradaPadrao: document.getElementById('horaEntrada').value,
        horaSaidaAlmocoPadrao: document.getElementById('horaSaidaAlmoco').value,
        horaRetornoAlmocoPadrao: document.getElementById('horaRetornoAlmoco').value,
        horaSaidaPadrao: document.getElementById('horaSaida').value,
        cargaHorariaDiaria: parseInt(parseFloat(document.getElementById('cargaHorariaDiaria').value) * 60),
        cargaHorariaSemanal: parseInt(parseFloat(document.getElementById('cargaHorariaSemanal').value) * 60),
        diasUteis: {
            segunda: document.getElementById('diaSegunda').checked,
            terca: document.getElementById('diaTerca').checked,
            quarta: document.getElementById('diaQuarta').checked,
            quinta: document.getElementById('diaQuinta').checked,
            sexta: document.getElementById('diaSexta').checked,
            sabado: document.getElementById('diaSabado').checked,
            domingo: document.getElementById('diaDomingo').checked
        },
        tolerancias: {
            entradaMinutos: parseInt(document.getElementById('toleranciaEntrada').value),
            retornoAlmocoMinutos: parseInt(document.getElementById('toleranciaRetorno').value)
        },
        intervaloAlmoco: {
            minimoMinutos: parseInt(document.getElementById('intervaloMinimo').value),
            maximoMinutos: parseInt(document.getElementById('intervaloMaximo').value)
        },
        horasExtras: {
            permite: document.getElementById('permiteHorasExtras').checked,
            limiteDiarioMinutos: parseInt(parseFloat(document.getElementById('limiteDiario').value) * 60),
            limiteMensalMinutos: parseInt(parseFloat(document.getElementById('limiteMensal').value) * 60),
            percentual: parseInt(document.getElementById('percentualHoraExtra').value)
        }
    };

    try {
        const response = await fetch('/api/configuracoes', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(dto)
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Erro ao salvar configurações');
        }

        const configAtualizada = await response.json();
        configuracaoId = configAtualizada.id;

        toast.success('Configurações salvas com sucesso!');

    } catch (error) {
        console.error('Erro:', error);
        toast.error(error.message || 'Erro ao salvar configurações');
    }
}

function validarFormulario() {
    // Validar que pelo menos um dia útil está selecionado
    const diasUteis = [
        document.getElementById('diaSegunda').checked,
        document.getElementById('diaTerca').checked,
        document.getElementById('diaQuarta').checked,
        document.getElementById('diaQuinta').checked,
        document.getElementById('diaSexta').checked,
        document.getElementById('diaSabado').checked,
        document.getElementById('diaDomingo').checked
    ];

    if (!diasUteis.some(dia => dia)) {
        toast.error('Selecione pelo menos um dia útil');
        return false;
    }

    // Validar intervalo de almoço
    const intervaloMin = parseInt(document.getElementById('intervaloMinimo').value);
    const intervaloMax = parseInt(document.getElementById('intervaloMaximo').value);

    if (intervaloMin >= intervaloMax) {
        toast.error('Intervalo mínimo deve ser menor que o máximo');
        return false;
    }

    return true;
}

// ===== Feriados =====
async function carregarFeriados() {
    const token = localStorage.getItem('token');
    const tbody = document.getElementById('feriadosTableBody');

    try {
        const response = await fetch('/api/feriados', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao carregar feriados');
        }

        const feriados = await response.json();

        if (feriados.length === 0) {
            tbody.innerHTML = '<tr class="empty-state"><td colspan="5">Nenhum feriado cadastrado</td></tr>';
            return;
        }

        tbody.innerHTML = feriados.map(feriado => `
            <tr>
                <td>${feriado.nome}</td>
                <td>${feriado.data}</td>
                <td><span class="tipo-badge tipo-${feriado.tipo}">${formatarTipo(feriado.tipo)}</span></td>
                <td><span class="recorrente-badge">${feriado.recorrente ? '✓ Sim' : '✗ Não'}</span></td>
                <td class="table-actions">
                    <button class="btn-primary btn-small" onclick="editarFeriado(${feriado.id})">Editar</button>
                    <button class="btn-danger btn-small" onclick="excluirFeriado(${feriado.id}, '${feriado.nome}')">Excluir</button>
                </td>
            </tr>
        `).join('');

    } catch (error) {
        console.error('Erro:', error);
        tbody.innerHTML = '<tr class="empty-state"><td colspan="5">Erro ao carregar feriados</td></tr>';
        toast.error('Erro ao carregar feriados');
    }
}

function formatarTipo(tipo) {
    const tipos = {
        'NACIONAL': 'Nacional',
        'ESTADUAL': 'Estadual',
        'MUNICIPAL': 'Municipal',
        'FACULTATIVO': 'Facultativo'
    };
    return tipos[tipo] || tipo;
}

// ===== Modal Feriado =====
function abrirModalNovoFeriado() {
    feriadoEditandoId = null;
    document.getElementById('modalFeriadoTitulo').textContent = 'Novo Feriado';
    document.getElementById('feriadoId').value = '';
    document.getElementById('feriadoNome').value = '';
    document.getElementById('feriadoData').value = '';
    document.getElementById('feriadoTipo').value = 'NACIONAL';
    document.getElementById('feriadoRecorrente').checked = true;

    document.getElementById('modalFeriado').classList.add('active');
}

async function editarFeriado(id) {
    const token = localStorage.getItem('token');

    try {
        const response = await fetch('/api/feriados', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao carregar feriado');
        }

        const feriados = await response.json();
        const feriado = feriados.find(f => f.id === id);

        if (!feriado) {
            toast.error('Feriado não encontrado');
            return;
        }

        feriadoEditandoId = id;
        document.getElementById('modalFeriadoTitulo').textContent = 'Editar Feriado';
        document.getElementById('feriadoId').value = id;
        document.getElementById('feriadoNome').value = feriado.nome;

        // Converter data de DD/MM/YYYY para YYYY-MM-DD
        const [dia, mes, ano] = feriado.data.split('/');
        document.getElementById('feriadoData').value = `${ano}-${mes}-${dia}`;

        document.getElementById('feriadoTipo').value = feriado.tipo;
        document.getElementById('feriadoRecorrente').checked = feriado.recorrente;

        document.getElementById('modalFeriado').classList.add('active');

    } catch (error) {
        console.error('Erro:', error);
        toast.error('Erro ao carregar dados do feriado');
    }
}

function fecharModalFeriado() {
    document.getElementById('modalFeriado').classList.remove('active');
}

async function salvarFeriado() {
    const token = localStorage.getItem('token');
    const nome = document.getElementById('feriadoNome').value.trim();
    const data = document.getElementById('feriadoData').value;
    const tipo = document.getElementById('feriadoTipo').value;
    const recorrente = document.getElementById('feriadoRecorrente').checked;

    // Validar
    if (!nome || !data) {
        toast.error('Preencha todos os campos obrigatórios');
        return;
    }

    const dto = {
        nome,
        data, // formato YYYY-MM-DD
        tipo,
        recorrente
    };

    try {
        const url = feriadoEditandoId
            ? `/api/feriados/${feriadoEditandoId}`
            : '/api/feriados';

        const method = feriadoEditandoId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(dto)
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Erro ao salvar feriado');
        }

        toast.success(feriadoEditandoId ? 'Feriado atualizado com sucesso!' : 'Feriado cadastrado com sucesso!');
        fecharModalFeriado();
        carregarFeriados();

    } catch (error) {
        console.error('Erro:', error);
        toast.error(error.message || 'Erro ao salvar feriado');
    }
}

async function excluirFeriado(id, nome) {
    if (!confirm(`Deseja realmente excluir o feriado "${nome}"?`)) {
        return;
    }

    const token = localStorage.getItem('token');

    try {
        const response = await fetch(`/api/feriados/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao excluir feriado');
        }

        toast.success('Feriado excluído com sucesso!');
        carregarFeriados();

    } catch (error) {
        console.error('Erro:', error);
        toast.error('Erro ao excluir feriado');
    }
}
