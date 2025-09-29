package br.com.ponto.online.servico

import br.com.ponto.online.dto.LoginRequisicaoDTO
import br.com.ponto.online.dto.LoginRespostaDTO
import br.com.ponto.online.entidade.Funcionario
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AutenticacaoServico(
    private val authenticationManager: AuthenticationManager,
    private val jwtServico: JwtServico
) {
    fun login(loginDTO: LoginRequisicaoDTO): LoginRespostaDTO {
        val tokenAutenticacao = UsernamePasswordAuthenticationToken(loginDTO.identificador, loginDTO.senha)

        val autenticacao = authenticationManager.authenticate(tokenAutenticacao)

        val funcionario = autenticacao.principal as Funcionario
        val jwtToken = jwtServico.gerarToken(funcionario)

        return LoginRespostaDTO(
            mensagem = "Login bem-sucedido!",
            token = jwtToken,
            funcionarioId = funcionario.id!!,
            nome = funcionario.nome
        )
    }
}