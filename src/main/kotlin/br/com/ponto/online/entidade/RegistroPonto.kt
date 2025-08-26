package br.com.ponto.online.entidade

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "registros_ponto")
class RegistroPonto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "data_hora", nullable = false)
    val dataHora: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    val funcionario: Funcionario
)