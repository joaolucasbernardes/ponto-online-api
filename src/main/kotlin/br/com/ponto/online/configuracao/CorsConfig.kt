package br.com.ponto.online.configuracao

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        
        // Permitir credenciais
        config.allowCredentials = true
        
        // Permitir todos os origins (localhost e 127.0.0.1)
        config.addAllowedOriginPattern("*")
        
        // Permitir todos os headers
        config.addAllowedHeader("*")
        
        // Permitir todos os métodos HTTP
        config.addAllowedMethod("*")
        
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}
