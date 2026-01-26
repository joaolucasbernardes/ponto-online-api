package br.com.ponto.online.dto

import br.com.ponto.online.entidade.Escala
import br.com.ponto.online.enumeracao.TipoEscala
import java.time.format.DateTimeFormatter

// ===== Escala DTOs =====

data class EscalaRequisicaoDTO(
    val nome: String,
    val descricao: String? = null,
    val tipo: TipoEscala,
    val horaEntrada: String, // "08:00"
    val horaSaida: String,   // "17:00"
    val intervaloMinutos: Int = 60,
    val cargaHorariaDiaria: Double = 8.0,
    val cargaHorariaSemanal: Double = 40.0,
    val diasSemana: List<String>, // ["SEGUNDA","TERCA","QUARTA","QUINTA","SEXTA"]
    val toleranciaEntradaMinutos: Int = 10,
    val toleranciaSaidaMinutos: Int = 10,
    val ativo: Boolean = true,
    val escalaPadrao: Boolean = false
)

data class EscalaRespostaDTO(
    val id: Long,
    val nome: String,
    val descricao: String?,
    val tipo: TipoEscala,
    val horaEntrada: String,
    val horaSaida: String,
    val intervaloMinutos: Int,
    val cargaHorariaDiaria: Double,
    val cargaHorariaSemanal: Double,
    val diasSemana: List<String>,
    val toleranciaEntradaMinutos: Int,
    val toleranciaSaidaMinutos: Int,
    val ativo: Boolean,
    val escalaPadrao: Boolean,
    val dataCriacao: String,
    val dataAtualizacao: String
) {
    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        
        fun deEntidade(escala: Escala): EscalaRespostaDTO {
            // Parse JSON array de dias
            val dias = escala.diasSemana
                .removeSurrounding("[", "]")
                .replace("\"", "")
                .split(",")
                .map { it.trim() }
            
            return EscalaRespostaDTO(
                id = escala.id,
                nome = escala.nome,
                descricao = escala.descricao,
                tipo = escala.tipo,
                horaEntrada = escala.horaEntrada.format(timeFormatter),
                horaSaida = escala.horaSaida.format(timeFormatter),
                intervaloMinutos = escala.intervaloMinutos,
                cargaHorariaDiaria = escala.cargaHorariaDiaria,
                cargaHorariaSemanal = escala.cargaHorariaSemanal,
                diasSemana = dias,
                toleranciaEntradaMinutos = escala.toleranciaEntradaMinutos,
                toleranciaSaidaMinutos = escala.toleranciaSaidaMinutos,
                ativo = escala.ativo,
                escalaPadrao = escala.escalaPadrao,
                dataCriacao = escala.dataCriacao.format(dateTimeFormatter),
                dataAtualizacao = escala.dataAtualizacao.format(dateTimeFormatter)
            )
        }
    }
}

// ===== Turno DTOs =====

data class TurnoTrabalhoRequisicaoDTO(
    val nome: String,
    val descricao: String? = null,
    val horaInicio: String, // "06:00"
    val horaFim: String,    // "14:00"
    val escalaId: Long? = null,
    val ativo: Boolean = true
)

data class TurnoTrabalhoRespostaDTO(
    val id: Long,
    val nome: String,
    val descricao: String?,
    val horaInicio: String,
    val horaFim: String,
    val escalaId: Long?,
    val escalaNome: String?,
    val ativo: Boolean,
    val dataCriacao: String
) {
    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        
        fun deEntidade(turno: br.com.ponto.online.entidade.TurnoTrabalho): TurnoTrabalhoRespostaDTO {
            return TurnoTrabalhoRespostaDTO(
                id = turno.id,
                nome = turno.nome,
                descricao = turno.descricao,
                horaInicio = turno.horaInicio.format(timeFormatter),
                horaFim = turno.horaFim.format(timeFormatter),
                escalaId = turno.escala?.id,
                escalaNome = turno.escala?.nome,
                ativo = turno.ativo,
                dataCriacao = turno.dataCriacao.format(dateTimeFormatter)
            )
        }
    }
}

// ===== Histórico DTOs =====

data class HistoricoEscalaRespostaDTO(
    val id: Long,
    val funcionarioId: Long,
    val funcionarioNome: String,
    val escalaAnteriorNome: String?,
    val escalaNovaNome: String,
    val dataAlteracao: String,
    val motivoAlteracao: String,
    val alteradoPor: String?
) {
    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        
        fun deEntidade(historico: br.com.ponto.online.entidade.HistoricoEscala): HistoricoEscalaRespostaDTO {
            return HistoricoEscalaRespostaDTO(
                id = historico.id,
                funcionarioId = historico.funcionario.id!!,
                funcionarioNome = historico.funcionario.nome,
                escalaAnteriorNome = historico.escalaAnterior?.nome,
                escalaNovaNome = historico.escalaNova.nome,
                dataAlteracao = historico.dataAlteracao.format(dateTimeFormatter),
                motivoAlteracao = historico.motivoAlteracao,
                alteradoPor = historico.alteradoPor
            )
        }
    }
}

data class AtribuirEscalaDTO(
    val escalaId: Long,
    val motivoAlteracao: String
)
