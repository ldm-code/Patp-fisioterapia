document.addEventListener("DOMContentLoaded", function () {

    const mensagemErro = document.getElementById("mensagemErro");

    if (mensagemErro && mensagemErro.textContent.trim() !== "") {
        alert(mensagemErro.textContent);
    }
    const selectEspecialidade =
    document.getElementById("especialidade");

async function carregarEspecialidades() {
    try {
        const resposta = await fetch("/especialidades");

        if (!resposta.ok) {
            throw new Error("Erro ao carregar especialidades");
        }

        const especialidades = await resposta.json();

        especialidades.forEach(especialidade => {
            const option = document.createElement("option");

            option.value = especialidade.id;
            option.textContent = especialidade.nome;

            selectEspecialidade.appendChild(option);
        });

    } catch (erro) {
        console.error(erro);
        alert("Não foi possível carregar as especialidades.");
    }
}

carregarEspecialidades();

});