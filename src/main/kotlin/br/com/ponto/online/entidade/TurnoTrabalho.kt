package br.com.ponto.online.entidade

import jakarta.persistence.*
import java.time.LocalTime
import java.time.LocalDateTime

@Entity
@Table(name = "turnos_trabalho")
data class TurnoTrabalho(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false, length = 100)
    val nome: String, // Ex: "Manhã", "Tarde", "Noite"
    
    @Column(length = 500)
    val descricao: String? = null,
    
    @Column(name = "hora_inicio", nullable = false)
    val horaInicio: LocalTime,
    
    @Column(name = "hora_fim", nullable = false)
    val horaFim: LocalTime,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escala_id")
    val escala: Escala? = null, // Turno pode ser independente ou vinculado a escala
    
    @Column(nullable = false)
    val ativo: Boolean = true,
    
    @Column(name = "data_criacao", nullable = false)
    val dataCriacao: LocalDateTime = LocalDateTime.now()
)
