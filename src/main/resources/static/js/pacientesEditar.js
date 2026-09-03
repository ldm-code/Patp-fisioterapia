document.addEventListener("DOMContentLoaded", function () {

    const formulario = document.getElementById("formulario");

    const campoId = document.getElementById("id");
    const campoNome = document.getElementById("nome");
    const campoCpf = document.getElementById("cpf");
    const campoTelefone = document.getElementById("telefone");


    // ============================================================
    // OBTÉM O ID DA URL
    // ============================================================

    const parametros = new URLSearchParams(
        window.location.search
    );

    const id = parametros.get("id");


    // ============================================================
    // VALIDA O ID
    // ============================================================

    if (!id || Number(id) <= 0) {

        alert("ID do paciente inválido.");

        window.location.href = "/pagina/pacientes";

        return;
    }


    // Coloca o ID no campo hidden
    campoId.value = id;


    // ============================================================
    // BUSCAR PACIENTE
    // ============================================================

    async function buscarPaciente() {

        try {

            const resposta = await fetch(
                `http://localhost:8080/pacientes/${id}`
            );


            if (!resposta.ok) {

                const mensagem = await resposta.text();

                alert(
                    mensagem || "Paciente não encontrado."
                );

                window.location.href = "/pagina/pacientes";

                return;
            }


            const paciente = await resposta.json();


            // ====================================================
            // PREENCHE OS CAMPOS
            // ====================================================

            campoNome.value = paciente.nome;
            campoCpf.value = paciente.cpf;
            campoTelefone.value = paciente.telefone;


        } catch (erro) {

            console.error("Erro:", erro);

            alert(
                "Não foi possível carregar os dados do paciente."
            );
        }
    }


    // ============================================================
    // EDITAR PACIENTE
    // ============================================================

    formulario.addEventListener(
        "submit",
        async function (evento) {

            evento.preventDefault();


            const nome = campoNome.value.trim();
            const cpf = campoCpf.value.trim();
            const telefone = campoTelefone.value.trim();


            // ====================================================
            // VALIDAÇÃO
            // ====================================================

            if (!nome || !cpf || !telefone) {

                alert("Campo não preenchido.");

                return;
            }


            // ====================================================
            // MONTA OS DADOS
            // ====================================================

            const dados = new URLSearchParams();

            dados.append("nome", nome);
            dados.append("cpf", cpf);
            dados.append("telefone", telefone);


            try {

                const resposta = await fetch(
                    `http://localhost:8080/pacientes/${id}`,
                    {
                        method: "PUT",

                        headers: {
                            "Content-Type":
                                "application/x-www-form-urlencoded"
                        },

                        body: dados
                    }
                );


                // =================================================
                // ERRO
                // =================================================

                if (!resposta.ok) {

                    const mensagem =
                        await resposta.text();

                    alert(
                        mensagem ||
                        "Não foi possível atualizar o paciente."
                    );

                    return;
                }


                // =================================================
                // SUCESSO
                // =================================================

                const mensagem =
                    await resposta.text();

                alert(mensagem);

                window.location.href =
                    "/pagina/pacientes";


            } catch (erro) {

                console.error("Erro:", erro);

                alert(
                    "Erro de comunicação com o servidor."
                );
            }

        }
    );


    // ============================================================
    // CARREGA OS DADOS
    // ============================================================

    buscarPaciente();

});

