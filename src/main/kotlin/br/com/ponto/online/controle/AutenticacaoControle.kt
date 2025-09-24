package br.com.ponto.online.controle

import br.com.ponto.online.dto.LoginRequisicaoDTO
import br.com.ponto.online.dto.LoginRespostaDTO
import br.com.ponto.online.servico.AutenticacaoServico
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class AutenticacaoControle(
    private val autenticacaoServico: AutenticacaoServico
) {

    @PostMapping
    fun login(@RequestBody loginDTO: LoginRequisicaoDTO): ResponseEntity<LoginRespostaDTO> {
        val resposta = autenticacaoServico.login(loginDTO)
        return ResponseEntity.ok(resposta)
    }
}