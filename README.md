# Sistema de Fisioterapia

Sistema web desenvolvido para o PATP da IDEAU do 4º semestre de Análise e Desenvolvimento de Sistemas (ADS).

O projeto tem como objetivo auxiliar no gerenciamento de **agendamentos de fisioterapia**, buscando solucionar dificuldades encontradas no processo de organização das consultas da faculdade.

## Tecnologias utilizadas

* **Java**
* **Spring Boot**
* **JDBC**
* **MySQL**
* **HTML**
* **CSS**
* **JavaScript**

## Arquitetura

A aplicação utiliza **Spring Boot** no backend, com acesso ao banco de dados realizado através de **JDBC** e **MySQL**.

O frontend é construído com **HTML, CSS e JavaScript**, utilizando **Thymeleaf** para integração das páginas com o backend.

A aplicação é organizada em camadas, separando responsabilidades entre componentes como controllers, services e DAOs.

## Banco de dados

O projeto utiliza **MySQL** para persistência dos dados.

Os scripts necessários para criação e configuração do banco de dados estão disponíveis no diretório:

```text
database/
```

## Configuração

As configurações de acesso ao banco de dados são definidas através de um arquivo:

```text
src/main/resources/application.properties
```

Por questões de segurança, **as credenciais do banco de dados não estão incluídas no repositório**.

Para executar o projeto localmente, crie ou configure o arquivo `application.properties` com as informações do seu próprio ambiente, por exemplo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nome_do_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

Substitua os valores pelos dados correspondentes ao banco de dados configurado na sua máquina.

> **Importante:** não publique senhas, credenciais ou outras informações sensíveis no repositório.

## Execução

Após configurar o banco de dados e o arquivo `application.properties`, o projeto pode ser executado utilizando o Maven Wrapper disponibilizado no repositório:

### Windows

```bash
./mvnw.cmd spring-boot:run
```

### Linux/macOS

```bash
./mvnw spring-boot:run
```

Após iniciar a aplicação, ela estará disponível localmente na porta 8080.

## Objetivo acadêmico

Conceitos Aprendidos:

* Desenvolvimento de aplicações web;
* Programação Orientada a Objetos;
* Spring Boot;
* Acesso a banco de dados com JDBC;
* Arquitetura em camadas;
* Desenvolvimento de interfaces web;
* Operações CRUD;
* Autenticação e controle de acesso.

Funcionalidades implementadas:

* Crud de Alunos.
* Cadastro de turnos.
* Cadastro de horarios.
* Crud de pacientes.
* Cadastro de turnos e especialidades para alunos.
* Seperação de acesso para Professor, Coordenador e Aluno.

## Autores do Projeto:


| Perfil | Nome | E-mail |
|---|---|---|
| ![Leonardo](https://github.com/ldm-code.png) | [Leonardo De Moraes](https://github.com/ldm-code) | [demoraesleonardo327@gmail.com](mailto:demoraesleonardo327@gmail.com) |
| ![Kevin](https://github.com/dilaozinhu.png) | [Kevin Sckariot](https://github.com/dilaozinhu) | [KevinScariot@Outlook.com](mailto:KevinScariot@Outlook.com) |
| ![Eduardo](https://github.com/EDUARDOPEREIRA2644.png) | [Eduardo](https://github.com/EDUARDOPEREIRA2644) | [eduardoserginho26@gmail.com](mailto:eduardoserginho26@gmail.com) |
| ![Nicolas](https://github.com/NicolasCCole.png) | [Nicolas Cole](https://github.com/NicolasCCole) | [nicolascole543@gmail.com](mailto:nicolascole543@gmail.com) |
| ![João Victor](https://github.com/JoaoVictorSilvestri.png) | [João Victor Silvestri](https://github.com/JoaoVictorSilvestri) | [joaovictorsilvestri59@gmail.com](mailto:joaovictorsilvestri59@gmail.com) |
