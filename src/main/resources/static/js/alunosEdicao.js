const formEditarAluno =
    document.getElementById("formEditarAluno");

const id =
    document.getElementById("id");

const nome =
    document.getElementById("nome");

const cpf =
    document.getElementById("cpf");

const email =
    document.getElementById("email");

const tipo =
    document.getElementById("tipo");

const alterarSenha =
    document.getElementById("alterarSenha");

const campoSenha =
    document.getElementById("campoSenha");

const senha =
    document.getElementById("senha");


// ========================================
// FORMATAR CPF
// ========================================

function formatarCPF(valor) {

    const apenasDigitos =
        valor.replace(/\D/g, "");

    return apenasDigitos
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d{1,2})/, "$1-$2")
        .replace(/(-\d{2})\d+$/, "$1");
}


// ========================================
// CPF
// ========================================

cpf.addEventListener("input", () => {

    cpf.value = formatarCPF(cpf.value);

});


// ========================================
// ID DO ALUNO
// ========================================

const parametros =
    new URLSearchParams(window.location.search);

const idAluno =
    parametros.get("id");


if (!idAluno) {

    alert("Aluno não informado.");

    window.location.href =
        "/pagina/alunos";
}


// ========================================
// CARREGAR ALUNO
// ========================================

async function carregarAluno() {

    try {

        const resposta = await fetch(
            `/alunos/editar/${encodeURIComponent(idAluno)}`
        );


        if (!resposta.ok) {

            throw new Error(
                "Não foi possível carregar o aluno."
            );
        }


        const aluno =
            await resposta.json();


        id.value =
            aluno.id;

        nome.value =
            aluno.nome ?? "";

        cpf.value =
            formatarCPF(aluno.cpf ?? "");

        email.value =
            aluno.email ?? "";

        tipo.value =
            aluno.tipo ?? "";


    } catch (erro) {

        console.error(
            "Erro ao carregar aluno:",
            erro
        );

        alert(erro.message);

        window.location.href =
            "/pagina/alunos";
    }
}


// ========================================
// MOSTRAR / ESCONDER SENHA
// ========================================

alterarSenha.addEventListener("change", () => {

    if (alterarSenha.checked) {

        campoSenha.style.display =
            "block";

        senha.required =
            true;

    } else {

        campoSenha.style.display =
            "none";

        senha.required =
            false;

        senha.value =
            "";
    }
});


// ========================================
// SALVAR ALTERAÇÕES
// ========================================

formEditarAluno.addEventListener(
    "submit",
    async (evento) => {

        evento.preventDefault();


        const aluno = {

            id: Number(id.value),

            nome: nome.value.trim(),

            cpf: cpf.value.trim(),

            email: email.value.trim(),

            tipo: tipo.value,

            senha: alterarSenha.checked
                ? senha.value
                : null
        };


        // ====================================
        // VALIDAÇÕES
        // ====================================

        if (!aluno.nome) {

            alert(
                "Informe o nome do aluno."
            );

            return;
        }


        if (!aluno.cpf) {

            alert(
                "Informe o CPF do aluno."
            );

            return;
        }


        if (!aluno.email) {

            alert(
                "Informe o e-mail do aluno."
            );

            return;
        }


        if (!aluno.tipo) {

            alert(
                "Selecione o tipo do aluno."
            );

            return;
        }


        if (
            alterarSenha.checked &&
            aluno.senha.length < 6
        ) {

            alert(
                "A nova senha deve possuir pelo menos 6 caracteres."
            );

            return;
        }


        // ====================================
        // ATUALIZAR
        // ====================================

        try {

            const resposta =
                await fetch(
                    "/alunos/editar",
                    {
                        method: "PUT",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body:
                            JSON.stringify(aluno)
                    }
                );


            const mensagem =
                await resposta.text();


            if (!resposta.ok) {

                throw new Error(
                    mensagem
                );
            }


            alert(mensagem);


            window.location.href =
                "/pagina/alunos";


        } catch (erro) {

            console.error(
                "Erro ao atualizar aluno:",
                erro
            );

            alert(
                erro.message ||
                "Não foi possível atualizar o aluno."
            );
        }

    }
);


// ========================================
// INICIALIZAÇÃO
// ========================================

carregarAluno();