package br.com.ponto.online.dto

import br.com.ponto.online.entidade.ConfiguracaoEmpresa
import java.time.format.DateTimeFormatter

data class ConfiguracaoEmpresaDTO(
    val id: Long?,
    val horaEntradaPadrao: String,
    val horaSaidaAlmocoPadrao: String,
    val horaRetornoAlmocoPadrao: String,
    val horaSaidaPadrao: String,
    val cargaHorariaDiaria: Int,
    val cargaHorariaSemanal: Int,
    val diasUteis: DiasUteisDTO,
    val tolerancias: ToleranciasDTO,
    val intervaloAlmoco: IntervaloAlmocoDTO,
    val horasExtras: HorasExtrasDTO
) {
    companion object {
        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        
        fun deEntidade(config: ConfiguracaoEmpresa): ConfiguracaoEmpresaDTO {
            return ConfiguracaoEmpresaDTO(
                id = config.id,
                horaEntradaPadrao = config.horaEntradaPadrao.format(timeFormatter),
                horaSaidaAlmocoPadrao = config.horaSaidaAlmocoPadrao.format(timeFormatter),
                horaRetornoAlmocoPadrao = config.horaRetornoAlmocoPadrao.format(timeFormatter),
                horaSaidaPadrao = config.horaSaidaPadrao.format(timeFormatter),
                cargaHorariaDiaria = config.cargaHorariaDiaria,
                cargaHorariaSemanal = config.cargaHorariaSemanal,
                diasUteis = DiasUteisDTO(
                    segunda = config.trabalhaSegunda,
                    terca = config.trabalhaTerca,
                    quarta = config.trabalhaQuarta,
                    quinta = config.trabalhaQuinta,
                    sexta = config.trabalhaSexta,
                    sabado = config.trabalhaSabado,
                    domingo = config.trabalhaDomingo
                ),
                tolerancias = ToleranciasDTO(
                    entradaMinutos = config.toleranciaEntradaMinutos,
                    retornoAlmocoMinutos = config.toleranciaRetornoAlmocoMinutos
                ),
                intervaloAlmoco = IntervaloAlmocoDTO(
                    minimoMinutos = config.intervaloAlmocoMinimoMinutos,
                    maximoMinutos = config.intervaloAlmocoMaximoMinutos
                ),
                horasExtras = HorasExtrasDTO(
                    permite = config.permiteHorasExtras,
                    limiteDiarioMinutos = config.limiteHorasExtrasDiariasMinutos,
                    limiteMensalMinutos = config.limiteHorasExtrasMensaisMinutos,
                    percentual = config.percentualHoraExtra
                )
            )
        }
    }
}

data class DiasUteisDTO(
    val segunda: Boolean,
    val terca: Boolean,
    val quarta: Boolean,
    val quinta: Boolean,
    val sexta: Boolean,
    val sabado: Boolean,
    val domingo: Boolean
)

data class ToleranciasDTO(
    val entradaMinutos: Int,
    val retornoAlmocoMinutos: Int
)

data class IntervaloAlmocoDTO(
    val minimoMinutos: Int,
    val maximoMinutos: Int
)

data class HorasExtrasDTO(
    val permite: Boolean,
    val limiteDiarioMinutos: Int,
    val limiteMensalMinutos: Int,
    val percentual: Int
)
