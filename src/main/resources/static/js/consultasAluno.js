const listaConsultas =
    document.getElementById("listaConsultas");

const mensagemVazia =
    document.getElementById("mensagemVazia");

const mensagemErro =
    document.getElementById("mensagemErro");

const contadorConsultas =
    document.getElementById("contadorConsultas");


async function carregarConsultas() {

    try {

        const resposta =
            await fetch("/consultas/aluno/agendadas");

        if (!resposta.ok) {
            throw new Error(
                "Não foi possível carregar as consultas."
            );
        }

        const consultas =
            await resposta.json();

        renderizarConsultas(consultas);

    } catch (erro) {

        console.error(erro);

        mensagemErro.style.display = "block";
    }
}


function renderizarConsultas(consultas) {

    listaConsultas.innerHTML = "";

    contadorConsultas.textContent =
        `${consultas.length} ${
            consultas.length === 1
                ? "consulta"
                : "consultas"
        }`;


    if (consultas.length === 0) {

        mensagemVazia.style.display = "block";

        return;
    }

    mensagemVazia.style.display = "none";


    consultas.forEach(consulta => {

        const card =
            document.createElement("article");

        card.classList.add(
            "card-consulta-aluno"
        );


        const data =
            formatarData(
                consulta.dataConsulta
            );

        const horario =
            formatarHorario(
                consulta.dataConsulta
            );


        card.innerHTML = `

            <div class="dados-consulta">

                <div class="icone-consulta">

                    <i class="bi bi-calendar-event"></i>

                </div>


                <div class="informacoes-consulta">

                    <h3>
                        ${escaparHtml(
                            consulta.nomePaciente || "-"
                        )}
                    </h3>

                    <p>
                        <strong>Data:</strong>
                        ${data}
                    </p>

                    <p>
                        <strong>Horário:</strong>
                        ${horario}
                    </p>

                    <p>
                        <strong>Motivo:</strong>
                        ${escaparHtml(
                            consulta.motivo || "-"
                        )}
                    </p>

                    ${
                        consulta.diagnostico
                            ? `
                                <p>
                                    <strong>Diagnóstico:</strong>
                                    ${escaparHtml(
                                        consulta.diagnostico
                                    )}
                                </p>
                            `
                            : ""
                    }

                </div>

            </div>


            <div class="acoes-consulta-aluno">

                <button
                    type="button"
                    class="btn-relatorio"
                    onclick="cadastrarRelatorio(${consulta.id})">

                    <i class="bi bi-file-medical"></i>

                    Cadastrar relatório

                </button>

            </div>

        `;

        listaConsultas.appendChild(card);
    });
}


function cadastrarRelatorio(idConsulta) {

    alert(
        "O cadastro de relatório ainda será implementado."
    );
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


function formatarHorario(data) {

    if (!data) {
        return "-";
    }

    return data.substring(11, 16);
}


function escaparHtml(texto) {

    const div =
        document.createElement("div");

    div.textContent = texto;

    return div.innerHTML;
}


carregarConsultas();