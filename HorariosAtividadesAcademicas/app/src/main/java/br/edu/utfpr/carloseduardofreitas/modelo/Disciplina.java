package br.edu.utfpr.carloseduardofreitas.modelo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

import br.edu.utfpr.carloseduardofreitas.R;

@Entity(tableName ="disciplina",indices = @Index(value = {"cod_disciplina"},unique = true))

public class Disciplina {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String cod_disciplina;
    @NonNull
    private String nome_disciplina;
    @NonNull
    private String nome_professor;
    @NonNull
    private String nome_curso;
    @NonNull
    private int envio_notificacoes;
    @NonNull
    private boolean ativar_alarme;
    @NonNull
    private String campo_estudo;
   // @Nullable
   // private ArrayList<Horario> horarios = new ArrayList<>();

    public Disciplina() {
    }

    public Disciplina(@NonNull String cod_disciplina, @NonNull String nome_disciplina, @NonNull String nome_professor, @NonNull String nome_curso, int envio_notificacoes, boolean ativar_alarme, @NonNull String campo_estudo) {
        this.cod_disciplina = cod_disciplina;
        this.nome_disciplina = nome_disciplina;
        this.nome_professor = nome_professor;
        this.nome_curso = nome_curso;
        this.envio_notificacoes = envio_notificacoes;
        this.ativar_alarme = ativar_alarme;
        this.campo_estudo = campo_estudo;
    }

    public Disciplina(@NonNull String cod_disciplina, @NonNull String nome_disciplina, @NonNull String nome_professor, @NonNull String nome_curso, int envio_notificacoes, boolean ativar_alarme, @NonNull String campo_estudo, @Nullable ArrayList<Horario> horarios) {
        this.cod_disciplina = cod_disciplina;
        this.nome_disciplina = nome_disciplina;
        this.nome_professor = nome_professor;
        this.nome_curso = nome_curso;
        this.envio_notificacoes = envio_notificacoes;
        this.ativar_alarme = ativar_alarme;
        this.campo_estudo = campo_estudo;
        //this.horarios = horarios;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getCod_disciplina() {
        return cod_disciplina;
    }

    public void setCod_disciplina(@NonNull String cod_disciplina) {
        this.cod_disciplina = cod_disciplina;
    }

    @NonNull
    public String getNome_disciplina() {
        return nome_disciplina;
    }

    public void setNome_disciplina(@NonNull String nome_disciplina) {
        this.nome_disciplina = nome_disciplina;
    }

    @NonNull
    public String getNome_professor() {
        return nome_professor;
    }

    public void setNome_professor(@NonNull String nome_professor) {
        this.nome_professor = nome_professor;
    }

    @NonNull
    public String getNome_curso() {
        return nome_curso;
    }

    public void setNome_curso(@NonNull String nome_curso) {
        this.nome_curso = nome_curso;
    }

    public int getEnvio_notificacoes() {
        return envio_notificacoes;
    }

    public void setEnvio_notificacoes(int envio_notificacoes) {
        this.envio_notificacoes = envio_notificacoes;
    }

    public boolean isAtivar_alarme() {
        return ativar_alarme;
    }

    public void setAtivar_alarme(boolean ativar_alarme) {
        this.ativar_alarme = ativar_alarme;
    }

    @NonNull
    public String getCampo_estudo() {
        return campo_estudo;
    }

    public void setCampo_estudo(@NonNull String campo_estudo) {
        this.campo_estudo = campo_estudo;
    }

//    @Nullable
//    public ArrayList<Horario> getHorarios() {
//        return horarios;
//    }
//
//    public void setHorarios(@Nullable ArrayList<Horario> horarios) {
//        this.horarios = horarios;
//    }

    public String listagemDisciplinasParaToast(Context context){
        return "\n"+context.getString(R.string.codigo_da_disciplina) + cod_disciplina +
                "\n"+context.getString(R.string.nome_da_disciplina) + nome_disciplina +
                "\n"+context.getString(R.string.nome_do_professor) + nome_professor +
                "\n"+context.getString(R.string.nome_do_curso) + nome_curso +
                "\n"+context.getString(R.string.envio_notificacoes) + envio_notificacoes +
                "\n"+context.getString(R.string.ativar_alarme) + ativar_alarme +
                "\n"+context.getString(R.string.campos_estudo_selecionados) + campo_estudo;
//                + "\n"+context.getString(R.string.horarios_de_aula)+ horarios;
    }


}

