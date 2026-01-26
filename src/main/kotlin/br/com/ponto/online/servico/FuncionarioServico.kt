package br.com.ponto.online.servico

import br.com.ponto.online.dto.FuncionarioCadastroDTO
import br.com.ponto.online.dto.FuncionarioDetalheDTO
import br.com.ponto.online.dto.FuncionarioEdicaoDTO
import br.com.ponto.online.dto.FuncionarioListagemDTO
import br.com.ponto.online.entidade.Funcionario
import br.com.ponto.online.enums.Role
import br.com.ponto.online.repositorio.EmpresaRepositorio
import br.com.ponto.online.repositorio.EscalaRepositorio
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class FuncionarioServico(
    private val funcionarioRepositorio: FuncionarioRepositorio,
    private val empresaRepositorio: EmpresaRepositorio,
    private val escalaRepositorio: EscalaRepositorio,
    private val passwordEncoder: PasswordEncoder
) {

    fun listarTodos(): List<FuncionarioListagemDTO> {
        val funcionarios = funcionarioRepositorio.findAll()
        return funcionarios.map { converterParaListagemDTO(it) }
    }

    fun buscarPorId(id: Long): FuncionarioDetalheDTO {
        val funcionario = funcionarioRepositorio.findById(id)
            .orElseThrow { RuntimeException("Funcionário não encontrado com ID: $id") }
        return converterParaDetalheDTO(funcionario)
    }

    fun criar(dto: FuncionarioCadastroDTO): FuncionarioDetalheDTO {
        // Validações
        if (funcionarioRepositorio.existsByEmail(dto.email)) {
            throw RuntimeException("Email já cadastrado: ${dto.email}")
        }
        if (funcionarioRepositorio.existsByCpf(dto.cpf)) {
            throw RuntimeException("CPF já cadastrado: ${dto.cpf}")
        }

        // Busca empresa
        val empresa = empresaRepositorio.findById(dto.empresaId)
            .orElseThrow { RuntimeException("Empresa não encontrada com ID: ${dto.empresaId}") }

        // Converte role
        val role = try {
            Role.valueOf(dto.role.uppercase())
        } catch (e: IllegalArgumentException) {
            throw RuntimeException("Role inválida: ${dto.role}. Use ADMIN ou FUNCIONARIO")
        }

        // Busca escala se fornecida
        val escala = dto.escalaId?.let { escalaId ->
            escalaRepositorio.findById(escalaId)
                .orElseThrow { RuntimeException("Escala não encontrada com ID: $escalaId") }
        }

        // Cria funcionário
        val funcionario = Funcionario(
            id = null,
            nome = dto.nome,
            cpf = dto.cpf,
            email = dto.email,
            senha = passwordEncoder.encode(dto.senha),
            role = role,
            empresa = empresa,
            escala = escala,
            ativo = true
        )

        val salvo = funcionarioRepositorio.save(funcionario)
        return converterParaDetalheDTO(salvo)
    }

    fun atualizar(id: Long, dto: FuncionarioEdicaoDTO): FuncionarioDetalheDTO {
        val funcionario = funcionarioRepositorio.findById(id)
            .orElseThrow { RuntimeException("Funcionário não encontrado com ID: $id") }

        // Valida email único (exceto o próprio)
        if (funcionarioRepositorio.existsByEmailAndIdNot(dto.email, id)) {
            throw RuntimeException("Email já cadastrado: ${dto.email}")
        }

        // Busca empresa
        val empresa = empresaRepositorio.findById(dto.empresaId)
            .orElseThrow { RuntimeException("Empresa não encontrada com ID: ${dto.empresaId}") }

        // Converte role
        val role = try {
            Role.valueOf(dto.role.uppercase())
        } catch (e: IllegalArgumentException) {
            throw RuntimeException("Role inválida: ${dto.role}. Use ADMIN ou FUNCIONARIO")
        }

        // Busca escala se fornecida
        val escala = dto.escalaId?.let { escalaId ->
            escalaRepositorio.findById(escalaId)
                .orElseThrow { RuntimeException("Escala não encontrada com ID: $escalaId") }
        }

        // Atualiza campos
        val funcionarioAtualizado = Funcionario(
            id = funcionario.id,
            nome = dto.nome,
            cpf = funcionario.cpf,
            email = dto.email,
            senha = if (dto.senha.isNullOrBlank()) funcionario.senha else passwordEncoder.encode(dto.senha),
            role = role,
            empresa = empresa,
            escala = escala,
            ativo = funcionario.ativo
        )

        val salvo = funcionarioRepositorio.save(funcionarioAtualizado)
        return converterParaDetalheDTO(salvo)
    }

    fun desativar(id: Long) {
        val funcionario = funcionarioRepositorio.findById(id)
            .orElseThrow { RuntimeException("Funcionário não encontrado com ID: $id") }
        
        funcionario.ativo = false
        funcionarioRepositorio.save(funcionario)
    }

    fun ativar(id: Long) {
        val funcionario = funcionarioRepositorio.findById(id)
            .orElseThrow { RuntimeException("Funcionário não encontrado com ID: $id") }
        
        funcionario.ativo = true
        funcionarioRepositorio.save(funcionario)
    }

    fun buscarComFiltros(
        nome: String?,
        empresaId: Long?,
        role: String?,
        ativo: Boolean?
    ): List<FuncionarioListagemDTO> {
        var funcionarios = funcionarioRepositorio.findAll()

        // Aplica filtros
        if (!nome.isNullOrBlank()) {
            funcionarios = funcionarios.filter { 
                it.nome.contains(nome, ignoreCase = true) 
            }
        }

        if (empresaId != null) {
            funcionarios = funcionarios.filter { it.empresa.id == empresaId }
        }

        if (!role.isNullOrBlank()) {
            val roleEnum = try {
                Role.valueOf(role.uppercase())
            } catch (e: IllegalArgumentException) {
                throw RuntimeException("Role inválida: $role")
            }
            funcionarios = funcionarios.filter { it.role == roleEnum }
        }

        if (ativo != null) {
            funcionarios = funcionarios.filter { it.ativo == ativo }
        }

        return funcionarios.map { converterParaListagemDTO(it) }
    }

    // Métodos auxiliares de conversão
    private fun converterParaListagemDTO(funcionario: Funcionario): FuncionarioListagemDTO {
        return FuncionarioListagemDTO(
            id = funcionario.id!!,
            nome = funcionario.nome,
            cpf = funcionario.cpf,
            email = funcionario.email,
            empresa = funcionario.empresa.razaoSocial,
            role = funcionario.role.name,
            ativo = funcionario.ativo,
            ultimoRegistro = null, // Será preenchido se necessário
            statusHoje = if (funcionario.ativo) "Ativo" else "Inativo",
            escalaId = funcionario.escala?.id,
            escalaNome = funcionario.escala?.nome
        )
    }

    private fun converterParaDetalheDTO(funcionario: Funcionario): FuncionarioDetalheDTO {
        return FuncionarioDetalheDTO(
            id = funcionario.id!!,
            nome = funcionario.nome,
            cpf = funcionario.cpf,
            email = funcionario.email,
            role = funcionario.role.name,
            empresaNome = funcionario.empresa.razaoSocial,
            empresaId = funcionario.empresa.id!!,
            ativo = funcionario.ativo,
            escalaId = funcionario.escala?.id,
            escalaNome = funcionario.escala?.nome
        )
    }
}
