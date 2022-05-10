package br.edu.utfpr.carloseduardofreitas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Database;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.carloseduardofreitas.modelo.Atividade;
import br.edu.utfpr.carloseduardofreitas.modelo.Disciplina;
import br.edu.utfpr.carloseduardofreitas.modelo.Horario;
import br.edu.utfpr.carloseduardofreitas.persistencia.AtividadeDatabase;
import br.edu.utfpr.carloseduardofreitas.utils.UtilsGUI;


public class PrincipalDisciplinas extends AppCompatActivity {

    private ArrayList<Disciplina> disciplinas = new ArrayList<>();

    private ListView listView_horarios;
    DisciplinaListAdapter adapter;
    private ConstraintLayout layout;
    private ActionMode actionMode;
    private int        posicaoSelecionada = -1;
    private View       viewSelecionada;
    Disciplina disciplinaSelecionada;

    public static final String ARQUIVO = "br.edu.utfpr.carloseduardofreitas.PREFERENCIA_FORMATO_HORARIO";
    public static final String FORMATOHORA = "FORMATOHORA";
    private boolean formatoHorario = true;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.horarios_item_selecionado, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
                case R.id.menuItemAlterar:
                    alterarHorario();
                    mode.finish();
                    return true;

                case R.id.menuItemExcluir:
                    final Disciplina d = (Disciplina) listView_horarios.getItemAtPosition(posicaoSelecionada);
                    excluirHorario(d);
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            if (viewSelecionada != null){
                viewSelecionada.setBackgroundColor(Color.TRANSPARENT);
            }

            actionMode         = null;
            viewSelecionada    = null;

            listView_horarios.setEnabled(true);
        }
    };
    private Object Horarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_disciplinas);
        setTitle(getString(R.string.disciplina));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        listView_horarios = findViewById(R.id.listView_horarios);

        listView_horarios.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id) {

                        posicaoSelecionada = position;
                        alterarHorario();
                    }
                });

        listView_horarios.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView_horarios.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view,
                                                   int position,
                                                   long id) {

                        if (actionMode != null){
                            return false;
                        }

                        posicaoSelecionada = position;

                        AtividadeDatabase database = AtividadeDatabase.getDatabase(PrincipalDisciplinas.this);
                        disciplinaSelecionada =  database.disciplinaDao().queryForId(position);
                        view.setBackgroundColor(Color.LTGRAY);

                        viewSelecionada = view;

                        listView_horarios.setEnabled(false);

                        actionMode = startSupportActionMode(mActionModeCallback);

                        return true;
                    }
                });
        lerPreferenciaHorario();
        popularLista();
    }

    private void popularLista(){
        AtividadeDatabase database = AtividadeDatabase.getDatabase(this);
        disciplinas =  (ArrayList<Disciplina>) database.disciplinaDao().queryAll();
        adapter = new DisciplinaListAdapter(this, disciplinas);
        listView_horarios.setAdapter(adapter);
    }



    private void excluirHorario(Disciplina d){
        String mensagem = getString(R.string.remover_disciplina) +
                "\n" + disciplinas.get(posicaoSelecionada).getCod_disciplina() +"?";


        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                AtividadeDatabase database = AtividadeDatabase.getDatabase(PrincipalDisciplinas.this);
                                int count = database.atividadeDao().queryForDisciplinaId(d.getId());
                                if (count > 0 ){
                                    Toast.makeText(PrincipalDisciplinas.this,R.string.disciplina_utilizada,Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(PrincipalDisciplinas.this, R.string.disciplina_removida, Toast.LENGTH_SHORT).show();
                                    database.disciplinaDao().delete(d);
                                    popularLista();
                                }

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };
        UtilsGUI.confirmaAcao(this, mensagem, listener);



    }

    private void alterarHorario(){

        Disciplina disciplina = disciplinas.get(posicaoSelecionada);
        CadastroDisciplina.alterarHorario(this, disciplina);
        popularLista();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {

            Bundle bundle = data.getExtras();

            String codDisciplina = bundle.getString(CadastroDisciplina.COD_DISCIPLINA);
            String nomeDisciplina = bundle.getString(CadastroDisciplina.NOME_DISCIPLINA);
            String nomeProfessor = bundle.getString(CadastroDisciplina.NOME_PROFESSOR);
            String nomeCurso = bundle.getString(CadastroDisciplina.NOME_CURSO);
            int notificacoes = bundle.getInt(CadastroDisciplina.ENVIO_NOTIFICACOES);
            Boolean alarme = bundle.getBoolean(CadastroDisciplina.ATIVAR_ALARME);
            String campoEstudo = bundle.getString(CadastroDisciplina.CAMPO_ESTUDO);
            int AlterarH = bundle.getInt(CadastroDisciplina.ALTERAR_H);
            bundle.putSerializable("lstHorarios", (Serializable) Horarios);
            ArrayList<Horario> arrayListHorarios = (ArrayList<Horario>) bundle.getSerializable(CadastroDisciplina.HORARIOS);

            AtividadeDatabase database = AtividadeDatabase.getDatabase(this);

            Disciplina d = new Disciplina(codDisciplina, nomeDisciplina, nomeProfessor, nomeCurso, notificacoes, alarme, campoEstudo, arrayListHorarios);
            if (requestCode == CadastroDisciplina.NOVO) {
                disciplinas.add(d);
                database.disciplinaDao().insert(d);
            } else {

                Disciplina disciplina = disciplinas.get(posicaoSelecionada);
                disciplina.setCod_disciplina(codDisciplina);
                disciplina.setNome_disciplina(nomeDisciplina);
                disciplina.setNome_professor(nomeProfessor);
                disciplina.setNome_curso(nomeCurso);
                disciplina.setEnvio_notificacoes(notificacoes);
                disciplina.setAtivar_alarme(alarme);
                disciplina.setCampo_estudo(campoEstudo);

                posicaoSelecionada = -1;

                database.disciplinaDao().update(disciplina);
                Toast.makeText(this, disciplina.listagemDisciplinasParaToast(this), Toast.LENGTH_LONG).show();

            }

            Disciplina dAux = database.disciplinaDao().queryDisciplinaForCod(codDisciplina);
            database.horarioDao().deleteAllForDisciplinaId(dAux.getId());

            for (Horario horario : arrayListHorarios) {
                horario.setDisciplinaId(dAux.getId());
                database.horarioDao().insert(horario);
            }

            Toast.makeText(this, d.listagemDisciplinasParaToast(this), Toast.LENGTH_LONG).show();
        }

        popularLista();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.horarios_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        HorarioFragment formato_horario = new HorarioFragment();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuItemAdicionar:
                CadastroDisciplina.novoHorario(this);
                return true;
            case R.id.menuItemSobre:
                Intent intent = new Intent(this, Sobre.class);
                startActivity(intent);
                return true;
            case R.id.menuItem12Horas:
                salvarPreferenciaFormatoHora(false);
                return true;
            case R.id.menuItem24Horas:
                salvarPreferenciaFormatoHora(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item;

        if (!formatoHorario) {
            item = menu.findItem(R.id.menuItem12Horas);
        } else if (formatoHorario) {
            item = menu.findItem(R.id.menuItem24Horas);
        } else {
            return false;
        }

        item.setChecked(true);
        return true;
    }

    private void lerPreferenciaHorario(){

        SharedPreferences shared = getSharedPreferences(ARQUIVO,
                Context.MODE_PRIVATE);

        formatoHorario = shared.getBoolean(FORMATOHORA, formatoHorario);

    }
    private void salvarPreferenciaFormatoHora(boolean novoFormato){

        SharedPreferences shared = getSharedPreferences(ARQUIVO,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean( FORMATOHORA, novoFormato);

        editor.commit();

        formatoHorario = novoFormato;
    }
    public static void abrirActivity(Activity activity){

        Intent intent = new Intent(activity, PrincipalDisciplinas.class);

        activity.startActivity(intent);
    }



}