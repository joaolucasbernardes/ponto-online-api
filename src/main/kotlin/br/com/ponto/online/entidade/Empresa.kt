package br.com.ponto.online.entidade

import jakarta.persistence.*

@Entity
@Table(name = "empresas")
class Empresa(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "razao_social", nullable = false)
    val razaoSocial: String,

    @Column(nullable = false, unique = true, length = 14)
    val cnpj: String
)