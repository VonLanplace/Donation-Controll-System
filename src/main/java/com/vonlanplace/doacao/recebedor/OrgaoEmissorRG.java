package com.vonlanplace.doacao.recebedor;

import jakarta.persistence.EnumType;
import lombok.Getter;

/**
 * Órgãos emissores de RG aceitos no cadastro do {@link Recebedor}.
 *
 * <p>O enum carrega duas labels: {@code sigla}, gravada no banco, e {@code descricao},
 * usada apenas em exibição. A coluna em {@code Recebedor} é gravada por
 * {@link EnumType @Enumerated(EnumType.STRING)}, então o valor persistido é o nome da
 * constante, não a sigla.</p>
 *
 * <p>As constantes a partir de {@code DICOM} e {@code PM} repetem nomes já existentes
 * acima ({@code DPF}/{@code PF}, {@code POM}/{@code PM}) com descrições diferentes. A
 * duplicidade é mantida de propósito: trocar os nomes alteraria o dado já gravado no
 * banco, e este projeto não tem migração para converter valores.</p>
 */
@Getter
public enum OrgaoEmissorRG {
    /** Secretaria de Segurança Pública. */
    SSP("SSP", "Secretaria de Segurança Pública"),
    /** Secretaria de Estado de Segurança Pública. */
    SESP("SESP", "Secretaria de Estado de Segurança Pública"),
    /** Departamento de Trânsito. */
    DETRAN("DETRAN", "Departamento de Trânsito"),
    /** Secretaria da Justiça e da Segurança. */
    SJS("SJS", "Secretaria da Justiça e da Segurança"),
    /** Secretaria da Justiça do Trabalho e da Segurança. */
    SJTS("SJTS", "Secretaria da Justiça do Trabalho e da Segurança"),
    /** Diretoria de Identificação Civil. */
    DIC("DIC", "Diretoria de Identificação Civil"),
    /** Instituto Félix Pacheco. */
    IFP("IFP", "Instituto Félix Pacheco"),
    /** Instituto Identification Lourenço Mendes. */
    IILM("IILM", "Instituto Identification Lourenço Mendes"),
    /** Instituto de Identificação Tavares Buril. */
    IITC("IITC", "Instituto de Identificação Tavares Buril"),
    /** Instituto Médico-Legal. */
    IML("IML", "Instituto Médico-Legal"),
    /** Instituto Pereira Faustino. */
    IPF("IPF", "Instituto Pereira Faustino"),
    /** Ministério do Trabalho e Emprego. */
    MTE("MTE", "Ministério do Trabalho e Emprego"),
    /** Ministério da Marinha. */
    MMA("MMA", "Ministério da Marinha"),
    /** Ministério da Aeronáutica. */
    MAE("MAE", "Ministério da Aeronáutica"),
    /** Ministério do Exército. */
    MEX("MEX", "Ministério do Exército"),
    /** Departamento de Polícia Federal. */
    DPF("DPF", "Departamento de Polícia Federal"),
    /** Polícia Militar. */
    POM("POM", "Polícia Militar"),
    /** Polícia Federal. */
    PF("PF", "Polícia Federal"),
    /** Polícia Rodoviária Federal. */
    PRF("PRF", "Polícia Rodoviária Federal"),
    /** Conselho Nacional de Valores. */
    CNV("CNV", "Conselho Nacional de Valores"),
    /** Carteira Nacional de Habilitação, extinta. */
    CNT("CNT", "Carteira Nacional de Habilitação (antiga)"),
    /** Diretoria de Identificação de Comunicação. */
    DICOM("DICOM", "Diretoria de Identificação de Comunicação"),
    /** Polícia Militar. Constante duplicada de {@link #POM}. */
    PM("PM", "Polícia Militar"),
    /** Corpo de Bombeiros. */
    CB("CB", "Corpo de Bombeiros"),
    /** Coordenação Geral de Privilégios e Imunidades. */
    CGPI("CGPI", "Coordenação Geral de Privilégios e Imunidades"),
    /** Coordenação Geral de Polícia Marítima, Aeronáutica e de Fronteiras. */
    CGPMAF("CGPMAF", "Coordenação Geral de Polícia Marítima, Aeronáutica e de Fronteiras"),
    /** Conselho Nacional de Imigração. */
    CNIG("CNIG", "Conselho Nacional de Imigração"),
    /** Conselho Regional de Enfermagem. */
    COREN("COREN", "Conselho Regional de Enfermagem"),
    /** Conselho Regional de Administração. */
    CRA("CRA", "Conselho Regional de Administração"),
    /** Conselho Regional de Assistentes Sociais. */
    CRAS("CRAS", "Conselho Regional de Assistentes Sociais"),
    /** Conselho Regional de Biblioteconomia. */
    CRB("CRB", "Conselho Regional de Biblioteconomia"),
    /** Conselho Regional de Contabilidade. */
    CRC("CRC", "Conselho Regional de Contabilidade"),
    /** Conselho Regional de Estatística. */
    CRE("CRE", "Conselho Regional de Estatística"),
    /** Conselho Regional de Engenharia e Agronomia. */
    CREA("CREA", "Conselho Regional de Engenharia e Agronomia"),
    /** Conselho Regional de Corretores de Imóveis. */
    CRECI("CRECI", "Conselho Regional de Corretores de Imóveis"),
    /** Conselho Regional de Fisioterapia e Terapia Ocupacional. */
    CREFIT("CREFIT", "Conselho Regional de Fisioterapia e Terapia Ocupacional"),
    /** Conselho Regional de Farmácia. */
    CRF("CRF", "Conselho Regional de Farmácia"),
    /** Conselho Regional de Medicina. */
    CRM("CRM", "Conselho Regional de Medicina"),
    /** Conselho Regional de Medicina Veterinária. */
    CRMV("CRMV", "Conselho Regional de Medicina Veterinária"),
    /** Conselho Regional de Nutrição. */
    CRN("CRN", "Conselho Regional de Nutrição"),
    /** Conselho Regional de Odontologia. */
    CRO("CRO", "Conselho Regional de Odontologia"),
    /** Conselho Regional de Psicologia. */
    CRP("CRP", "Conselho Regional de Psicologia"),
    /** Conselho Regional de Química. */
    CRQ("CRQ", "Conselho Regional de Química"),
    /** Conselho Regional de Representantes Comerciais. */
    CRRC("CRRC", "Conselho Regional de Representantes Comerciais"),
    /** Conselho Regional de Técnicos. */
    CRT("CRT", "Conselho Regional de Técnicos"),
    /** Conselho Regional de Veterinária. */
    CRV("CRV", "Conselho Regional de Veterinária"),
    /** Divisão de Polícia Marítima, Aérea e de Fronteiras. */
    DPMAF("DPMAF", "Divisão de Polícia Marítima, Aérea e de Fronteiras"),
    /** Diretoria de Execução Provisória. */
    DREX("DREX", "Diretoria de Execução Provisória"),
    /** Delegacia Regional do Trabalho. */
    DRT("DRT", "Delegacia Regional do Trabalho"),
    /** Ministério da Defesa. */
    MINDEF("MINDEF", "Ministério da Defesa"),
    /** Ordem dos Advogados do Brasil. */
    OAB("OAB", "Ordem dos Advogados do Brasil"),
    /** Qualquer órgão não listado acima. Usado como fallback de {@link #fromSigla(String)}. */
    OUTRO("OUTRO", "Outro Órgão Emissor");

    /** Sigla gravada e exibida para o órgão. */
    private final String sigla;

    /** Descrição legível do órgão, usada apenas em interface. */
    private final String descricao;

    /**
     * Construtor do enum.
     *
     * @param sigla      sigla do órgão
     * @param descricao  descrição legível
     */
    OrgaoEmissorRG(String sigla, String descricao) {
        this.sigla = sigla;
        this.descricao = descricao;
    }

    /**
     * Resolve uma constante pela sigla, ignorando maiúsculas, minúsculas e espaços nas
     * pontas.
     *
     * <p>Não lança exceção para sigla desconhecida: devolve {@link #OUTRO}. Isso mantém a
     * importação de dados tolerante a órgãos que ainda não entraram na lista, mas também
     * significa que uma sigla digitada errado vira {@code OUTRO} em silêncio.</p>
     *
     * @param sigla sigla informada; pode ser {@code null}
     * @return a constante correspondente, {@link #OUTRO} se não houver correspondência,
     * ou {@code null} se a sigla for {@code null}
     */
    public static OrgaoEmissorRG fromSigla(String sigla) {
        if (sigla == null) return null;
        for (OrgaoEmissorRG orgao : OrgaoEmissorRG.values()) {
            if (orgao.getSigla().equalsIgnoreCase(sigla.trim())) {
                return orgao;
            }
        }
        return OUTRO;
    }

}
