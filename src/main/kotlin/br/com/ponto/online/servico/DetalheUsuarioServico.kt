package br.com.ponto.online.servico

import br.com.ponto.online.repositorio.FuncionarioRepositorio
import br.com.ponto.online.repositorio.AdminRepositorio
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class DetalheUsuarioServico(
    private val funcionarioRepositorio: FuncionarioRepositorio,
    private val adminRepositorio: AdminRepositorio
) : UserDetailsService {

    private val logger = LoggerFactory.getLogger(DetalheUsuarioServico::class.java)

    override fun loadUserByUsername(username: String): UserDetails {
        logger.info("🔍 Buscando usuário: $username")
        
        // Primeiro tenta buscar como admin
        logger.info("📋 Tentando buscar como ADMIN...")
        val admin = adminRepositorio.findByEmail(username)
        if (admin != null) {
            logger.info("✅ ADMIN encontrado: ${admin.nome} - Authorities: ${admin.authorities.map { it.authority }}")
            return admin
        }
        logger.info("❌ ADMIN não encontrado")

        // Se não for admin, busca como funcionário
        logger.info("📋 Tentando buscar como FUNCIONÁRIO...")
        val funcionario = funcionarioRepositorio.buscarPorEmailOuCpf(username).orElse(null)
        if (funcionario != null) {
            logger.info("✅ FUNCIONÁRIO encontrado: ${funcionario.nome} - Authorities: ${funcionario.authorities.map { it.authority }}")
            return funcionario
        }
        
        logger.error("❌ Usuário '$username' não encontrado em nenhuma tabela")
        throw UsernameNotFoundException("Usuário '$username' não encontrado.")
    }
}