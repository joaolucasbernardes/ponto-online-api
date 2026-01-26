package br.com.ponto.online.servico

import br.com.ponto.online.dto.EscalaRequisicaoDTO
import br.com.ponto.online.entidade.Escala
import br.com.ponto.online.enumeracao.TipoEscala
import br.com.ponto.online.repositorio.EscalaRepositorio
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import java.time.LocalTime
import java.time.LocalDateTime

@Service
class EscalaServico(
    private val escalaRepositorio: EscalaRepositorio
) {
    
    private val objectMapper = jacksonObjectMapper()
    
    /**
     * Lista todas as escalas
     */
    fun listarTodas(): List<Escala> {
        return escalaRepositorio.findAll()
    }
    
    /**
     * Lista apenas escalas ativas
     */
    fun listarAtivas(): List<Escala> {
        return escalaRepositorio.findByAtivoTrueOrderByNomeAsc()
    }
    
    /**
     * Busca escala por ID
     */
    fun buscarPorId(id: Long): Escala {
        return escalaRepositorio.findById(id)
            .orElseThrow { IllegalArgumentException("Escala com ID $id não encontrada") }
    }
    
    /**
     * Busca escala padrão
     */
    fun buscarEscalaPadrao(): Escala? {
        return escalaRepositorio.findByEscalaPadraoTrue()
    }
    
    /**
     * Cadastra nova escala
     */
    fun cadastrar(dto: EscalaRequisicaoDTO): Escala {
        validarEscala(dto)
        
        // Se esta escala for padrão, desmarcar outras
        if (dto.escalaPadrao) {
            desmarcarEscalasPadrao()
        }
        
        val escala = Escala(
            nome = dto.nome.trim(),
            descricao = dto.descricao?.trim(),
            tipo = dto.tipo,
            horaEntrada = LocalTime.parse(dto.horaEntrada),
            horaSaida = LocalTime.parse(dto.horaSaida),
            intervaloMinutos = dto.intervaloMinutos,
            cargaHorariaDiaria = dto.cargaHorariaDiaria,
            cargaHorariaSemanal = dto.cargaHorariaSemanal,
            diasSemana = objectMapper.writeValueAsString(dto.diasSemana),
            toleranciaEntradaMinutos = dto.toleranciaEntradaMinutos,
            toleranciaSaidaMinutos = dto.toleranciaSaidaMinutos,
            ativo = dto.ativo,
            escalaPadrao = dto.escalaPadrao
        )
        
        return escalaRepositorio.save(escala)
    }
    
    /**
     * Atualiza escala existente
     */
    fun atualizar(id: Long, dto: EscalaRequisicaoDTO): Escala {
        validarEscala(dto)
        
        val escalaExistente = buscarPorId(id)
        
        // Se esta escala for padrão, desmarcar outras
        if (dto.escalaPadrao && !escalaExistente.escalaPadrao) {
            desmarcarEscalasPadrao()
        }
        
        val escalaAtualizada = escalaExistente.copy(
            nome = dto.nome.trim(),
            descricao = dto.descricao?.trim(),
            tipo = dto.tipo,
            horaEntrada = LocalTime.parse(dto.horaEntrada),
            horaSaida = LocalTime.parse(dto.horaSaida),
            intervaloMinutos = dto.intervaloMinutos,
            cargaHorariaDiaria = dto.cargaHorariaDiaria,
            cargaHorariaSemanal = dto.cargaHorariaSemanal,
            diasSemana = objectMapper.writeValueAsString(dto.diasSemana),
            toleranciaEntradaMinutos = dto.toleranciaEntradaMinutos,
            toleranciaSaidaMinutos = dto.toleranciaSaidaMinutos,
            ativo = dto.ativo,
            escalaPadrao = dto.escalaPadrao,
            dataAtualizacao = LocalDateTime.now()
        )
        
        return escalaRepositorio.save(escalaAtualizada)
    }
    
    /**
     * Exclui escala
     */
    fun excluir(id: Long) {
        val escala = buscarPorId(id)
        
        // Não permitir excluir escala padrão
        if (escala.escalaPadrao) {
            throw IllegalStateException("Não é possível excluir a escala padrão")
        }
        
        escalaRepositorio.deleteById(id)
    }
    
    /**
     * Ativa ou desativa escala
     */
    fun alterarStatus(id: Long, ativo: Boolean): Escala {
        val escala = buscarPorId(id)
        
        // Não permitir desativar escala padrão
        if (!ativo && escala.escalaPadrao) {
            throw IllegalStateException("Não é possível desativar a escala padrão")
        }
        
        val escalaAtualizada = escala.copy(
            ativo = ativo,
            dataAtualizacao = LocalDateTime.now()
        )
        return escalaRepositorio.save(escalaAtualizada)
    }
    
    /**
     * Define escala como padrão
     */
    fun definirComoPadrao(id: Long): Escala {
        desmarcarEscalasPadrao()
        
        val escala = buscarPorId(id)
        val escalaAtualizada = escala.copy(
            escalaPadrao = true,
            ativo = true, // Escala padrão deve estar ativa
            dataAtualizacao = LocalDateTime.now()
        )
        return escalaRepositorio.save(escalaAtualizada)
    }
    
    /**
     * Desmarca todas as escalas como padrão
     */
    private fun desmarcarEscalasPadrao() {
        val escalaPadraoAtual = escalaRepositorio.findByEscalaPadraoTrue()
        if (escalaPadraoAtual != null) {
            val escalaAtualizada = escalaPadraoAtual.copy(
                escalaPadrao = false,
                dataAtualizacao = LocalDateTime.now()
            )
            escalaRepositorio.save(escalaAtualizada)
        }
    }
    
    /**
     * Valida dados da escala
     */
    private fun validarEscala(dto: EscalaRequisicaoDTO) {
        require(dto.nome.isNotBlank()) {
            "Nome da escala é obrigatório"
        }
        require(dto.nome.length <= 100) {
            "Nome da escala deve ter no máximo 100 caracteres"
        }
        
        val horaEntrada = LocalTime.parse(dto.horaEntrada)
        val horaSaida = LocalTime.parse(dto.horaSaida)
        
        // Validar horários (permitir virada de dia para turnos noturnos)
        if (dto.tipo != TipoEscala.ESCALA_12X36) {
            // Para escalas normais, saída deve ser após entrada (mesmo dia)
            if (horaSaida <= horaEntrada) {
                // Permitir apenas se for turno noturno (ex: 22:00-06:00)
                if (horaEntrada.hour < 18) {
                    throw IllegalArgumentException(
                        "Hora de saída deve ser posterior à hora de entrada"
                    )
                }
            }
        }
        
        require(dto.intervaloMinutos >= 0) {
            "Intervalo não pode ser negativo"
        }
        require(dto.intervaloMinutos <= 240) {
            "Intervalo não pode ser maior que 4 horas"
        }
        
        require(dto.cargaHorariaDiaria > 0) {
            "Carga horária diária deve ser maior que zero"
        }
        require(dto.cargaHorariaDiaria <= 12) {
            "Carga horária diária não pode exceder 12 horas"
        }
        
        require(dto.cargaHorariaSemanal > 0) {
            "Carga horária semanal deve ser maior que zero"
        }
        require(dto.cargaHorariaSemanal <= 60) {
            "Carga horária semanal não pode exceder 60 horas"
        }
        
        require(dto.diasSemana.isNotEmpty()) {
            "Selecione pelo menos um dia da semana"
        }
        
        require(dto.toleranciaEntradaMinutos >= 0) {
            "Tolerância de entrada não pode ser negativa"
        }
        require(dto.toleranciaSaidaMinutos >= 0) {
            "Tolerância de saída não pode ser negativa"
        }
    }
}
