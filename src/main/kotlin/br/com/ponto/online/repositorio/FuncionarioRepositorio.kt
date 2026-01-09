package br.com.ponto.online.repositorio
//...
import br.com.ponto.online.entidade.Funcionario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface FuncionarioRepositorio : JpaRepository<Funcionario, Long> {
    @Query("SELECT f FROM Funcionario f WHERE f.email = :identificador OR f.cpf = :identificador")
    fun buscarPorEmailOuCpf(identificador: String): Optional<Funcionario>
    
    // Métodos para CRUD de funcionários
    fun findByNomeContainingIgnoreCase(nome: String): List<Funcionario>
    fun findByEmpresaId(empresaId: Long): List<Funcionario>
    fun findByRole(role: br.com.ponto.online.enums.Role): List<Funcionario>
    fun existsByEmail(email: String): Boolean
    fun existsByCpf(cpf: String): Boolean
    fun existsByEmailAndIdNot(email: String, id: Long): Boolean
}