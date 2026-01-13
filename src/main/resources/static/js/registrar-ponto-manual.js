/**
 * Manual Time Entry Page - JavaScript
 * Handles form submission, employee loading, and history display
 */

document.addEventListener('DOMContentLoaded', () => {
    console.log('Manual time entry page loaded');

    const form = document.getElementById('formRegistroManual');
    const funcionarioSelect = document.getElementById('funcionarioSelect');
    const dataRegistro = document.getElementById('dataRegistro');
    const horaRegistro = document.getElementById('horaRegistro');

    // Set default date to today
    const hoje = new Date();
    dataRegistro.value = hoje.toISOString().split('T')[0];

    // Load employees and history
    carregarFuncionarios();
    carregarHistorico();

    // Form submission
    form.addEventListener('submit', handleSubmit);
});

/**
 * Load active employees for dropdown
 */
async function carregarFuncionarios() {
    const token = localStorage.getItem('token');

    if (!token) {
        toast.error('Sessão expirada. Faça login novamente.');
        window.location.href = '/login.html';
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/admin/dashboard/funcionarios-ativos', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error('Erro ao carregar funcionários');

        const funcionarios = await response.json();
        const select = document.getElementById('funcionarioSelect');

        // Clear existing options except the first one
        select.innerHTML = '<option value="">Selecione um funcionário...</option>';

        // Add employee options
        funcionarios.forEach(func => {
            const option = document.createElement('option');
            option.value = func.id;
            option.textContent = `${func.nome} - ${func.email}`;
            select.appendChild(option);
        });

        console.log(`${funcionarios.length} funcionários carregados`);

    } catch (error) {
        console.error('Erro ao carregar funcionários:', error);
        toast.error('Erro ao carregar lista de funcionários');
    }
}

/**
 * Handle form submission
 */
async function handleSubmit(event) {
    event.preventDefault();

    const funcionarioId = document.getElementById('funcionarioSelect').value;
    const data = document.getElementById('dataRegistro').value;
    const hora = document.getElementById('horaRegistro').value;
    const tipo = document.querySelector('input[name="tipo"]:checked').value;
    const observacao = document.getElementById('observacao').value;

    if (!funcionarioId || !data || !hora) {
        toast.error('Preencha todos os campos obrigatórios');
        return;
    }

    // Combine date and time
    const dataHora = `${data}T${hora}:00`;

    const token = localStorage.getItem('token');

    try {
        const response = await fetch('http://localhost:8080/registros-ponto', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                funcionarioId: parseInt(funcionarioId),
                dataHora: dataHora,
                tipo: tipo
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.mensagem || 'Erro ao registrar ponto');
        }

        const resultado = await response.json();

        toast.success(`Ponto registrado com sucesso! ${tipo} às ${hora}`);

        // Reset form
        limparFormulario();

        // Reload history
        carregarHistorico();

        console.log('Registro criado:', resultado);

    } catch (error) {
        console.error('Erro ao registrar ponto:', error);
        toast.error(error.message);
    }
}

/**
 * Clear form fields
 */
function limparFormulario() {
    document.getElementById('formRegistroManual').reset();

    // Reset date to today
    const hoje = new Date();
    document.getElementById('dataRegistro').value = hoje.toISOString().split('T')[0];

    // Reset radio to ENTRADA
    document.querySelector('input[name="tipo"][value="ENTRADA"]').checked = true;
}

/**
 * Load recent manual entries history
 */
async function carregarHistorico() {
    const token = localStorage.getItem('token');
    const historicoDiv = document.getElementById('historicoRegistros');

    try {
        // Get all employees to build a map of ID -> name
        const funcionariosResponse = await fetch('http://localhost:8080/api/admin/dashboard/funcionarios-ativos', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const funcionarios = await funcionariosResponse.json();
        const funcionariosMap = {};
        funcionarios.forEach(f => {
            funcionariosMap[f.id] = f.nome;
        });

        // Get recent records (last 10)
        // Note: This endpoint doesn't exist yet, so we'll show a placeholder
        // In a real implementation, you'd create a backend endpoint for this

        historicoDiv.innerHTML = `
            <div class="history-item">
                <div class="history-item-header">
                    <span class="history-item-name">Histórico não disponível</span>
                </div>
                <div class="history-item-details">
                    Para visualizar o histórico completo, acesse o painel de cada funcionário.
                </div>
            </div>
        `;

    } catch (error) {
        console.error('Erro ao carregar histórico:', error);
        historicoDiv.innerHTML = '<div class="loading">Erro ao carregar histórico</div>';
    }
}

/**
 * Logout function
 */
function logout() {
    localStorage.clear();
    window.location.href = '/login.html';
}
