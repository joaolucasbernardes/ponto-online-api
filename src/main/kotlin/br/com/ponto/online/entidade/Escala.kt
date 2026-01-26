package br.com.ponto.online.entidade

import br.com.ponto.online.enumeracao.TipoEscala
import jakarta.persistence.*
import java.time.LocalTime
import java.time.LocalDateTime

@Entity
@Table(name = "escalas")
data class Escala(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false, length = 100)
    val nome: String,
    
    @Column(length = 500)
    val descricao: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val tipo: TipoEscala,
    
    @Column(name = "hora_entrada", nullable = false)
    val horaEntrada: LocalTime,
    
    @Column(name = "hora_saida", nullable = false)
    val horaSaida: LocalTime,
    
    @Column(name = "intervalo_minutos", nullable = false)
    val intervaloMinutos: Int = 60, // Padrão: 1 hora
    
    @Column(name = "carga_horaria_diaria", nullable = false)
    val cargaHorariaDiaria: Double = 8.0,
    
    @Column(name = "carga_horaria_semanal", nullable = false)
    val cargaHorariaSemanal: Double = 40.0,
    
    // JSON array de dias: ["SEGUNDA","TERCA","QUARTA","QUINTA","SEXTA"]
    @Column(name = "dias_semana", nullable = false, length = 200)
    val diasSemana: String,
    
    @Column(name = "tolerancia_entrada_minutos", nullable = false)
    val toleranciaEntradaMinutos: Int = 10,
    
    @Column(name = "tolerancia_saida_minutos", nullable = false)
    val toleranciaSaidaMinutos: Int = 10,
    
    @Column(nullable = false)
    val ativo: Boolean = true,
    
    @Column(name = "escala_padrao", nullable = false)
    val escalaPadrao: Boolean = false, // Apenas uma escala pode ser padrão
    
    @Column(name = "data_criacao", nullable = false)
    val dataCriacao: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "data_atualizacao", nullable = false)
    var dataAtualizacao: LocalDateTime = LocalDateTime.now()
)
