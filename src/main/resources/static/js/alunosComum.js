
const listaAlunos = document.getElementById("listaAlunos");
const filtroEmail = document.getElementById("filtroEmail");
const btnFiltrar = document.getElementById("btnFiltrar");

const turnos = {
    1: "Manhã",
    2: "Tarde",
    3: "Noite"
};

// ========================================
// BUSCAR TURNOS DO ALUNO
// ========================================

async function buscarTurnosAluno(idAluno) {

    const resposta = await fetch(
        `/alunos-turnos?idAluno=${encodeURIComponent(idAluno)}`
    );

    if (!resposta.ok) {
        throw new Error("Não foi possível carregar os turnos.");
    }

    return await resposta.json();
}

// ========================================
// FORMATAR TIPO DO ALUNO
// ========================================

function formatarTipo(tipo) {
    if (tipo === "estagio") return "Estágio";
    if (tipo === "curso") return "Cursando";
    return tipo ?? "";
}

// ========================================
// CARREGAR ALUNOS
// ========================================

async function carregarAlunos(email = "") {

    try {

       const idProfessor = document.body.dataset.idProfessor;

        let url = `/alunos/professor?idProfessor=${encodeURIComponent(idProfessor)}`;

        if (email.trim() !== "") {
            url = `/alunos/professor/email?email=${encodeURIComponent(email)}&idProfessor=${encodeURIComponent(idProfessor)}`;
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
                    <p>Nenhum aluno encontrado.</p>
                </div>
            `;
            return;
        }

        const alunosComTurnos = await Promise.all(
            alunos.map(async aluno => {

                try {
                    const turnosAluno =
                        await buscarTurnosAluno(aluno.id);

                    return {
                        ...aluno,
                        turnos: turnosAluno
                    };

                } catch (erro) {

                    console.error(
                        "Erro ao buscar turnos:",
                        erro
                    );

                    return {
                        ...aluno,
                        turnos: []
                    };
                }
            })
        );

        alunosComTurnos.forEach(aluno => {

            const card = document.createElement("article");
            card.classList.add("aluno");

            const listaTurnos = aluno.turnos.length > 0
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

            card.innerHTML = `
                <div class="dados-aluno">

                    <div class="icone">
                        <i class="bi bi-person"></i>
                    </div>

                    <div class="informacoes">
                        <h2>${aluno.nome ?? ""}</h2>

                        <p>
                            <strong>ID:</strong>
                            ${aluno.id}
                        </p>

                        <br>

                        <p>
                            <strong>E-mail:</strong>
                            ${aluno.email ?? ""}
                        </p>

                        <br>

                        <p>
                            <strong>CPF:</strong>
                            ${formatarCPF(aluno.cpf)}
                        </p>

                        <br>

                        <p>
                            <strong>Tipo:</strong>
                            <span class="tipo-badge ${aluno.tipo}">
                                ${formatarTipo(aluno.tipo)}
                            </span>
                        </p>
                    </div>

                </div>

                <div class="campo-aluno">
                    <strong>Turnos:</strong>

                    <div class="lista-especialidades">
                        ${listaTurnos}
                    </div>
                </div>

                <div class="acoes-aluno">

                    <button
                        type="button"
                        class="btn-turno"
                        data-aluno="${aluno.id}">
                        <i class="bi bi-clock"></i>
                        Gerenciar turnos
                    </button>

                </div>

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
                                    Selecione um turno para este aluno.
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
// FORMATAR CPF
// ========================================

function formatarCPF(valor = "") {

    const apenasDigitos = String(valor).replace(/\D/g, "");

    return apenasDigitos
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d{1,2})/, "$1-$2")
        .replace(/(-\d{2})\d+$/, "$1");
}

// ========================================
// ABRIR / FECHAR PAINEL DE TURNOS
// ========================================

listaAlunos.addEventListener("click", async evento => {

    const botaoTurno =
        evento.target.closest(".btn-turno");

    const botaoVincular =
        evento.target.closest(".btn-vincular-turno");

    const botaoRemover =
        evento.target.closest(".btn-remover-turno");

    // ABRIR PAINEL
    if (botaoTurno) {

        const idAluno = botaoTurno.dataset.aluno;

        const area = document.getElementById(
            `turno-${idAluno}`
        );

        if (!area) return;

        const estavaAberto = area.style.display === "block";

        document.querySelectorAll(".area-turno").forEach(
            painel => painel.style.display = "none"
        );

        if (!estavaAberto) {

            area.style.display = "block";

            const select = area.querySelector(".select-turno");

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

        return;
    }

    // VINCULAR TURNO
    if (botaoVincular) {

        const idAluno = botaoVincular.dataset.aluno;

        const area = document.getElementById(
            `turno-${idAluno}`
        );

        const select = area.querySelector(".select-turno");
        const idTurno = select.value;

        if (!idTurno) {
            alert("Selecione um turno.");
            return;
        }

        try {

            const resposta = await fetch(
                `/alunos-turnos/cadastrar?idAluno=${idAluno}&idTurno=${idTurno}`,
                {
                    method: "POST"
                }
            );

            const mensagem = await resposta.text();

            if (!resposta.ok) {
                throw new Error(mensagem);
            }

            alert(mensagem);

            await carregarAlunos(filtroEmail.value);

        } catch (erro) {

            console.error("Erro ao vincular turno:", erro);

            alert(
                erro.message ||
                "Não foi possível vincular o turno."
            );
        }

        return;
    }

    // REMOVER TURNO
    if (botaoRemover) {

        const idAluno = botaoRemover.dataset.aluno;
        const idTurno = botaoRemover.dataset.turno;

        if (!confirm("Deseja remover este turno do aluno?")) {
            return;
        }

        try {

            const resposta = await fetch(
                `/alunos-turnos/remover?idAluno=${idAluno}&idTurno=${idTurno}`,
                {
                    method: "DELETE"
                }
            );

            const mensagem = await resposta.text();

            if (!resposta.ok) {
                throw new Error(mensagem);
            }

            alert(mensagem);

            await carregarAlunos(filtroEmail.value);

        } catch (erro) {

            console.error("Erro ao remover turno:", erro);

            alert(
                erro.message ||
                "Não foi possível remover o turno."
            );
        }
    }
});

// ========================================
// FILTRAR
// ========================================

btnFiltrar.addEventListener("click", () => {
    carregarAlunos(filtroEmail.value);
});

filtroEmail.addEventListener("keydown", evento => {
    if (evento.key === "Enter") {
        carregarAlunos(filtroEmail.value);
    }
});

// ========================================
// INICIALIZAÇÃO
// ========================================

carregarAlunos();