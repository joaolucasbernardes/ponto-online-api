package br.com.ponto.online.dto

data class AprovacaoJustificativaDTO(
    val aprovadorId: Long,
    val aprovada: Boolean,
    val observacao: String? = null
)
