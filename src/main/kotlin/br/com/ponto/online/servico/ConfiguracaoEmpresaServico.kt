package br.com.ponto.online.servico

import br.com.ponto.online.dto.ConfiguracaoEmpresaDTO
import br.com.ponto.online.entidade.ConfiguracaoEmpresa
import br.com.ponto.online.repositorio.ConfiguracaoEmpresaRepositorio
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
class ConfiguracaoEmpresaServico(
    private val configuracaoRepositorio: ConfiguracaoEmpresaRepositorio
) {
    
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    /**
     * Obtém a configuração atual da empresa
     * Se não existir, cria uma configuração padrão
     */
    fun obterConfiguracao(): ConfiguracaoEmpresa {
        return configuracaoRepositorio.findFirstByOrderByIdDesc()
            ?: criarConfiguracaoPadrao()
    }
    
    /**
     * Atualiza a configuração da empresa
     */
    fun atualizarConfiguracao(dto: ConfiguracaoEmpresaDTO): ConfiguracaoEmpresa {
        // Validar dados
        validarConfiguracao(dto)
        
        // Converter DTO para entidade
        val configuracao = ConfiguracaoEmpresa(
            id = dto.id ?: 0,
            horaEntradaPadrao = LocalTime.parse(dto.horaEntradaPadrao, timeFormatter),
            horaSaidaAlmocoPadrao = LocalTime.parse(dto.horaSaidaAlmocoPadrao, timeFormatter),
            horaRetornoAlmocoPadrao = LocalTime.parse(dto.horaRetornoAlmocoPadrao, timeFormatter),
            horaSaidaPadrao = LocalTime.parse(dto.horaSaidaPadrao, timeFormatter),
            cargaHorariaDiaria = dto.cargaHorariaDiaria,
            cargaHorariaSemanal = dto.cargaHorariaSemanal,
            trabalhaSegunda = dto.diasUteis.segunda,
            trabalhaTerca = dto.diasUteis.terca,
            trabalhaQuarta = dto.diasUteis.quarta,
            trabalhaQuinta = dto.diasUteis.quinta,
            trabalhaSexta = dto.diasUteis.sexta,
            trabalhaSabado = dto.diasUteis.sabado,
            trabalhaDomingo = dto.diasUteis.domingo,
            toleranciaEntradaMinutos = dto.tolerancias.entradaMinutos,
            toleranciaRetornoAlmocoMinutos = dto.tolerancias.retornoAlmocoMinutos,
            intervaloAlmocoMinimoMinutos = dto.intervaloAlmoco.minimoMinutos,
            intervaloAlmocoMaximoMinutos = dto.intervaloAlmoco.maximoMinutos,
            permiteHorasExtras = dto.horasExtras.permite,
            limiteHorasExtrasDiariasMinutos = dto.horasExtras.limiteDiarioMinutos,
            limiteHorasExtrasMensaisMinutos = dto.horasExtras.limiteMensalMinutos,
            percentualHoraExtra = dto.horasExtras.percentual,
            dataCriacao = if (dto.id != null && dto.id > 0) {
                configuracaoRepositorio.findById(dto.id).get().dataCriacao
            } else {
                LocalDateTime.now()
            },
            dataAtualizacao = LocalDateTime.now()
        )
        
        return configuracaoRepositorio.save(configuracao)
    }
    
    /**
     * Cria configuração padrão (8h/dia, seg-sex, 1h almoço)
     */
    fun criarConfiguracaoPadrao(): ConfiguracaoEmpresa {
        val configuracaoPadrao = ConfiguracaoEmpresa(
            horaEntradaPadrao = LocalTime.of(8, 0),
            horaSaidaAlmocoPadrao = LocalTime.of(12, 0),
            horaRetornoAlmocoPadrao = LocalTime.of(13, 0),
            horaSaidaPadrao = LocalTime.of(17, 0),
            cargaHorariaDiaria = 480, // 8 horas = 480 minutos
            cargaHorariaSemanal = 2400, // 40 horas = 2400 minutos
            trabalhaSegunda = true,
            trabalhaTerca = true,
            trabalhaQuarta = true,
            trabalhaQuinta = true,
            trabalhaSexta = true,
            trabalhaSabado = false,
            trabalhaDomingo = false,
            toleranciaEntradaMinutos = 10,
            toleranciaRetornoAlmocoMinutos = 10,
            intervaloAlmocoMinimoMinutos = 60,
            intervaloAlmocoMaximoMinutos = 120,
            permiteHorasExtras = true,
            limiteHorasExtrasDiariasMinutos = 120, // 2 horas
            limiteHorasExtrasMensaisMinutos = 2400, // 40 horas
            percentualHoraExtra = 50
        )
        
        return configuracaoRepositorio.save(configuracaoPadrao)
    }
    
    /**
     * Valida consistência da configuração
     */
    private fun validarConfiguracao(dto: ConfiguracaoEmpresaDTO) {
        val entrada = LocalTime.parse(dto.horaEntradaPadrao, timeFormatter)
        val saidaAlmoco = LocalTime.parse(dto.horaSaidaAlmocoPadrao, timeFormatter)
        val retornoAlmoco = LocalTime.parse(dto.horaRetornoAlmocoPadrao, timeFormatter)
        val saida = LocalTime.parse(dto.horaSaidaPadrao, timeFormatter)
        
        // Validar sequência de horários
        require(entrada.isBefore(saidaAlmoco)) {
            "Hora de entrada deve ser antes da saída para almoço"
        }
        require(saidaAlmoco.isBefore(retornoAlmoco)) {
            "Hora de saída para almoço deve ser antes do retorno"
        }
        require(retornoAlmoco.isBefore(saida)) {
            "Hora de retorno do almoço deve ser antes da saída"
        }
        
        // Validar intervalo de almoço
        require(dto.intervaloAlmoco.minimoMinutos < dto.intervaloAlmoco.maximoMinutos) {
            "Intervalo mínimo de almoço deve ser menor que o máximo"
        }
        require(dto.intervaloAlmoco.minimoMinutos >= 0) {
            "Intervalo mínimo de almoço deve ser positivo"
        }
        
        // Validar que pelo menos um dia útil está selecionado
        val temDiaUtil = dto.diasUteis.segunda || dto.diasUteis.terca || 
                        dto.diasUteis.quarta || dto.diasUteis.quinta || 
                        dto.diasUteis.sexta || dto.diasUteis.sabado || 
                        dto.diasUteis.domingo
        require(temDiaUtil) {
            "Deve haver pelo menos um dia útil configurado"
        }
        
        // Validar tolerâncias
        require(dto.tolerancias.entradaMinutos >= 0) {
            "Tolerância de entrada deve ser positiva"
        }
        require(dto.tolerancias.retornoAlmocoMinutos >= 0) {
            "Tolerância de retorno do almoço deve ser positiva"
        }
        
        // Validar horas extras
        require(dto.horasExtras.limiteDiarioMinutos >= 0) {
            "Limite diário de horas extras deve ser positivo"
        }
        require(dto.horasExtras.limiteMensalMinutos >= 0) {
            "Limite mensal de horas extras deve ser positivo"
        }
        require(dto.horasExtras.percentual >= 0) {
            "Percentual de hora extra deve ser positivo"
        }
        
        // Validar carga horária
        require(dto.cargaHorariaDiaria > 0) {
            "Carga horária diária deve ser positiva"
        }
        require(dto.cargaHorariaSemanal > 0) {
            "Carga horária semanal deve ser positiva"
        }
    }
    
    /**
     * Verifica se uma data é dia útil
     */
    fun isDiaUtil(data: LocalDate): Boolean {
        val config = obterConfiguracao()
        
        return when (data.dayOfWeek) {
            DayOfWeek.MONDAY -> config.trabalhaSegunda
            DayOfWeek.TUESDAY -> config.trabalhaTerca
            DayOfWeek.WEDNESDAY -> config.trabalhaQuarta
            DayOfWeek.THURSDAY -> config.trabalhaQuinta
            DayOfWeek.FRIDAY -> config.trabalhaSexta
            DayOfWeek.SATURDAY -> config.trabalhaSabado
            DayOfWeek.SUNDAY -> config.trabalhaDomingo
        }
    }
}
