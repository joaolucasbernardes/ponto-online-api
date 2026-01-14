package br.com.ponto.online.dto

import java.time.LocalDateTime

data class RegistroPontoRequisicaoDTO(
    val funcionarioId: Long,
    val tipo: String? = null, // "ENTRADA" ou "SAIDA" - opcional, será inferido se não fornecido
    val dataHora: LocalDateTime? = null // Opcional - apenas para ADMIN fazer ajustes retroativos
)