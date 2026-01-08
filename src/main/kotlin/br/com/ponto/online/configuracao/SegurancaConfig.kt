package br.com.ponto.online.configuracao

import br.com.ponto.online.servico.DetalheUsuarioServico
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SegurancaConfig(
    private val jwtFiltro: JwtFiltroAutenticacao,
    private val userDetailsService: DetalheUsuarioServico
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            authorizeHttpRequests {
                // Rotas públicas - Login
                authorize("/login.html", permitAll)
                authorize("/login", permitAll)
                
                // Recursos estáticos (CSS, JS, HTML)
                authorize("/css/**", permitAll)
                authorize("/js/**", permitAll)
                authorize("/*.html", permitAll) // Todas as páginas HTML são públicas (verificação de token via JS)
                authorize("/", permitAll)
                authorize("/index.html", permitAll)
                
                // Endpoints de diagnóstico (temporário para debug)
                authorize("/diagnostico/**", permitAll)
                
                // APIs protegidas por ADMIN
                authorize("/api/admin/**", hasRole("ADMIN"))
                authorize("/api/funcionarios/**", hasRole("ADMIN"))
                
                // APIs protegidas (requerem autenticação)
                authorize("/api/**", authenticated)
                
                // Demais rotas requerem autenticação
                authorize(anyRequest, authenticated)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtFiltro)
        }
        return http.build()
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}