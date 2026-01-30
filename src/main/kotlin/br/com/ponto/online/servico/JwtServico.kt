package br.com.ponto.online.servico

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.Date
import javax.crypto.SecretKey
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class JwtServico {

    // CHAVE TEMPORARIA, ALTERAR DEPOIS O LOCAL.
    private val CHAVE_SECRETA = "7d20a7d5b7a110a1b6a1b15c54c12d19f15e1f1c24b2a3c3e4f4g5h6i7j8k9l0"

    // Expirações
    private val ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60 * 10L // 10 horas
    private val REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7L // 7 dias

    fun gerarToken(userDetails: UserDetails): String {
        // Extrai o role do primeiro authority (ROLE_ADMIN ou ROLE_FUNCIONARIO)
        val role =
                userDetails.authorities.firstOrNull()?.authority?.removePrefix("ROLE_")
                        ?: "FUNCIONARIO"
        val claims = mapOf("role" to role, "type" to "access")
        return criarToken(claims, userDetails.username, ACCESS_TOKEN_EXPIRATION)
    }

    fun gerarRefreshToken(userDetails: UserDetails): String {
        val claims = mapOf("type" to "refresh")
        return criarToken(claims, userDetails.username, REFRESH_TOKEN_EXPIRATION)
    }

    private fun criarToken(claims: Map<String, Any>, username: String, expiration: Long): String {
        return Jwts.builder()
                .claims(claims)
                .subject(username) // O "dono" do token
                .issuedAt(Date(System.currentTimeMillis())) // Data de criação
                .expiration(Date(System.currentTimeMillis() + expiration)) // Validade
                .signWith(getChaveDeAssinatura(), Jwts.SIG.HS256) // Assina com o algoritmo HS256
                .compact()
    }

    private fun getChaveDeAssinatura(): SecretKey {
        return Keys.hmacShaKeyFor(CHAVE_SECRETA.toByteArray())
    }

    fun extrairUsername(token: String): String {
        return extrairClaim(token, Claims::getSubject)
    }

    fun isTokenValido(token: String, userDetails: UserDetails): Boolean {
        val username = extrairUsername(token)
        return (username == userDetails.username && !isTokenExpirado(token))
    }

    fun isRefreshToken(token: String): Boolean {
        return try {
            val claims = extrairTodosClaims(token)
            claims["type"] == "refresh"
        } catch (e: Exception) {
            false
        }
    }

    fun isRefreshTokenValido(token: String, userDetails: UserDetails): Boolean {
        return isTokenValido(token, userDetails) && isRefreshToken(token)
    }

    private fun isTokenExpirado(token: String): Boolean {
        return extrairClaim(token, Claims::getExpiration).before(Date())
    }

    private fun <T> extrairClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extrairTodosClaims(token)
        return claimsResolver(claims)
    }

    private fun extrairTodosClaims(token: String): Claims {
        return Jwts.parser()
                .verifyWith(getChaveDeAssinatura())
                .build()
                .parseSignedClaims(token)
                .payload
    }
}
