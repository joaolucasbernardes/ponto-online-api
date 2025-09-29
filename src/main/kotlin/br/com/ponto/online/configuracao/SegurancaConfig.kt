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
                authorize("/login.html", permitAll)
                authorize("/css/**", permitAll)
                authorize("/js/**", permitAll)
                authorize("/login", permitAll)
                authorize(anyRequest, authenticated)
            }
            authenticationProvider()
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