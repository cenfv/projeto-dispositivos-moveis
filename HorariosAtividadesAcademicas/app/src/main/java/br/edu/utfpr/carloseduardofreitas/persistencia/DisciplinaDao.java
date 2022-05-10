package br.edu.utfpr.carloseduardofreitas.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.edu.utfpr.carloseduardofreitas.modelo.Disciplina;

@Dao
public interface DisciplinaDao {
    @Insert
    long insert(Disciplina disciplina);

    @Delete
    void delete(Disciplina disciplina);

    @Update
    void update(Disciplina disciplina);

    @Query("SELECT * FROM disciplina WHERE id = :id")
    Disciplina queryForId(long id);

    @Query("SELECT * FROM disciplina ORDER BY cod_disciplina ASC")
    List<Disciplina> queryAll();

    @Query("SELECT * FROM disciplina WHERE cod_disciplina = :cod_disciplina")
    Disciplina queryForCod_disciplina(String cod_disciplina);

    @Query("SELECT * FROM disciplina WHERE cod_disciplina = :cod_disciplina")
    Disciplina queryDisciplinaForCod(String cod_disciplina);

    @Query("SELECT cod_disciplina FROM disciplina ORDER BY cod_disciplina ASC")
    List<String> queryAllcod();

    @Query("SELECT count(*) FROM disciplina")
    int total();



}
