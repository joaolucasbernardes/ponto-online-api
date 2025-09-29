package br.com.ponto.online.configuracao

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SegurancaConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            csrf { disable() }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            // Configura as regras de autorização para as requisições HTTP
            authorizeHttpRequests {
                // Libera o acesso arquivos estáticos do frontend
                authorize("/login.html", permitAll)
                authorize("/principal.html", permitAll)
                authorize("/historico.html", permitAll)
                authorize("/css/**", permitAll)
                authorize("/js/**", permitAll)

                // Libera o acesso à API de login
                authorize("/login", permitAll)

                // Todas as outras requisições (anyRequest) precisam estar autenticadas
                authorize(anyRequest, authenticated)
            }
        }
        return http.build()
    }
}