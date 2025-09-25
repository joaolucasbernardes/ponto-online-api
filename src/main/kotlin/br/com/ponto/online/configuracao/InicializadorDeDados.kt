package br.com.ponto.online.configuracao

import br.com.ponto.online.entidade.Empresa
import br.com.ponto.online.entidade.Funcionario
import br.com.ponto.online.repositorio.EmpresaRepositorio
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class InicializadorDeDados(
    private val empresaRepositorio: EmpresaRepositorio,
    private val funcionarioRepositorio: FuncionarioRepositorio
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (funcionarioRepositorio.count() == 0L) {
            println(">>> Nenhum funcionário encontrado. Criando dados de teste...")

            val empresa = empresaRepositorio.save(
                Empresa(razaoSocial = "Empresa de Teste", cnpj = "11222333000144")
            )

            val funcionario = funcionarioRepositorio.save(
                Funcionario(
                    nome = "Colaborador Padrão",
                    cpf = "12345678900",
                    email = "teste@email.com",
                    senha = "hash_inseguro_temporario",
                    empresa = empresa
                )
            )
            println(">>> Funcionário de teste criado com ID: ${funcionario.id}")
        } else {
            println(">>> Base de dados já contém dados.")
        }
    }
}