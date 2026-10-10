const listaRelatorios = document.getElementById("listaRelatorios");
const mensagemVazia = document.getElementById("mensagemVazia");
const mensagemErro = document.getElementById("mensagemErro");
const contadorRelatorios = document.getElementById("contadorRelatorios");
const filtroStatus = document.getElementById("filtroStatus");
const btnFiltrarStatus = document.getElementById("btnFiltrarStatus");
const btnLimparStatus = document.getElementById("btnLimparStatus");

const formatosAnexo =
    ".pdf,.png,.jpg,.jpeg,.gif,.bmp,.tif,.tiff,.webp,.dcm,.dicom,.zip,.7z,.rar,.doc,.docx,.odt,.xls,.xlsx,.ods,.txt,.csv";

const tamanhoMaximoAnexo = 20 * 1024 * 1024;

async function carregarRelatorios() {
    mensagemErro.style.display = "none";

    try {
        const parametros = new URLSearchParams();
        const status = filtroStatus?.value || "";

        if (status) parametros.append("status", status);

        const query = parametros.toString();
        const url = query
            ? `/relatorios/aluno?${query}`
            : "/relatorios/aluno";

        const resposta = await fetch(url);

        if (!resposta.ok) {
            throw new Error("Não foi possível carregar seus relatórios.");
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

        const linkAnexo = relatorio.anexoExame
            ? `<p><strong>Anexo:</strong>
                   <a href="/relatorios/${relatorio.id}/anexo">
                       Baixar exame/documento
                   </a>
               </p>`
            : `<p><strong>Anexo:</strong> Nenhum arquivo enviado</p>`;

        const observacao = relatorio.observacao
            ? `<div class="observacao-relatorio">
                   <strong>Observação do professor:</strong>
                   <p>${escaparHtml(relatorio.observacao)}</p>
               </div>`
            : "";

        const botaoEditar = relatorio.status === "reprovado"
            ? `<div class="acoes-relatorio-aluno">
                   <button type="button"
                       class="btn-editar-relatorio"
                       onclick="abrirEdicaoRelatorio(${relatorio.id}, this)">
                       <i class="bi bi-pencil"></i> Editar relatório
                   </button>
               </div>`
            : "";

        card.innerHTML = `
            <div class="cabecalho-relatorio">
                <div class="identificacao-relatorio">
                    <div class="icone-relatorio">
                        <i class="bi bi-file-medical"></i>
                    </div>

                    <div class="informacoes-relatorio">
                        <h3>Consulta #${relatorio.consultaId}</h3>
                        <p><strong>Data:</strong> ${formatarData(relatorio.dataCriacao)}</p>
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

            ${botaoEditar}
        `;

        listaRelatorios.appendChild(card);
    });
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

function abrirEdicaoRelatorio(idRelatorio, botao) {
    const card = botao.closest(".card-relatorio-aluno");

    if (card.querySelector(".formulario-edicao-relatorio")) return;

    const descricaoAtual =
        card.querySelector(".descricao-relatorio-aluno").textContent.trim();

    const formulario = document.createElement("div");
    formulario.className = "formulario-edicao-relatorio";

    formulario.innerHTML = `
        <div class="cabecalho-edicao-relatorio">
            <h4><i class="bi bi-pencil-square"></i> Editar relatório</h4>
            <p>Corrija o relatório de acordo com a observação do professor.</p>
        </div>

        <div class="campo-edicao-relatorio">
            <label for="descricaoEdicao-${idRelatorio}">
                Descrição do relatório
            </label>

            <textarea id="descricaoEdicao-${idRelatorio}"
                class="descricao-edicao-relatorio"
                maxlength="5000"
                rows="6"></textarea>

            <div class="rodape-edicao-relatorio">
                <span class="contador-edicao-relatorio">0 / 5000</span>
            </div>
        </div>

        <div class="campo-edicao-relatorio">
            <label for="anexoEdicao-${idRelatorio}">
                Substituir ou adicionar exame (opcional)
            </label>

            <input type="file"
                id="anexoEdicao-${idRelatorio}"
                class="anexo-edicao-relatorio"
                accept="${formatosAnexo}">

            <small>
                Se não selecionar outro arquivo, o anexo atual será mantido.
                Limite: 20 MB.
            </small>
        </div>

        <div class="mensagem-edicao-relatorio" style="display:none;"></div>

        <div class="acoes-edicao-relatorio">
            <button type="button"
                class="btn-cancelar-edicao"
                onclick="fecharEdicaoRelatorio(this)">
                Cancelar
            </button>

            <button type="button"
                class="btn-salvar-edicao"
                onclick="salvarEdicaoRelatorio(${idRelatorio}, this)">
                <i class="bi bi-check2"></i> Salvar alterações
            </button>
        </div>
    `;

    card.appendChild(formulario);

    const textarea = formulario.querySelector(".descricao-edicao-relatorio");
    const contador = formulario.querySelector(".contador-edicao-relatorio");
    const campoArquivo = formulario.querySelector(".anexo-edicao-relatorio");

    textarea.value = descricaoAtual === "-" ? "" : descricaoAtual;
    contador.textContent = `${textarea.value.length} / 5000`;

    textarea.addEventListener("input", () => {
        contador.textContent = `${textarea.value.length} / 5000`;
    });

    campoArquivo.addEventListener("change", () => {
        const arquivo = campoArquivo.files[0];

        if (arquivo && arquivo.size > tamanhoMaximoAnexo) {
            campoArquivo.value = "";
            mostrarMensagemEdicao(
                formulario.querySelector(".mensagem-edicao-relatorio"),
                "O arquivo excede o limite de 20 MB.",
                "erro"
            );
        }
    });

    textarea.focus();
    formulario.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

function fecharEdicaoRelatorio(botao) {
    botao.closest(".formulario-edicao-relatorio")?.remove();
}

async function salvarEdicaoRelatorio(idRelatorio, botao) {
    const formulario = botao.closest(".formulario-edicao-relatorio");
    const textarea = formulario.querySelector(".descricao-edicao-relatorio");
    const campoArquivo = formulario.querySelector(".anexo-edicao-relatorio");
    const mensagem = formulario.querySelector(".mensagem-edicao-relatorio");
    const descricao = textarea.value.trim();
    const arquivo = campoArquivo.files[0];

    if (!descricao) {
        mostrarMensagemEdicao(mensagem, "Informe a descrição do relatório.", "erro");
        textarea.focus();
        return;
    }

    if (arquivo && arquivo.size > tamanhoMaximoAnexo) {
        mostrarMensagemEdicao(mensagem, "O arquivo excede o limite de 20 MB.", "erro");
        return;
    }

    const dados = new FormData();
    dados.append("descricao", descricao);

    if (arquivo) {
        dados.append("anexo", arquivo);
    }

    botao.disabled = true;
    botao.textContent = "Salvando...";

    try {
        const resposta = await fetch(`/relatorios/${idRelatorio}`, {
            method: "PUT",
            body: dados
        });

        const texto = await resposta.text();

        if (!resposta.ok) {
            throw new Error(texto || "Não foi possível atualizar o relatório.");
        }

        mostrarMensagemEdicao(mensagem, texto, "sucesso");
        setTimeout(carregarRelatorios, 1000);
    } catch (erro) {
        console.error(erro);
        mostrarMensagemEdicao(
            mensagem,
            erro.message || "Não foi possível conectar ao servidor.",
            "erro"
        );

        botao.disabled = false;
        botao.innerHTML = '<i class="bi bi-check2"></i> Salvar alterações';
    }
}

function mostrarMensagemEdicao(elemento, texto, tipo) {
    elemento.textContent = texto;
    elemento.className = "mensagem-edicao-relatorio";
    elemento.classList.add(
        tipo === "sucesso" ? "mensagem-sucesso" : "mensagem-erro"
    );
    elemento.style.display = "block";
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

btnFiltrarStatus?.addEventListener("click", carregarRelatorios);

btnLimparStatus?.addEventListener("click", () => {
    if (filtroStatus) filtroStatus.value = "";
    carregarRelatorios();
});

carregarRelatorios();