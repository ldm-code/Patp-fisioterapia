
const listaAlunos = document.getElementById("listaAlunos");
const filtroEmail = document.getElementById("filtroEmail");
const btnFiltrar = document.getElementById("btnFiltrar");
const btnAluno = document.getElementById("btnNovoAluno");

btnAluno.addEventListener("click", () => {
    window.location.href = "/alunos/cadastrarAlunos";
});


function formatarCPF(valor) {

    const apenasDigitos = valor.replace(/\D/g, "");

    return apenasDigitos
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d{1,2})/, "$1-$2")
        .replace(/(-\d{2})\d+$/, "$1");
}


function formatarTipo(tipo) {

    if (tipo === "estagio") {
        return "Estágio";
    }

    if (tipo === "curso") {
        return "Cursando";
    }

    return tipo;
}


/*
 * Carrega as especialidades no select
 */
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


/*
 * Clique em Adicionar Especialidade
 *
 * Como os cards são criados dinamicamente,
 * usamos delegação de eventos.
 */
listaAlunos.addEventListener("click", async (evento) => {

    const botaoEspecialidade =
        evento.target.closest(".btn-especialidade");
    const botaoRemover =
    evento.target.closest(".btn-remover-especialidade");

        if (botaoRemover) {

            const idAluno = botaoRemover.dataset.aluno;
            const idEspecialidade =
                botaoRemover.dataset.especialidade;

            const confirmar =
                confirm("Deseja remover esta especialidade?");

            if (!confirmar) {
                return;
            }

            try {

                const resposta = await fetch(
                    `/alunos-especialidade/remover?idAluno=${idAluno}&idEspecialidade=${idEspecialidade}`,
                    {
                        method: "DELETE"
                    }
                );

                const mensagem = await resposta.text();

                if (!resposta.ok) {
                    throw new Error(mensagem);
                }

                alert(mensagem);

                window.location.reload();

            } catch (erro) {

                console.error(
                    "Erro ao remover especialidade:",
                    erro
                );

                alert(
                    erro.message ||
                    "Não foi possível remover a especialidade."
                );
            }
        }

    if (botaoEspecialidade) {

        const idAluno = botaoEspecialidade.dataset.id;

        const area =
            document.getElementById(`especialidade-${idAluno}`);

        if (!area) {
            return;
        }

        /*
         * Fecha outras áreas abertas
         */
        document
            .querySelectorAll(".area-especialidade")
            .forEach(areaAberta => {

                if (areaAberta !== area) {
                    areaAberta.style.display = "none";
                }

            });


        /*
         * Abre/fecha a área do aluno clicado
         */
        if (area.style.display === "none") {

            area.style.display = "block";

            const select =
                area.querySelector(".select-especialidade");

            if (select && select.options.length <= 1) {

                await carregarEspecialidades(select);

            }

        } else {

            area.style.display = "none";

        }

        return;
    }


    /*
     * Clique em Vincular
     */
    const botaoVincular =
        evento.target.closest(".btn-vincular");

    if (botaoVincular) {

        const idAluno = botaoVincular.dataset.id;

        const area =
            document.getElementById(`especialidade-${idAluno}`);

        const select =
            area.querySelector(".select-especialidade");

        const idEspecialidade = select.value;


        if (!idEspecialidade) {

            alert("Selecione uma especialidade.");

            return;
        }


        try {

            const resposta = await fetch(
                `/alunos-especialidade/cadastrar?idAluno=${idAluno}&idEspecialidade=${idEspecialidade}`,
                {
                    method: "POST"
                }
            );


            const mensagem = await resposta.text();


            if (!resposta.ok) {

                throw new Error(mensagem);
            }


            alert(mensagem);

            /*
             * Fecha o painel depois de vincular
             */
            area.style.display = "none";

            select.value = "";
            window.location.reload();

        } catch (erro) {

            console.error(
                "Erro ao vincular especialidade:",
                erro
            );

            alert(
                erro.message ||
                "Não foi possível vincular a especialidade."
            );
        }

    }

});


async function carregarAlunos(email = "") {

    try {

        let url = "/alunos";

        if (email.trim() !== "") {

            url =
                `/alunos/email?email=${encodeURIComponent(email)}`;
        }


        const resposta = await fetch(url);

        if (!resposta.ok) {

            throw new Error("Erro ao buscar alunos");
        }


        const alunos = await resposta.json();
        

        listaAlunos.innerHTML = "";


        if (alunos.length > 0) {

            alunos.forEach(aluno => {
                console.log(aluno.especialidades)
                const card =
                    document.createElement("article");

                card.classList.add("aluno");


                card.innerHTML = `

                    <div class="dados-aluno">

                        <div class="icone">
                            <i class="bi bi-person"></i>
                        </div>

                        <div class="informacoes">

                            <h2>${aluno.nome}</h2>

                            <p>
                                <strong>ID:</strong>
                                ${aluno.id}
                            </p>

                            <p>
                                <strong>CPF:</strong>
                                ${formatarCPF(aluno.cpf)}
                            </p>

                            <p>
                                <strong>E-mail:</strong>
                                ${aluno.email}
                            </p>

                        </div>

                    </div>


                    <div class="especialidades-aluno">
                    
                    <strong>Especialidades:</strong><br><br>

                   ${aluno.especialidades &&
aluno.especialidades.length > 0 &&
aluno.especialidades.some(especialidade => especialidade.nome)
    ? aluno.especialidades
        .filter(especialidade => especialidade.nome)
        .map(especialidade => `
            <span class="especialidade-item">
                ${especialidade.nome}
                <button
                    type="button"
                    class="btn-remover-especialidade"
                    data-aluno="${aluno.id}"
                    data-especialidade="${especialidade.id}"
                >
                <i class="bi bi-x-lg"></i>
                </button>
                </span>
                `)
        .join("<br>")
    : "Nenhuma cadastrada"
}

</div>
<div class="tipo-aluno">

    <span class="tipo-badge ${aluno.tipo}">
        ${formatarTipo(aluno.tipo)}
    </span>

</div>


                    <div class="acoes-aluno">

                        <button
                            type="button"
                            class="btn-especialidade"
                            title="Vincular especialidade"
                            data-id="${aluno.id}"
                        >

                            <i class="bi bi-bookmark-plus"></i>

                            Adicionar Especialidade

                        </button>


                        <button
                            type="button"
                            class="btn-editar"
                            title="Editar aluno"
                            data-id="${aluno.id}"
                        >

                            <i class="bi bi-pencil"></i>

                            Editar

                        </button>

                    </div>


                    <!--
                        Área que aparece embaixo
                        do aluno selecionado
                    -->
                    <div
                        class="area-especialidade"
                        id="especialidade-${aluno.id}"
                        style="display: none;"
                    >

                        <div class="card-especialidade">

                            <div class="titulo-especialidade">

                                <i class="bi bi-bookmark-plus"></i>

                                <div>

                                    <h3>
                                        Adicionar especialidade
                                    </h3>

                                    <p>
                                        Selecione uma especialidade
                                        para este aluno.
                                    </p>

                                </div>

                            </div>


                            <div class="form-especialidade">

                                <select
                                    class="select-especialidade"
                                    data-id="${aluno.id}"
                                >

                                    <option
                                        value=""
                                        selected
                                        disabled
                                    >
                                        Selecione uma especialidade
                                    </option>

                                </select>


                                <button
                                    type="button"
                                    class="btn-vincular"
                                    data-id="${aluno.id}"
                                >

                                    <i class="bi bi-check-lg"></i>

                                    Vincular

                                </button>

                            </div>

                        </div>

                    </div>

                `;


                listaAlunos.appendChild(card);

            });

        } else {

            listaAlunos.innerHTML = `

                <div class="nenhum-aluno">

                    <i class="bi bi-person-x"></i>

                    <p>
                        Nenhum aluno foi encontrado.
                    </p>

                </div>

            `;
        }


    } catch (erro) {

        console.error("Erro:", erro);

        listaAlunos.innerHTML = `

            <p>
                Não foi possível carregar os alunos.
            </p>

        `;
    }
}


btnFiltrar.addEventListener("click", () => {

    carregarAlunos(filtroEmail.value);

});


carregarAlunos();

