document.addEventListener("DOMContentLoaded", function () {

    const formulario = document.getElementById("formulario");

    const campoId = document.getElementById("id");
    const campoNome = document.getElementById("nome");
    const campoCpf = document.getElementById("cpf");
    const campoTelefone = document.getElementById("telefone");
    function formatarCPF(valor) {

    const apenasDigitos = valor.replace(/\D/g, "");

    return apenasDigitos
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d{1,2})/, "$1-$2")
        .replace(/(-\d{2})\d+$/, "$1");
}


function formatarTelefone(valor) {
    let apenasDigitos = valor.replace(/\D/g, "");

    // Limita a 11 dígitos

    if (apenasDigitos.length <= 10) {
        return apenasDigitos
            .replace(/(\d{2})(\d)/, "($1) $2")
            .replace(/(\d{4})(\d)/, "$1-$2");
    }else{

        return apenasDigitos.replace(
            /(\d{2})(\d{5})(\d{4})/,
            "($1) $2-$3"
        );
    }

}
campoCpf.addEventListener("input", function () {
    campoCpf.value = formatarCPF(campoCpf.value).slice(0, 14);
});

campoTelefone.addEventListener("input", function () {
    campoTelefone.value = formatarTelefone(campoTelefone.value).slice(0,15);
});



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
            campoCpf.value = formatarCPF(paciente.cpf);
            campoTelefone.value = formatarTelefone(paciente.telefone);


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
            const cpfSemMascara = cpf.replace(/\D/g, "");
            const telefoneSemMascara = telefone.replace(/\D/g, "");

            dados.append("nome", nome);
            dados.append("cpf", cpfSemMascara);
            dados.append("telefone", telefoneSemMascara);


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

