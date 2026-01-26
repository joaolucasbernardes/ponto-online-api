package br.com.ponto.online.entidade

import br.com.ponto.online.enums.TipoAlteracao
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "historico_alteracoes")
data class HistoricoAlteracao(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registro_ponto_id")
    val registroPonto: RegistroPonto? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "justificativa_id")
    val justificativa: Justificativa? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alteracao", nullable = false, length = 20)
    val tipoAlteracao: TipoAlteracao,
    
    @Column(nullable = false, length = 1000)
    val descricao: String,
    
    @Column(name = "valor_anterior", length = 500)
    val valorAnterior: String? = null,
    
    @Column(name = "valor_novo", length = 500)
    val valorNovo: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    val usuario: Funcionario,
    
    @Column(name = "data_hora", nullable = false)
    val dataHora: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "ip_origem", length = 50)
    val ipOrigem: String? = null
)
