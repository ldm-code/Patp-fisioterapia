const formConsulta =
    document.getElementById("formConsulta");

const alunoNome =
    document.getElementById("alunoNome");

const alunoId =
    document.getElementById("alunoId");

const alunoOpcoes =
    document.getElementById("alunoOpcoes");

const pacienteNome =
    document.getElementById("pacienteNome");

const pacienteId =
    document.getElementById("pacienteId");

const pacienteOpcoes =
    document.getElementById("pacienteOpcoes");

const dataConsulta =
    document.getElementById("dataConsulta");

const horario =
    document.getElementById("horario");

const motivo =
    document.getElementById("motivo");

const diagnostico =
    document.getElementById("diagnostico");

const mensagem =
    document.getElementById("mensagem");

const parametroId =
    new URLSearchParams(window.location.search).get("id");


/* ==================================================
   MENSAGEM
   ================================================== */

function mostrarMensagem(texto, erro = false) {

    mensagem.textContent = texto;

    mensagem.className =
        erro
            ? "mensagem erro"
            : "mensagem sucesso";
}


/* ==================================================
   AUTOCOMPLETE
   ================================================== */

function limparOpcoes(elemento) {

    elemento.innerHTML = "";
}


/* ==================================================
   BUSCAR ALUNOS
   ================================================== */

async function buscarAlunos(nome) {

    if (nome.length < 2) {

        limparOpcoes(alunoOpcoes);

        return;
    }

    try {

        const resposta =
            await fetch(
                `/consultas/alunos/busca?nome=${encodeURIComponent(nome)}`
            );

        if (!resposta.ok) {

            throw new Error(
                "Não foi possível buscar os alunos."
            );
        }

        const alunos =
            await resposta.json();

        limparOpcoes(alunoOpcoes);

        alunos.forEach(aluno => {

            const opcao =
                document.createElement("button");

            opcao.type = "button";

            opcao.className =
                "opcao-autocomplete";

            opcao.textContent =
                aluno.nome;

            opcao.addEventListener(
                "click",
                () => selecionarAluno(aluno)
            );

            alunoOpcoes.appendChild(opcao);
        });

    } catch (erro) {

        console.error(erro);
    }
}


/* ==================================================
   SELECIONAR ALUNO
   ================================================== */

function selecionarAluno(aluno) {

    alunoNome.value =
        aluno.nome;

    alunoId.value =
        aluno.id;

    limparOpcoes(
        alunoOpcoes
    );


    /*
     * Agora o horário depende
     * também da data selecionada.
     */

    if (dataConsulta.value) {

        carregarHorarios(
            aluno.id
        );

    } else {

        horario.disabled = true;

        horario.innerHTML = `
            <option value="">
                Selecione primeiro a data
            </option>
        `;
    }
}


/* ==================================================
   BUSCAR PACIENTES
   ================================================== */

async function buscarPacientes(nome) {

    if (nome.length < 2) {

        limparOpcoes(
            pacienteOpcoes
        );

        return;
    }

    try {

        const resposta =
            await fetch(
                `/consultas/pacientes/busca?nome=${encodeURIComponent(nome)}`
            );

        if (!resposta.ok) {

            throw new Error(
                "Não foi possível buscar os pacientes."
            );
        }

        const pacientes =
            await resposta.json();

        limparOpcoes(
            pacienteOpcoes
        );

        pacientes.forEach(paciente => {

            const opcao =
                document.createElement("button");

            opcao.type = "button";

            opcao.className =
                "opcao-autocomplete";

            opcao.textContent =
                paciente.nome;

            opcao.addEventListener(
                "click",
                () => selecionarPaciente(paciente)
            );

            pacienteOpcoes.appendChild(opcao);
        });

    } catch (erro) {

        console.error(erro);
    }
}


/* ==================================================
   SELECIONAR PACIENTE
   ================================================== */

function selecionarPaciente(paciente) {

    pacienteNome.value =
        paciente.nome;

    pacienteId.value =
        paciente.id;

    limparOpcoes(
        pacienteOpcoes
    );
}


/* ==================================================
   CARREGAR HORÁRIOS
   ================================================== */

async function carregarHorarios(idAluno) {

    horario.disabled = true;

    horario.innerHTML = `
        <option value="">
            Carregando horários...
        </option>
    `;


    /*
     * Sem data não existe como
     * verificar disponibilidade.
     */

    if (!dataConsulta.value) {

        horario.innerHTML = `
            <option value="">
                Selecione primeiro a data
            </option>
        `;

        return;
    }


    try {

        const resposta =
            await fetch(
                `/consultas/horarios/aluno/${idAluno}?data=${encodeURIComponent(dataConsulta.value)}`
            );


        if (!resposta.ok) {

            throw new Error(
                "Não foi possível carregar os horários."
            );
        }


        const horarios =
            await resposta.json();


        horario.innerHTML = "";


        if (horarios.length === 0) {

            horario.innerHTML = `
                <option value="">
                    Nenhum horário disponível
                </option>
            `;

            return;
        }


        horario.innerHTML = `
            <option value="">
                Selecione o horário
            </option>
        `;


        horarios.forEach(hora => {

            const opcao =
                document.createElement("option");

            opcao.value =
                hora;

            opcao.textContent =
                hora;

            horario.appendChild(opcao);
        });


        horario.disabled = false;


    } catch (erro) {

        horario.innerHTML = `
            <option value="">
                Erro ao carregar horários
            </option>
        `;

        console.error(erro);
    }
}


/* ==================================================
   ALTERAÇÃO DO ALUNO
   ================================================== */

alunoNome.addEventListener(
    "input",
    () => {

        alunoId.value = "";

        horario.disabled = true;

        horario.innerHTML = `
            <option value="">
                Selecione o aluno
            </option>
        `;

        buscarAlunos(
            alunoNome.value.trim()
        );
    }
);


/* ==================================================
   ALTERAÇÃO DO PACIENTE
   ================================================== */

pacienteNome.addEventListener(
    "input",
    () => {

        pacienteId.value = "";

        buscarPacientes(
            pacienteNome.value.trim()
        );
    }
);


/* ==================================================
   ALTERAÇÃO DA DATA
   ================================================== */

dataConsulta.addEventListener(
    "change",
    () => {

        horario.disabled = true;

        horario.innerHTML = `
            <option value="">
                Selecione o horário
            </option>
        `;


        /*
         * Se ainda não escolheu aluno,
         * não existe horário para carregar.
         */

        if (!alunoId.value) {

            horario.innerHTML = `
                <option value="">
                    Selecione primeiro o aluno
                </option>
            `;

            return;
        }


        carregarHorarios(
            Number(alunoId.value)
        );
    }
);


/* ==================================================
   FECHAR AUTOCOMPLETE
   ================================================== */

document.addEventListener(
    "click",
    evento => {

        if (
            !evento.target.closest(
                ".campo-autocomplete"
            )
        ) {

            limparOpcoes(
                alunoOpcoes
            );

            limparOpcoes(
                pacienteOpcoes
            );
        }
    }
);


/* ==================================================
   SUBMIT
   ================================================== */

formConsulta.addEventListener(
    "submit",
    async evento => {

        evento.preventDefault();


        if (!alunoId.value) {

            mostrarMensagem(
                "Selecione um aluno da lista.",
                true
            );

            return;
        }


        if (!pacienteId.value) {

            mostrarMensagem(
                "Selecione um paciente da lista.",
                true
            );

            return;
        }


        if (!dataConsulta.value) {

            mostrarMensagem(
                "Selecione uma data.",
                true
            );

            return;
        }


        if (!horario.value) {

            mostrarMensagem(
                "Selecione um horário.",
                true
            );

            return;
        }


        const dataHora =
            `${dataConsulta.value}T${horario.value}:00`;


        const dados = {

            alunoId:
                Number(alunoId.value),

            paciente:
                Number(pacienteId.value),

            motivo:
                motivo.value.trim(),

            dataConsulta:
                dataHora,

            diagnostico:
                diagnostico.value.trim() || null
        };


        const botaoSalvar =
            formConsulta.querySelector(
                ".btn-salvar"
            );


        botaoSalvar.disabled = true;


        try {

            const url =
                parametroId
                    ? `/consultas/${parametroId}`
                    : "/consultas";


            const metodo =
                parametroId
                    ? "PUT"
                    : "POST";


            const resposta =
                await fetch(
                    url,
                    {
                        method: metodo,

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body:
                            JSON.stringify(dados)
                    }
                );


            const texto =
                await resposta.text();


            if (!resposta.ok) {

                throw new Error(
                    texto
                );
            }


            mostrarMensagem(
                texto
            );


            setTimeout(
                () => {

                    window.location.href =
                        "/pagina/consultas";

                },
                900
            );


        } catch (erro) {

            mostrarMensagem(
                erro.message ||
                "Não foi possível salvar a consulta.",
                true
            );


        } finally {

            botaoSalvar.disabled =
                false;
        }
    }
);