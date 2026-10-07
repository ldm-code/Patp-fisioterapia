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

        mensagemErro.textContent =
            "Não foi possível carregar suas consultas.";

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
                    onclick="abrirFormularioRelatorio(${consulta.id}, this)">

                    <i class="bi bi-file-medical"></i>

                    Cadastrar relatório

                </button>

            </div>

        `;

        listaConsultas.appendChild(card);
    });
}


function abrirFormularioRelatorio(
    idConsulta,
    botao
) {

    const card =
        botao.closest(".card-consulta-aluno");


    if (
        card.querySelector(
            ".formulario-relatorio"
        )
    ) {
        return;
    }


    const formulario =
        document.createElement("div");

    formulario.classList.add(
        "formulario-relatorio"
    );


    formulario.innerHTML = `

        <div class="cabecalho-formulario-relatorio">

            <div>

                <h4>
                    <i class="bi bi-file-medical"></i>
                    Relatório da consulta
                </h4>

                <p>
                    Descreva as informações referentes
                    à consulta realizada.
                </p>

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

                <span class="contador-caracteres">
                    0 / 5000
                </span>

            </div>

        </div>


        <div
            class="mensagem-formulario-relatorio"
            style="display: none;">
        </div>


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


    const textarea =
        formulario.querySelector(
            ".descricao-relatorio"
        );

    const contador =
        formulario.querySelector(
            ".contador-caracteres"
        );


    textarea.addEventListener(
        "input",
        () => {

            contador.textContent =
                `${textarea.value.length} / 5000`;

        }
    );


    textarea.focus();

}


function fecharFormularioRelatorio(botao) {

    const formulario =
        botao.closest(
            ".formulario-relatorio"
        );

    if (formulario) {
        formulario.remove();
    }
}


async function enviarRelatorio(
    idConsulta,
    botao
) {

    const formulario =
        botao.closest(
            ".formulario-relatorio"
        );

    const textarea =
        formulario.querySelector(
            ".descricao-relatorio"
        );

    const mensagem =
        formulario.querySelector(
            ".mensagem-formulario-relatorio"
        );


    const descricao =
        textarea.value.trim();


    if (!descricao) {

        mostrarMensagemRelatorio(
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

        Enviando...
    `;


    try {

        const resposta =
            await fetch("/relatorios", {

                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body: JSON.stringify({

                    consultaId: idConsulta,

                    descricao: descricao

                })

            });


        const texto =
            await resposta.text();


        if (!resposta.ok) {

            mostrarMensagemRelatorio(
                mensagem,
                texto ||
                    "Não foi possível cadastrar o relatório.",
                "erro"
            );

            botao.disabled = false;

            botao.innerHTML = `
                <i class="bi bi-send"></i>
                Enviar relatório
            `;

            return;
        }


        mostrarMensagemRelatorio(
            mensagem,
            texto ||
                "Relatório cadastrado com sucesso.",
            "sucesso"
        );


        textarea.disabled = true;

        botao.disabled = true;

        botao.innerHTML = `
            <i class="bi bi-check-circle"></i>
            Relatório enviado
        `;


        const botaoCancelar =
            formulario.querySelector(
                ".btn-cancelar-relatorio"
            );

        botaoCancelar.disabled = true;


        /*
         * A consulta passa para "a validar"
         * no backend após o envio.
         *
         * Recarregamos a lista para refletir
         * o novo estado da consulta.
         */
        setTimeout(() => {

            carregarConsultas();

        }, 1200);


    } catch (erro) {

        console.error(erro);

        mostrarMensagemRelatorio(
            mensagem,
            "Não foi possível conectar ao servidor.",
            "erro"
        );

        botao.disabled = false;

        botao.innerHTML = `
            <i class="bi bi-send"></i>
            Enviar relatório
        `;
    }
}


function mostrarMensagemRelatorio(
    elemento,
    texto,
    tipo
) {

    elemento.textContent = texto;

    elemento.className =
        "mensagem-formulario-relatorio";

    elemento.classList.add(
        tipo === "sucesso"
            ? "mensagem-sucesso"
            : "mensagem-erro"
    );

    elemento.style.display = "block";
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
