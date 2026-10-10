package Patp_fisioterapia.controller;

import Patp_fisioterapia.dto.RelatorioDTO;
import Patp_fisioterapia.service.ArquivoExameService;
import Patp_fisioterapia.service.RelatorioService;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final ArquivoExameService arquivoExameService;

    public RelatorioController(
            RelatorioService relatorioService,
            ArquivoExameService arquivoExameService) {
        this.relatorioService = relatorioService;
        this.arquivoExameService = arquivoExameService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> cadastrar(
            @RequestParam int consultaId,
            @RequestParam String descricao,
            @RequestParam(required = false) MultipartFile anexo,
            HttpSession session) {

        if (!"aluno".equals(
                String.valueOf(session.getAttribute("tipoUsuario")))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
        }

        Integer idAluno = obterIdAluno(session);
        if (idAluno == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Aluno não identificado.");
        }

        RelatorioDTO relatorio = new RelatorioDTO();
        relatorio.setConsultaId(consultaId);
        relatorio.setDescricao(descricao);

        String nomeArquivo = null;

        try {
            if (anexo != null && !anexo.isEmpty()) {
                nomeArquivo = arquivoExameService.salvar(anexo);
                relatorio.setAnexoExame(nomeArquivo);
            }

            String mensagem = relatorioService.cadastrar(relatorio, idAluno);

            if ("Relatório cadastrado com sucesso.".equals(mensagem)) {
                return ResponseEntity.status(HttpStatus.CREATED).body(mensagem);
            }

            arquivoExameService.excluir(nomeArquivo);
            return ResponseEntity.badRequest().body(mensagem);

        } catch (IllegalArgumentException e) {
            arquivoExameService.excluir(nomeArquivo);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            arquivoExameService.excluir(nomeArquivo);
            return ResponseEntity.internalServerError()
                .body("Não foi possível armazenar o anexo.");
        }
    }

    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<?> buscarPorConsulta(
            @PathVariable int consultaId,
            HttpSession session) {

        if (session.getAttribute("tipoUsuario") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Usuário não identificado.");
        }

        RelatorioDTO relatorio =
            relatorioService.buscarPorConsulta(consultaId);

        if (relatorio == null) {
            return ResponseEntity.notFound().build();
        }

        if (!podeAcessarRelatorio(relatorio.getId(), session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
        }

        return ResponseEntity.ok(relatorio);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> atualizarStatus(
            @PathVariable int id,
            @RequestParam String status,
            @RequestParam(required = false) String observacao,
            HttpSession session) {

        Object tipo = session.getAttribute("tipoUsuario");

        if (tipo == null || "aluno".equals(tipo.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
        }

        String mensagem =
            relatorioService.atualizarStatus(id, status, observacao);

        if ("Status do relatório atualizado com sucesso.".equals(mensagem)) {
            return ResponseEntity.ok(mensagem);
        }

        return ResponseEntity.badRequest().body(mensagem);
    }

    @PutMapping(
        value = "/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> atualizar(
            @PathVariable int id,
            @RequestParam String descricao,
            @RequestParam(required = false) MultipartFile anexo,
            HttpSession session) {

        if (!"aluno".equals(
                String.valueOf(session.getAttribute("tipoUsuario")))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
        }

        Integer idAluno = obterIdAluno(session);
        if (idAluno == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Aluno não identificado.");
        }

        String novoAnexo = null;

        try {
            if (anexo != null && !anexo.isEmpty()) {
                novoAnexo = arquivoExameService.salvar(anexo);
            }

            String mensagem = relatorioService.atualizarDescricao(
                id, descricao, idAluno, novoAnexo
            );

            if ("Relatório atualizado com sucesso.".equals(mensagem)) {
                return ResponseEntity.ok(mensagem);
            }

            arquivoExameService.excluir(novoAnexo);
            return ResponseEntity.badRequest().body(mensagem);

        } catch (IllegalArgumentException e) {
            arquivoExameService.excluir(novoAnexo);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            arquivoExameService.excluir(novoAnexo);
            return ResponseEntity.internalServerError()
                .body("Não foi possível armazenar o anexo.");
        }
    }

    @GetMapping("/{id}/anexo")
    public ResponseEntity<?> baixarAnexo(
            @PathVariable int id,
            HttpSession session) {

        RelatorioDTO relatorio = relatorioService.buscarPorId(id);

        if (relatorio == null || relatorio.getAnexoExame() == null) {
            return ResponseEntity.notFound().build();
        }

        if (!podeAcessarRelatorio(id, session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
        }

        try {
            Path caminho =
                arquivoExameService.localizar(relatorio.getAnexoExame());

            Resource recurso = new UrlResource(caminho.toUri());

            String tipo = Files.probeContentType(caminho);
            MediaType mediaType = tipo == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(tipo);

            return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment()
                        .filename(caminho.getFileName().toString())
                        .build()
                        .toString()
                )
                .body(recurso);

        } catch (IOException | IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/aluno")
    public ResponseEntity<?> listarRelatoriosAluno(
            @RequestParam(required = false) String status,
            HttpSession session) {

        if (!"aluno".equals(
                String.valueOf(session.getAttribute("tipoUsuario")))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
        }

        Integer idAluno = obterIdAluno(session);
        if (idAluno == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Aluno não identificado.");
        }

        return ResponseEntity.ok(
            relatorioService.listarPorAluno(idAluno, status)
        );
    }

    @GetMapping("/coordenador")
    public ResponseEntity<?> listarRelatoriosCoordenador(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            HttpSession session) {

        Object tipo = session.getAttribute("tipoUsuario");

        if (tipo == null || !"coordenador".equals(tipo.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
        }

        List<RelatorioDTO> relatorios =
            relatorioService.listarPorCoordenador(email, status);

        return ResponseEntity.ok(relatorios);
    }

    private Integer obterIdAluno(HttpSession session) {
        Object valor = session.getAttribute("idAluno");

        if (valor == null) {
            return null;
        }

        try {
            return Integer.parseInt(valor.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean podeAcessarRelatorio(
            int idRelatorio,
            HttpSession session) {

        Object tipo = session.getAttribute("tipoUsuario");

        if (tipo == null) {
            return false;
        }

        if ("aluno".equals(tipo.toString())) {
            Integer idAluno = obterIdAluno(session);

            return idAluno != null
                && relatorioService.anexoPertenceAoAluno(
                    idRelatorio, idAluno
                );
        }

        // O projeto pode ter outros perfis autorizados.
        // O acesso a esses perfis deve ser limitado às permissões reais.
        return "coordenador".equals(tipo.toString())
            || "professor".equals(tipo.toString());
    }
}