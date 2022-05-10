package br.edu.utfpr.carloseduardofreitas.modelo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "atividade", foreignKeys = @ForeignKey(entity = Disciplina.class,
                                  parentColumns = "id",
                                  childColumns = "disciplinaId"))
public class Atividade {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String titulo;

    @NonNull
    private String descricao;

    @NonNull
    private Date dataEntrega;

    @NonNull
    private Date horarioEntrega;

    @ColumnInfo(index = true)
    private int disciplinaId;


    public Atividade(@NonNull String titulo, @NonNull String descricao, @NonNull Date dataEntrega, @NonNull Date horarioEntrega) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataEntrega = dataEntrega;
        this.horarioEntrega = horarioEntrega;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(@NonNull String titulo) {
        this.titulo = titulo;
    }

    @NonNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NonNull String descricao) {
        this.descricao = descricao;
    }

    @NonNull
    public Date getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(@NonNull Date dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    @NonNull
    public Date getHorarioEntrega() {
        return horarioEntrega;
    }

    public void setHorarioEntrega(@NonNull Date horarioEntrega) {
        this.horarioEntrega = horarioEntrega;
    }

    public int getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(int disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    @Override
    public String toString() {
        return "Atividade{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", dataEntrega=" + dataEntrega +
                ", horarioEntrega=" + horarioEntrega +
                ", disciplinaId=" + disciplinaId +
                '}';
    }
}
