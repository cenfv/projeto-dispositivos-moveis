package br.edu.utfpr.carloseduardofreitas.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.edu.utfpr.carloseduardofreitas.modelo.Atividade;


@Dao
public interface AtividadeDao {

    @Insert
    long insert(Atividade atividade);

    @Delete
    void delete(Atividade atividade);

    @Update
    void update(Atividade atividade);

    @Query("SELECT * FROM atividade WHERE id = :id")
    Atividade queryForId(long id);

    @Query("SELECT * FROM atividade")
    List<Atividade> queryAll();

    @Query("SELECT count(*) FROM atividade WHERE disciplinaId = :id LIMIT 1")
    int queryForDisciplinaId(long id);
}


