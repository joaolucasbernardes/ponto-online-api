package br.com.ponto.online.dto

import br.com.ponto.online.enums.StatusFerias
import br.com.ponto.online.enums.TipoAfastamento
import java.time.LocalDate
import java.time.LocalDateTime

data class FeriasRespostaDTO(
    val id: Long,
    val funcionarioId: Long,
    val funcionarioNome: String,
    val dataInicio: LocalDate,
    val dataFim: LocalDate,
    val dias: Long,
    val tipo: TipoAfastamento,
    val status: StatusFerias,
    val observacao: String?,
    val observacaoGestor: String?,
    val dataSolicitacao: LocalDateTime,
    val dataAprovacao: LocalDateTime?,
    val aprovadorNome: String?
)
