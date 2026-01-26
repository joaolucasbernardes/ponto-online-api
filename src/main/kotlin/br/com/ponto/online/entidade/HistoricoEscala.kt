package br.com.ponto.online.entidade

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "historico_escalas")
data class HistoricoEscala(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    val funcionario: Funcionario,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escala_anterior_id")
    val escalaAnterior: Escala? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escala_nova_id", nullable = false)
    val escalaNova: Escala,
    
    @Column(name = "data_alteracao", nullable = false)
    val dataAlteracao: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "motivo_alteracao", nullable = false, length = 500)
    val motivoAlteracao: String,
    
    @Column(name = "alterado_por", length = 100)
    val alteradoPor: String? = null // Email ou nome do admin que fez a alteração
)
