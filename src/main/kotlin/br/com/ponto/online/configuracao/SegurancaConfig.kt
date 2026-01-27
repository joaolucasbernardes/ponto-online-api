package br.com.ponto.online.configuracao

import br.com.ponto.online.servico.DetalheUsuarioServico
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SegurancaConfig(
        private val jwtFiltro: JwtFiltroAutenticacao,
        private val userDetailsService: DetalheUsuarioServico
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            csrf { disable() }
            cors {}
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            authorizeHttpRequests {
                // Rotas públicas - Login
                authorize("/login.html", permitAll)
                authorize("/login", permitAll)
                authorize("/", permitAll)
                authorize("/index.html", permitAll)

                // Recursos estáticos (CSS, JS)
                authorize("/css/**", permitAll)
                authorize("/js/**", permitAll)

                // Endpoints de diagnóstico (temporário para debug)
                authorize("/diagnostico/**", permitAll)

                // Documentação API (Swagger)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/swagger-ui.html", permitAll)
                authorize("/api/relatorios/**", permitAll) // Temporário para teste

                // Páginas protegidas - requerem autenticação
                authorize("/admin.html", hasRole("ADMIN"))
                authorize("/gerenciar-funcionarios.html", hasRole("ADMIN"))
                authorize("/registrar-ponto-manual.html", hasRole("ADMIN"))
                authorize("/aprovar-justificativas.html", hasRole("ADMIN"))
                authorize("/configuracoes-empresa.html", hasRole("ADMIN"))
                authorize("/gerenciar-locais.html", hasRole("ADMIN"))
                authorize("/gerenciar-escalas.html", hasRole("ADMIN"))
                authorize("/principal.html", authenticated)
                authorize("/historico.html", authenticated)
                authorize("/justificar-ausencia.html", authenticated)

                // APIs protegidas por ADMIN
                authorize("/api/admin/**", hasRole("ADMIN"))
                authorize("/api/funcionarios/**", hasRole("ADMIN"))
                authorize("/api/justificativas/pendentes", hasRole("ADMIN"))
                authorize("/api/justificativas/*/processar", hasRole("ADMIN"))
                authorize("/api/configuracoes/**", hasRole("ADMIN"))
                authorize("/api/feriados/**", hasRole("ADMIN"))
                authorize("/api/locais-permitidos/**", hasRole("ADMIN"))
                authorize("/api/escalas/**", hasRole("ADMIN"))
                authorize("/api/turnos/**", hasRole("ADMIN"))

                // Rotas de Férias e Afastamentos
                authorize("/solicitar-ferias.html", authenticated)
                authorize("/gerenciar-ferias.html", hasRole("ADMIN"))
                authorize(
                        "/api/ferias/funcionario/**",
                        authenticated
                ) // Próprio funcionário vê suas
                authorize("/api/ferias/*/analisar", hasRole("ADMIN")) // Apenas admin analisa
                authorize("/api/ferias", authenticated) // Listar/Solicitar (Restrições internas)

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
