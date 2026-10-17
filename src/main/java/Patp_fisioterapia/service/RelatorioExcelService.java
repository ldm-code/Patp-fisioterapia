
package Patp_fisioterapia.service;

import Patp_fisioterapia.dto.ConsultaDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RelatorioExcelService {

    private static final String[] COLUNAS = {
        "ID da consulta",
        "Aluno",
        "Paciente",
        "Data de agendamento",
        "Data da consulta",
        "Motivo da consulta",
        "Motivo do cancelamento",
        "Status",
        "Diagnóstico",
        "Descrição do relatório"
    };

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] gerarExcel(List<ConsultaDTO> consultas)
            throws IOException {

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Consultas");

            // Estilo do título.
            CellStyle estiloTitulo = workbook.createCellStyle();
            estiloTitulo.setFillForegroundColor(
                    IndexedColors.DARK_BLUE.getIndex());
            estiloTitulo.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND);

            Font fonteTitulo = workbook.createFont();
            fonteTitulo.setBold(true);
            fonteTitulo.setFontHeightInPoints((short) 16);
            fonteTitulo.setColor(IndexedColors.WHITE.getIndex());
            estiloTitulo.setFont(fonteTitulo);
            estiloTitulo.setAlignment(HorizontalAlignment.CENTER);
            estiloTitulo.setVerticalAlignment(
                    VerticalAlignment.CENTER);

            // Estilo do cabeçalho.
            CellStyle estiloCabecalho = workbook.createCellStyle();
            estiloCabecalho.setFillForegroundColor(
                    IndexedColors.GREEN.getIndex());
            estiloCabecalho.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND);

            Font fonteCabecalho = workbook.createFont();
            fonteCabecalho.setBold(true);
            fonteCabecalho.setColor(IndexedColors.WHITE.getIndex());
            estiloCabecalho.setFont(fonteCabecalho);

            estiloCabecalho.setAlignment(HorizontalAlignment.CENTER);
            estiloCabecalho.setVerticalAlignment(
                    VerticalAlignment.CENTER);
            estiloCabecalho.setWrapText(true);
            adicionarBordas(estiloCabecalho);

            // Estilo das células de dados.
            CellStyle estiloDados = workbook.createCellStyle();
            estiloDados.setVerticalAlignment(
                    VerticalAlignment.TOP);
            estiloDados.setWrapText(true);
            adicionarBordas(estiloDados);

            // Título.
            Row linhaTitulo = sheet.createRow(0);
            linhaTitulo.setHeightInPoints(30);

            Cell titulo = linhaTitulo.createCell(0);
            titulo.setCellValue(
                    "RELATÓRIO DE CONSULTAS FISIOTERAPÊUTICAS");
            titulo.setCellStyle(estiloTitulo);

            sheet.addMergedRegion(
                    new CellRangeAddress(0, 0, 0, COLUNAS.length - 1));

            // Cabeçalhos.
            Row cabecalho = sheet.createRow(2);
            cabecalho.setHeightInPoints(35);

            for (int i = 0; i < COLUNAS.length; i++) {
                Cell celula = cabecalho.createCell(i);
                celula.setCellValue(COLUNAS[i]);
                celula.setCellStyle(estiloCabecalho);
            }

            // Dados.
            int numeroLinha = 3;

            for (ConsultaDTO consulta : consultas) {

                Row linha = sheet.createRow(numeroLinha++);

                criarCelula(linha, 0,
                        String.valueOf(consulta.getId()),
                        estiloDados);

                criarCelula(linha, 1,
                        consulta.getNomeAluno(),
                        estiloDados);

                criarCelula(linha, 2,
                        consulta.getNomePaciente(),
                        estiloDados);

                criarCelula(linha, 3,
                        formatarData(consulta.getDataAgendada()),
                        estiloDados);

                criarCelula(linha, 4,
                        formatarData(consulta.getDataConsulta()),
                        estiloDados);

                criarCelula(linha, 5,
                        consulta.getMotivo(),
                        estiloDados);

                criarCelula(linha, 6,
                        consulta.getMotivoCancelamento(),
                        estiloDados);

                criarCelula(linha, 7,
                        consulta.getStatus(),
                        estiloDados);

                criarCelula(linha, 8,
                        consulta.getDiagnostico(),
                        estiloDados);

                criarCelula(linha, 9,
                        consulta.getDescricaoRelatorio(),
                        estiloDados);
            }

            // Permite filtrar as colunas no Excel.
            int ultimaLinha = Math.max(2, numeroLinha - 1);

            sheet.setAutoFilter(
                    new CellRangeAddress(
                            2, ultimaLinha, 0, COLUNAS.length - 1));

            // Mantém o cabeçalho visível durante a rolagem.
            sheet.createFreezePane(0, 3);

            // Ajusta a largura das colunas.
            for (int i = 0; i < COLUNAS.length; i++) {
                sheet.autoSizeColumn(i);

                int largura = sheet.getColumnWidth(i);
                sheet.setColumnWidth(
                        i, Math.min(largura + 1000, 15000));
            }

            // Dá mais espaço aos campos de texto.
            sheet.setColumnWidth(5, 9000);
            sheet.setColumnWidth(6, 8000);
            sheet.setColumnWidth(8, 7000);
            sheet.setColumnWidth(9, 10000);

            workbook.write(output);

            return output.toByteArray();
        }
    }

    private void criarCelula(
            Row linha,
            int coluna,
            String valor,
            CellStyle estilo) {

        Cell celula = linha.createCell(coluna);

        celula.setCellValue(
                valor == null || valor.isBlank()
                        ? "-"
                        : valor
        );

        celula.setCellStyle(estilo);
    }

    private String formatarData(
            java.time.LocalDateTime data) {

        return data == null
                ? "-"
                : data.format(FORMATO_DATA);
    }

    private void adicionarBordas(CellStyle estilo) {
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);

        estilo.setTopBorderColor(
                IndexedColors.GREY_50_PERCENT.getIndex());
        estilo.setBottomBorderColor(
                IndexedColors.GREY_50_PERCENT.getIndex());
        estilo.setLeftBorderColor(
                IndexedColors.GREY_50_PERCENT.getIndex());
        estilo.setRightBorderColor(
                IndexedColors.GREY_50_PERCENT.getIndex());
    }
}
