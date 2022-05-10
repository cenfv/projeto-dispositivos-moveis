package br.edu.utfpr.carloseduardofreitas.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.edu.utfpr.carloseduardofreitas.modelo.Horario;

@Dao
public interface HorarioDao {
    @Insert
    long insert(Horario horario);

    @Delete
    void delete(Horario horario);

    @Update
    void update(Horario horario);

    @Query("SELECT * FROM horario WHERE id = :id")
    Horario queryForId(long id);

    @Query("SELECT * FROM horario WHERE disciplinaId = :disciplinaId ")
    List<Horario> queryForDisciplinaId(long disciplinaId);

    @Query("DELETE FROM horario WHERE disciplinaId = :disciplinaId")
    void deleteAllForDisciplinaId(long disciplinaId);


}
