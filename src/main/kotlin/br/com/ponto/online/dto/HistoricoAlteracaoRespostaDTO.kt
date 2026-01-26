package br.com.ponto.online.dto

import br.com.ponto.online.entidade.HistoricoAlteracao
import java.time.format.DateTimeFormatter

data class HistoricoAlteracaoRespostaDTO(
    val id: Long?,
    val tipoAlteracao: String,
    val descricao: String,
    val valorAnterior: String?,
    val valorNovo: String?,
    val usuarioNome: String,
    val dataHora: String
) {
    companion object {
        fun deEntidade(historico: HistoricoAlteracao): HistoricoAlteracaoRespostaDTO {
            return HistoricoAlteracaoRespostaDTO(
                id = historico.id,
                tipoAlteracao = historico.tipoAlteracao.name,
                descricao = historico.descricao,
                valorAnterior = historico.valorAnterior,
                valorNovo = historico.valorNovo,
                usuarioNome = historico.usuario.nome,
                dataHora = historico.dataHora.toString()
            )
        }
    }
}
