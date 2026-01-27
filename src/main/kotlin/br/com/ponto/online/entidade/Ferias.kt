package br.com.ponto.online.entidade

import br.com.ponto.online.enums.StatusFerias
import br.com.ponto.online.enums.TipoAfastamento
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "ferias")
data class Ferias(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    val funcionario: Funcionario,

    @Column(name = "data_inicio", nullable = false)
    val dataInicio: LocalDate,

    @Column(name = "data_fim", nullable = false)
    val dataFim: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    val tipo: TipoAfastamento,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: StatusFerias = StatusFerias.SOLICITADA,

    @Column(length = 500)
    val observacao: String? = null,

    @Column(name = "observacao_gestor", length = 500)
    val observacaoGestor: String? = null,

    @Column(name = "data_solicitacao", nullable = false)
    val dataSolicitacao: LocalDateTime = LocalDateTime.now(),

    @Column(name = "data_aprovacao")
    val dataAprovacao: LocalDateTime? = null,

    @Column(name = "aprovador_nome")
    val aprovadorNome: String? = null
)
