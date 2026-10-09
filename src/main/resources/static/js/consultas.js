const listaConsultas =
    document.getElementById("listaConsultas");

const mensagemVazia =
    document.getElementById("mensagemVazia");

const contadorConsultas =
    document.getElementById("contadorConsultas");


const filtroAluno =
    document.getElementById("filtroAluno");

const filtroPaciente =
    document.getElementById("filtroPaciente");

const filtroStatus =
    document.getElementById("filtroStatus");

const filtroData =
    document.getElementById("filtroData");


let consultas = [];


/* ==================================================
   CARREGAR CONSULTAS
   ================================================== */

async function carregarConsultas() {

    try {

        const resposta =
            await fetch("/consultas");


        if (!resposta.ok) {

            throw new Error(
                "Não foi possível carregar as consultas."
            );
        }


        consultas =
            await resposta.json();


        aplicarFiltros();


    } catch (erro) {

        console.error(erro);


        listaConsultas.innerHTML = `
            <tr>

                <td
                    colspan="7"
                    class="text-center text-danger"
                >
                    Não foi possível carregar as consultas.
                </td>

            </tr>
        `;
    }
}


/* ==================================================
   FILTROS
   ================================================== */

function aplicarFiltros() {

    const aluno =
        filtroAluno.value
            .trim()
            .toLowerCase();


    const paciente =
        filtroPaciente.value
            .trim()
            .toLowerCase();


    const status =
        filtroStatus.value;


    const data =
        filtroData.value;


    const resultado =
        consultas.filter(consulta => {

            const nomeAluno =
                (consulta.nomeAluno || "")
                    .toLowerCase();


            const nomePaciente =
                (consulta.nomePaciente || "")
                    .toLowerCase();


            const correspondeAluno =
                !aluno ||
                nomeAluno.includes(aluno);


            const correspondePaciente =
                !paciente ||
                nomePaciente.includes(paciente);


            const correspondeStatus =
                !status ||
                consulta.status === status;


            const correspondeData =
                !data ||
                (
                    consulta.dataConsulta &&
                    consulta.dataConsulta.startsWith(data)
                );


            return (
                correspondeAluno &&
                correspondePaciente &&
                correspondeStatus &&
                correspondeData
            );

        });


    renderizarConsultas(resultado);
}


/* ==================================================
   RENDERIZAR CONSULTAS
   ================================================== */

function renderizarConsultas(lista) {

    listaConsultas.innerHTML = "";


    contadorConsultas.textContent =
        `${lista.length} ${
            lista.length === 1
                ? "consulta"
                : "consultas"
        }`;


    if (lista.length === 0) {

        mensagemVazia.classList.remove("d-none");

        return;
    }


    mensagemVazia.classList.add("d-none");


    lista.forEach(consulta => {

        const linha =
            document.createElement("tr");


        const data =
            formatarData(
                consulta.dataConsulta
            );


        const horario =
            formatarHorario(
                consulta.dataConsulta
            );


        const status =
            criarStatus(
                consulta.status
            );


        const acoes =
            criarAcoes(
                consulta
            );


        linha.innerHTML = `

            <td>
                ${escaparHtml(
                    consulta.nomeAluno || "-"
                )}
            </td>


            <td>
                ${escaparHtml(
                    consulta.nomePaciente || "-"
                )}
            </td>


            <td>
                ${data}
            </td>


            <td>
                ${horario}
            </td>


            <td
                class="motivo-consulta"
                title="${escaparHtml(
                    consulta.motivo || ""
                )}"
            >
                ${escaparHtml(
                    consulta.motivo || "-"
                )}
            </td>


            <td>
                ${status}
            </td>


            <td>
                ${acoes}
            </td>

        `;


        listaConsultas.appendChild(linha);

    });
}


/* ==================================================
   STATUS
   ================================================== */

function criarStatus(status) {

    let classe =
        "status-agendada";

    let texto =
        "Agendada";


    if (status === "concluida") {

        classe =
            "status-concluida";

        texto =
            "Concluída";


    } else if (status === "cancelada") {

        classe =
            "status-cancelada";

        texto =
            "Cancelada";


    } else if (status === "a validar") {

        classe =
            "status-validar";

        texto =
            "A validar";


    } else if (status === "em aberto") {

        classe =
            "status-aberta";

        texto =
            "Em aberto";
    }


    return `
        <span class="status-badge ${classe}">
            ${texto}
        </span>
    `;
}


/* ==================================================
   AÇÕES
   ================================================== */

function criarAcoes(consulta) {

    /*
     * Consultas canceladas ou concluídas
     * não possuem ações disponíveis.
     */
    if (
        consulta.status === "cancelada" ||
        consulta.status === "concluida"
    ) {
        return "";
    }

    /*
     * O botão Editar aparece somente
     * quando a consulta está agendada.
     */
    const botaoEditar =
        consulta.status === "agendada"
            ? `
                <button
                    type="button"
                    class="btn-acao btn-editar"
                    title="Editar Consulta"
                    onclick="editarConsulta(${consulta.id})"
                >
                    <i class="bi bi-pencil"></i>
                </button>
            `
            : "";

    return `
        <div class="acoes-consulta">

            ${botaoEditar}

            <button
                type="button"
                class="btn-acao btn-cancelar-consulta"
                title="Cancelar consulta"
                onclick="cancelarConsulta(${consulta.id})"
            >
                <i class="bi bi-x-lg"></i>
            </button>

            <button
                type="button"
                class="btn-acao btn-concluir"
                title="Concluir consulta"
                onclick="concluirConsulta(${consulta.id})"
            >
                <i class="bi bi-check-lg"></i>
            </button>

        </div>
    `;
}



/* ==================================================
   EDITAR
   ================================================== */


async function editarConsulta(id) {
    window.location.href = `/pagina/consultas/editar?id=${id}`;
}


/* ==================================================
   CANCELAR
   ================================================== */

async function cancelarConsulta(id) {

    const confirmar =
        confirm(
            "Deseja realmente cancelar esta consulta?"
        );


    if (!confirmar) {
        return;
    }


    try {

        const resposta =
            await fetch(
                `/consultas/${id}/cancelar`,
                {
                    method: "PUT"
                }
            );


        const texto =
            await resposta.text();


        if (!resposta.ok) {

            throw new Error(texto);
        }


        alert(texto);


        await carregarConsultas();


    } catch (erro) {

        alert(
            erro.message ||
            "Não foi possível cancelar a consulta."
        );
    }
}


/* ==================================================
   CONCLUIR
   ================================================== */

async function concluirConsulta(id) {

    const consulta =
        consultas.find(
            c => c.id === id
        );


    /*
     * O backend também faz essa validação.
     *
     * Esta verificação apenas melhora a UX,
     * evitando uma requisição desnecessária.
     */

    if (
        consulta &&
        !consulta.temRelatorio
    ) {

        alert(
            "A consulta não pode ser concluída porque ainda não possui relatório."
        );

        return;
    }


    const confirmar =
        confirm(
            "Deseja realmente concluir esta consulta?"
        );


    if (!confirmar) {
        return;
    }


    try {

        const resposta =
            await fetch(
                `/consultas/${id}/concluir`,
                {
                    method: "PUT"
                }
            );


        const texto =
            await resposta.text();


        if (!resposta.ok) {

            throw new Error(texto);
        }


        alert(texto);


        await carregarConsultas();


    } catch (erro) {

        alert(
            erro.message ||
            "Não foi possível concluir a consulta."
        );
    }
}


/* ==================================================
   FORMATAR DATA
   ================================================== */

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


/* ==================================================
   FORMATAR HORÁRIO
   ================================================== */

function formatarHorario(data) {

    if (!data) {
        return "-";
    }


    return data.substring(11, 16);
}


/* ==================================================
   ESCAPAR HTML
   ================================================== */

function escaparHtml(texto) {

    const div =
        document.createElement("div");


    div.textContent =
        texto;


    return div.innerHTML;
}


/* ==================================================
   EVENTOS DOS FILTROS
   ================================================== */

filtroAluno.addEventListener(
    "input",
    aplicarFiltros
);


filtroPaciente.addEventListener(
    "input",
    aplicarFiltros
);


filtroStatus.addEventListener(
    "change",
    aplicarFiltros
);


filtroData.addEventListener(
    "change",
    aplicarFiltros
);


/* ==================================================
   INICIALIZAÇÃO
   ================================================== */

carregarConsultas();