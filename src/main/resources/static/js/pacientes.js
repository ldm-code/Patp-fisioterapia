function formatarCPF(valor) {

    const apenasDigitos = valor.replace(/\D/g, "");

    return apenasDigitos
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d{1,2})/, "$1-$2")
        .replace(/(-\d{2})\d+$/, "$1");
}


function formatarTelefone(valor) {

    const apenasDigitos = valor.replace(/\D/g, "");

    if (apenasDigitos.length === 11) {

        return apenasDigitos.replace(
            /(\d{2})(\d{5})(\d{4})/,
            "($1) $2-$3"
        );
    }

    if (apenasDigitos.length === 10) {

        return apenasDigitos.replace(
            /(\d{2})(\d{4})(\d{4})/,
            "($1) $2-$3"
        );
    }

    return valor;
}


document.addEventListener("DOMContentLoaded", function () {

    const btnPacientes =
        document.getElementById("btnPaciente");

    const listaPacientes =
        document.getElementById("lista-pacientes");

    const btnFiltrar =
        document.getElementById("btnFiltrar");

    const filtroNome =
        document.getElementById("filtroNome");

    const filtroCpf =
        document.getElementById("filtroCpf");


    // ============================================================
    // BOTÃO NOVO PACIENTE
    // ============================================================

    btnPacientes.addEventListener("click", function () {

        window.location.href = "/pagina/cadastro";

    });


    // ============================================================
    // BUSCAR PACIENTES
    // ============================================================

    async function buscarPacientes() {

        const nome = filtroNome.value.trim();
        const cpf = filtroCpf.value.replace(/\D/g, "").trim();

        let url = "http://localhost:8080/pacientes";


        // ========================================================
        // ADICIONAR FILTROS
        // ========================================================

        if (nome || cpf) {

            const parametros = new URLSearchParams();

            if (nome) {
                parametros.append("nome", nome);
            }

            if (cpf) {
                parametros.append("cpf", cpf);
            }

            url += "?" + parametros.toString();
        }


        try {

            const resposta = await fetch(url);

            if (!resposta.ok) {
                throw new Error("Erro ao buscar pacientes.");
            }

            const pacientes = await resposta.json();

            exibirPacientes(pacientes);

        } catch (erro) {

            console.error("Erro:", erro);

            listaPacientes.innerHTML = `
                <p>Não foi possível carregar os pacientes.</p>
            `;
        }
    }


    // ============================================================
    // EXIBIR PACIENTES
    // ============================================================

    function exibirPacientes(pacientes) {

        listaPacientes.innerHTML = "";

        if (pacientes.length === 0) {

            listaPacientes.innerHTML = `
                 <article class="paciente">
                <div class="dados-paciente">

                    <div class="icone">
                        <i class="bi bi-person-x"></i>
                    </div>

                    <div>
                        <h2>Nenhum paciente encontrado</h2>
                    </div>

                </div>
            </article>
            `;

            return;
        }


        pacientes.forEach(function (paciente) {

            const cpfFormatado =
                formatarCPF(paciente.cpf);

            const telefoneFormatado =
                formatarTelefone(paciente.telefone);


            const card =
                document.createElement("div");

            card.classList.add("card-paciente");


            card.innerHTML = `

                <article class="paciente">

                    <div class="dados-paciente">

                        <div class="icone">
                            <i class="bi bi-person"></i>
                        </div>

                        <div>

                            <h2>${paciente.nome}</h2>

                            <p>
                                <strong>CPF:</strong>
                                ${cpfFormatado}
                            </p>

                            <p>
                                <strong>Telefone:</strong>
                                ${telefoneFormatado}
                            </p>

                        </div>

                    </div>


                    <button
                        type="button"
                        class="btn-editar"
                        data-id="${paciente.id}"
                    >

                        <i class="bi bi-pencil"></i>

                        Editar

                    </button>

                </article>

            `;

            listaPacientes.appendChild(card);

        });


        adicionarEventosEditar();
    }


    // ============================================================
    // BOTÕES EDITAR
    // ============================================================

    function adicionarEventosEditar() {

        const botoesEditar =
            document.querySelectorAll(".btn-editar");


        botoesEditar.forEach(function (botao) {

            botao.addEventListener("click", function () {

                const id = botao.dataset.id;

                window.location.href =
                    `/editarPacientes?id=${id}`;

            });

        });
    }


    // ============================================================
    // BOTÃO FILTRAR
    // ============================================================

    btnFiltrar.addEventListener("click", function () {

        buscarPacientes();

    });


    // ============================================================
    // CARREGAR PACIENTES
    // ============================================================

    buscarPacientes();

});

