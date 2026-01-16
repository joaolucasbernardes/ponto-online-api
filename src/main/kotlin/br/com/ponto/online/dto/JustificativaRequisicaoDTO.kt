package br.com.ponto.online.dto

import br.com.ponto.online.enums.TipoJustificativa
import java.time.LocalDate
import java.time.LocalTime

data class JustificativaRequisicaoDTO(
    val funcionarioId: Long,
    val data: LocalDate,
    val tipo: TipoJustificativa,
    val motivo: String,
    val horaOriginal: LocalTime? = null,
    val horaSolicitada: LocalTime? = null
)
