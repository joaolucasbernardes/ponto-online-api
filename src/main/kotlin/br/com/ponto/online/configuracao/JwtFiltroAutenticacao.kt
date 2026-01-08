package br.com.ponto.online.configuracao

import br.com.ponto.online.servico.DetalheUsuarioServico
import br.com.ponto.online.servico.JwtServico
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFiltroAutenticacao(
    private val jwtServico: JwtServico,
    private val userDetailsService: DetalheUsuarioServico
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Tenta obter o token do header Authorization
        var jwt: String? = null
        val authHeader = request.getHeader("Authorization")
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7)
        } else {
            // Se não estiver no header, tenta obter do cookie
            val cookies = request.cookies
            if (cookies != null) {
                val jwtCookie = cookies.find { it.name == "jwt_token" }
                jwt = jwtCookie?.value
            }
        }
        
        // Se não encontrou token, continua sem autenticar
        if (jwt == null) {
            filterChain.doFilter(request, response)
            return
        }

        val username = jwtServico.extrairUsername(jwt)

        if (SecurityContextHolder.getContext().authentication == null) {
            val userDetails = this.userDetailsService.loadUserByUsername(username)
            if (jwtServico.isTokenValido(jwt, userDetails)) {
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }
        filterChain.doFilter(request, response)
    }
}