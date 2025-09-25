package br.com.ponto.online.dto

import br.com.ponto.online.entidade.RegistroPonto
import java.time.format.DateTimeFormatter

data class RegistroPontoRespostaDTO(
    val id: Long?,
    val nomeFuncionario: String,
    val dataHora: String
) {
    companion object {
        private val formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

        fun deEntidade(registro: RegistroPonto): RegistroPontoRespostaDTO {
            return RegistroPontoRespostaDTO(
                id = registro.id,
                nomeFuncionario = registro.funcionario.nome,
                dataHora = registro.dataHora.format(formatador)
            )
        }
    }
}