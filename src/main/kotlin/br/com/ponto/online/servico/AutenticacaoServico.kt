package br.com.ponto.online.servico

import br.com.ponto.online.dto.LoginRequisicaoDTO
import br.com.ponto.online.dto.LoginRespostaDTO
import br.com.ponto.online.entidade.Funcionario
import br.com.ponto.online.entidade.Admin
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class AutenticacaoServico(
    private val authenticationManager: AuthenticationManager,
    private val jwtServico: JwtServico
) {
    fun login(loginDTO: LoginRequisicaoDTO): LoginRespostaDTO {
        val tokenAutenticacao = UsernamePasswordAuthenticationToken(loginDTO.identificador, loginDTO.senha)

        val autenticacao = authenticationManager.authenticate(tokenAutenticacao)

        val userDetails = autenticacao.principal as UserDetails
        val jwtToken = jwtServico.gerarToken(userDetails)

        // Verifica se é Admin ou Funcionário
        return when (userDetails) {
            is Admin -> LoginRespostaDTO(
                mensagem = "Login bem-sucedido!",
                token = jwtToken,
                funcionarioId = userDetails.id!!,
                nome = userDetails.nome,
                role = "ADMIN"
            )
            is Funcionario -> LoginRespostaDTO(
                mensagem = "Login bem-sucedido!",
                token = jwtToken,
                funcionarioId = userDetails.id!!,
                nome = userDetails.nome,
                role = "FUNCIONARIO"
            )
            else -> throw IllegalStateException("Tipo de usuário desconhecido")
        }
    }
}