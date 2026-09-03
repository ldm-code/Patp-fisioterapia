
document.addEventListener("DOMContentLoaded", function () {

    const formulario = document.getElementById("formCadastro");

    const campoNome = document.getElementById("nome");

    const campoCpf = document.getElementById("cpf");

    const campoTelefone = document.getElementById("telefone");

    const botaoCadastrar = document.getElementById("enviar");


    // ============================================================
    // FORMATAR CPF
    // ============================================================

    function formatarCPF(valor) {

        const apenasDigitos = valor.replace(/\D/g, "");

        return apenasDigitos
            .replace(/(\d{3})(\d)/, "$1.$2")
            .replace(/(\d{3})(\d)/, "$1.$2")
            .replace(/(\d{3})(\d{1,2})/, "$1-$2")
            .replace(/(-\d{2})\d+$/, "$1");
    }


    // ============================================================
    // FORMATAR TELEFONE
    // ============================================================

    function formatarTelefone(valor) {

        const apenasDigitos = valor.replace(/\D/g, "");


        // Telefone com 11 dígitos

        if (apenasDigitos.length === 11) {

            return apenasDigitos.replace(
                /(\d{2})(\d{5})(\d{4})/,
                "($1) $2-$3"
            );
        }


        // Telefone com 10 dígitos

        if (apenasDigitos.length === 10) {

            return apenasDigitos.replace(
                /(\d{2})(\d{4})(\d{4})/,
                "($1) $2-$3"
            );
        }


        return valor;
    }


    // ============================================================
    // MÁSCARA DO CPF
    // ============================================================

    campoCpf.addEventListener("input", function () {

        campoCpf.value = formatarCPF(campoCpf.value);

    });


    // ============================================================
    // MÁSCARA DO TELEFONE
    // ============================================================

    campoTelefone.addEventListener("input", function () {

        campoTelefone.value =
            formatarTelefone(campoTelefone.value);

    });


    // ============================================================
    // CADASTRAR PACIENTE
    // ============================================================

    formulario.addEventListener("submit", async function (evento) {

        evento.preventDefault();


        // ========================================================
        // OBTÉM OS VALORES
        // ========================================================

        const nome = campoNome.value.trim();

        const cpf = campoCpf.value.trim();

        const telefone = campoTelefone.value.trim();


        // ========================================================
        // VALIDA CAMPOS VAZIOS
        // ========================================================

        if (!nome || !cpf || !telefone) {

            alert("Campo não preenchido.");

            return;
        }


        // ========================================================
        // REMOVE MÁSCARA DO CPF
        // ========================================================

        const cpfSemMascara =
            cpf.replace(/\D/g, "");


        // ========================================================
        // REMOVE MÁSCARA DO TELEFONE
        // ========================================================

        const telefoneSemMascara =
            telefone.replace(/\D/g, "");


        // ========================================================
        // VALIDA CPF
        // ========================================================

        if (!/^\d{11}$/.test(cpfSemMascara)) {

            alert(
                "CPF deve possuir exatamente 11 números."
            );

            return;
        }


        // ========================================================
        // VALIDA TELEFONE
        // ========================================================

        if (
            telefoneSemMascara.length !== 10 &&
            telefoneSemMascara.length !== 11
        ) {

            alert(
                "Telefone deve possuir 10 ou 11 números."
            );

            return;
        }


        // ========================================================
        // MONTA OS DADOS
        // ========================================================

        const dados = new URLSearchParams();

        dados.append("nome", nome);

        dados.append("cpf", cpfSemMascara);

        dados.append("telefone", telefoneSemMascara);


        // ========================================================
        // DESABILITA BOTÃO
        // ========================================================

        botaoCadastrar.disabled = true;

        botaoCadastrar.textContent = "Cadastrando...";


        try {

            // ====================================================
            // CHAMADA PARA O CONTROLLER
            // ====================================================

            const resposta = await fetch(
                "http://localhost:8080/pacientes",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },

                    body: dados
                }
            );


            // ====================================================
            // RESPOSTA DO CONTROLLER
            // ====================================================

            const mensagem = await resposta.text();


            // ====================================================
            // ERRO
            // ====================================================

            if (!resposta.ok) {

                alert(
                    mensagem ||
                    "Não foi possível cadastrar o paciente."
                );

                return;
            }


            // ====================================================
            // SUCESSO
            // ====================================================

            alert(
                mensagem ||
                "Paciente cadastrado com sucesso."
            );


            // Limpa o formulário

            formulario.reset();


            // Volta para a tela de pacientes

            window.location.href =
                "/pagina/pacientes";


        } catch (erro) {

            console.error(
                "Erro ao cadastrar paciente:",
                erro
            );

            alert(
                "Erro de comunicação com o servidor."
            );

        } finally {

            // Reativa o botão

            botaoCadastrar.disabled = false;

            botaoCadastrar.textContent = "Cadastrar";
        }

    });

});

