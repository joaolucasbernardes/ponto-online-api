package br.com.ponto.online.entidade

import jakarta.persistence.*

@Entity
@Table(name = "funcionarios")
class Funcionario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val nome: String,

    @Column(nullable = false, unique = true, length = 11)
    val cpf: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val senha: String,

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    val empresa: Empresa
)