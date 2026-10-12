
const listaRelatorios = document.getElementById("listaRelatorios");
const mensagemVazia = document.getElementById("mensagemVazia");
const mensagemErro = document.getElementById("mensagemErro");
const mensagemCarregamento =
    document.getElementById("mensagemCarregamento");

const contadorRelatorios =
    document.getElementById("contadorRelatorios");

const filtroEspecialidade =
    document.getElementById("filtroEspecialidade");

const filtroEmail = document.getElementById("filtroEmail");
const filtroStatus = document.getElementById("filtroStatus");

const btnFiltrar = document.getElementById("btnFiltrar");
const btnLimpar = document.getElementById("btnLimpar");


async function carregarEspecialidades() {
    filtroEspecialidade.disabled = true;
    mensagemErro.style.display = "none";

    try {
        // Carrega todas as especialidades cadastradas no banco.
        const resposta = await fetch("/especialidades");

        if (!resposta.ok) {
            throw new Error(
                "Não foi possível carregar as especialidades."
            );
        }

        const especialidades = await resposta.json();

        filtroEspecialidade.innerHTML = "";

        if (!especialidades.length) {
            filtroEspecialidade.add(
                new Option("Nenhuma especialidade cadastrada", "")
            );

            throw new Error(
                "Não existem especialidades cadastradas."
            );
        }

        especialidades.forEach(especialidade => {
            filtroEspecialidade.add(
                new Option(
                    especialidade.nome,
                    String(especialidade.id)
                )
            );
        });

        // Recupera a especialidade vinculada ao cadastro do professor.
        const respostaPadrao = await fetch(
            "/relatorios/professor/especialidades"
        );

        if (!respostaPadrao.ok) {
            throw new Error(
                "Não foi possível identificar a especialidade do professor."
            );
        }

        const especialidadesProfessor =
            await respostaPadrao.json();

        if (!especialidadesProfessor.length) {
            throw new Error(
                "O professor não possui especialidade vinculada ao cadastro."
            );
        }

        // O cadastro atual vincula a especialidade escolhida no registro.
        const idEspecialidadePadrao =
            String(especialidadesProfessor[0].id);

        const existeNoSelect = Array.from(
            filtroEspecialidade.options
        ).some(opcao => opcao.value === idEspecialidadePadrao);

        if (!existeNoSelect) {
            throw new Error(
                "A especialidade cadastrada não foi encontrada na lista."
            );
        }

        filtroEspecialidade.value = idEspecialidadePadrao;
        filtroEspecialidade.disabled = false;

        // Carrega os relatórios já com a especialidade padrão selecionada.
        await carregarRelatorios();

    } catch (erro) {
        console.error(erro);
        filtroEspecialidade.disabled = false;
        exibirErro(erro.message);
    }
}


async function carregarRelatorios() {
    const especialidadeId = filtroEspecialidade.value;

    if (!especialidadeId) {
        listaRelatorios.innerHTML = "";
        contadorRelatorios.textContent = "0 relatórios";
        mensagemVazia.style.display = "none";
        mensagemCarregamento.style.display = "none";
        return;
    }

    mensagemErro.style.display = "none";
    mensagemVazia.style.display = "none";
    mensagemCarregamento.style.display = "block";
    listaRelatorios.innerHTML = "";

    try {
        const parametros = new URLSearchParams({
            especialidadeId: especialidadeId
        });

        const email = filtroEmail.value.trim();
        const status = filtroStatus.value;

        if (email) {
            parametros.append("email", email);
        }

        if (status) {
            parametros.append("status", status);
        }

        const resposta = await fetch(
            `/relatorios/professor?${parametros.toString()}`
        );

        if (!resposta.ok) {
            throw new Error(
                await resposta.text() ||
                "Não foi possível carregar os relatórios."
            );
        }

        const relatorios = await resposta.json();
        renderizarRelatorios(relatorios);

    } catch (erro) {
        console.error(erro);

        listaRelatorios.innerHTML = "";
        contadorRelatorios.textContent = "0 relatórios";
        mensagemVazia.style.display = "none";

        exibirErro(erro.message);

    } finally {
        mensagemCarregamento.style.display = "none";
    }
}


function renderizarRelatorios(relatorios) {
    listaRelatorios.innerHTML = "";

    contadorRelatorios.textContent =
        `${relatorios.length} ${
            relatorios.length === 1 ? "relatório" : "relatórios"
        }`;

    if (!relatorios.length) {
        mensagemVazia.style.display = "block";
        return;
    }

    mensagemVazia.style.display = "none";

    relatorios.forEach(relatorio => {
        const card = document.createElement("article");
        card.className = "card-relatorio-aluno";

        const status = obterStatusRelatorio(relatorio.status);
        const pendente = !relatorio.status;

        const anexo = relatorio.anexoExame
            ? `
                <p>
                    <strong>Anexo:</strong>
                    <a href="/relatorios/${Number(relatorio.id)}/anexo">
                        Baixar exame/documento
                    </a>
                </p>
            `
            : `
                <p>
                    <strong>Anexo:</strong>
                    Nenhum arquivo enviado
                </p>
            `;

        const observacao = relatorio.observacao
            ? `
                <div class="observacao-relatorio">
                    <strong>Observação da avaliação:</strong>
                    <p>${escaparHtml(relatorio.observacao)}</p>
                </div>
            `
            : "";

        card.innerHTML = `
            <div class="cabecalho-relatorio">
                <div class="identificacao-relatorio">
                    <div class="icone-relatorio">
                        <i class="bi bi-file-medical"></i>
                    </div>

                    <div class="informacoes-relatorio">
                        <h3>
                            ${escaparHtml(
                                relatorio.nomeAluno ||
                                "Aluno não identificado"
                            )}
                        </h3>

                        <p>
                            <strong>E-mail:</strong>
                            ${escaparHtml(relatorio.emailAluno || "-")}
                        </p>

                        <p>
                            <strong>Consulta:</strong>
                            #${Number(relatorio.consultaId)}
                        </p>

                        <p>
                            <strong>Data de criação:</strong>
                            ${formatarData(relatorio.dataCriacao)}
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
                    ${escaparHtml(relatorio.descricao || "-")}
                </div>

                <br>

                ${anexo}
                ${observacao}
            </div>

            ${pendente ? `
                <div class="acoes-relatorio">
                    <button
                        type="button"
                        class="btn-aprovar-relatorio"
                        data-id="${Number(relatorio.id)}">

                        <i class="bi bi-check-circle"></i>
                        Aprovar
                    </button>

                    <button
                        type="button"
                        class="btn-reprovar-relatorio"
                        data-id="${Number(relatorio.id)}">

                        <i class="bi bi-x-circle"></i>
                        Reprovar
                    </button>
                </div>
            ` : ""}
        `;

        listaRelatorios.appendChild(card);
    });

    adicionarEventosAvaliacao();
}


function adicionarEventosAvaliacao() {
    document
        .querySelectorAll(".btn-aprovar-relatorio")
        .forEach(botao => {
            botao.addEventListener("click", () => {
                if (!confirm("Deseja aprovar este relatório?")) {
                    return;
                }

                atualizarStatusRelatorio(
                    botao.dataset.id,
                    "aprovado",
                    "",
                    botao
                );
            });
        });

    document
        .querySelectorAll(".btn-reprovar-relatorio")
        .forEach(botao => {
            botao.addEventListener("click", () => {
                const observacao = prompt(
                    "Informe a observação que explica a reprovação:"
                );

                if (observacao === null) {
                    return;
                }

                if (!observacao.trim()) {
                    alert(
                        "A observação é obrigatória para reprovar o relatório."
                    );
                    return;
                }

                atualizarStatusRelatorio(
                    botao.dataset.id,
                    "reprovado",
                    observacao.trim(),
                    botao
                );
            });
        });
}


async function atualizarStatusRelatorio(
    id,
    status,
    observacao,
    botao
) {
    const card = botao.closest(".card-relatorio-aluno");

    card.querySelectorAll("button").forEach(button => {
        button.disabled = true;
    });

    try {
        const parametros = new URLSearchParams({ status });

        if (status === "reprovado") {
            parametros.append("observacao", observacao);
        }

        const resposta = await fetch(
            `/relatorios/${encodeURIComponent(id)}/status?${parametros}`,
            { method: "PUT" }
        );

        const mensagem = await resposta.text();

        if (!resposta.ok) {
            throw new Error(
                mensagem || "Não foi possível avaliar o relatório."
            );
        }

        await carregarRelatorios();

    } catch (erro) {
        console.error(erro);
        exibirErro(erro.message);

        card.querySelectorAll("button").forEach(button => {
            button.disabled = false;
        });
    }
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


function formatarData(data) {
    if (!data) {
        return "-";
    }

    const partes = data.substring(0, 10).split("-");

    if (partes.length !== 3) {
        return data.substring(0, 10);
    }

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}


function escaparHtml(texto) {
    const elemento = document.createElement("div");
    elemento.textContent = String(texto);
    return elemento.innerHTML;
}


function exibirErro(mensagem) {
    mensagemErro.textContent = mensagem;
    mensagemErro.style.display = "block";
}


btnFiltrar.addEventListener("click", carregarRelatorios);

btnLimpar.addEventListener("click", () => {
    filtroEmail.value = "";
    filtroStatus.value = "";

    // Limpa somente e-mail e status; mantém a especialidade escolhida.
    carregarRelatorios();
});



carregarEspecialidades();
