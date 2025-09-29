package br.com.ponto.online.servico

import br.com.ponto.online.repositorio.FuncionarioRepositorio
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class DetalheUsuarioServico(
    private val funcionarioRepositorio: FuncionarioRepositorio
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return funcionarioRepositorio.buscarPorEmailOuCpf(username)
            .orElseThrow { UsernameNotFoundException("Usuário '$username' não encontrado.") }
    }
}