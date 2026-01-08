package br.com.ponto.online.controle

import br.com.ponto.online.repositorio.AdminRepositorio
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/diagnostico")
class DiagnosticoControle(
    private val adminRepositorio: AdminRepositorio,
    private val funcionarioRepositorio: FuncionarioRepositorio,
    private val passwordEncoder: PasswordEncoder
) {

    @GetMapping("/verificar-admin")
    fun verificarAdmin(@RequestParam email: String): ResponseEntity<Map<String, Any>> {
        val admin = adminRepositorio.findByEmail(email)
        
        val resultado = mutableMapOf<String, Any>(
            "email_buscado" to email,
            "admin_encontrado" to (admin != null)
        )
        
        if (admin != null) {
            resultado["nome"] = admin.nome
            resultado["ativo"] = admin.ativo
            resultado["username"] = admin.username
            resultado["authorities"] = admin.authorities.map { it.authority }
            resultado["senha_length"] = admin.password.length
            resultado["senha_comeca_com_bcrypt"] = admin.password.startsWith("$2a$")
        }
        
        return ResponseEntity.ok(resultado)
    }

    @GetMapping("/verificar-funcionario")
    fun verificarFuncionario(@RequestParam email: String): ResponseEntity<Map<String, Any>> {
        val funcionario = funcionarioRepositorio.buscarPorEmailOuCpf(email).orElse(null)
        
        val resultado = mutableMapOf<String, Any>(
            "email_buscado" to email,
            "funcionario_encontrado" to (funcionario != null)
        )
        
        if (funcionario != null) {
            resultado["nome"] = funcionario.nome
            resultado["username"] = funcionario.username
            resultado["authorities"] = funcionario.authorities.map { it.authority }
            resultado["senha_length"] = funcionario.password.length
            resultado["senha_comeca_com_bcrypt"] = funcionario.password.startsWith("$2a$")
        }
        
        return ResponseEntity.ok(resultado)
    }

    @GetMapping("/testar-senha")
    fun testarSenha(
        @RequestParam senha: String,
        @RequestParam hash: String
    ): ResponseEntity<Map<String, Any>> {
        val matches = passwordEncoder.matches(senha, hash)
        
        return ResponseEntity.ok(mapOf(
            "senha_fornecida" to senha,
            "hash_length" to hash.length,
            "hash_comeca_com_bcrypt" to hash.startsWith("$2a$"),
            "senha_valida" to matches
        ))
    }

    @GetMapping("/contar-usuarios")
    fun contarUsuarios(): ResponseEntity<Map<String, Any>> {
        val totalAdmins = adminRepositorio.count()
        val totalFuncionarios = funcionarioRepositorio.count()
        
        return ResponseEntity.ok(mapOf(
            "total_admins" to totalAdmins,
            "total_funcionarios" to totalFuncionarios
        ))
    }
}
