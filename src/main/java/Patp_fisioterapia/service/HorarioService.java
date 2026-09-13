package Patp_fisioterapia.service;

import Patp_fisioterapia.dao.HorarioDAO;
import Patp_fisioterapia.dto.HorarioDTO;

import java.time.LocalTime;

public class HorarioService {

    private HorarioDAO horarioDAO = new HorarioDAO();

    public String cadastrarHorario(HorarioDTO horario) {

    if (horario.getHorario() == null ||
        horario.getHorario().trim().isEmpty()) {

        return "Informe um horário.";
    }

    if (horario.getTurnoId() < 1 ||
        horario.getTurnoId() > 3) {

        return "Selecione um turno válido.";
    }

    LocalTime hora;

    try {
        hora = LocalTime.parse(horario.getHorario());
    } catch (Exception e) {
        return "Informe um horário válido.";
    }

    LocalTime inicioManha = LocalTime.of(6, 0);
    LocalTime inicioTarde = LocalTime.of(12, 0);
    LocalTime inicioNoite = LocalTime.of(18, 0);

    if (horario.getTurnoId() == 1 &&
        (hora.isBefore(inicioManha) || !hora.isBefore(inicioTarde))) {

        return "O turno da manhã aceita horários entre 06:00 e 11:59.";
    }

    if (horario.getTurnoId() == 2 &&
        (hora.isBefore(inicioTarde) || !hora.isBefore(inicioNoite))) {

        return "O turno da tarde aceita horários entre 12:00 e 17:59.";
    }

    if (horario.getTurnoId() == 3 &&
        hora.isBefore(inicioNoite)) {

        return "O turno da noite aceita horários entre 18:00 e 23:59.";
    }

    if (horarioDAO.existeHorario(horario)) {
        return "Este horário já está cadastrado para este turno.";
    }

    if (horarioDAO.cadastrarHorario(horario)) {
        return "Horário cadastrado com sucesso.";
    }

    return "Não foi possível cadastrar o horário.";
}
}