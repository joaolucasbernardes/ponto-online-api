package br.com.ponto.online.entidade

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "locais_permitidos")
data class LocalPermitido(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false, length = 100)
    val nome: String,
    
    @Column(nullable = false, length = 200)
    val endereco: String,
    
    @Column(nullable = false)
    val latitude: Double,
    
    @Column(nullable = false)
    val longitude: Double,
    
    @Column(nullable = false)
    val raioMetros: Int = 100, // Raio padrão: 100 metros
    
    @Column(nullable = false)
    val ativo: Boolean = true,
    
    @Column(length = 500)
    val descricao: String? = null,
    
    @Column(nullable = false)
    val dataCriacao: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    var dataAtualizacao: LocalDateTime = LocalDateTime.now()
)
