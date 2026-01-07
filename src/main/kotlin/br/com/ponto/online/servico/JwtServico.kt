package br.com.ponto.online.servico

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtServico {

    // CHAVE TEMPORARIA, ALTERAR DEPOIS O LOCAL.
    private val CHAVE_SECRETA = "7d20a7d5b7a110a1b6a1b15c54c12d19f15e1f1c24b2a3c3e4f4g5h6i7j8k9l0"

    fun gerarToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = HashMap()
        return criarToken(claims, userDetails.username)
    }

    private fun criarToken(claims: Map<String, Any>, username: String): String {
        return Jwts.builder()
            .claims(claims)
            .subject(username) // O "dono" do token
            .issuedAt(Date(System.currentTimeMillis())) // Data de criação
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Validade de 10 horas
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