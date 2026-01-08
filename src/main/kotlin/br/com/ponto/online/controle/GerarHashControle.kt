package br.com.ponto.online.controle

import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/diagnostico")
class GerarHashControle(
    private val passwordEncoder: PasswordEncoder
) {

    @GetMapping("/gerar-hash")
    fun gerarHash(@RequestParam senha: String): ResponseEntity<Map<String, String>> {
        val hash = passwordEncoder.encode(senha)
        
        return ResponseEntity.ok(mapOf(
            "senha" to senha,
            "hash" to hash,
            "hash_length" to hash.length.toString()
        ))
    }
}
