package br.com.ponto.online.servico

import br.com.ponto.online.dto.LoginRequisicaoDTO
import br.com.ponto.online.dto.LoginRespostaDTO
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import org.springframework.stereotype.Service

@Service
class AutenticacaoServico(
    private val funcionarioRepositorio: FuncionarioRepositorio
) {
    fun login(loginDTO: LoginRequisicaoDTO): LoginRespostaDTO {
        // LÓGICA DE LOGIN TEMPORÁRIA
        println("Tentando autenticar o usuário: ${loginDTO.identificador}")

        if (loginDTO.identificador.isNotEmpty() && loginDTO.senha == "admin123") {
            println("Autenticação bem-sucedida (LÓGICA TEMPORÁRIA)")
            return LoginRespostaDTO("Login realizado com sucesso!")
        } else {
            println("Falha na autenticação (LÓGICA TEMPORÁRIA)")
            throw IllegalArgumentException("Identificador ou senha inválidos.")
        }
    }
}