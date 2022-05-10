package br.edu.utfpr.carloseduardofreitas.modelo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.carloseduardofreitas.utils.UtilsDate;

import static androidx.room.ForeignKey.CASCADE;


@Entity(tableName = "horario", foreignKeys = {@ForeignKey(entity = Disciplina.class,parentColumns = "id",childColumns = "disciplinaId",onDelete = CASCADE)})

public class Horario implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String diaDaSemana;
    @NonNull
    private Date horarioIinicio;
    @NonNull
    private Date horarioFim;
    @ColumnInfo(index = true)
    private int disciplinaId;

    public Horario() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getDiaDaSemana() {
        return diaDaSemana;
    }

    public void setDiaDaSemana(@NonNull String diaDaSemana) {
        this.diaDaSemana = diaDaSemana;
    }

    @NonNull
    public Date getHorarioIinicio() {
        return horarioIinicio;
    }

    public void setHorarioIinicio(@NonNull Date horarioIinicio) {
        this.horarioIinicio = horarioIinicio;
    }

    @NonNull
    public Date getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(@NonNull Date horarioFim) {
        this.horarioFim = horarioFim;
    }

    public int getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(int disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    @Override
    public String toString() {
        return diaDaSemana+", "+ UtilsDate.formatTimeParaClasse(horarioIinicio)+" - "+UtilsDate.formatTimeParaClasse(horarioFim);
    }
}
