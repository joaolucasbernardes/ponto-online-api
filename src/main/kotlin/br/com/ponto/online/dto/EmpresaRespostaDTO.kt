package br.com.ponto.online.dto

import br.com.ponto.online.entidade.Empresa

data class EmpresaRespostaDTO(
    val id: Long?,
    val razaoSocial: String,
    val cnpj: String
) {
    constructor(empresa: Empresa) : this(
        id = empresa.id,
        razaoSocial = empresa.razaoSocial,
        cnpj = empresa.cnpj
    )
}