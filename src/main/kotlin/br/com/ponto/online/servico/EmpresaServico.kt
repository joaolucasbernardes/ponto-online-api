package br.com.ponto.online.servico

import br.com.ponto.online.dto.EmpresaRequisicaoDTO
import br.com.ponto.online.entidade.Empresa
import br.com.ponto.online.repositorio.EmpresaRepositorio
import org.springframework.stereotype.Service

@Service
class EmpresaServico(private val empresaRepositorio: EmpresaRepositorio) {

    fun criar(requisicaoDTO: EmpresaRequisicaoDTO): Empresa {
        val empresa = Empresa(
            razaoSocial = requisicaoDTO.razaoSocial,
            cnpj = requisicaoDTO.cnpj
        )

        return empresaRepositorio.save(empresa)
    }
}