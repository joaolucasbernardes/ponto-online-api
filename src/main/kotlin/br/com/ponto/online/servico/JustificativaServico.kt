package br.com.ponto.online.servico

import br.com.ponto.online.dto.AprovacaoJustificativaDTO
import br.com.ponto.online.dto.JustificativaRequisicaoDTO
import br.com.ponto.online.entidade.Justificativa
import br.com.ponto.online.enums.StatusJustificativa
import br.com.ponto.online.enums.TipoAlteracao
import br.com.ponto.online.enums.TipoJustificativa
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import br.com.ponto.online.repositorio.JustificativaRepositorio
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.NoSuchElementException

@Service
class JustificativaServico(
    private val justificativaRepositorio: JustificativaRepositorio,
    private val funcionarioRepositorio: FuncionarioRepositorio,
    private val historicoServico: HistoricoAlteracaoServico
) {
    
    fun criarJustificativa(requisicaoDTO: JustificativaRequisicaoDTO): Justificativa {
        // Validar funcionário
        val funcionario = funcionarioRepositorio.findById(requisicaoDTO.funcionarioId)
            .orElseThrow { NoSuchElementException("Funcionário com ID ${requisicaoDTO.funcionarioId} não encontrado.") }
        
        // Validação 1: Não pode justificar data futura
        if (requisicaoDTO.data.isAfter(LocalDate.now())) {
            throw IllegalStateException("Não é possível justificar datas futuras.")
        }
        
        // Validação 2: Verificar se já existe justificativa para a data
        val justificativaExistente = justificativaRepositorio.findByFuncionarioIdAndData(
            requisicaoDTO.funcionarioId,
            requisicaoDTO.data
        )
        
        if (justificativaExistente != null) {
            throw IllegalStateException(
                "Já existe uma justificativa para a data ${requisicaoDTO.data}. " +
                "Status: ${justificativaExistente.status}"
            )
        }
        
        // Validação 3: Para AJUSTE_PONTO, hora original e solicitada são obrigatórias
        if (requisicaoDTO.tipo == TipoJustificativa.AJUSTE_PONTO) {
            if (requisicaoDTO.horaOriginal == null || requisicaoDTO.horaSolicitada == null) {
                throw IllegalArgumentException(
                    "Para ajuste de ponto, é necessário informar a hora original e a hora solicitada."
                )
            }
        }
        
        // Validação 4: Motivo não pode estar vazio
        if (requisicaoDTO.motivo.isBlank()) {
            throw IllegalArgumentException("O motivo da justificativa não pode estar vazio.")
        }
        
        // Criar justificativa
        val novaJustificativa = Justificativa(
            funcionario = funcionario,
            data = requisicaoDTO.data,
            tipo = requisicaoDTO.tipo,
            motivo = requisicaoDTO.motivo.trim(),
            horaOriginal = requisicaoDTO.horaOriginal,
            horaSolicitada = requisicaoDTO.horaSolicitada,
            status = StatusJustificativa.PENDENTE
        )
        
        val justificativaSalva = justificativaRepositorio.save(novaJustificativa)
        
        // Registrar no histórico
        historicoServico.registrarCriacao(
            descricao = "Justificativa criada: ${requisicaoDTO.tipo} para ${requisicaoDTO.data}",
            usuario = funcionario,
            ipOrigem = null
        )
        
        return justificativaSalva
    }
    
    fun listarPorFuncionario(funcionarioId: Long): List<Justificativa> {
        return justificativaRepositorio.findByFuncionarioIdOrderByDataCriacaoDesc(funcionarioId)
    }
    
    fun listarTodas(): List<Justificativa> {
        return justificativaRepositorio.findAll().sortedByDescending { it.dataCriacao }
    }
    
    fun listarPendentes(): List<Justificativa> {
        return justificativaRepositorio.findByStatusOrderByDataCriacaoAsc(StatusJustificativa.PENDENTE)
    }
    
    fun aprovarOuRejeitar(justificativaId: Long, aprovacaoDTO: AprovacaoJustificativaDTO): Justificativa {
        // Buscar justificativa
        val justificativa = justificativaRepositorio.findById(justificativaId)
            .orElseThrow { NoSuchElementException("Justificativa com ID $justificativaId não encontrada.") }
        
        // Validar se já foi processada
        if (justificativa.status != StatusJustificativa.PENDENTE) {
            throw IllegalStateException(
                "Esta justificativa já foi processada. Status atual: ${justificativa.status}"
            )
        }
        
        // Buscar aprovador
        val aprovador = funcionarioRepositorio.findById(aprovacaoDTO.aprovadorId)
            .orElseThrow { NoSuchElementException("Aprovador com ID ${aprovacaoDTO.aprovadorId} não encontrado.") }
        
        // Atualizar status
        val novoStatus = if (aprovacaoDTO.aprovada) StatusJustificativa.APROVADA else StatusJustificativa.REJEITADA
        
        val justificativaAtualizada = justificativa.copy(
            status = novoStatus,
            aprovador = aprovador,
            dataAprovacao = LocalDateTime.now(),
            observacaoAprovador = aprovacaoDTO.observacao?.trim()
        )
        
        val justificativaSalva = justificativaRepositorio.save(justificativaAtualizada)
        
        // Registrar no histórico
        historicoServico.registrarAlteracao(
            descricao = "Justificativa ${if (aprovacaoDTO.aprovada) "aprovada" else "rejeitada"} por ${aprovador.nome}",
            valorAnterior = "Status: PENDENTE",
            valorNovo = "Status: $novoStatus",
            usuario = aprovador,
            ipOrigem = null
        )
        
        // TODO: Se aprovada e tipo AJUSTE_PONTO, criar/ajustar registro de ponto
        // Isso será implementado na próxima sprint
        
        return justificativaSalva
    }
    
    fun contarPendentes(): Long {
        return justificativaRepositorio.countByStatus(StatusJustificativa.PENDENTE)
    }
}
