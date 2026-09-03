document.addEventListener("DOMContentLoaded", function () {

    const mensagemErro = document.getElementById("mensagemErro");

    if (mensagemErro && mensagemErro.textContent.trim() !== "") {
        alert(mensagemErro.textContent);
    }

});