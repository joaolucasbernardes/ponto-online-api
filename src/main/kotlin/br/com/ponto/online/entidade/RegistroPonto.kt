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

    @Column(name = "tipo", nullable = false, length = 10)
    val tipo: String, // "ENTRADA" ou "SAIDA"

    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    val funcionario: Funcionario,
    
    // Campos de geolocalização
    @Column(name = "latitude")
    val latitude: Double? = null,
    
    @Column(name = "longitude")
    val longitude: Double? = null,
    
    @Column(name = "precisao_metros")
    val precisaoMetros: Double? = null,
    
    @Column(name = "dentro_do_raio")
    val dentroDoRaio: Boolean? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_permitido_id")
    val localPermitido: LocalPermitido? = null
)