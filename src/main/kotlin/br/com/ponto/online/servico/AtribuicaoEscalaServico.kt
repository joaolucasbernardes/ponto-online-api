package br.com.ponto.online.servico

import br.com.ponto.online.dto.AtribuirEscalaDTO
import br.com.ponto.online.entidade.Funcionario
import br.com.ponto.online.entidade.HistoricoEscala
import br.com.ponto.online.repositorio.EscalaRepositorio
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import br.com.ponto.online.repositorio.HistoricoEscalaRepositorio
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AtribuicaoEscalaServico(
    private val funcionarioRepositorio: FuncionarioRepositorio,
    private val escalaRepositorio: EscalaRepositorio,
    private val historicoEscalaRepositorio: HistoricoEscalaRepositorio
) {
    
    /**
     * Atribui escala a um funcionário
     */
    @Transactional
    fun atribuirEscala(funcionarioId: Long, dto: AtribuirEscalaDTO): Funcionario {
        val funcionario = funcionarioRepositorio.findById(funcionarioId)
            .orElseThrow { IllegalArgumentException("Funcionário com ID $funcionarioId não encontrado") }
        
        val novaEscala = escalaRepositorio.findById(dto.escalaId)
            .orElseThrow { IllegalArgumentException("Escala com ID ${dto.escalaId} não encontrada") }
        
        require(novaEscala.ativo) {
            "Não é possível atribuir uma escala inativa"
        }
        
        // Registrar no histórico
        val historico = HistoricoEscala(
            funcionario = funcionario,
            escalaAnterior = funcionario.escala,
            escalaNova = novaEscala,
            motivoAlteracao = dto.motivoAlteracao,
            alteradoPor = obterUsuarioLogado()
        )
        historicoEscalaRepositorio.save(historico)
        
        // Criar novo funcionário com escala atualizada
        val funcionarioAtualizado = Funcionario(
            id = funcionario.id,
            nome = funcionario.nome,
            cpf = funcionario.cpf,
            email = funcionario.email,
            senha = funcionario.senha,
            role = funcionario.role,
            empresa = funcionario.empresa,
            escala = novaEscala,
            ativo = funcionario.ativo
        )
        return funcionarioRepositorio.save(funcionarioAtualizado)
    }
    
    /**
     * Remove escala de um funcionário (volta para escala padrão)
     */
    @Transactional
    fun removerEscala(funcionarioId: Long, motivo: String): Funcionario {
        val funcionario = funcionarioRepositorio.findById(funcionarioId)
            .orElseThrow { IllegalArgumentException("Funcionário com ID $funcionarioId não encontrado") }
        
        val escalaPadrao = escalaRepositorio.findByEscalaPadraoTrue()
        
        // Registrar no histórico
        val historico = HistoricoEscala(
            funcionario = funcionario,
            escalaAnterior = funcionario.escala,
            escalaNova = escalaPadrao ?: throw IllegalStateException("Nenhuma escala padrão configurada"),
            motivoAlteracao = motivo,
            alteradoPor = obterUsuarioLogado()
        )
        historicoEscalaRepositorio.save(historico)
        
        // Criar novo funcionário com escala atualizada
        val funcionarioAtualizado = Funcionario(
            id = funcionario.id,
            nome = funcionario.nome,
            cpf = funcionario.cpf,
            email = funcionario.email,
            senha = funcionario.senha,
            role = funcionario.role,
            empresa = funcionario.empresa,
            escala = escalaPadrao,
            ativo = funcionario.ativo
        )
        return funcionarioRepositorio.save(funcionarioAtualizado)
    }
    
    /**
     * Busca histórico de escalas de um funcionário
     */
    fun buscarHistorico(funcionarioId: Long): List<HistoricoEscala> {
        return historicoEscalaRepositorio.findByFuncionarioIdOrderByDataAlteracaoDesc(funcionarioId)
    }
    
    /**
     * Obtém usuário logado
     */
    private fun obterUsuarioLogado(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication?.name ?: "Sistema"
    }
}
