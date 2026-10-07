const listaRelatorios =
    document.getElementById("listaRelatorios");

const mensagemVazia =
    document.getElementById("mensagemVazia");

const mensagemErro =
    document.getElementById("mensagemErro");

const contadorRelatorios =
    document.getElementById("contadorRelatorios");


async function carregarRelatorios() {

    mensagemErro.style.display = "none";

    try {

        const resposta =
            await fetch("/relatorios/aluno");

        if (!resposta.ok) {

            throw new Error(
                "Não foi possível carregar os relatórios."
            );
        }

        const relatorios =
            await resposta.json();

        renderizarRelatorios(relatorios);

    } catch (erro) {

        console.error(erro);

        mensagemErro.textContent =
            "Não foi possível carregar seus relatórios.";

        mensagemErro.style.display = "block";
    }
}


function renderizarRelatorios(relatorios) {

    listaRelatorios.innerHTML = "";

    contadorRelatorios.textContent =
        `${relatorios.length} ${
            relatorios.length === 1
                ? "relatório"
                : "relatórios"
        }`;


    if (!relatorios ||
        relatorios.length === 0) {

        mensagemVazia.style.display =
            "block";

        return;
    }


    mensagemVazia.style.display =
        "none";


    relatorios.forEach(relatorio => {

        const card =
            document.createElement("article");

        card.classList.add(
            "card-relatorio-aluno"
        );


        const status =
            obterStatusRelatorio(
                relatorio.status
            );


        const nomePaciente =
            relatorio.nomePaciente ||
            relatorio.paciente ||
            `Consulta #${relatorio.consultaId}`;


       const dataCriacao =
                    relatorio.dataCriacao
                    ? formatarData(relatorio.dataCriacao)
                    : "-";


        card.innerHTML = `

            <div class="cabecalho-relatorio">

                <div class="identificacao-relatorio">

                    <div class="icone-relatorio">

                        <i class="bi bi-file-medical"></i>

                    </div>


                    <div class="informacoes-relatorio">

                        <h3>
                            ${escaparHtml(
                                nomePaciente
                            )}
                        </h3>

                        <p>
                            <strong>Consulta:</strong>
                            #${relatorio.consultaId}
                        </p>

                        <p>
                            <strong>Data:</strong>
                            ${dataCriacao}
                        </p>

                    </div>

                </div>


                <span class="status-relatorio ${status.classe}">
                    ${status.texto}
                </span>

            </div>


            <div class="conteudo-relatorio">

                <div class="titulo-descricao">
                    Descrição do relatório
                </div>


                <div class="descricao-relatorio-aluno">

                    ${escaparHtml(
                        relatorio.descricao || "-"
                    )}

                </div>

            </div>


            ${
                relatorio.status === "reprovado"
                    ? `

                        <div class="acoes-relatorio-aluno">

                            <button
                                type="button"
                                class="btn-editar-relatorio"
                                onclick="abrirEdicaoRelatorio(
                                    ${relatorio.id},
                                    this
                                )">

                                <i class="bi bi-pencil"></i>

                                Editar relatório

                            </button>

                        </div>

                    `
                    : ""
            }

        `;


        listaRelatorios.appendChild(card);
    });
}


function obterStatusRelatorio(status) {

    if (status === "aprovado") {

        return {
            texto: "Aprovado",
            classe: "status-aprovado"
        };
    }


    if (status === "reprovado") {

        return {
            texto: "Reprovado",
            classe: "status-reprovado"
        };
    }


    return {
        texto: "Aguardando avaliação",
        classe: "status-pendente"
    };
}


function abrirEdicaoRelatorio(
    idRelatorio,
    botao
) {

    const card =
        botao.closest(
            ".card-relatorio-aluno"
        );


    if (
        card.querySelector(
            ".formulario-edicao-relatorio"
        )
    ) {
        return;
    }


    const descricaoAtual =
        card.querySelector(
            ".descricao-relatorio-aluno"
        ).textContent;


    const formulario =
        document.createElement("div");

    formulario.classList.add(
        "formulario-edicao-relatorio"
    );


    formulario.innerHTML = `

        <div class="cabecalho-edicao-relatorio">

            <h4>
                <i class="bi bi-pencil-square"></i>
                Editar relatório
            </h4>

            <p>
                Ajuste a descrição do relatório
                conforme necessário.
            </p>

        </div>


        <div class="campo-edicao-relatorio">

            <label
                for="descricaoEdicao-${idRelatorio}">

                Descrição do relatório

            </label>


            <textarea
                id="descricaoEdicao-${idRelatorio}"
                class="descricao-edicao-relatorio"
                maxlength="5000"
                rows="6"
                placeholder="Digite aqui a nova descrição..."
            ></textarea>


            <div class="rodape-edicao-relatorio">

                <span
                    class="contador-edicao-relatorio">

                    0 / 5000

                </span>

            </div>

        </div>


        <div
            class="mensagem-edicao-relatorio"
            style="display: none;">
        </div>


        <div class="acoes-edicao-relatorio">

            <button
                type="button"
                class="btn-cancelar-edicao"
                onclick="fecharEdicaoRelatorio(this)">

                Cancelar

            </button>


            <button
                type="button"
                class="btn-salvar-edicao"
                onclick="salvarEdicaoRelatorio(
                    ${idRelatorio},
                    this
                )">

                <i class="bi bi-check2"></i>

                Salvar alterações

            </button>

        </div>

    `;


    card.appendChild(formulario);


    const textarea =
        formulario.querySelector(
            ".descricao-edicao-relatorio"
        );


    const contador =
        formulario.querySelector(
            ".contador-edicao-relatorio"
        );


    textarea.value =
        descricaoAtual === "-"
            ? ""
            : descricaoAtual;


    contador.textContent =
        `${textarea.value.length} / 5000`;


    textarea.addEventListener(
        "input",
        () => {

            contador.textContent =
                `${textarea.value.length} / 5000`;

        }
    );


    textarea.focus();


    formulario.scrollIntoView({
        behavior: "smooth",
        block: "nearest"
    });
}


function fecharEdicaoRelatorio(
    botao
) {

    const formulario =
        botao.closest(
            ".formulario-edicao-relatorio"
        );


    if (formulario) {

        formulario.remove();
    }
}


async function salvarEdicaoRelatorio(
    idRelatorio,
    botao
) {

    const formulario =
        botao.closest(
            ".formulario-edicao-relatorio"
        );


    const textarea =
        formulario.querySelector(
            ".descricao-edicao-relatorio"
        );


    const mensagem =
        formulario.querySelector(
            ".mensagem-edicao-relatorio"
        );


    const descricao =
        textarea.value.trim();


    if (!descricao) {

        mostrarMensagemEdicao(
            mensagem,
            "Informe a descrição do relatório.",
            "erro"
        );

        textarea.focus();

        return;
    }


    botao.disabled = true;


    botao.innerHTML = `

        <span
            class="spinner-border spinner-border-sm"
            aria-hidden="true">
        </span>

        Salvando...

    `;


    try {

        const resposta =
            await fetch(
                `/relatorios/${idRelatorio}`,
                {

                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        descricao: descricao

                    })
                }
            );


        const texto =
            await resposta.text();


        if (!resposta.ok) {

            mostrarMensagemEdicao(
                mensagem,
                texto ||
                    "Não foi possível atualizar o relatório.",
                "erro"
            );


            botao.disabled = false;


            botao.innerHTML = `

                <i class="bi bi-check2"></i>

                Salvar alterações

            `;

            return;
        }


        mostrarMensagemEdicao(
            mensagem,
            texto ||
                "Relatório atualizado com sucesso.",
            "sucesso"
        );


        textarea.disabled = true;


        const botaoCancelar =
            formulario.querySelector(
                ".btn-cancelar-edicao"
            );


        botaoCancelar.disabled = true;

        botao.disabled = true;


        botao.innerHTML = `

            <i class="bi bi-check-circle"></i>

            Alterações salvas

        `;


        /*
         * Depois da edição o backend
         * coloca o status do relatório
         * novamente como NULL.
         *
         * Recarregamos a tela para mostrar
         * "Aguardando avaliação".
         */

        setTimeout(() => {

            carregarRelatorios();

        }, 1000);


    } catch (erro) {

        console.error(erro);


        mostrarMensagemEdicao(
            mensagem,
            "Não foi possível conectar ao servidor.",
            "erro"
        );


        botao.disabled = false;


        botao.innerHTML = `

            <i class="bi bi-check2"></i>

            Salvar alterações

        `;
    }
}


function mostrarMensagemEdicao(
    elemento,
    texto,
    tipo
) {

    elemento.textContent = texto;


    elemento.className =
        "mensagem-edicao-relatorio";


    elemento.classList.add(
        tipo === "sucesso"
            ? "mensagem-sucesso"
            : "mensagem-erro"
    );


    elemento.style.display =
        "block";
}


function formatarData(data) {

    if (!data) {
        return "-";
    }


    const somenteData =
        data.substring(0, 10);


    const partes =
        somenteData.split("-");


    if (partes.length !== 3) {

        return somenteData;
    }


    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}


function escaparHtml(texto) {

    const div =
        document.createElement("div");


    div.textContent =
        texto;


    return div.innerHTML;
}


carregarRelatorios();