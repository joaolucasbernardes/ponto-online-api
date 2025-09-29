package br.com.ponto.online.configuracao

import br.com.ponto.online.entidade.Empresa
import br.com.ponto.online.entidade.Funcionario
import br.com.ponto.online.repositorio.EmpresaRepositorio
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class InicializadorDeDados(
    private val empresaRepositorio: EmpresaRepositorio,
    private val funcionarioRepositorio: FuncionarioRepositorio,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (funcionarioRepositorio.count() == 0L) {
            println(">>> Nenhum funcionário encontrado. Criando dados de teste...")

            val empresa = empresaRepositorio.save(
                Empresa(razaoSocial = "Empresa de Teste", cnpj = "11222333000144")
            )

            // CODIFICAR A SENHA ANTES DE SALVAR
            val senhaCriptografada = passwordEncoder.encode("admin")

            val funcionario = funcionarioRepositorio.save(
                Funcionario(
                    nome = "João",
                    cpf = "12345678900",
                    email = "teste@email.com",
                    senha = senhaCriptografada, // SALVAR A SENHA CRIPTOGRAFADA
                    empresa = empresa
                )
            )
            println(">>> Funcionário de teste criado com ID: ${funcionario.id}")
        } else {
            println(">>> Base de dados já contém dados.")
        }
    }
}