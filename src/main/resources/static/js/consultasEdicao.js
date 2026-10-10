
const formConsulta = document.getElementById("formConsulta");

const alunoNome = document.getElementById("alunoNome");
const alunoId = document.getElementById("alunoId");
const alunoOpcoes = document.getElementById("alunoOpcoes");

const pacienteNome = document.getElementById("pacienteNome");
const pacienteId = document.getElementById("pacienteId");

const dataConsulta = document.getElementById("dataConsulta");
const horario = document.getElementById("horario");
const motivo = document.getElementById("motivo");
const diagnostico = document.getElementById("diagnostico");

const mensagem = document.getElementById("mensagem");
const botaoSalvar = document.getElementById("btnSalvar");

const consultaId = new URLSearchParams(
    window.location.search
).get("id");

let consultaCarregada = null;
let pacienteIdOriginal = null;
let carregando = false;
let buscaAlunosTimeout = null;
let buscaAlunosController = null;
let calendario = null;

/* ==================================================
   CALENDÁRIO
   Exibição brasileira; valor interno yyyy-MM-dd.
   Sábados e domingos não podem ser selecionados.
   ================================================== */

if (typeof flatpickr !== "undefined") {
    calendario = flatpickr(dataConsulta, {
        locale: "pt",
        dateFormat: "Y-m-d",
        altInput: true,
        altFormat: "d/m/Y",
        disableMobile: true,
        allowInput: false,

        disable: [
            function (data) {
                return data.getDay() === 0 ||
                       data.getDay() === 6;
            }
        ],

        onChange: function (datasSelecionadas, dataTexto) {
            if (dataTexto) {
                atualizarHorarios();
            }
        }
    });
}

/* ==================================================
   MENSAGENS
   ================================================== */

function mostrarMensagem(texto, erro = false) {
    mensagem.textContent = texto;
    mensagem.className = erro
        ? "mensagem erro"
        : "mensagem sucesso";
}

function limparMensagem() {
    mensagem.textContent = "";
    mensagem.className = "mensagem";
}

async function obterTextoErro(resposta, mensagemPadrao) {
    const texto = await resposta.text();
    return texto || mensagemPadrao;
}

/* ==================================================
   BLOQUEIO DA TELA
   ================================================== */

function bloquearFormulario(bloquear) {
    formConsulta.querySelectorAll(
        "input, select, textarea"
    ).forEach(campo => {
        campo.disabled = bloquear;
    });

    // O paciente sempre permanece somente para leitura.
    pacienteNome.readOnly = true;

    if (calendario) {
        calendario.set("clickOpens", !bloquear);

        if (calendario.altInput) {
            calendario.altInput.disabled = bloquear;
        }
    }

    botaoSalvar.disabled = bloquear;
}

/* ==================================================
   AUTOCOMPLETE DE ALUNOS
   ================================================== */

function limparOpcoesAlunos() {
    alunoOpcoes.innerHTML = "";
}

async function buscarAlunos(nome) {
    limparOpcoesAlunos();

    if (nome.trim().length < 2) {
        return;
    }

    if (buscaAlunosController) {
        buscaAlunosController.abort();
    }

    buscaAlunosController = new AbortController();

    try {
        const resposta = await fetch(
            `/consultas/alunos/busca?nome=${
                encodeURIComponent(nome.trim())
            }`,
            {
                signal: buscaAlunosController.signal
            }
        );

        if (!resposta.ok) {
            throw new Error(
                await obterTextoErro(
                    resposta,
                    "Não foi possível buscar os alunos."
                )
            );
        }

        const alunos = await resposta.json();

        // Evita exibir resultados de uma busca antiga.
        if (alunoNome.value.trim() !== nome.trim()) {
            return;
        }

        limparOpcoesAlunos();

        if (!alunos.length) {
            const opcao = document.createElement("div");
            opcao.className = "opcao-autocomplete";
            opcao.textContent = "Nenhum aluno encontrado";
            alunoOpcoes.appendChild(opcao);
            return;
        }

        alunos.forEach(aluno => {
            const opcao = document.createElement("button");

            opcao.type = "button";
            opcao.className = "opcao-autocomplete";
            opcao.textContent = aluno.nome;

            opcao.addEventListener("click", () => {
                selecionarAluno(aluno);
            });

            alunoOpcoes.appendChild(opcao);
        });
    } catch (erro) {
        if (erro.name !== "AbortError") {
            console.error(erro);
            mostrarMensagem(erro.message, true);
        }
    }
}

function selecionarAluno(aluno) {
    alunoNome.value = aluno.nome;
    alunoId.value = aluno.id;

    limparOpcoesAlunos();
    limparMensagem();

    atualizarHorarios();
}

/* ==================================================
   HORÁRIOS DISPONÍVEIS
   Envia consultaId para o backend preservar a
   disponibilidade do horário da própria consulta.
   ================================================== */

async function carregarHorarios(
    idAluno,
    horarioSelecionado = ""
) {
    horario.disabled = true;
    horario.innerHTML = `
        <option value="">Carregando horários...</option>
    `;

    if (!idAluno || !dataConsulta.value) {
        horario.innerHTML = `
            <option value="">Selecione o aluno e a data</option>
        `;
        return;
    }

    try {
        const parametros = new URLSearchParams({
            data: dataConsulta.value,
            consultaId: consultaId
        });

        const resposta = await fetch(
            `/consultas/horarios/aluno/${encodeURIComponent(idAluno)}?${parametros}`
        );

        if (!resposta.ok) {
            throw new Error(
                await obterTextoErro(
                    resposta,
                    "Não foi possível carregar os horários."
                )
            );
        }

        const horarios = await resposta.json();

        horario.innerHTML = "";

        const opcaoInicial = document.createElement("option");
        opcaoInicial.value = "";
        opcaoInicial.textContent = "Selecione o horário";

        horario.appendChild(opcaoInicial);

        horarios.forEach(item => {
            const hora = String(item).substring(0, 5);
            const opcao = document.createElement("option");

            opcao.value = hora;
            opcao.textContent = hora;

            horario.appendChild(opcao);
        });

        if (horarioSelecionado) {
            const horaAtual = horarioSelecionado.substring(0, 5);

            const existe = Array.from(horario.options).some(
                opcao => opcao.value === horaAtual
            );

            /*
             * Se o backend não retornar o horário atual,
             * mantém a opção visível para não apagar o valor
             * da consulta sem uma escolha do usuário.
             */
            if (!existe) {
                const opcaoAtual = document.createElement("option");

                opcaoAtual.value = horaAtual;
                opcaoAtual.textContent = `${horaAtual} (horário atual)`;

                horario.appendChild(opcaoAtual);
            }

            horario.value = horaAtual;
        }

        horario.disabled = false;

        if (horarios.length === 0 && !horarioSelecionado) {
            horario.innerHTML = `
                <option value="">Nenhum horário disponível</option>
            `;
            horario.disabled = true;
        }
    } catch (erro) {
        console.error(erro);

        horario.innerHTML = `
            <option value="">Erro ao carregar horários</option>
        `;

        horario.disabled = true;
        mostrarMensagem(erro.message, true);
    }
}

function atualizarHorarios() {
    limparMensagem();

    if (!alunoId.value) {
        horario.disabled = true;
        horario.innerHTML = `
            <option value="">Selecione primeiro o aluno</option>
        `;
        return;
    }

    carregarHorarios(Number(alunoId.value));
}

/* ==================================================
   CARREGAR CONSULTA EXISTENTE
   ================================================== */

async function carregarConsulta() {
    if (!consultaId || !/^\d+$/.test(consultaId)) {
        mostrarMensagem("ID da consulta inválido.", true);
        bloquearFormulario(true);
        return;
    }

    carregando = true;
    bloquearFormulario(true);

    try {
        const resposta = await fetch(
            `/consultas/${encodeURIComponent(consultaId)}`
        );

        if (!resposta.ok) {
            throw new Error(
                await obterTextoErro(
                    resposta,
                    "Não foi possível carregar a consulta."
                )
            );
        }

        const consulta = await resposta.json();

        /*
         * Validação visual complementar.
         * O backend também precisa validar essa regra.
         */
        if (
            String(consulta.status || "").toLowerCase() !== "agendada"
        ) {
            throw new Error(
                "Somente consultas agendadas podem ser editadas."
            );
        }

        if (
            consulta.paciente === null ||
            consulta.paciente === undefined
        ) {
            throw new Error(
                "Não foi possível identificar o paciente da consulta."
            );
        }

        const dataHora = consulta.dataConsulta;

        if (!dataHora || dataHora.length < 16) {
            throw new Error(
                "A consulta não possui data e horário válidos."
            );
        }

        consultaCarregada = consulta;
        pacienteIdOriginal = Number(consulta.paciente);

        alunoNome.value = consulta.nomeAluno || "";
        alunoId.value = consulta.alunoId;

        pacienteNome.value = consulta.nomePaciente || "";
        pacienteId.value = pacienteIdOriginal;

        motivo.value = consulta.motivo || "";
        diagnostico.value = consulta.diagnostico || "";

        const data = dataHora.substring(0, 10);
        const hora = dataHora.substring(11, 16);

        if (calendario) {
            calendario.setDate(data, false, "Y-m-d");
        } else {
            dataConsulta.value = data;
        }

        await carregarHorarios(
            Number(consulta.alunoId),
            hora
        );

        bloquearFormulario(false);

        mostrarMensagem(
            "Consulta carregada. Você já pode editar os campos."
        );
    } catch (erro) {
        console.error(erro);
        mostrarMensagem(erro.message, true);
        bloquearFormulario(true);
    } finally {
        carregando = false;
    }
}

/* ==================================================
   EVENTOS
   ================================================== */

alunoNome.addEventListener("input", () => {
    // Digitar um nome não significa que um aluno foi selecionado.
    alunoId.value = "";

    horario.disabled = true;
    horario.innerHTML = `
        <option value="">Selecione o aluno e a data</option>
    `;

    limparMensagem();
    clearTimeout(buscaAlunosTimeout);

    if (buscaAlunosController) {
        buscaAlunosController.abort();
    }

    buscaAlunosTimeout = setTimeout(() => {
        buscarAlunos(alunoNome.value);
    }, 300);
});

document.addEventListener("click", evento => {
    if (!evento.target.closest(".campo-autocomplete")) {
        limparOpcoesAlunos();
    }
});

horario.addEventListener("change", limparMensagem);

/* ==================================================
   SALVAR ALTERAÇÕES
   ================================================== */

formConsulta.addEventListener("submit", async evento => {
    evento.preventDefault();

    if (carregando || !consultaCarregada) {
        return;
    }

    if (!alunoId.value) {
        mostrarMensagem("Selecione um aluno da lista.", true);
        return;
    }

    if (!dataConsulta.value || !horario.value) {
        mostrarMensagem("Selecione a data e o horário.", true);
        return;
    }

    if (!motivo.value.trim()) {
        mostrarMensagem("Informe o motivo da consulta.", true);
        return;
    }

    if (
        pacienteIdOriginal === null ||
        !Number.isInteger(pacienteIdOriginal)
    ) {
        mostrarMensagem(
            "Não foi possível identificar o paciente original.",
            true
        );
        return;
    }

    const dados = {
        id: Number(consultaId),
        alunoId: Number(alunoId.value),
        paciente: pacienteIdOriginal,
        motivo: motivo.value.trim(),
        dataConsulta: `${dataConsulta.value}T${horario.value}:00`,
        diagnostico: diagnostico.value.trim() || null
    };

    botaoSalvar.disabled = true;
    limparMensagem();

    try {
        const resposta = await fetch(
            `/consultas/${encodeURIComponent(consultaId)}`,
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(dados)
            }
        );

        if (!resposta.ok) {
            throw new Error(
                await obterTextoErro(
                    resposta,
                    "Não foi possível atualizar a consulta."
                )
            );
        }

        const texto = await resposta.text();

        mostrarMensagem(
            texto || "Consulta atualizada com sucesso."
        );

        setTimeout(() => {
            window.location.href = "/pagina/consultas";
        }, 900);
    } catch (erro) {
        console.error(erro);

        mostrarMensagem(
            erro.message || "Não foi possível atualizar a consulta.",
            true
        );

        botaoSalvar.disabled = false;
    }
});

/* ==================================================
   INICIALIZAÇÃO
   ================================================== */

carregarConsulta();
