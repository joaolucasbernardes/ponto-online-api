package br.com.ponto.online.entidade

import br.com.ponto.online.enumeracao.TipoFeriado
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "feriados")
data class Feriado(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(name = "nome", nullable = false, length = 100)
    val nome: String,
    
    @Column(name = "data", nullable = false)
    val data: LocalDate,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    val tipo: TipoFeriado,
    
    @Column(name = "recorrente", nullable = false)
    val recorrente: Boolean = true, // Se repete todo ano
    
    @Column(name = "data_criacao", nullable = false)
    val dataCriacao: LocalDateTime = LocalDateTime.now()
)
