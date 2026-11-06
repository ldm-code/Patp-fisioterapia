const listaConsultas = document.getElementById("listaConsultas");
const mensagemVazia = document.getElementById("mensagemVazia");
const contadorConsultas = document.getElementById("contadorConsultas");

const filtroEspecialidade =
    document.getElementById("filtroEspecialidade");
const filtroAluno = document.getElementById("filtroAluno");
const filtroPaciente = document.getElementById("filtroPaciente");
const filtroStatus = document.getElementById("filtroStatus");
const filtroData = document.getElementById("filtroData");
const btnExportarExcel = document.getElementById("btnExportarExcel");

let temporizadorFiltro;

async function carregarEspecialidades() {
    filtroEspecialidade.disabled = true;

    try {
        const resposta = await fetch("/especialidades");

        if (!resposta.ok) {
            throw new Error(
                await resposta.text() ||
                "Não foi possível carregar as especialidades."
            );
        }

        const especialidades = await resposta.json();
        filtroEspecialidade.innerHTML = "";

        if (!especialidades.length) {
            filtroEspecialidade.add(
                new Option("Nenhuma especialidade vinculada", "")
            );
            renderizarConsultas([]);
            return;
        }

        especialidades.forEach(especialidade => {
            filtroEspecialidade.add(
                new Option(especialidade.nome, String(especialidade.id))
            );
        });

        filtroEspecialidade.disabled = false;
        await carregarConsultas();
    } catch (erro) {
        console.error(erro);
        filtroEspecialidade.innerHTML =
            '<option value="">Erro ao carregar especialidades</option>';
        renderizarErro(erro.message);
    }
}

function montarParametros() {
    const parametros = new URLSearchParams();
    const especialidadeId = filtroEspecialidade.value;

    if (!especialidadeId) {
        return parametros;
    }

    parametros.set("especialidadeId", especialidadeId);

    const aluno = filtroAluno.value.trim();
    const paciente = filtroPaciente.value.trim();
    const status = filtroStatus.value;
    const data = filtroData.value;

    if (aluno) parametros.set("aluno", aluno);
    if (paciente) parametros.set("paciente", paciente);
    if (status) parametros.set("status", status);
    if (data) parametros.set("data", data);

    return parametros;
}

async function carregarConsultas() {
    if (!filtroEspecialidade.value) {
        renderizarConsultas([]);
        return;
    }

    listaConsultas.innerHTML = `
        <tr>
            <td colspan="7" class="text-center">Carregando consultas...</td>
        </tr>
    `;
    mensagemVazia.classList.add("d-none");

    try {
        const resposta = await fetch(
            `/consultas/professor?${montarParametros().toString()}`
        );

        if (!resposta.ok) {
            throw new Error(
                await resposta.text() ||
                "Não foi possível carregar as consultas."
            );
        }

        renderizarConsultas(await resposta.json());
    } catch (erro) {
        console.error(erro);
        renderizarErro(erro.message);
    }
}

function renderizarConsultas(consultas) {
    listaConsultas.innerHTML = "";

    contadorConsultas.textContent =
        `${consultas.length} ${
            consultas.length === 1 ? "consulta" : "consultas"
        }`;

    if (!consultas.length) {
        mensagemVazia.classList.remove("d-none");
        return;
    }

    mensagemVazia.classList.add("d-none");

    consultas.forEach(consulta => {
        const linha = document.createElement("tr");

        const motivoCancelamento =
            consulta.status === "cancelada"
                ? consulta.motivoCancelamento || "Não informado"
                : "-";

        linha.innerHTML = `
            <td>${escaparHtml(consulta.nomeAluno || "-")}</td>
            <td>${escaparHtml(consulta.nomePaciente || "-")}</td>
            <td>${formatarData(consulta.dataConsulta)}</td>
            <td>${formatarHorario(consulta.dataConsulta)}</td>
            <td class="motivo-consulta"
                style="white-space: normal; overflow-wrap: anywhere; min-width: 180px;"
                title="${escaparHtml(consulta.motivo || "")}">
                ${escaparHtml(consulta.motivo || "-")}
            </td>
            <td>${criarStatus(consulta.status)}</td>
            <td style="white-space: normal; overflow-wrap: anywhere; min-width: 180px;">
                ${escaparHtml(motivoCancelamento)}
            </td>
        `;

        listaConsultas.appendChild(linha);
    });
}

function criarStatus(status) {
    const statusNormalizado = (status || "").toLowerCase();

    const estados = {
        "agendada": ["status-agendada", "Agendada"],
        "concluida": ["status-concluida", "Concluída"],
        "cancelada": ["status-cancelada", "Cancelada"],
        "a validar": ["status-validar", "A validar"],
        "em aberto": ["status-aberta", "Em aberto"]
    };

    const [classe, texto] =
        estados[statusNormalizado] || ["status-aberta", status || "—"];

    return `
        <span class="status-badge ${classe}">
            ${escaparHtml(texto)}
        </span>
    `;
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
    const elemento = document.createElement("div");
    elemento.textContent = String(texto);
    return elemento.innerHTML;
}

function renderizarErro(mensagem) {
    listaConsultas.innerHTML = `
        <tr>
            <td colspan="7" class="text-center text-danger">
                ${escaparHtml(mensagem || "Não foi possível carregar as consultas.")}
            </td>
        </tr>
    `;
    contadorConsultas.textContent = "0 consultas";
    mensagemVazia.classList.add("d-none");
}

function agendarFiltro() {
    clearTimeout(temporizadorFiltro);
    temporizadorFiltro = setTimeout(carregarConsultas, 300);
}

filtroEspecialidade.addEventListener("change", carregarConsultas);
filtroAluno.addEventListener("input", agendarFiltro);
filtroPaciente.addEventListener("input", agendarFiltro);
filtroStatus.addEventListener("change", carregarConsultas);
filtroData.addEventListener("change", carregarConsultas);

btnExportarExcel.addEventListener("click", () => {
    if (!filtroEspecialidade.value) {
        alert("Selecione uma especialidade antes de exportar.");
        return;
    }

    window.location.href =
        `/consultas/professor/exportar-excel?${montarParametros().toString()}`;
});

carregarEspecialidades();