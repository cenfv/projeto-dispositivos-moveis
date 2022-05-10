package br.edu.utfpr.carloseduardofreitas.persistencia;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.edu.utfpr.carloseduardofreitas.modelo.Atividade;
import br.edu.utfpr.carloseduardofreitas.modelo.Disciplina;
import br.edu.utfpr.carloseduardofreitas.modelo.Horario;

@Database(entities = {Disciplina.class, Atividade.class, Horario.class}, version = 1, exportSchema = true)
@TypeConverters({Converters.class})
public abstract class AtividadeDatabase extends RoomDatabase {

    public abstract HorarioDao horarioDao();
    public abstract AtividadeDao atividadeDao();
    public abstract DisciplinaDao disciplinaDao();

    private static AtividadeDatabase instance;

    public static AtividadeDatabase getDatabase(final Context context){
        if (instance == null) {

            synchronized (AtividadeDatabase.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(context, AtividadeDatabase.class,"Atividade.db").allowMainThreadQueries().build();
                }
            }
        }
        return instance;
    }
}
