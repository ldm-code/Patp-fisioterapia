
const formConsulta = document.getElementById("formConsulta");
const alunoNome = document.getElementById("alunoNome");
const alunoId = document.getElementById("alunoId");
const alunoOpcoes = document.getElementById("alunoOpcoes");
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

function mostrarMensagem(texto, erro = false) {
    mensagem.textContent = texto;
    mensagem.className = erro
        ? "mensagem erro"
        : "mensagem sucesso";
}

function limparOpcoes() {
    alunoOpcoes.innerHTML = "";
}

function bloquearFormulario(bloquear) {
    formConsulta.querySelectorAll("input, select, textarea")
        .forEach(campo => {
            campo.disabled = bloquear;
        });

    botaoSalvar.disabled = bloquear;
}

async function buscarAlunos(nome) {
    if (nome.trim().length < 2) {
        limparOpcoes();
        return;
    }

    try {
        const resposta = await fetch(
            `/consultas/alunos/busca?nome=${encodeURIComponent(nome.trim())}`
        );

        if (!resposta.ok) {
            throw new Error("Não foi possível buscar os alunos.");
        }

        const alunos = await resposta.json();
        limparOpcoes();

        alunos.forEach(aluno => {
            const opcao = document.createElement("button");

            opcao.type = "button";
            opcao.className = "opcao-autocomplete";
            opcao.textContent = aluno.nome;

            opcao.addEventListener("click", () => {
                alunoNome.value = aluno.nome;
                alunoId.value = aluno.id;

                limparOpcoes();
                carregarHorarios(Number(aluno.id));
            });

            alunoOpcoes.appendChild(opcao);
        });
    } catch (erro) {
        console.error(erro);
        mostrarMensagem(erro.message, true);
    }
}

async function carregarHorarios(
    idAluno,
    horarioSelecionado = null
) {
    horario.disabled = true;
    horario.innerHTML =
        '<option value="">Carregando horários...</option>';

    if (!idAluno || !dataConsulta.value) {
        horario.innerHTML =
            '<option value="">Selecione a data</option>';
        return;
    }

    try {
        const parametros = new URLSearchParams({
            data: dataConsulta.value
        });

        if (consultaId) {
            parametros.set("consultaId", consultaId);
        }

        const resposta = await fetch(
            `/consultas/horarios/aluno/${idAluno}?${parametros}`
        );

        if (!resposta.ok) {
            const texto = await resposta.text();
            throw new Error(
                texto || "Não foi possível carregar os horários."
            );
        }

        const horarios = await resposta.json();

        horario.innerHTML = "";

        const opcaoInicial = document.createElement("option");
        opcaoInicial.value = "";
        opcaoInicial.textContent = "Selecione o horário";

        horario.appendChild(opcaoInicial);

        horarios.forEach(hora => {
            const opcao = document.createElement("option");

            opcao.value = hora.substring(0, 5);
            opcao.textContent = hora.substring(0, 5);

            horario.appendChild(opcao);
        });

        if (horarioSelecionado) {
            const horaAtual = horarioSelecionado.substring(0, 5);

            const existe = horarios.some(
                hora => hora.substring(0, 5) === horaAtual
            );

            // Mantém o horário original selecionável caso ele não
            // apareça na lista de horários disponíveis.
            if (!existe) {
                const opcaoOriginal = document.createElement("option");

                opcaoOriginal.value = horaAtual;
                opcaoOriginal.textContent =
                    `${horaAtual} (horário atual)`;

                horario.appendChild(opcaoOriginal);
            }

            horario.value = horaAtual;
        }

        horario.disabled = horarios.length === 0 &&
            !horarioSelecionado;

        if (horarios.length === 0 && !horarioSelecionado) {
            horario.innerHTML =
                '<option value="">Nenhum horário disponível</option>';
        }
    } catch (erro) {
        console.error(erro);

        horario.innerHTML =
            '<option value="">Erro ao carregar horários</option>';

        horario.disabled = true;
        mostrarMensagem(erro.message, true);
    }
}

async function carregarConsulta() {
    if (!consultaId || !Number.isInteger(Number(consultaId))) {
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
            const texto = await resposta.text();

            throw new Error(
                texto || "Não foi possível carregar a consulta."
            );
        }

        const consulta = await resposta.json();

        if (consulta.status?.toLowerCase() !== "agendada") {
            throw new Error(
                "Somente consultas agendadas podem ser editadas."
            );
        }

        consultaCarregada = consulta;

        // Preserva o paciente original sem exibir o campo na tela.
        pacienteIdOriginal = consulta.paciente;

        if (
            pacienteIdOriginal === null ||
            pacienteIdOriginal === undefined
        ) {
            throw new Error(
                "Não foi possível identificar o paciente original da consulta."
            );
        }

        alunoNome.value = consulta.nomeAluno || "";
        alunoId.value = consulta.alunoId;
        motivo.value = consulta.motivo || "";
        diagnostico.value = consulta.diagnostico || "";

        const dataHora = consulta.dataConsulta;

        if (!dataHora) {
            throw new Error(
                "A consulta não possui data e horário válidos."
            );
        }

        dataConsulta.value = dataHora.substring(0, 10);

        const agora = new Date();
        const deslocamento = agora.getTimezoneOffset();

        dataConsulta.min = new Date(
            agora.getTime() - deslocamento * 60000
        ).toISOString().slice(0, 10);

        await carregarHorarios(
            Number(consulta.alunoId),
            dataHora.substring(11, 16)
        );

        bloquearFormulario(false);

        // O horário pode continuar desabilitado se não houver opções.
        if (horario.options.length === 0) {
            horario.disabled = true;
        }

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

alunoNome.addEventListener("input", () => {
    alunoId.value = "";

    horario.disabled = true;
    horario.innerHTML =
        '<option value="">Selecione o aluno e a data</option>';

    clearTimeout(buscaAlunosTimeout);

    buscaAlunosTimeout = setTimeout(() => {
        buscarAlunos(alunoNome.value);
    }, 300);
});

dataConsulta.addEventListener("change", () => {
    if (!alunoId.value) {
        horario.disabled = true;
        horario.innerHTML =
            '<option value="">Selecione primeiro o aluno</option>';
        return;
    }

    carregarHorarios(Number(alunoId.value));
});

document.addEventListener("click", evento => {
    if (!evento.target.closest(".campo-autocomplete")) {
        limparOpcoes();
    }
});

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
        pacienteIdOriginal === undefined
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
        paciente: Number(pacienteIdOriginal),
        motivo: motivo.value.trim(),
        dataConsulta: `${dataConsulta.value}T${horario.value}:00`,
        diagnostico: diagnostico.value.trim() || null
    };

    botaoSalvar.disabled = true;

    try {
        const resposta = await fetch(`/consultas/${consultaId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dados)
        });

        const texto = await resposta.text();

        if (!resposta.ok) {
            throw new Error(
                texto || "Não foi possível atualizar a consulta."
            );
        }

        mostrarMensagem(texto || "Consulta atualizada com sucesso.");

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

carregarConsulta();
