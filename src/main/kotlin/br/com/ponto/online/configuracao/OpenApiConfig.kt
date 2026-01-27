package br.com.ponto.online.configuracao

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
                .info(
                        Info().title("Ponto Online API")
                                .version("1.0.0")
                                .description(
                                        "API REST completa para o sistema de Ponto Online. " +
                                                "Permite o gerenciamento de registros de ponto, funcionários, férias, horas extras e relatórios.\n\n" +
                                                "**Principais Funcionalidades:**\n" +
                                                "- Registro de Ponto e Ajustes\n" +
                                                "- Gestão de Férias e Afastamentos\n" +
                                                "- Administração de Funcionários e Regras de Empresa\n" +
                                                "- Relatórios e Exportação de Espelhos de Ponto\n\n" +
                                                "Utilize os endpoints abaixo para integrar ou testar as funcionalidades do sistema."
                                )
                                .contact(
                                        Contact()
                                                .name("Time de Desenvolvimento")
                                                .email("dev@pontoonline.com.br")
                                )
                                .license(License().name("Apache 2.0").url("http://springdoc.org"))
                )
    }
}
