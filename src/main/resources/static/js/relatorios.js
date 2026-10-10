const listaRelatorios = document.getElementById("listaRelatorios");
const mensagemVazia = document.getElementById("mensagemVazia");
const mensagemErro = document.getElementById("mensagemErro");
const contadorRelatorios = document.getElementById("contadorRelatorios");
const filtroEmail = document.getElementById("filtroEmail");
const filtroStatus = document.getElementById("filtroStatus");
const btnFiltrar = document.getElementById("btnFiltrar");
const btnLimpar = document.getElementById("btnLimpar");

async function carregarRelatorios() {
    mensagemErro.style.display = "none";

    try {
        const parametros = new URLSearchParams();
        const email = filtroEmail?.value.trim() || "";
        const status = filtroStatus?.value || "";

        if (email) parametros.append("email", email);
        if (status) parametros.append("status", status);

        const query = parametros.toString();
        const url = query
            ? `/relatorios/coordenador?${query}`
            : "/relatorios/coordenador";

        const resposta = await fetch(url);

        if (!resposta.ok) {
            throw new Error("Não foi possível carregar os relatórios.");
        }

        renderizarRelatorios(await resposta.json());
    } catch (erro) {
        console.error(erro);
        listaRelatorios.innerHTML = "";
        contadorRelatorios.textContent = "0 relatórios";
        mensagemVazia.style.display = "none";
        mensagemErro.textContent = erro.message;
        mensagemErro.style.display = "block";
    }
}

function renderizarRelatorios(relatorios) {
    listaRelatorios.innerHTML = "";

    if (!relatorios || relatorios.length === 0) {
        contadorRelatorios.textContent = "0 relatórios";
        mensagemVazia.style.display = "block";
        return;
    }

    mensagemVazia.style.display = "none";
    contadorRelatorios.textContent =
        `${relatorios.length} ${relatorios.length === 1 ? "relatório" : "relatórios"}`;

    relatorios.forEach(relatorio => {
        const card = document.createElement("article");
        card.className = "card-relatorio-aluno";

        const status = obterStatusRelatorio(relatorio.status);
        const pendente = !relatorio.status;

        const linkAnexo = relatorio.anexoExame
            ? `<p><strong>Anexo:</strong>
                 <a href="/relatorios/${relatorio.id}/anexo">
                    Baixar exame/documento
                 </a>
               </p>`
            : `<p><strong>Anexo:</strong> Nenhum arquivo enviado</p>`;

        const observacao = relatorio.observacao
            ? `<div class="observacao-relatorio">
                   <strong>Observação da avaliação:</strong>
                   <p>${escaparHtml(relatorio.observacao)}</p>
               </div>`
            : "";

        card.innerHTML = `
            <div class="cabecalho-relatorio">
                <div class="identificacao-relatorio">
                    <div class="icone-relatorio">
                        <i class="bi bi-file-medical"></i>
                    </div>

                    <div class="informacoes-relatorio">
                        <h3>${escaparHtml(relatorio.nomeAluno || "Aluno não identificado")}</h3>
                        <p><strong>E-mail:</strong> ${escaparHtml(relatorio.emailAluno || "-")}</p>
                        <p><strong>Consulta:</strong> #${relatorio.consultaId}</p>
                        <p><strong>Data de criação:</strong> ${formatarData(relatorio.dataCriacao)}</p>
                    </div>
                </div>

                <span class="status-relatorio ${status.classe}">
                    ${status.texto}
                </span>
            </div>

            <div class="conteudo-relatorio">
                <div class="titulo-descricao">Descrição do relatório</div>
                <div class="descricao-relatorio-aluno">
                    ${escaparHtml(relatorio.descricao || "-")}
                </div><br>
                ${linkAnexo}
                ${observacao}
            </div>

            ${pendente ? `
                <div class="acoes-relatorio">
                    <button type="button"
                        class="btn-aprovar-relatorio"
                        data-id="${relatorio.id}">
                        <i class="bi bi-check-circle"></i> Aprovar
                    </button>

                    <button type="button"
                        class="btn-reprovar-relatorio"
                        data-id="${relatorio.id}">
                        <i class="bi bi-x-circle"></i> Reprovar
                    </button>
                </div>
            ` : ""}
        `;

        listaRelatorios.appendChild(card);
    });

    adicionarEventosAvaliacao();
}

function adicionarEventosAvaliacao() {
    document.querySelectorAll(".btn-aprovar-relatorio").forEach(botao => {
        botao.addEventListener("click", () => {
            atualizarStatusRelatorio(botao.dataset.id, "aprovado", "", botao);
        });
    });

    document.querySelectorAll(".btn-reprovar-relatorio").forEach(botao => {
        botao.addEventListener("click", () => {
            const observacao = prompt(
                "Informe a observação que explica por que o relatório foi reprovado:"
            );

            if (observacao === null) return;

            if (!observacao.trim()) {
                alert("A observação é obrigatória para reprovar o relatório.");
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

async function atualizarStatusRelatorio(id, status, observacao, botao) {
    const card = botao.closest(".card-relatorio-aluno");

    card.querySelectorAll("button").forEach(b => b.disabled = true);

    try {
        const parametros = new URLSearchParams({ status });

        if (status === "reprovado") {
            parametros.append("observacao", observacao);
        }

        const resposta = await fetch(
            `/relatorios/${id}/status?${parametros.toString()}`,
            { method: "PUT" }
        );

        const texto = await resposta.text();

        if (!resposta.ok) {
            throw new Error(texto || "Não foi possível atualizar o relatório.");
        }

        await carregarRelatorios();
    } catch (erro) {
        console.error(erro);
        mensagemErro.textContent = erro.message;
        mensagemErro.style.display = "block";
        card.querySelectorAll("button").forEach(b => b.disabled = false);
    }
}

function obterStatusRelatorio(status) {
    if (status === "aprovado") {
        return { texto: "Aprovado", classe: "status-aprovado" };
    }

    if (status === "reprovado") {
        return { texto: "Reprovado", classe: "status-reprovado" };
    }

    return { texto: "Aguardando avaliação", classe: "status-pendente" };
}

function formatarData(data) {
    if (!data) return "-";

    const partes = data.substring(0, 10).split("-");
    if (partes.length !== 3) return data.substring(0, 10);

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

function escaparHtml(texto) {
    const div = document.createElement("div");
    div.textContent = texto;
    return div.innerHTML;
}

btnFiltrar?.addEventListener("click", carregarRelatorios);

btnLimpar?.addEventListener("click", () => {
    if (filtroEmail) filtroEmail.value = "";
    if (filtroStatus) filtroStatus.value = "";
    carregarRelatorios();
});

filtroEmail?.addEventListener("keydown", evento => {
    if (evento.key === "Enter") carregarRelatorios();
});

carregarRelatorios();