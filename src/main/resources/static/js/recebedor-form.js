(function () {
    "use strict";

    var configuracao = {
        dependente: {
            tbody: "dependentes-linhas",
            template: "template-dependente",
            tipo: "dependentes",
            vazio: "Nenhum dependente informado."
        },
        despesa: {
            tbody: "despesas-linhas",
            template: "template-despesa",
            tipo: "despesas",
            vazio: "Nenhuma despesa informada."
        },
        auxilio: {
            tbody: "auxilios-linhas",
            template: "template-auxilio",
            tipo: "auxilios",
            vazio: "Nenhum auxílio informado."
        }
    };

    function ativarAba(nome) {
        document.querySelectorAll(".aba").forEach(function (aba) {
            var ativa = aba.dataset.aba === nome;
            aba.classList.toggle("ativa", ativa);
            aba.setAttribute("aria-selected", ativa ? "true" : "false");
        });
        document.querySelectorAll(".aba-conteudo").forEach(function (secao) {
            secao.classList.toggle("ativa", secao.dataset.aba === nome);
        });
    }

    function tipoDoTbody(tbody) {
        var encontrado = null;
        Object.keys(configuracao).forEach(function (chave) {
            if (configuracao[chave].tbody === tbody.id) {
                encontrado = configuracao[chave];
            }
        });
        return encontrado;
    }

    function reindexar(tbody, tipo) {
        var regex = new RegExp("^" + tipo + "\\[\\d+\\]");
        tbody.querySelectorAll("tr.linha-form").forEach(function (linha, indice) {
            linha.querySelectorAll("[name]").forEach(function (campo) {
                campo.name = campo.name.replace(regex, tipo + "[" + indice + "]");
            });
        });
    }

    function mostrarVazio(tbody, mensagem) {
        if (!tbody.querySelector("tr.linha-form")) {
            var linha = document.createElement("tr");
            linha.className = "linha-vazia";
            var celula = document.createElement("td");
            celula.colSpan = 4;
            celula.textContent = mensagem;
            linha.appendChild(celula);
            tbody.appendChild(linha);
        }
    }

    function removerLinhasVazias() {
        Object.keys(configuracao).forEach(function (chave) {
            var cfg = configuracao[chave];
            var tbody = document.getElementById(cfg.tbody);
            if (!tbody) {
                return;
            }
            tbody.querySelectorAll("tr.linha-form").forEach(function (linha) {
                var id = linha.querySelector("input[type='hidden']");
                var temId = id !== null && id.value.trim() !== "";
                var algumValor = Array.from(linha.querySelectorAll("input:not([type='hidden'])"))
                    .some(function (campo) {
                        return campo.value.trim() !== "";
                    });
                if (!temId && !algumValor) {
                    linha.remove();
                }
            });
            reindexar(tbody, cfg.tipo);
            mostrarVazio(tbody, cfg.vazio);
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll(".aba").forEach(function (aba) {
            aba.addEventListener("click", function () {
                ativarAba(aba.dataset.aba);
            });
        });

        var primeiroErro = document.querySelector(".aba-conteudo .invalido, .aba-conteudo .erro");
        if (primeiroErro) {
            var secao = primeiroErro.closest(".aba-conteudo");
            if (secao) {
                ativarAba(secao.dataset.aba);
            }
        }

        document.querySelectorAll("[data-adicionar]").forEach(function (botao) {
            botao.addEventListener("click", function () {
                var cfg = configuracao[botao.dataset.adicionar];
                var tbody = document.getElementById(cfg.tbody);
                var template = document.getElementById(cfg.template);
                var indice = tbody.querySelectorAll("tr.linha-form").length;
                var vazio = tbody.querySelector("tr.linha-vazia");
                if (vazio) {
                    vazio.remove();
                }
                tbody.insertAdjacentHTML(
                    "beforeend",
                    template.innerHTML.split("__index__").join(String(indice))
                );
                var ultimaLinha = tbody.querySelector("tr.linha-form:last-child");
                if (ultimaLinha) {
                    var primeiroCampo = ultimaLinha.querySelector("input:not([type='hidden'])");
                    if (primeiroCampo) {
                        primeiroCampo.focus();
                    }
                }
            });
        });

        document.addEventListener("click", function (evento) {
            var remover = evento.target.closest(".remover-linha");
            if (!remover) {
                return;
            }
            var linha = remover.closest("tr.linha-form");
            if (!linha) {
                return;
            }
            var tbody = linha.parentElement;
            var cfg = tipoDoTbody(tbody);
            linha.remove();
            if (cfg) {
                reindexar(tbody, cfg.tipo);
                mostrarVazio(tbody, cfg.vazio);
            }
        });

        var form = document.querySelector("form");
        if (form) {
            form.addEventListener("submit", function () {
                removerLinhasVazias();
            });
        }
    });
})();
