package br.edu.utfpr.carloseduardofreitas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.edu.utfpr.carloseduardofreitas.modelo.Atividade;
import br.edu.utfpr.carloseduardofreitas.modelo.Disciplina;
import br.edu.utfpr.carloseduardofreitas.persistencia.AtividadeDatabase;
import br.edu.utfpr.carloseduardofreitas.utils.UtilsGUI;

public class PrincipalAtividades extends AppCompatActivity {

    private static final int REQUEST_NOVA_ATIVIDADE    = 1;
    private static final int REQUEST_ALTERAR_ATIVIDADE = 2;
    private ActionMode actionMode;
    private View       viewSelecionada;
    private int        posicaoSelecionada = -1;
    private Atividade atividadeSelecionada;

    private ListView listViewAtividade;
    private ArrayAdapter<Atividade> listaAdapter;
    private List<Atividade> atividades;

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
                    Atividade atividade = atividades.get(posicaoSelecionada) ;

                    CadastroAtividade.alterar(PrincipalAtividades.this,
                            REQUEST_ALTERAR_ATIVIDADE,
                            atividade);
                    mode.finish();
                    return true;

                case R.id.menuItemExcluir:
                    excluirAtividade();
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

            listViewAtividade.setEnabled(true);
        }
    };
    private void carregarMenu(){
        listViewAtividade.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listViewAtividade.setOnItemLongClickListener(
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

                        view.setBackgroundColor(Color.LTGRAY);

                        viewSelecionada = view;

                        listViewAtividade.setEnabled(false);

                        actionMode = startSupportActionMode(mActionModeCallback);

                        return true;
                    }
                });
    }
    private void alterarAtividade(){
        listViewAtividade.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Atividade atividade = (Atividade) parent.getItemAtPosition(position);

                CadastroAtividade.alterar(PrincipalAtividades.this,
                        REQUEST_ALTERAR_ATIVIDADE,
                        atividade);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(getString(R.string.title_atividades));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_atividades);

        listViewAtividade = findViewById(R.id.listView_atividades);
        lerPreferenciaHorario();
        alterarAtividade();
        carregarMenu();
        carregarAtividades();
        registerForContextMenu(listViewAtividade);


    }
    private void excluirAtividade(){
        String mensagem = getString(R.string.apagar_atividade) +
                "\n" + atividades.get(posicaoSelecionada).getTitulo() +"?";


        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                AtividadeDatabase database = AtividadeDatabase.getDatabase(PrincipalAtividades.this);
                                Atividade atividade = atividades.get(posicaoSelecionada) ;
                                database.atividadeDao().delete(atividade);
                                carregarAtividades();
                                Toast.makeText(PrincipalAtividades.this, R.string.atividade_removida, Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };
            UtilsGUI.confirmaAcao(this, mensagem, listener);
    }
    private void carregarAtividades(){
        AtividadeDatabase database = AtividadeDatabase.getDatabase(PrincipalAtividades.this);
        atividades = database.atividadeDao().queryAll();
        AtividadeListAdapter adapter = new AtividadeListAdapter(this, atividades);
        listViewAtividade.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_NOVA_ATIVIDADE || requestCode == REQUEST_ALTERAR_ATIVIDADE)
                && resultCode == Activity.RESULT_OK) {

            carregarAtividades();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.atividades_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemAdicionar:
                if(verificarDisciplinas() != true) {
                    CadastroAtividade.novo(this, REQUEST_NOVA_ATIVIDADE);
                }else{
                    Toast.makeText(this, R.string.cadastre_disciplina,Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menuItemSobre:
                Intent intent = new Intent(this, Sobre.class);
                startActivity(intent);
                return true;
            case R.id.menuItemDisciplinas:
                PrincipalDisciplinas.abrirActivity(this);
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

    private boolean verificarDisciplinas(){
        AtividadeDatabase database = AtividadeDatabase.getDatabase(this);
        int count = database.disciplinaDao().total();
        System.out.println(count);
        if (count>0) {
            return false;
        }
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


}