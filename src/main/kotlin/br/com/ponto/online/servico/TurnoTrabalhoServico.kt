package br.com.ponto.online.servico

import br.com.ponto.online.dto.TurnoTrabalhoRequisicaoDTO
import br.com.ponto.online.entidade.TurnoTrabalho
import br.com.ponto.online.repositorio.EscalaRepositorio
import br.com.ponto.online.repositorio.TurnoTrabalhoRepositorio
import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class TurnoTrabalhoServico(
    private val turnoTrabalhoRepositorio: TurnoTrabalhoRepositorio,
    private val escalaRepositorio: EscalaRepositorio
) {
    
    fun listarTodos(): List<TurnoTrabalho> {
        return turnoTrabalhoRepositorio.findAll()
    }
    
    fun listarAtivos(): List<TurnoTrabalho> {
        return turnoTrabalhoRepositorio.findByAtivoTrue()
    }
    
    fun buscarPorId(id: Long): TurnoTrabalho {
        return turnoTrabalhoRepositorio.findById(id)
            .orElseThrow { IllegalArgumentException("Turno com ID $id não encontrado") }
    }
    
    fun cadastrar(dto: TurnoTrabalhoRequisicaoDTO): TurnoTrabalho {
        validarTurno(dto)
        
        val escala = if (dto.escalaId != null) {
            escalaRepositorio.findById(dto.escalaId)
                .orElseThrow { IllegalArgumentException("Escala com ID ${dto.escalaId} não encontrada") }
        } else null
        
        val turno = TurnoTrabalho(
            nome = dto.nome.trim(),
            descricao = dto.descricao?.trim(),
            horaInicio = LocalTime.parse(dto.horaInicio),
            horaFim = LocalTime.parse(dto.horaFim),
            escala = escala,
            ativo = dto.ativo
        )
        
        return turnoTrabalhoRepositorio.save(turno)
    }
    
    fun atualizar(id: Long, dto: TurnoTrabalhoRequisicaoDTO): TurnoTrabalho {
        validarTurno(dto)
        
        val turnoExistente = buscarPorId(id)
        
        val escala = if (dto.escalaId != null) {
            escalaRepositorio.findById(dto.escalaId)
                .orElseThrow { IllegalArgumentException("Escala com ID ${dto.escalaId} não encontrada") }
        } else null
        
        val turnoAtualizado = turnoExistente.copy(
            nome = dto.nome.trim(),
            descricao = dto.descricao?.trim(),
            horaInicio = LocalTime.parse(dto.horaInicio),
            horaFim = LocalTime.parse(dto.horaFim),
            escala = escala,
            ativo = dto.ativo
        )
        
        return turnoTrabalhoRepositorio.save(turnoAtualizado)
    }
    
    fun excluir(id: Long) {
        if (!turnoTrabalhoRepositorio.existsById(id)) {
            throw IllegalArgumentException("Turno com ID $id não encontrado")
        }
        turnoTrabalhoRepositorio.deleteById(id)
    }
    
    private fun validarTurno(dto: TurnoTrabalhoRequisicaoDTO) {
        require(dto.nome.isNotBlank()) {
            "Nome do turno é obrigatório"
        }
        require(dto.nome.length <= 100) {
            "Nome do turno deve ter no máximo 100 caracteres"
        }
        
        val horaInicio = LocalTime.parse(dto.horaInicio)
        val horaFim = LocalTime.parse(dto.horaFim)
        
        // Permitir turnos que cruzam meia-noite (ex: 22:00-06:00)
        // Não validar hora fim > hora início para permitir turnos noturnos
    }
}
