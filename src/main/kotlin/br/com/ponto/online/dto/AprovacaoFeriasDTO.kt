package br.com.ponto.online.dto

data class AprovacaoFeriasDTO(
    val aprovadorId: Long,
    val aprovada: Boolean,
    val observacao: String? = null
)
