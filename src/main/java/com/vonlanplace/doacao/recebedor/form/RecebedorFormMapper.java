package com.vonlanplace.doacao.recebedor.form;

import com.vonlanplace.doacao.recebedor.RecebedorCreateDTO;
import com.vonlanplace.doacao.recebedor.RecebedorResponseDTO;
import com.vonlanplace.doacao.recebedor.RecebedorUpdateDTO;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioCreateDTO;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioResponseDTO;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioUpdateDTO;
import com.vonlanplace.doacao.recebedor.cesta.StatusRecebimento;
import com.vonlanplace.doacao.recebedor.dependente.DependenteCreateDTO;
import com.vonlanplace.doacao.recebedor.dependente.DependenteResponseDTO;
import com.vonlanplace.doacao.recebedor.dependente.DependenteUpdateDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaCreateDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaUpdateDTO;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaCreateDTO;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaUpdateDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Conversão entre o formulário unificado e os DTOs dos serviços.
 *
 * <p>A conversão é manual de propósito: os DTOs de criação e edição são records
 * com vínculos por {@code UUID}, e o formulário carrega os filhos em listas
 * aninhadas. Um mapper explícito deixa claro onde cada vínculo é resolvido e
 * evita configuração de múltiplas fontes no MapStruct.</p>
 */
@Component
public class RecebedorFormMapper {

    /**
     * Monta o formulário de edição a partir dos dados persistidos.
     *
     * @param recebedor    dados do recebedor
     * @param dependentes  dependentes atuais
     * @param auxilios     auxílios atuais
     * @param despesas     despesas atuais da residência
     * @param statusCesta  status da cesta no mês corrente
     * @return formulário preenchido
     */
    public RecebedorFormDTO toFormDTO(
            RecebedorResponseDTO recebedor,
            List<DependenteResponseDTO> dependentes,
            List<AuxilioResponseDTO> auxilios,
            List<DespesaResponseDTO> despesas,
            StatusRecebimento statusCesta
    ) {
        RecebedorFormDTO form = new RecebedorFormDTO();
        form.setNomeCompleto(recebedor.nomeCompleto());
        form.setCpf(recebedor.cpf());
        form.setRgNum(recebedor.rgNum());
        form.setRgEmissor(recebedor.rgEmissor());
        form.setDataNascimento(recebedor.dataNascimento());
        form.setTelefonePessoal(recebedor.telefonePessoal());
        form.setTelefoneRecado(recebedor.telefoneRecado());
        form.setLeEscreve(recebedor.leEscreve());
        form.setEmail(recebedor.email());
        form.setPix(recebedor.pix());
        form.setNecessitaJuridico(recebedor.necessitaJuridico());
        form.setNecessitaPsicologo(recebedor.necessitaPsicologo());
        form.setStatusCesta(statusCesta);
        form.setParecerEntrevistador(recebedor.parecerEntrevistador());
        form.setComentarios(recebedor.comentarios());
        form.setResidencia(toResidenciaForm(recebedor.residencia()));
        form.setDependentes(new ArrayList<>(dependentes.stream().map(this::toDependenteForm).toList()));
        form.setAuxilios(new ArrayList<>(auxilios.stream().map(this::toAuxilioForm).toList()));
        form.setDespesas(new ArrayList<>(despesas.stream().map(this::toDespesaForm).toList()));
        return form;
    }

    /**
     * Converte os dados residenciais para o DTO de criação.
     *
     * @param form dados residenciais
     * @return DTO de criação
     */
    public ResidenciaCreateDTO toResidenciaCreateDTO(ResidenciaFormDTO form) {
        return new ResidenciaCreateDTO(
                form.getLogradouro(),
                form.getNumero(),
                form.getCep(),
                form.getComplemento(),
                form.getCidade(),
                form.getBairro(),
                form.getNumMoradores(),
                form.getNumTrabalhadores(),
                form.getTipoOcupacao(),
                form.getTipoResidencia()
        );
    }

    /**
     * Converte os dados residenciais para o DTO de atualização.
     *
     * @param form dados residenciais
     * @return DTO de atualização
     */
    public ResidenciaUpdateDTO toResidenciaUpdateDTO(ResidenciaFormDTO form) {
        return new ResidenciaUpdateDTO(
                form.getLogradouro(),
                form.getNumero(),
                form.getCep(),
                form.getComplemento(),
                form.getCidade(),
                form.getBairro(),
                form.getNumMoradores(),
                form.getNumTrabalhadores(),
                form.getTipoOcupacao(),
                form.getTipoResidencia()
        );
    }

    /**
     * Converte o formulário para o DTO de criação do recebedor.
     *
     * @param form         dados do formulário
     * @param residenciaId identificador da residência criada
     * @return DTO de criação
     */
    public RecebedorCreateDTO toCreateDTO(RecebedorFormDTO form, UUID residenciaId) {
        return new RecebedorCreateDTO(
                form.getNomeCompleto(),
                form.getCpf(),
                form.getRgNum(),
                form.getRgEmissor(),
                form.getDataNascimento(),
                form.getTelefonePessoal(),
                form.getTelefoneRecado(),
                form.isLeEscreve(),
                form.getEmail(),
                form.getPix(),
                form.isNecessitaJuridico(),
                form.isNecessitaPsicologo(),
                form.getStatusCesta(),
                form.isParecerEntrevistador(),
                form.getComentarios(),
                residenciaId
        );
    }

    /**
     * Converte o formulário para o DTO de atualização do recebedor.
     *
     * @param form         dados do formulário
     * @param residenciaId identificador da residência vinculada
     * @return DTO de atualização
     */
    public RecebedorUpdateDTO toUpdateDTO(RecebedorFormDTO form, UUID residenciaId) {
        return new RecebedorUpdateDTO(
                form.getNomeCompleto(),
                form.getCpf(),
                form.getRgNum(),
                form.getRgEmissor(),
                form.getDataNascimento(),
                form.getTelefonePessoal(),
                form.getTelefoneRecado(),
                form.isLeEscreve(),
                form.getEmail(),
                form.getPix(),
                form.isNecessitaJuridico(),
                form.isNecessitaPsicologo(),
                form.getStatusCesta(),
                form.isParecerEntrevistador(),
                form.getComentarios(),
                residenciaId
        );
    }

    /**
     * Converte uma linha de dependente para criação.
     *
     * @param form         linha do formulário
     * @param recebedorId  identificador do recebedor
     * @return DTO de criação
     */
    public DependenteCreateDTO toDependenteCreateDTO(DependenteFormDTO form, UUID recebedorId) {
        return new DependenteCreateDTO(
                form.getNome(),
                form.getIdade(),
                form.getDataNascimento(),
                recebedorId
        );
    }

    /**
     * Converte uma linha de dependente para atualização.
     *
     * @param form         linha do formulário
     * @param recebedorId  identificador do recebedor
     * @return DTO de atualização
     */
    public DependenteUpdateDTO toDependenteUpdateDTO(DependenteFormDTO form, UUID recebedorId) {
        return new DependenteUpdateDTO(
                form.getNome(),
                form.getIdade(),
                form.getDataNascimento(),
                recebedorId
        );
    }

    /**
     * Converte uma linha de auxílio para criação.
     *
     * @param form        linha do formulário
     * @param recebedorId identificador do recebedor
     * @return DTO de criação
     */
    public AuxilioCreateDTO toAuxilioCreateDTO(AuxilioFormDTO form, UUID recebedorId) {
        return new AuxilioCreateDTO(
                form.getValor(),
                form.getFonte(),
                form.getDescricao(),
                recebedorId
        );
    }

    /**
     * Converte uma linha de auxílio para atualização.
     *
     * @param form        linha do formulário
     * @param recebedorId identificador do recebedor
     * @return DTO de atualização
     */
    public AuxilioUpdateDTO toAuxilioUpdateDTO(AuxilioFormDTO form, UUID recebedorId) {
        return new AuxilioUpdateDTO(
                form.getValor(),
                form.getFonte(),
                form.getDescricao(),
                recebedorId
        );
    }

    /**
     * Converte uma linha de despesa para criação.
     *
     * @param form          linha do formulário
     * @param residenciaId  identificador da residência
     * @return DTO de criação
     */
    public DespesaCreateDTO toDespesaCreateDTO(DespesaFormDTO form, UUID residenciaId) {
        return new DespesaCreateDTO(
                form.getValor(),
                form.getFonte(),
                form.getDescricao(),
                residenciaId
        );
    }

    /**
     * Converte uma linha de despesa para atualização.
     *
     * @param form          linha do formulário
     * @param residenciaId  identificador da residência
     * @return DTO de atualização
     */
    public DespesaUpdateDTO toDespesaUpdateDTO(DespesaFormDTO form, UUID residenciaId) {
        return new DespesaUpdateDTO(
                form.getValor(),
                form.getFonte(),
                form.getDescricao(),
                residenciaId
        );
    }

    private ResidenciaFormDTO toResidenciaForm(ResidenciaResponseDTO residencia) {
        ResidenciaFormDTO form = new ResidenciaFormDTO();
        form.setLogradouro(residencia.logradouro());
        form.setNumero(residencia.numero());
        form.setCep(residencia.cep());
        form.setComplemento(residencia.complemento());
        form.setCidade(residencia.cidade());
        form.setBairro(residencia.bairro());
        form.setNumMoradores(residencia.numMoradores());
        form.setNumTrabalhadores(residencia.numTrabalhadores());
        form.setTipoOcupacao(residencia.tipoOcupacao());
        form.setTipoResidencia(residencia.tipoResidencia());
        return form;
    }

    private DependenteFormDTO toDependenteForm(DependenteResponseDTO dependente) {
        DependenteFormDTO form = new DependenteFormDTO();
        form.setId(dependente.id());
        form.setNome(dependente.nome());
        form.setIdade(dependente.idade());
        form.setDataNascimento(dependente.dataNascimento());
        return form;
    }

    private AuxilioFormDTO toAuxilioForm(AuxilioResponseDTO auxilio) {
        AuxilioFormDTO form = new AuxilioFormDTO();
        form.setId(auxilio.id());
        form.setValor(auxilio.valor());
        form.setFonte(auxilio.fonte());
        form.setDescricao(auxilio.descricao());
        return form;
    }

    private DespesaFormDTO toDespesaForm(DespesaResponseDTO despesa) {
        DespesaFormDTO form = new DespesaFormDTO();
        form.setId(despesa.id());
        form.setValor(despesa.valor());
        form.setFonte(despesa.fonte());
        form.setDescricao(despesa.descricao());
        return form;
    }
}
