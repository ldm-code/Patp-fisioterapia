const btn=document.getElementById("btnSenha");
const input=document.getElementById("senha");
btn.addEventListener("click",function(){
          if (input.type==="password"){
                    input.type="text";
          }
          else{
                    input.type="password";
          }
})