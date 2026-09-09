const formAluno = document.getElementById("formAluno");
const cpf = document.getElementById("cpf");


function formatarCPF(valor) {

    const apenasDigitos = valor.replace(/\D/g, "");

    return apenasDigitos
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d{1,2})/, "$1-$2")
        .replace(/(-\d{2})\d+$/, "$1");
}


cpf.addEventListener("input", () => {

    cpf.value = formatarCPF(cpf.value);

});


formAluno.addEventListener("submit", async (evento) => {

    evento.preventDefault();


    const aluno = {

        nome: document.getElementById("nome").value,

        senha: document.getElementById("senha").value,

        cpf: cpf.value,

        email: document.getElementById("email").value,

        tipo: document.getElementById("tipo").value

    };


    try {

        const resposta = await fetch("/alunos/cadastrarAlunos", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(aluno)

        });


        const mensagem = await resposta.text();


        if (!resposta.ok) {

            throw new Error(mensagem);

        }


        alert(mensagem);


        formAluno.reset();


        window.location.href = "/pagina/alunos";


    } catch (erro) {

        console.error("Erro ao cadastrar aluno:", erro);

        alert(
            erro.message ||
            "Não foi possível cadastrar o aluno."
        );

    }

});

