package br.com.ponto.online.entidade

import br.com.ponto.online.enums.StatusJustificativa
import br.com.ponto.online.enums.TipoJustificativa
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "justificativas")
data class Justificativa(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    val funcionario: Funcionario,
    
    @Column(nullable = false)
    val data: LocalDate,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val tipo: TipoJustificativa,
    
    @Column(nullable = false, length = 1000)
    val motivo: String,
    
    @Column(name = "hora_original")
    val horaOriginal: LocalTime? = null,
    
    @Column(name = "hora_solicitada")
    val horaSolicitada: LocalTime? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: StatusJustificativa = StatusJustificativa.PENDENTE,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprovador_id")
    val aprovador: Funcionario? = null,
    
    @Column(name = "data_aprovacao")
    val dataAprovacao: LocalDateTime? = null,
    
    @Column(name = "observacao_aprovador", length = 500)
    val observacaoAprovador: String? = null,
    
    @Column(name = "data_criacao", nullable = false)
    val dataCriacao: LocalDateTime = LocalDateTime.now()
)
