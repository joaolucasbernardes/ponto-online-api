// Gerenciar Funcionários - JavaScript

let funcionarios = [];
let empresas = [];
let escalas = [];
let modoEdicao = false;
let funcionarioEditandoId = null;

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
    verificarAutenticacao();
    carregarEmpresas();
    carregarEscalas();
    carregarFuncionarios();
});

// Autenticação
function verificarAutenticacao() {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    if (!token || role !== 'ADMIN') {
        window.location.href = '/login.html';
        return;
    }
}

function logout() {
    localStorage.clear();
    window.location.href = '/login.html';
}

// Carregar Empresas
async function carregarEmpresas() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch('/empresas', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Erro ao carregar empresas');

        empresas = await response.json();
        preencherSelectEmpresas();
    } catch (error) {
        console.error('Erro ao carregar empresas:', error);
        alert('Erro ao carregar empresas');
    }
}

function preencherSelectEmpresas() {
    const select = document.getElementById('inputEmpresa');
    select.innerHTML = '<option value="">Selecione...</option>';

    empresas.forEach(empresa => {
        const option = document.createElement('option');
        option.value = empresa.id;
        option.textContent = empresa.razaoSocial;
        select.appendChild(option);
    });
}

// Carregar Escalas
async function carregarEscalas() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch('/api/escalas/ativas', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Erro ao carregar escalas');

        escalas = await response.json();
        preencherSelectEscalas();
    } catch (error) {
        console.error('Erro ao carregar escalas:', error);
    }
}

function preencherSelectEscalas() {
    const select = document.getElementById('inputEscala');
    if (!select) return;

    select.innerHTML = '<option value="">Nenhuma (usar padrão)</option>';

    escalas.forEach(escala => {
        const option = document.createElement('option');
        option.value = escala.id;
        option.textContent = `${escala.nome} (${escala.horaEntrada}-${escala.horaSaida})`;
        select.appendChild(option);
    });
}

// Carregar Funcionários
async function carregarFuncionarios() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch('/api/admin/funcionarios', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Erro ao carregar funcionários');

        funcionarios = await response.json();
        renderizarTabela(funcionarios);
    } catch (error) {
        console.error('Erro ao carregar funcionários:', error);
        document.getElementById('funcionariosTableBody').innerHTML =
            '<tr><td colspan="7" class="loading">Erro ao carregar funcionários</td></tr>';
    }
}

// Renderizar Tabela
function renderizarTabela(lista) {
    const tbody = document.getElementById('funcionariosTableBody');

    if (lista.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="loading">Nenhum funcionário encontrado</td></tr>';
        return;
    }

    tbody.innerHTML = lista.map(func => `
        <tr>
            <td>${func.nome}</td>
            <td>${func.email}</td>
            <td>${formatarCPF(func.cpf || 'N/A')}</td>
            <td>${func.empresa || func.empresaNome || 'N/A'}</td>
            <td>${func.escalaNome || '<span style="color: var(--text-muted);">Padrão</span>'}</td>
            <td><span class="badge ${func.role?.toLowerCase() || 'funcionario'}">${func.role || 'FUNCIONARIO'}</span></td>
            <td><span class="badge ${func.ativo ? 'ativo' : 'inativo'}">${func.ativo ? 'Ativo' : 'Inativo'}</span></td>
            <td>
                <button onclick="abrirModalDetalhes(${func.id})" class="btn-action">Ver</button>
                <button onclick="abrirModalEditar(${func.id})" class="btn-action edit">Editar</button>
                ${func.ativo
            ? `<button onclick="desativarFuncionario(${func.id})" class="btn-action delete">Desativar</button>`
            : `<button onclick="ativarFuncionario(${func.id})" class="btn-action activate">Ativar</button>`
        }
            </td>
        </tr>
    `).join('');
}

// Filtros
function aplicarFiltros() {
    const nome = document.getElementById('searchNome').value.toLowerCase();
    const role = document.getElementById('filterRole').value;
    const status = document.getElementById('filterStatus').value;

    let filtrados = funcionarios;

    if (nome) {
        filtrados = filtrados.filter(f => f.nome.toLowerCase().includes(nome));
    }

    if (role) {
        filtrados = filtrados.filter(f => f.role === role);
    }

    if (status !== '') {
        const ativo = status === 'true';
        filtrados = filtrados.filter(f => f.ativo === ativo);
    }

    renderizarTabela(filtrados);
}

function limparFiltros() {
    document.getElementById('searchNome').value = '';
    document.getElementById('filterRole').value = '';
    document.getElementById('filterStatus').value = '';
    renderizarTabela(funcionarios);
}

// Busca em tempo real
document.getElementById('searchNome')?.addEventListener('input', aplicarFiltros);

// Modal Criar
function abrirModalCriar() {
    modoEdicao = false;
    funcionarioEditandoId = null;

    document.getElementById('modalTitle').textContent = 'Novo Funcionário';
    document.getElementById('formFuncionario').reset();
    document.getElementById('inputCpf').disabled = false;
    document.getElementById('inputSenha').required = true;
    document.getElementById('senhaOpcional').textContent = '';
    document.getElementById('senhaHint').textContent = 'Mínimo 6 caracteres';

    document.getElementById('modalFuncionario').classList.add('active');
}

// Modal Editar
async function abrirModalEditar(id) {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`/api/admin/funcionarios/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Erro ao carregar funcionário');

        const funcionario = await response.json();

        modoEdicao = true;
        funcionarioEditandoId = id;

        document.getElementById('modalTitle').textContent = 'Editar Funcionário';
        document.getElementById('inputNome').value = funcionario.nome;
        document.getElementById('inputCpf').value = funcionario.cpf;
        document.getElementById('inputCpf').disabled = true; // CPF não editável
        document.getElementById('inputEmail').value = funcionario.email;
        document.getElementById('inputSenha').value = '';
        document.getElementById('inputSenha').required = false;
        document.getElementById('senhaOpcional').textContent = '(opcional)';
        document.getElementById('senhaHint').textContent = 'Deixe em branco para manter a senha atual';
        document.getElementById('inputRole').value = funcionario.role;
        document.getElementById('inputEmpresa').value = funcionario.empresaId;
        document.getElementById('inputEscala').value = funcionario.escalaId || '';

        document.getElementById('modalFuncionario').classList.add('active');
    } catch (error) {
        console.error('Erro ao carregar funcionário:', error);
        alert('Erro ao carregar dados do funcionário');
    }
}

// Modal Detalhes
async function abrirModalDetalhes(id) {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`/api/admin/funcionarios/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Erro ao carregar funcionário');

        const funcionario = await response.json();

        document.getElementById('detalheNome').textContent = funcionario.nome;
        document.getElementById('detalheEmail').textContent = funcionario.email;
        document.getElementById('detalheCpf').textContent = formatarCPF(funcionario.cpf);
        document.getElementById('detalheEmpresa').textContent = funcionario.empresaNome;
        document.getElementById('detalheRole').textContent = funcionario.role;
        document.getElementById('detalheStatus').innerHTML =
            `<span class="badge ${funcionario.ativo ? 'ativo' : 'inativo'}">${funcionario.ativo ? 'Ativo' : 'Inativo'}</span>`;

        document.getElementById('modalDetalhes').classList.add('active');
    } catch (error) {
        console.error('Erro ao carregar funcionário:', error);
        alert('Erro ao carregar detalhes do funcionário');
    }
}

// Fechar Modais
function fecharModal() {
    document.getElementById('modalFuncionario').classList.remove('active');
}

function fecharModalDetalhes() {
    document.getElementById('modalDetalhes').classList.remove('active');
}

// Salvar Funcionário
async function salvarFuncionario(event) {
    event.preventDefault();

    const nome = document.getElementById('inputNome').value;
    const cpf = document.getElementById('inputCpf').value;
    const email = document.getElementById('inputEmail').value;
    const senha = document.getElementById('inputSenha').value;
    const role = document.getElementById('inputRole').value;
    const empresaId = parseInt(document.getElementById('inputEmpresa').value);

    // Validações
    if (!validarCPF(cpf) && !modoEdicao) {
        alert('CPF inválido');
        return;
    }

    if (!validarEmail(email)) {
        alert('Email inválido');
        return;
    }

    // VALIDAÇÃO CRÍTICA: Senha obrigatória ao criar novo funcionário
    if (!modoEdicao) {
        if (!senha || senha.trim().length === 0) {
            alert('Senha é obrigatória para criar um novo funcionário!');
            return;
        }
        if (senha.length < 6) {
            alert('Senha deve ter no mínimo 6 caracteres');
            return;
        }
    }

    // Ao editar, validar senha apenas se foi preenchida
    if (modoEdicao && senha && senha.length > 0 && senha.length < 6) {
        alert('Se informar uma nova senha, ela deve ter no mínimo 6 caracteres');
        return;
    }

    try {
        const token = localStorage.getItem('token');
        const url = modoEdicao
            ? `/api/admin/funcionarios/${funcionarioEditandoId}`
            : '/api/admin/funcionarios';

        const method = modoEdicao ? 'PUT' : 'POST';

        const escalaId = document.getElementById('inputEscala').value;

        const dados = modoEdicao
            ? { nome, email, senha: senha || null, role, empresaId, escalaId: escalaId ? parseInt(escalaId) : null }
            : { nome, cpf, email, senha, role, empresaId, escalaId: escalaId ? parseInt(escalaId) : null };

        const response = await fetch(url, {
            method: method,
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dados)
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Erro ao salvar funcionário');
        }

        alert(modoEdicao ? 'Funcionário atualizado com sucesso!' : 'Funcionário criado com sucesso!');
        fecharModal();
        carregarFuncionarios();
    } catch (error) {
        console.error('Erro ao salvar funcionário:', error);
        alert(error.message || 'Erro ao salvar funcionário');
    }
}

// Desativar Funcionário
async function desativarFuncionario(id) {
    if (!confirm('Deseja realmente desativar este funcionário?')) return;

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`/api/admin/funcionarios/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Erro ao desativar funcionário');

        alert('Funcionário desativado com sucesso!');
        carregarFuncionarios();
    } catch (error) {
        console.error('Erro ao desativar funcionário:', error);
        alert('Erro ao desativar funcionário');
    }
}

// Ativar Funcionário
async function ativarFuncionario(id) {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`/api/admin/funcionarios/${id}/ativar`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Erro ao ativar funcionário');

        alert('Funcionário ativado com sucesso!');
        carregarFuncionarios();
    } catch (error) {
        console.error('Erro ao ativar funcionário:', error);
        alert('Erro ao ativar funcionário');
    }
}

// Validações
function validarCPF(cpf) {
    cpf = cpf.replace(/[^\d]/g, '');
    return cpf.length === 11;
}

function validarEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

// Formatação
function formatarCPF(cpf) {
    if (!cpf) return 'N/A';
    cpf = cpf.replace(/[^\d]/g, '');
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}
