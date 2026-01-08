package br.com.ponto.online.controle

import br.com.ponto.online.dto.LoginRequisicaoDTO
import br.com.ponto.online.dto.LoginRespostaDTO
import br.com.ponto.online.servico.AutenticacaoServico
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
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
    fun login(
        @RequestBody loginDTO: LoginRequisicaoDTO,
        response: HttpServletResponse
    ): ResponseEntity<LoginRespostaDTO> {
        val resposta = autenticacaoServico.login(loginDTO)
        
        // Adiciona token como cookie HttpOnly para autenticação de páginas HTML
        val cookie = Cookie("jwt_token", resposta.token)
        cookie.isHttpOnly = true
        cookie.secure = false // Mudar para true em produção com HTTPS
        cookie.path = "/"
        cookie.maxAge = 24 * 60 * 60 // 24 horas
        response.addCookie(cookie)
        
        return ResponseEntity.ok(resposta)
    }
}