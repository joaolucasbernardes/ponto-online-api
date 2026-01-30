package br.com.ponto.online.servico

import br.com.ponto.online.dto.LoginRequisicaoDTO
import br.com.ponto.online.dto.LoginRespostaDTO
import br.com.ponto.online.entidade.Admin
import br.com.ponto.online.entidade.Funcionario
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class AutenticacaoServico(
        private val authenticationManager: AuthenticationManager,
        private val jwtServico: JwtServico,
        private val detalheUsuarioServico: DetalheUsuarioServico
) {
    fun login(loginDTO: LoginRequisicaoDTO): LoginRespostaDTO {
        val tokenAutenticacao =
                UsernamePasswordAuthenticationToken(loginDTO.identificador, loginDTO.senha)

        val autenticacao = authenticationManager.authenticate(tokenAutenticacao)

        val userDetails = autenticacao.principal as UserDetails
        val jwtToken = jwtServico.gerarToken(userDetails)
        val refreshToken = jwtServico.gerarRefreshToken(userDetails)

        // Verifica se é Admin ou Funcionário
        return when (userDetails) {
            is Admin ->
                    LoginRespostaDTO(
                            mensagem = "Login bem-sucedido!",
                            token = jwtToken,
                            refreshToken = refreshToken,
                            funcionarioId = userDetails.id!!,
                            nome = userDetails.nome,
                            role = "ADMIN"
                    )
            is Funcionario ->
                    LoginRespostaDTO(
                            mensagem = "Login bem-sucedido!",
                            token = jwtToken,
                            refreshToken = refreshToken,
                            funcionarioId = userDetails.id!!,
                            nome = userDetails.nome,
                            role = "FUNCIONARIO"
                    )
            else -> throw IllegalStateException("Tipo de usuário desconhecido")
        }
    }

    fun refresh(refreshToken: String): LoginRespostaDTO {
        val username = jwtServico.extrairUsername(refreshToken)
        val userDetails = detalheUsuarioServico.loadUserByUsername(username)

        if (!jwtServico.isRefreshTokenValido(refreshToken, userDetails)) {
            throw IllegalArgumentException("Refresh token inválido ou expirado")
        }

        val novoAccessToken = jwtServico.gerarToken(userDetails)
        val novoRefreshToken = jwtServico.gerarRefreshToken(userDetails)

        return when (userDetails) {
            is Admin ->
                    LoginRespostaDTO(
                            mensagem = "Token renovado com sucesso!",
                            token = novoAccessToken,
                            refreshToken = novoRefreshToken,
                            funcionarioId = userDetails.id!!,
                            nome = userDetails.nome,
                            role = "ADMIN"
                    )
            is Funcionario ->
                    LoginRespostaDTO(
                            mensagem = "Token renovado com sucesso!",
                            token = novoAccessToken,
                            refreshToken = novoRefreshToken,
                            funcionarioId = userDetails.id!!,
                            nome = userDetails.nome,
                            role = "FUNCIONARIO"
                    )
            else -> throw IllegalStateException("Tipo de usuário desconhecido")
        }
    }
}
