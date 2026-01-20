package br.com.ponto.online.dto

import br.com.ponto.online.entidade.Feriado
import br.com.ponto.online.enumeracao.TipoFeriado
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FeriadoRequisicaoDTO(
    val nome: String,
    val data: String, // formato: YYYY-MM-DD
    val tipo: TipoFeriado,
    val recorrente: Boolean = true
)

data class FeriadoRespostaDTO(
    val id: Long,
    val nome: String,
    val data: String,
    val tipo: TipoFeriado,
    val recorrente: Boolean,
    val dataCriacao: String
) {
    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        
        fun deEntidade(feriado: Feriado): FeriadoRespostaDTO {
            return FeriadoRespostaDTO(
                id = feriado.id,
                nome = feriado.nome,
                data = feriado.data.format(dateFormatter),
                tipo = feriado.tipo,
                recorrente = feriado.recorrente,
                dataCriacao = feriado.dataCriacao.format(dateTimeFormatter)
            )
        }
    }
}
