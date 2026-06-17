package com.vonlanplace.doacao.entity;

public enum OrgaoEmissorRG {
    SSP("SSP", "Secretaria de Segurança Pública"),
    SESP("SESP", "Secretaria de Estado de Segurança Pública"),
    DETRAN("DETRAN", "Departamento de Trânsito"),
    SJS("SJS", "Secretaria da Justiça e da Segurança"),
    SJTS("SJTS", "Secretaria da Justiça do Trabalho e da Segurança"),
    DIC("DIC", "Diretoria de Identificação Civil"),
    IFP("IFP", "Instituto Félix Pacheco"),
    IILM("IILM", "Instituto Identification Lourenço Mendes"),
    IITC("IITC", "Instituto de Identificação Tavares Buril"),
    IML("IML", "Instituto Médico-Legal"),
    IPF("IPF", "Instituto Pereira Faustino"),
    MTE("MTE", "Ministério do Trabalho e Emprego"),
    MMA("MMA", "Ministério da Marinha"),
    MAE("MAE", "Ministério da Aeronáutica"),
    MEX("MEX", "Ministério do Exército"),
    DPF("DPF", "Departamento de Polícia Federal"),
    POM("POM", "Polícia Militar"),
    PF("PF", "Polícia Federal"),
    PRF("PRF", "Polícia Rodoviária Federal"),
    CNV("CNV", "Conselho Nacional de Valores"),
    CNT("CNT", "Carteira Nacional de Habilitação (antiga)"),
    DICOM("DICOM", "Diretoria de Identificação de Comunicação"),
    PM("PM", "Polícia Militar"),
    CB("CB", "Corpo de Bombeiros"),
    CGPI("CGPI", "Coordenação Geral de Privilégios e Imunidades"),
    CGPMAF("CGPMAF", "Coordenação Geral de Polícia Marítima, Aeronáutica e de Fronteiras"),
    CNIG("CNIG", "Conselho Nacional de Imigração"),
    COREN("COREN", "Conselho Regional de Enfermagem"),
    CRA("CRA", "Conselho Regional de Administração"),
    CRAS("CRAS", "Conselho Regional de Assistentes Sociais"),
    CRB("CRB", "Conselho Regional de Biblioteconomia"),
    CRC("CRC", "Conselho Regional de Contabilidade"),
    CRE("CRE", "Conselho Regional de Estatística"),
    CREA("CREA", "Conselho Regional de Engenharia e Agronomia"),
    CRECI("CRECI", "Conselho Regional de Corretores de Imóveis"),
    CREFIT("CREFIT", "Conselho Regional de Fisioterapia e Terapia Ocupacional"),
    CRF("CRF", "Conselho Regional de Farmácia"),
    CRM("CRM", "Conselho Regional de Medicina"),
    CRMV("CRMV", "Conselho Regional de Medicina Veterinária"),
    CRN("CRN", "Conselho Regional de Nutrição"),
    CRO("CRO", "Conselho Regional de Odontologia"),
    CRP("CRP", "Conselho Regional de Psicologia"),
    CRQ("CRQ", "Conselho Regional de Química"),
    CRRC("CRRC", "Conselho Regional de Representantes Comerciais"),
    CRT("CRT", "Conselho Regional de Técnicos"),
    CRV("CRV", "Conselho Regional de Veterinária"),
    DPMAF("DPMAF", "Divisão de Polícia Marítima, Aérea e de Fronteiras"),
    DREX("DREX", "Diretoria de Execução Provisória"),
    DRT("DRT", "Delegacia Regional do Trabalho"),
    MINDEF("MINDEF", "Ministério da Defesa"),
    OAB("OAB", "Ordem dos Advogados do Brasil"),
    OUTRO("OUTRO", "Outro Órgão Emissor");

    private final String sigla;
    private final String descricao;

    OrgaoEmissorRG(String sigla, String descricao) {
        this.sigla = sigla;
        this.descricao = descricao;
    }
    
    public static OrgaoEmissorRG fromSigla(String sigla) {
        if (sigla == null) return null;
        for (OrgaoEmissorRG orgao : OrgaoEmissorRG.values()) {
            if (orgao.getSigla().equalsIgnoreCase(sigla.trim())) {
                return orgao;
            }
        }
        return OUTRO;
    }

    public String getSigla() {
        return sigla;
    }

    public String getDescricao() {
        return descricao;
    }
}