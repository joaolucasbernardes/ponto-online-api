package br.com.ponto.online.dto

import br.com.ponto.online.entidade.LocalPermitido
import java.time.format.DateTimeFormatter

data class LocalPermitidoRequisicaoDTO(
    val nome: String,
    val endereco: String,
    val latitude: Double,
    val longitude: Double,
    val raioMetros: Int,
    val ativo: Boolean = true,
    val descricao: String? = null
)

data class LocalPermitidoRespostaDTO(
    val id: Long,
    val nome: String,
    val endereco: String,
    val latitude: Double,
    val longitude: Double,
    val raioMetros: Int,
    val ativo: Boolean,
    val descricao: String?,
    val dataCriacao: String,
    val dataAtualizacao: String
) {
    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        
        fun deEntidade(local: LocalPermitido): LocalPermitidoRespostaDTO {
            return LocalPermitidoRespostaDTO(
                id = local.id,
                nome = local.nome,
                endereco = local.endereco,
                latitude = local.latitude,
                longitude = local.longitude,
                raioMetros = local.raioMetros,
                ativo = local.ativo,
                descricao = local.descricao,
                dataCriacao = local.dataCriacao.format(dateTimeFormatter),
                dataAtualizacao = local.dataAtualizacao.format(dateTimeFormatter)
            )
        }
    }
}

data class ValidacaoLocalizacaoDTO(
    val latitude: Double,
    val longitude: Double,
    val precisaoMetros: Double? = null
)

data class ResultadoValidacaoDTO(
    val valido: Boolean,
    val localPermitido: LocalPermitidoRespostaDTO?,
    val distanciaMetros: Double?,
    val mensagem: String
)
