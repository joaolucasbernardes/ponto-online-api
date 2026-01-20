package br.com.ponto.online.entidade

import jakarta.persistence.*
import java.time.LocalTime
import java.time.LocalDateTime

@Entity
@Table(name = "configuracoes_empresa")
data class ConfiguracaoEmpresa(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    // Horário de Trabalho
    @Column(name = "hora_entrada_padrao", nullable = false)
    val horaEntradaPadrao: LocalTime,
    
    @Column(name = "hora_saida_almoco_padrao", nullable = false)
    val horaSaidaAlmocoPadrao: LocalTime,
    
    @Column(name = "hora_retorno_almoco_padrao", nullable = false)
    val horaRetornoAlmocoPadrao: LocalTime,
    
    @Column(name = "hora_saida_padrao", nullable = false)
    val horaSaidaPadrao: LocalTime,
    
    @Column(name = "carga_horaria_diaria", nullable = false)
    val cargaHorariaDiaria: Int, // em minutos
    
    @Column(name = "carga_horaria_semanal", nullable = false)
    val cargaHorariaSemanal: Int, // em minutos
    
    // Dias Úteis
    @Column(name = "trabalha_segunda", nullable = false)
    val trabalhaSegunda: Boolean = true,
    
    @Column(name = "trabalha_terca", nullable = false)
    val trabalhaTerca: Boolean = true,
    
    @Column(name = "trabalha_quarta", nullable = false)
    val trabalhaQuarta: Boolean = true,
    
    @Column(name = "trabalha_quinta", nullable = false)
    val trabalhaQuinta: Boolean = true,
    
    @Column(name = "trabalha_sexta", nullable = false)
    val trabalhaSexta: Boolean = true,
    
    @Column(name = "trabalha_sabado", nullable = false)
    val trabalhaSabado: Boolean = false,
    
    @Column(name = "trabalha_domingo", nullable = false)
    val trabalhaDomingo: Boolean = false,
    
    // Tolerâncias
    @Column(name = "tolerancia_entrada_minutos", nullable = false)
    val toleranciaEntradaMinutos: Int = 0,
    
    @Column(name = "tolerancia_retorno_almoco_minutos", nullable = false)
    val toleranciaRetornoAlmocoMinutos: Int = 0,
    
    // Intervalo de Almoço
    @Column(name = "intervalo_almoco_minimo_minutos", nullable = false)
    val intervaloAlmocoMinimoMinutos: Int = 60,
    
    @Column(name = "intervalo_almoco_maximo_minutos", nullable = false)
    val intervaloAlmocoMaximoMinutos: Int = 120,
    
    // Horas Extras
    @Column(name = "permite_horas_extras", nullable = false)
    val permiteHorasExtras: Boolean = true,
    
    @Column(name = "limite_horas_extras_diarias_minutos", nullable = false)
    val limiteHorasExtrasDiariasMinutos: Int = 120,
    
    @Column(name = "limite_horas_extras_mensais_minutos", nullable = false)
    val limiteHorasExtrasMensaisMinutos: Int = 2400,
    
    @Column(name = "percentual_hora_extra", nullable = false)
    val percentualHoraExtra: Int = 50,
    
    // Metadados
    @Column(name = "data_criacao", nullable = false)
    val dataCriacao: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "data_atualizacao", nullable = false)
    val dataAtualizacao: LocalDateTime = LocalDateTime.now()
)
