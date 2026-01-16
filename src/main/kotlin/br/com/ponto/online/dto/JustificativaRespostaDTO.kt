package br.com.ponto.online.dto

import br.com.ponto.online.entidade.Justificativa
import br.com.ponto.online.enums.StatusJustificativa
import br.com.ponto.online.enums.TipoJustificativa
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class JustificativaRespostaDTO(
    val id: Long,
    val funcionarioId: Long,
    val funcionarioNome: String,
    val data: String,
    val tipo: TipoJustificativa,
    val motivo: String,
    val horaOriginal: String?,
    val horaSolicitada: String?,
    val status: StatusJustificativa,
    val aprovadorId: Long?,
    val aprovadorNome: String?,
    val dataAprovacao: String?,
    val observacaoAprovador: String?,
    val dataCriacao: String
) {
    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        
        fun deEntidade(justificativa: Justificativa): JustificativaRespostaDTO {
            return JustificativaRespostaDTO(
                id = justificativa.id!!,
                funcionarioId = justificativa.funcionario.id!!,
                funcionarioNome = justificativa.funcionario.nome,
                data = justificativa.data.format(dateFormatter),
                tipo = justificativa.tipo,
                motivo = justificativa.motivo,
                horaOriginal = justificativa.horaOriginal?.format(timeFormatter),
                horaSolicitada = justificativa.horaSolicitada?.format(timeFormatter),
                status = justificativa.status,
                aprovadorId = justificativa.aprovador?.id,
                aprovadorNome = justificativa.aprovador?.nome,
                dataAprovacao = justificativa.dataAprovacao?.format(dateTimeFormatter),
                observacaoAprovador = justificativa.observacaoAprovador,
                dataCriacao = justificativa.dataCriacao.format(dateTimeFormatter)
            )
        }
    }
}
