const listaRelatorios =
    document.getElementById("listaRelatorios");

const mensagemVazia =
    document.getElementById("mensagemVazia");

const mensagemErro =
    document.getElementById("mensagemErro");

const contadorRelatorios =
    document.getElementById("contadorRelatorios");

const filtroEmail =
    document.getElementById("filtroEmail");

const filtroStatus =
    document.getElementById("filtroStatus");

const btnFiltrar =
    document.getElementById("btnFiltrar");

const btnLimpar =
    document.getElementById("btnLimpar");


/* =========================================================
   CARREGAR RELATÓRIOS
   ========================================================= */

async function carregarRelatorios() {

    mensagemErro.style.display = "none";

    try {

        const email =
            filtroEmail
                ? filtroEmail.value.trim()
                : "";

        const status =
            filtroStatus
                ? filtroStatus.value
                : "";


        const parametros =
            new URLSearchParams();


        if (email) {

            parametros.append(
                "email",
                email
            );
        }


        if (status) {

            parametros.append(
                "status",
                status
            );
        }


        const queryString =
            parametros.toString();


        const url =
            queryString
                ? `/relatorios/coordenador?${queryString}`
                : "/relatorios/coordenador";


        const resposta =
            await fetch(url);


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


        listaRelatorios.innerHTML = "";

        contadorRelatorios.textContent =
            "0 relatórios";

        mensagemVazia.style.display =
            "none";


        mensagemErro.textContent =
            "Não foi possível carregar os relatórios.";

        mensagemErro.style.display =
            "block";
    }
}


/* =========================================================
   RENDERIZAR RELATÓRIOS
   ========================================================= */

function renderizarRelatorios(relatorios) {

    listaRelatorios.innerHTML = "";


    if (!relatorios || relatorios.length === 0) {

        contadorRelatorios.textContent =
            "0 relatórios";

        mensagemVazia.style.display =
            "block";

        return;
    }


    contadorRelatorios.textContent =
        `${relatorios.length} ${
            relatorios.length === 1
                ? "relatório"
                : "relatórios"
        }`;


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


        const nomeAluno =
            relatorio.nomeAluno ||
            "Aluno não identificado";


        const emailAluno =
            relatorio.emailAluno ||
            "-";


        const dataCriacao =
            relatorio.dataCriacao
                ? formatarData(
                    relatorio.dataCriacao
                )
                : "-";


        /*
         * Os botões só aparecem enquanto o relatório
         * ainda estiver aguardando avaliação.
         *
         * NULL representa relatório pendente.
         */

        const aguardandoAvaliacao =
            relatorio.status === null ||
            relatorio.status === undefined ||
            relatorio.status === "";


        let botoesAvaliacao = "";


        if (aguardandoAvaliacao) {

            botoesAvaliacao = `

                <div class="acoes-relatorio">

                    <button
                        type="button"
                        class="btn-aprovar-relatorio"
                        data-id="${relatorio.id}">

                        <i class="bi bi-check-circle"></i>

                        Aprovar

                    </button>

                    <button
                        type="button"
                        class="btn-reprovar-relatorio"
                        data-id="${relatorio.id}">

                        <i class="bi bi-x-circle"></i>

                        Reprovar

                    </button>

                </div>

            `;
        }


        card.innerHTML = `

            <div class="cabecalho-relatorio">

                <div class="identificacao-relatorio">

                    <div class="icone-relatorio">

                        <i class="bi bi-file-medical"></i>

                    </div>


                    <div class="informacoes-relatorio">

                        <h3>
                            ${escaparHtml(nomeAluno)}
                        </h3>


                        <p>
                            <strong>E-mail:</strong>
                            ${escaparHtml(emailAluno)}
                        </p>


                        <p>
                            <strong>Consulta:</strong>
                            #${relatorio.consultaId}
                        </p>


                        <p>
                            <strong>Data de criação:</strong>
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


            ${botoesAvaliacao}

        `;


        listaRelatorios.appendChild(card);

    });


    /*
     * Adiciona os eventos aos botões depois que
     * os cards foram criados.
     */

    adicionarEventosAvaliacao();
}


/* =========================================================
   EVENTOS DOS BOTÕES
   ========================================================= */

function adicionarEventosAvaliacao() {

    const botoesAprovar =
        document.querySelectorAll(
            ".btn-aprovar-relatorio"
        );


    const botoesReprovar =
        document.querySelectorAll(
            ".btn-reprovar-relatorio"
        );


    botoesAprovar.forEach(botao => {

        botao.addEventListener(
            "click",
            async () => {

                const id =
                    botao.dataset.id;


                await atualizarStatusRelatorio(
                    id,
                    "aprovado",
                    botao
                );

            }
        );

    });


    botoesReprovar.forEach(botao => {

        botao.addEventListener(
            "click",
            async () => {

                const id =
                    botao.dataset.id;


                await atualizarStatusRelatorio(
                    id,
                    "reprovado",
                    botao
                );

            }
        );

    });
}


/* =========================================================
   APROVAR / REPROVAR
   ========================================================= */

async function atualizarStatusRelatorio(
    id,
    status,
    botao
) {

    if (!id) {

        return;
    }


    /*
     * Desabilita os dois botões do mesmo card
     * para impedir dois cliques simultâneos.
     */

    const card =
        botao.closest(
            ".card-relatorio-aluno"
        );


    if (card) {

        const botoes =
            card.querySelectorAll(
                "button"
            );


        botoes.forEach(botaoCard => {

            botaoCard.disabled = true;

        });
    }


    try {

        const resposta =
            await fetch(
                `/relatorios/${id}/status?status=${status}`,
                {
                    method: "PUT"
                }
            );


        if (!resposta.ok) {

            const mensagem =
                await resposta.text();


            throw new Error(
                mensagem ||
                "Não foi possível atualizar o relatório."
            );
        }


        /*
         * Atualização concluída.
         * Recarrega a lista para mostrar imediatamente
         * o novo status.
         */

        await carregarRelatorios();


    } catch (erro) {

        console.error(erro);


        mensagemErro.textContent =
            erro.message ||
            "Não foi possível atualizar o relatório.";


        mensagemErro.style.display =
            "block";


        /*
         * Caso tenha ocorrido erro, libera
         * novamente os botões.
         */

        if (card) {

            const botoes =
                card.querySelectorAll(
                    "button"
                );


            botoes.forEach(botaoCard => {

                botaoCard.disabled = false;

            });
        }
    }
}


/* =========================================================
   FILTROS
   ========================================================= */

if (btnFiltrar) {

    btnFiltrar.addEventListener(
        "click",
        carregarRelatorios
    );
}


if (btnLimpar) {

    btnLimpar.addEventListener(
        "click",
        () => {

            if (filtroEmail) {

                filtroEmail.value = "";
            }


            if (filtroStatus) {

                filtroStatus.value = "";
            }


            carregarRelatorios();

        }
    );
}


/*
 * Permite usar o filtro de e-mail pressionando Enter.
 */

if (filtroEmail) {

    filtroEmail.addEventListener(
        "keydown",
        evento => {

            if (evento.key === "Enter") {

                carregarRelatorios();
            }

        }
    );
}


/* =========================================================
   STATUS
   ========================================================= */

function obterStatusRelatorio(status) {

    /*
     * NULL = aguardando avaliação
     */

    if (
        status === null ||
        status === undefined ||
        status === ""
    ) {

        return {

            texto: "Aguardando avaliação",

            classe: "status-pendente"

        };
    }


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


/* =========================================================
   FORMATAR DATA
   ========================================================= */

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


/* =========================================================
   ESCAPAR HTML
   ========================================================= */

function escaparHtml(texto) {

    const div =
        document.createElement("div");


    div.textContent =
        texto;


    return div.innerHTML;
}


/* =========================================================
   INICIALIZAÇÃO
   ========================================================= */

carregarRelatorios();