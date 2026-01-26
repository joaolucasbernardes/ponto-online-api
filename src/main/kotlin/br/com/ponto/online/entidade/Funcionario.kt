package br.com.ponto.online.entidade

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import br.com.ponto.online.enums.Role

@Entity
@Table(name = "funcionarios")
class Funcionario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    // ... (outros campos: nome, cpf, email)
    @Column(nullable = false)
    val nome: String,
    @Column(nullable = false, unique = true, length = 11)
    val cpf: String,
    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val senha: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.FUNCIONARIO,

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    val empresa: Empresa,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escala_id")
    val escala: Escala? = null, // Escala de trabalho do funcionário

    @Column(nullable = false)
    var ativo: Boolean = true

) : UserDetails { // Implementa a interface UserDetails

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_${role.name}"))
    }

    override fun getPassword(): String {
        return this.senha
    }

    override fun getUsername(): String {
        return this.email // Usaremos o e-mail como "username" para o Spring
    }

    override fun isAccountNonExpired(): Boolean {
        return true // A conta não expira
    }

    override fun isAccountNonLocked(): Boolean {
        return true // A conta não está bloqueada
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true // As credenciais não expiram
    }

    override fun isEnabled(): Boolean {
        return ativo // Retorna status de ativação
    }
}