package br.com.ponto.online.servico

import br.com.ponto.online.dto.LocalPermitidoRequisicaoDTO
import br.com.ponto.online.entidade.LocalPermitido
import br.com.ponto.online.repositorio.LocalPermitidoRepositorio
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class LocalPermitidoServico(
    private val localPermitidoRepositorio: LocalPermitidoRepositorio
) {
    
    /**
     * Lista todos os locais permitidos
     */
    fun listarTodos(): List<LocalPermitido> {
        return localPermitidoRepositorio.findAll()
    }
    
    /**
     * Lista apenas locais ativos
     */
    fun listarAtivos(): List<LocalPermitido> {
        return localPermitidoRepositorio.findByAtivoTrueOrderByNomeAsc()
    }
    
    /**
     * Busca local por ID
     */
    fun buscarPorId(id: Long): LocalPermitido {
        return localPermitidoRepositorio.findById(id)
            .orElseThrow { IllegalArgumentException("Local com ID $id não encontrado") }
    }
    
    /**
     * Cadastra novo local permitido
     */
    fun cadastrar(dto: LocalPermitidoRequisicaoDTO): LocalPermitido {
        validarLocal(dto)
        
        val local = LocalPermitido(
            nome = dto.nome.trim(),
            endereco = dto.endereco.trim(),
            latitude = dto.latitude,
            longitude = dto.longitude,
            raioMetros = dto.raioMetros,
            ativo = dto.ativo,
            descricao = dto.descricao?.trim()
        )
        
        return localPermitidoRepositorio.save(local)
    }
    
    /**
     * Atualiza local existente
     */
    fun atualizar(id: Long, dto: LocalPermitidoRequisicaoDTO): LocalPermitido {
        validarLocal(dto)
        
        val localExistente = buscarPorId(id)
        
        val localAtualizado = localExistente.copy(
            nome = dto.nome.trim(),
            endereco = dto.endereco.trim(),
            latitude = dto.latitude,
            longitude = dto.longitude,
            raioMetros = dto.raioMetros,
            ativo = dto.ativo,
            descricao = dto.descricao?.trim(),
            dataAtualizacao = LocalDateTime.now()
        )
        
        return localPermitidoRepositorio.save(localAtualizado)
    }
    
    /**
     * Exclui local
     */
    fun excluir(id: Long) {
        if (!localPermitidoRepositorio.existsById(id)) {
            throw IllegalArgumentException("Local com ID $id não encontrado")
        }
        localPermitidoRepositorio.deleteById(id)
    }
    
    /**
     * Ativa ou desativa local
     */
    fun alterarStatus(id: Long, ativo: Boolean): LocalPermitido {
        val local = buscarPorId(id)
        val localAtualizado = local.copy(
            ativo = ativo,
            dataAtualizacao = LocalDateTime.now()
        )
        return localPermitidoRepositorio.save(localAtualizado)
    }
    
    /**
     * Busca locais por nome
     */
    fun buscarPorNome(nome: String): List<LocalPermitido> {
        return localPermitidoRepositorio.findByNomeContainingIgnoreCase(nome)
    }
    
    /**
     * Valida dados do local
     */
    private fun validarLocal(dto: LocalPermitidoRequisicaoDTO) {
        require(dto.nome.isNotBlank()) {
            "Nome do local é obrigatório"
        }
        require(dto.nome.length <= 100) {
            "Nome do local deve ter no máximo 100 caracteres"
        }
        require(dto.endereco.isNotBlank()) {
            "Endereço é obrigatório"
        }
        require(dto.endereco.length <= 200) {
            "Endereço deve ter no máximo 200 caracteres"
        }
        require(dto.latitude in -90.0..90.0) {
            "Latitude deve estar entre -90 e 90 graus"
        }
        require(dto.longitude in -180.0..180.0) {
            "Longitude deve estar entre -180 e 180 graus"
        }
        require(dto.raioMetros > 0) {
            "Raio deve ser maior que zero"
        }
        require(dto.raioMetros <= 10000) {
            "Raio não pode ser maior que 10km (10000 metros)"
        }
    }
}
