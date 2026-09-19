
const turnos = {
    1: "Manhã",
    2: "Tarde",
    3: "Noite"
};

const listaAlunos = document.getElementById("listaAlunos");
const filtroEmail = document.getElementById("filtroEmail");
const btnFiltrar = document.getElementById("btnFiltrar");
const btnAluno = document.getElementById("btnNovoAluno");


// ========================================
// NOVO ALUNO
// ========================================

btnAluno.addEventListener("click", () => {
    window.location.href = "/alunos/cadastrarAlunos";
});


// ========================================
// FORMATAÇÃO
// ========================================

function formatarCPF(valor = "") {
    const apenasDigitos = String(valor).replace(/\D/g, "");

    return apenasDigitos
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d{1,2})/, "$1-$2")
        .replace(/(-\d{2})\d+$/, "$1");
}

function formatarTipo(tipo) {
    if (tipo === "estagio") return "Estágio";
    if (tipo === "curso") return "Cursando";
    return tipo ?? "";
}


// ========================================
// CARREGAR ESPECIALIDADES NO SELECT
// ========================================

async function carregarEspecialidades(select) {
    try {
        const resposta = await fetch("/especialidades");

        if (!resposta.ok) {
            throw new Error("Não foi possível carregar as especialidades.");
        }

        const especialidades = await resposta.json();

        select.innerHTML = `
            <option value="" selected disabled>
                Selecione uma especialidade
            </option>
        `;

        especialidades.forEach(especialidade => {
            const option = document.createElement("option");

            option.value = especialidade.id;
            option.textContent = especialidade.nome;

            select.appendChild(option);
        });

    } catch (erro) {
        console.error("Erro ao carregar especialidades:", erro);

        select.innerHTML = `
            <option value="" selected disabled>
                Erro ao carregar especialidades
            </option>
        `;
    }
}


// ========================================
// CARREGAR TURNOS NO SELECT
// ========================================

function carregarTurnos(select) {
    select.innerHTML = `
        <option value="" selected disabled>
            Selecione um turno
        </option>
    `;

    Object.entries(turnos).forEach(([id, nome]) => {
        const option = document.createElement("option");

        option.value = id;
        option.textContent = nome;

        select.appendChild(option);
    });
}


// ========================================
// FECHAR PAINÉIS
// ========================================

function fecharPaineis(areaAtual) {
    document
        .querySelectorAll(".area-especialidade")
        .forEach(area => {
            if (area !== areaAtual) {
                area.style.display = "none";
            }
        });
}


// ========================================
// BUSCAR TURNOS DE UM ALUNO
// ========================================

async function buscarTurnosAluno(idAluno) {
    const resposta = await fetch(
        `/alunos-turnos?idAluno=${encodeURIComponent(idAluno)}`
    );

    if (!resposta.ok) {
        throw new Error("Não foi possível carregar os turnos do aluno.");
    }

    return await resposta.json();
}


// ========================================
// EVENTOS DOS CARDS
// ========================================

listaAlunos.addEventListener("click", async (evento) => {

    const botaoVincularTurno =
        evento.target.closest(".btn-vincular-turno");

    const botaoRemoverTurno =
        evento.target.closest(".btn-remover-turno");

    const botaoTurno =
        evento.target.closest(".btn-turno");

    const botaoEspecialidade =
        evento.target.closest(".btn-especialidade");

    const botaoRemoverEspecialidade =
        evento.target.closest(".btn-remover-especialidade");

    const botaoVincularEspecialidade =
        evento.target.closest(".btn-vincular");


    // ====================================
    // VINCULAR TURNO
    // ====================================

    if (botaoVincularTurno) {
        const idAluno = botaoVincularTurno.dataset.aluno;

        const area = document.getElementById(`turno-${idAluno}`);
        const select = area?.querySelector(".select-turno");

        if (!select) return;

        const idTurno = select.value;

        if (!idTurno) {
            alert("Selecione um turno.");
            return;
        }

        try {
            const resposta = await fetch(
                `/alunos-turnos/cadastrar?idAluno=${idAluno}&idTurno=${idTurno}`,
                { method: "POST" }
            );

            const mensagem = await resposta.text();

            if (!resposta.ok) {
                throw new Error(mensagem);
            }

            alert(mensagem);

            area.style.display = "none";

            await carregarAlunos(filtroEmail.value);

        } catch (erro) {
            console.error("Erro ao vincular turno:", erro);
            alert(erro.message || "Não foi possível vincular o turno.");
        }

        return;
    }


    // ====================================
    // REMOVER TURNO
    // ====================================

    if (botaoRemoverTurno) {
        const idAluno = botaoRemoverTurno.dataset.aluno;
        const idTurno = botaoRemoverTurno.dataset.turno;

        if (!confirm("Deseja remover este turno do aluno?")) {
            return;
        }

        try {
            const resposta = await fetch(
                `/alunos-turnos/remover?idAluno=${idAluno}&idTurno=${idTurno}`,
                { method: "DELETE" }
            );

            const mensagem = await resposta.text();

            if (!resposta.ok) {
                throw new Error(mensagem);
            }

            alert(mensagem);

            await carregarAlunos(filtroEmail.value);

        } catch (erro) {
            console.error("Erro ao remover turno:", erro);
            alert(erro.message || "Não foi possível remover o turno.");
        }

        return;
    }


    // ====================================
    // ABRIR / FECHAR PAINEL DE TURNOS
    // ====================================

    if (botaoTurno) {
        const idAluno = botaoTurno.dataset.aluno;
        const area = document.getElementById(`turno-${idAluno}`);

        if (!area) return;

        fecharPaineis(area);

        if (area.style.display === "none") {
            area.style.display = "block";

            const select = area.querySelector(".select-turno");

            if (select && select.options.length <= 1) {
                carregarTurnos(select);
            }
        } else {
            area.style.display = "none";
        }

        return;
    }


    // ====================================
    // REMOVER ESPECIALIDADE
    // ====================================

    if (botaoRemoverEspecialidade) {
        const idAluno = botaoRemoverEspecialidade.dataset.aluno;
        const idEspecialidade =
            botaoRemoverEspecialidade.dataset.especialidade;

        if (!confirm("Deseja remover esta especialidade?")) {
            return;
        }

        try {
            const resposta = await fetch(
                `/alunos-especialidade/remover?idAluno=${idAluno}&idEspecialidade=${idEspecialidade}`,
                { method: "DELETE" }
            );

            const mensagem = await resposta.text();

            if (!resposta.ok) {
                throw new Error(mensagem);
            }

            alert(mensagem);

            await carregarAlunos(filtroEmail.value);

        } catch (erro) {
            console.error("Erro ao remover especialidade:", erro);
            alert(erro.message || "Não foi possível remover a especialidade.");
        }

        return;
    }


    // ====================================
    // ABRIR / FECHAR PAINEL DE ESPECIALIDADES
    // ====================================

    if (botaoEspecialidade) {
        const idAluno = botaoEspecialidade.dataset.id;

        const area = document.getElementById(
            `especialidade-${idAluno}`
        );

        if (!area) return;

        fecharPaineis(area);

        if (area.style.display === "none") {
            area.style.display = "block";

            const select = area.querySelector(".select-especialidade");

            if (select && select.options.length <= 1) {
                await carregarEspecialidades(select);
            }
        } else {
            area.style.display = "none";
        }

        return;
    }


    // ====================================
    // VINCULAR ESPECIALIDADE
    // ====================================

    if (
        botaoVincularEspecialidade &&
        !botaoVincularEspecialidade.classList.contains(
            "btn-vincular-turno"
        )
    ) {
        const idAluno = botaoVincularEspecialidade.dataset.id;

        const area = document.getElementById(
            `especialidade-${idAluno}`
        );

        if (!area) return;

        const select = area.querySelector(".select-especialidade");
        const idEspecialidade = select.value;

        if (!idEspecialidade) {
            alert("Selecione uma especialidade.");
            return;
        }

        try {
            const resposta = await fetch(
                `/alunos-especialidade/cadastrar?idAluno=${idAluno}&idEspecialidade=${idEspecialidade}`,
                { method: "POST" }
            );

            const mensagem = await resposta.text();

            if (!resposta.ok) {
                throw new Error(mensagem);
            }

            alert(mensagem);

            await carregarAlunos(filtroEmail.value);

        } catch (erro) {
            console.error("Erro ao vincular especialidade:", erro);
            alert(erro.message || "Não foi possível vincular a especialidade.");
        }

        return;
    }

});


// ========================================
// CARREGAR ALUNOS
// ========================================

async function carregarAlunos(email = "") {
    try {
        let url = "/alunos";

        if (email.trim() !== "") {
            url = `/alunos/email?email=${encodeURIComponent(email)}`;
        }

        const resposta = await fetch(url);

        if (!resposta.ok) {
            throw new Error("Erro ao buscar alunos.");
        }

        const alunos = await resposta.json();

        listaAlunos.innerHTML = "";

        if (!Array.isArray(alunos) || alunos.length === 0) {
            listaAlunos.innerHTML = `
                <div class="nenhum-aluno">
                    <i class="bi bi-person-x"></i>
                    <p>Nenhum aluno foi encontrado.</p>
                </div>
            `;
            return;
        }

        // Busca os turnos de cada aluno pelo GET do controller.
        const alunosComTurnos = await Promise.all(
            alunos.map(async aluno => {
                try {
                    const turnosAluno = await buscarTurnosAluno(aluno.id);

                    return {
                        ...aluno,
                        turnos: turnosAluno
                    };
                } catch (erro) {
                    console.error(
                        `Erro ao buscar turnos do aluno ${aluno.id}:`,
                        erro
                    );

                    return {
                        ...aluno,
                        turnos: []
                    };
                }
            })
        );


        // ====================================
        // CRIAR CARDS
        // ====================================

        alunosComTurnos.forEach(aluno => {

            // --------------------------------
            // TURNOS
            // --------------------------------

            const listaTurnos =
                aluno.turnos.length > 0
                    ? aluno.turnos.map(turno => `
                        <span class="especialidade-item">
                            ${turno.nomeTurno || turnos[turno.turno] || "Turno"}

                            <button
                                type="button"
                                class="btn-remover-turno"
                                data-aluno="${aluno.id}"
                                data-turno="${turno.turno}"
                                title="Remover turno">
                                <i class="bi bi-x-lg"></i>
                            </button>
                        </span>
                    `).join("<br>")
                    : "<span>Nenhum turno cadastrado</span>";


            // --------------------------------
            // ESPECIALIDADES
            // --------------------------------

            const especialidades = Array.isArray(aluno.especialidades)
                ? aluno.especialidades.filter(item => item.nome)
                : [];

            const listaEspecialidades =
                especialidades.length > 0
                    ? especialidades.map(especialidade => `
                        <span class="especialidade-item">
                            ${especialidade.nome}

                            <button
                                type="button"
                                class="btn-remover-especialidade"
                                data-aluno="${aluno.id}"
                                data-especialidade="${especialidade.id}"
                                title="Remover especialidade">
                                <i class="bi bi-x-lg"></i>
                            </button>
                        </span>
                    `).join("<br>")
                    : "Nenhuma cadastrada";


            // --------------------------------
            // CARD
            // --------------------------------

            const card = document.createElement("article");
            card.classList.add("aluno");

            card.innerHTML = `
                <div class="dados-aluno">
                    <div class="icone">
                        <i class="bi bi-person"></i>
                    </div>

                    <div class="informacoes">
                        <h2>${aluno.nome ?? ""}</h2>

                        <p><strong>ID:</strong> ${aluno.id}</p>
                        <br>

                        <p><strong>CPF:</strong> ${formatarCPF(aluno.cpf)}</p>
                        <br>

                        <p><strong>E-mail:</strong> ${aluno.email ?? ""}</p>
                        <br>

                        <p>
                            <strong>Tipo:</strong>
                            <span class="tipo-badge ${aluno.tipo}">
                                ${formatarTipo(aluno.tipo)}
                            </span>
                        </p>
                    </div>
                </div>

                <!-- ESPECIALIDADES -->
                <div class="especialidades-aluno">
                    <strong>Especialidades:</strong>
                    <br><br>
                    ${listaEspecialidades}
                </div>

                <!-- TURNOS -->
                <div class="campo-aluno">
                    <strong>Turnos:</strong>

                    <div class="lista-especialidades">
                        ${listaTurnos}
                    </div>
                </div>



                <!-- AÇÕES -->
                <div class="acoes-aluno">
                    <button
                        type="button"
                        class="btn-especialidade"
                        title="Vincular especialidade"
                        data-id="${aluno.id}">
                        <i class="bi bi-bookmark-plus"></i>
                        Adicionar Especialidade
                    </button>

                    <button
                        type="button"
                        class="btn-turno"
                        data-aluno="${aluno.id}">
                        <i class="bi bi-clock"></i>
                        Adicionar Turno
                    </button>

                    <button
                        type="button"
                        class="btn-editar"
                        title="Editar aluno"
                        data-id="${aluno.id}">
                        <i class="bi bi-pencil"></i>
                        Editar
                    </button>
                </div>

                <!-- PAINEL ESPECIALIDADES -->
                <div
                    class="area-especialidade"
                    id="especialidade-${aluno.id}"
                    style="display: none;">

                    <div class="card-especialidade">
                        <div class="titulo-especialidade">
                            <i class="bi bi-bookmark-plus"></i>

                            <div>
                                <h3>Adicionar especialidade</h3>
                                <p>
                                    Selecione uma especialidade
                                    para este aluno.
                                </p>
                            </div>
                        </div>

                        <div class="form-especialidade">
                            <select class="select-especialidade">
                                <option value="" selected disabled>
                                    Selecione uma especialidade
                                </option>
                            </select>

                            <button
                                type="button"
                                class="btn-vincular"
                                data-id="${aluno.id}">
                                <i class="bi bi-check-lg"></i>
                                Vincular
                            </button>
                        </div>
                    </div>
                </div>

                <!-- PAINEL TURNOS -->
                <div
                    class="area-turno area-especialidade"
                    id="turno-${aluno.id}"
                    style="display: none;">

                    <div class="card-especialidade">
                        <div class="titulo-especialidade">
                            <i class="bi bi-clock"></i>

                            <div>
                                <h3>Adicionar turno</h3>
                                <p>
                                    Selecione um turno
                                    para este aluno.
                                </p>
                            </div>
                        </div>

                        <div class="form-especialidade">
                            <select class="select-especialidade select-turno">
                                <option value="" selected disabled>
                                    Selecione um turno
                                </option>
                            </select>

                            <button
                                type="button"
                                class="btn-vincular btn-vincular-turno"
                                data-aluno="${aluno.id}">
                                <i class="bi bi-check-lg"></i>
                                Vincular
                            </button>
                        </div>
                    </div>
                </div>
            `;

            listaAlunos.appendChild(card);
        });

    } catch (erro) {
        console.error("Erro ao carregar alunos:", erro);

        listaAlunos.innerHTML = `
            <p>Não foi possível carregar os alunos.</p>
        `;
    }
}


// ========================================
// FILTRAR ALUNOS POR E-MAIL
// ========================================

btnFiltrar.addEventListener("click", () => {
    carregarAlunos(filtroEmail.value);
});


// ========================================
// CARREGAMENTO INICIAL
// ========================================

carregarAlunos();