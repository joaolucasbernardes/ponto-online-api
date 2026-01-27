package br.com.ponto.online.dto

import br.com.ponto.online.enums.TipoAfastamento
import java.time.LocalDate

data class FeriasRequisicaoDTO(
    val funcionarioId: Long,
    val dataInicio: LocalDate,
    val dataFim: LocalDate,
    val tipo: TipoAfastamento,
    val observacao: String? = null
)
