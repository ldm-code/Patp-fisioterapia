const formHorario = document.getElementById("formHorario");

formHorario.addEventListener("submit", async (evento) => {

    evento.preventDefault();

    const horario = {

        horario: document.getElementById("horario").value,

        turnoId: Number(
            document.getElementById("turnoId").value
        )

    };

    try {

        const resposta = await fetch("/horarios/cadastrar", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(horario)

        });

        const mensagem = await resposta.text();

        if (!resposta.ok) {

            throw new Error(mensagem);

        }

        alert(mensagem);

        formHorario.reset();

    } catch (erro) {

        console.error(
            "Erro ao cadastrar horário:",
            erro
        );

        alert(
            erro.message ||
            "Não foi possível cadastrar o horário."
        );
    }

});