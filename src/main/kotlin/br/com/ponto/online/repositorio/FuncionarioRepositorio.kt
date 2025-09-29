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
}