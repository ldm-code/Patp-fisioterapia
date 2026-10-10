package Patp_fisioterapia.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArquivoExameService {

    private static final long TAMANHO_MAXIMO = 20L * 1024 * 1024;

    private static final Set<String> EXTENSOES_PERMITIDAS = Set.of(
        "pdf", "png", "jpg", "jpeg", "gif", "bmp",
        "tif", "tiff", "webp", "dcm", "dicom",
        "zip", "7z", "rar", "doc", "docx",
        "odt", "xls", "xlsx", "ods", "txt", "csv"
    );

    @Value("${patp.arquivos.diretorio}")
    private String diretorioConfigurado;

    public String salvar(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            return null;
        }

        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException(
                "O arquivo excede o limite de 20 MB."
            );
        }

        String nomeOriginal = arquivo.getOriginalFilename();

        if (nomeOriginal == null || nomeOriginal.isBlank()) {
            throw new IllegalArgumentException("Nome de arquivo inválido.");
        }

        // Descarta qualquer caminho enviado pelo navegador.
        nomeOriginal = Paths.get(nomeOriginal).getFileName().toString();

        int ponto = nomeOriginal.lastIndexOf('.');
        if (ponto < 1 || ponto == nomeOriginal.length() - 1) {
            throw new IllegalArgumentException(
                "O arquivo precisa possuir uma extensão válida."
            );
        }

        String extensao = nomeOriginal.substring(ponto + 1)
            .toLowerCase(Locale.ROOT);

        if (!EXTENSOES_PERMITIDAS.contains(extensao)) {
            throw new IllegalArgumentException(
                "Formato de arquivo não permitido."
            );
        }

        Path diretorio = Paths.get(diretorioConfigurado)
            .toAbsolutePath()
            .normalize();

        Files.createDirectories(diretorio);

        String nomeGerado = UUID.randomUUID() + "." + extensao;
        Path destino = diretorio.resolve(nomeGerado).normalize();

        if (!destino.startsWith(diretorio)) {
            throw new IllegalArgumentException("Caminho de arquivo inválido.");
        }

        arquivo.transferTo(destino);
        return nomeGerado;
    }

    public Path localizar(String nomeArquivo) throws IOException {
        if (nomeArquivo == null
                || !nomeArquivo.matches(
                    "[a-fA-F0-9-]+\\.[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("Arquivo inválido.");
        }

        Path diretorio = Paths.get(diretorioConfigurado)
            .toAbsolutePath()
            .normalize();

        Path caminho = diretorio.resolve(nomeArquivo).normalize();

        if (!caminho.startsWith(diretorio)
                || !Files.isRegularFile(caminho)) {
            throw new IOException("Arquivo não encontrado.");
        }

        return caminho;
    }

    public void excluir(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(localizar(nomeArquivo));
        } catch (IOException | IllegalArgumentException e) {
            // Não mascara a resposta principal por falha de limpeza.
            System.err.println(
                "Não foi possível remover o arquivo temporário: "
                + e.getMessage()
            );
        }
    }
}