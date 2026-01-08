package br.com.ponto.online.configuracao

import br.com.ponto.online.entidade.Empresa
import br.com.ponto.online.entidade.Funcionario
import br.com.ponto.online.entidade.Admin
import br.com.ponto.online.repositorio.EmpresaRepositorio
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import br.com.ponto.online.repositorio.AdminRepositorio
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class InicializadorDeDados(
    private val empresaRepositorio: EmpresaRepositorio,
    private val funcionarioRepositorio: FuncionarioRepositorio,
    private val adminRepositorio: AdminRepositorio,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // Criar ou atualizar Admin
        val adminEmail = "admin@email.com"
        val adminExistente = adminRepositorio.findByEmail(adminEmail)
        
        if (adminExistente == null) {
            println(">>> Criando usuário ADMIN...")
            val senhaCriptografada = passwordEncoder.encode("admin")
            val admin = adminRepositorio.save(
                Admin(
                    nome = "Administrador",
                    email = adminEmail,
                    senha = senhaCriptografada,
                    ativo = true
                )
            )
            println(">>> ADMIN criado com ID: ${admin.id}")
        } else {
            println(">>> ADMIN já existe. Atualizando senha...")
            // Como Admin é imutável, precisamos deletar e recriar
            adminRepositorio.delete(adminExistente)
            val senhaCriptografada = passwordEncoder.encode("admin")
            val admin = adminRepositorio.save(
                Admin(
                    nome = "Administrador",
                    email = adminEmail,
                    senha = senhaCriptografada,
                    ativo = true
                )
            )
            println(">>> ADMIN atualizado com ID: ${admin.id}")
        }
        
        // Criar Funcionário de teste
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
            println(">>> Base de dados já contém funcionários.")
        }
    }
}