const listaConsultas = document.getElementById("listaConsultas");
const mensagemVazia = document.getElementById("mensagemVazia");
const mensagemErro = document.getElementById("mensagemErro");
const contadorConsultas = document.getElementById("contadorConsultas");
const filtroDataConsultas = document.getElementById("filtroDataConsultas");

const formatosAnexo =
    ".pdf,.png,.jpg,.jpeg,.gif,.bmp,.tif,.tiff,.webp,.dcm,.dicom,.zip,.7z,.rar,.doc,.docx,.odt,.xls,.xlsx,.ods,.txt,.csv";

const tamanhoMaximoAnexo = 20 * 1024 * 1024;

function obterDataLocalHoje() {
    const hoje = new Date();
    const ano = hoje.getFullYear();
    const mes = String(hoje.getMonth() + 1).padStart(2, "0");
    const dia = String(hoje.getDate()).padStart(2, "0");
    return `${ano}-${mes}-${dia}`;
}

async function carregarConsultas() {
    mensagemErro.style.display = "none";
    mensagemVazia.style.display = "none";

    try {
        const dataSelecionada = filtroDataConsultas.value;
        const url = `/consultas/aluno/agendadas?data=${encodeURIComponent(dataSelecionada)}`;
        const resposta = await fetch(url);

        if (!resposta.ok) {
            throw new Error("Não foi possível carregar as consultas.");
        }

        const consultas = await resposta.json();
        renderizarConsultas(consultas);
    } catch (erro) {
        console.error(erro);
        listaConsultas.innerHTML = "";
        contadorConsultas.textContent = "0 consultas";
        mensagemErro.textContent = "Não foi possível carregar suas consultas.";
        mensagemErro.style.display = "block";
    }
}

function renderizarConsultas(consultas) {
    listaConsultas.innerHTML = "";

    contadorConsultas.textContent =
        `${consultas.length} ${consultas.length === 1 ? "consulta" : "consultas"}`;

    if (!consultas.length) {
        mensagemVazia.style.display = "block";
        return;
    }

    mensagemVazia.style.display = "none";

    consultas.forEach(consulta => {
        const card = document.createElement("article");
        card.classList.add("card-consulta-aluno");

        card.innerHTML = `
            <div class="dados-consulta">
                <div class="icone-consulta">
                    <i class="bi bi-calendar-event"></i>
                </div>

                <div class="informacoes-consulta">
                    <h3>${escaparHtml(consulta.nomePaciente || "-")}</h3>
                    <p><strong>Data:</strong> ${formatarData(consulta.dataConsulta)}</p>
                    <p><strong>Horário:</strong> ${formatarHorario(consulta.dataConsulta)}</p>
                    <p><strong>Motivo:</strong> ${escaparHtml(consulta.motivo || "-")}</p>

                    ${consulta.diagnostico ? `
                        <p><strong>Diagnóstico:</strong>
                        ${escaparHtml(consulta.diagnostico)}</p>
                    ` : ""}
                </div>
            </div>

            <div class="acoes-consulta-aluno">
                <button
                    type="button"
                    class="btn-relatorio"
                    onclick="abrirFormularioRelatorio(${consulta.id}, this)">
                    <i class="bi bi-file-medical"></i>
                    Cadastrar relatório
                </button>
            </div>
        `;

        listaConsultas.appendChild(card);
    });
}

function abrirFormularioRelatorio(idConsulta, botao) {
    const card = botao.closest(".card-consulta-aluno");

    if (card.querySelector(".formulario-relatorio")) {
        return;
    }

    const formulario = document.createElement("div");
    formulario.classList.add("formulario-relatorio");

    formulario.innerHTML = `
        <div class="cabecalho-formulario-relatorio">
            <div>
                <h4><i class="bi bi-file-medical"></i> Relatório da consulta</h4>
                <p>Descreva as informações referentes à consulta realizada.</p>
            </div>
        </div>

        <div class="campo-relatorio">
            <label for="descricaoRelatorio-${idConsulta}">
                Descrição do relatório
            </label>

            <textarea
                id="descricaoRelatorio-${idConsulta}"
                class="descricao-relatorio"
                maxlength="5000"
                rows="6"
                placeholder="Digite aqui a descrição do relatório..."
            ></textarea>

            <div class="rodape-campo-relatorio">
                <span class="contador-caracteres">0 / 5000</span>
            </div>
        </div>

        <div class="campo-relatorio">
            <label for="anexoExame-${idConsulta}">
                Anexar exames ou documentos (opcional)
            </label>

            <input
                type="file"
                id="anexoExame-${idConsulta}"
                class="anexo-exame-relatorio"
                accept="${formatosAnexo}"
            >

            <small>
                PDF, imagens, DICOM, ZIP e documentos. Tamanho máximo: 20 MB.
            </small>
        </div>

        <div class="mensagem-formulario-relatorio" style="display:none;"></div>

        <div class="acoes-formulario-relatorio">
            <button
                type="button"
                class="btn-cancelar-relatorio"
                onclick="fecharFormularioRelatorio(this)">
                Cancelar
            </button>

            <button
                type="button"
                class="btn-enviar-relatorio"
                onclick="enviarRelatorio(${idConsulta}, this)">
                <i class="bi bi-send"></i>
                Enviar relatório
            </button>
        </div>
    `;

    card.appendChild(formulario);

    const textarea = formulario.querySelector(".descricao-relatorio");
    const contador = formulario.querySelector(".contador-caracteres");

    textarea.addEventListener("input", () => {
        contador.textContent = `${textarea.value.length} / 5000`;
    });

    formulario.querySelector(".anexo-exame-relatorio")
        .addEventListener("change", evento => {
            const arquivo = evento.target.files[0];

            if (arquivo && arquivo.size > tamanhoMaximoAnexo) {
                evento.target.value = "";
                mostrarMensagemRelatorio(
                    formulario.querySelector(".mensagem-formulario-relatorio"),
                    "O arquivo excede o limite de 20 MB.",
                    "erro"
                );
            }
        });

    textarea.focus();
}

function fecharFormularioRelatorio(botao) {
    const formulario = botao.closest(".formulario-relatorio");
    if (formulario) {
        formulario.remove();
    }
}

async function enviarRelatorio(idConsulta, botao) {
    const formulario = botao.closest(".formulario-relatorio");
    const textarea = formulario.querySelector(".descricao-relatorio");
    const campoArquivo = formulario.querySelector(".anexo-exame-relatorio");
    const mensagem = formulario.querySelector(".mensagem-formulario-relatorio");
    const descricao = textarea.value.trim();
    const arquivo = campoArquivo.files[0];

    if (!descricao) {
        mostrarMensagemRelatorio(
            mensagem,
            "Informe a descrição do relatório.",
            "erro"
        );
        textarea.focus();
        return;
    }

    if (arquivo && arquivo.size > tamanhoMaximoAnexo) {
        mostrarMensagemRelatorio(
            mensagem,
            "O arquivo excede o limite de 20 MB.",
            "erro"
        );
        return;
    }

    const dados = new FormData();
    dados.append("consultaId", idConsulta);
    dados.append("descricao", descricao);

    if (arquivo) {
        dados.append("anexo", arquivo);
    }

    botao.disabled = true;
    botao.textContent = "Enviando...";

    try {
        const resposta = await fetch("/relatorios", {
            method: "POST",
            body: dados
        });

        const texto = await resposta.text();

        if (!resposta.ok) {
            throw new Error(texto || "Não foi possível cadastrar o relatório.");
        }

        mostrarMensagemRelatorio(mensagem, texto, "sucesso");
        textarea.disabled = true;
        campoArquivo.disabled = true;
        botao.disabled = true;
        botao.textContent = "Relatório enviado";

        formulario.querySelector(".btn-cancelar-relatorio").disabled = true;

        setTimeout(carregarConsultas, 1200);
    } catch (erro) {
        console.error(erro);
        mostrarMensagemRelatorio(
            mensagem,
            erro.message || "Não foi possível conectar ao servidor.",
            "erro"
        );

        botao.disabled = false;
        botao.innerHTML = '<i class="bi bi-send"></i> Enviar relatório';
    }
}

function mostrarMensagemRelatorio(elemento, texto, tipo) {
    elemento.textContent = texto;
    elemento.className = "mensagem-formulario-relatorio";
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

function formatarHorario(data) {
    return data ? data.substring(11, 16) : "-";
}

function escaparHtml(texto) {
    const div = document.createElement("div");
    div.textContent = texto;
    return div.innerHTML;
}

filtroDataConsultas.value = obterDataLocalHoje();
filtroDataConsultas.addEventListener("change", carregarConsultas);
carregarConsultas();