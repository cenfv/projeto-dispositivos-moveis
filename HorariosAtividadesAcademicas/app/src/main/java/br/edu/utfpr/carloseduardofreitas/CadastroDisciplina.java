package br.edu.utfpr.carloseduardofreitas;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import br.edu.utfpr.carloseduardofreitas.modelo.Atividade;
import br.edu.utfpr.carloseduardofreitas.modelo.Disciplina;
import br.edu.utfpr.carloseduardofreitas.modelo.Horario;
import br.edu.utfpr.carloseduardofreitas.persistencia.AtividadeDatabase;
import br.edu.utfpr.carloseduardofreitas.utils.UtilsGUI;
import br.edu.utfpr.carloseduardofreitas.utils.UtilsListViewSize;


public class CadastroDisciplina extends AppCompatActivity implements HorarioFragment.HorarioDialogListener {
    private EditText editTextCodDisciplina,editTextNomeDisciplina,editTextNomeProfessor,editTextNomeCurso;
    private Spinner spinnerNotificacoes;
    private RadioGroup radioGroupAlarme;
    private CheckBox cbBiologicas, cbContabeis, cbExatas,cbNatureza, cbSociais;
    private RadioButton rbLigado, rbDesligado;
    public static final String COD_DISCIPLINA    = "COD_DISCIPLINA";
    public static final String NOME_DISCIPLINA    = "NOME_DISCIPLINA";
    public static final String NOME_PROFESSOR    = "NOME_PROFESSOR";
    public static final String NOME_CURSO    = "NOME_CURSO";
    public static final String ENVIO_NOTIFICACOES    = "ENVIO_NOTIFICACOES";
    public static final String ATIVAR_ALARME    = "ATIVAR_ALARME";
    public static final String CAMPO_ESTUDO    = "CAMPO_ESTUDO";
    public static final String MODO         = "MODO";
    public static final String HORARIOS         = "HORARIOS";
    public static final String ALTERAR_H        = "ALTERAR_H";

    private String diaDaSemana;
    private Date horarioInicio = new Date(), horarioFim = new Date();
    public static final int NOVO         = 1;
    public static final int ALTERAR         = 2;
    private int alterarHorario = 0;
    public static Horario horarioSelecionado ;
    private View       viewSelecionada;
    private ActionMode actionMode;

    private int modo;

    private String cod_disciplina_original;
    private String nome_disciplina_original;
    private String nome_professor_original;
    private String nome_curso_original;
    private int envio_notificacoes_original;
    private boolean ativar_alarme_original;
    private String campo_estudo_original;
    private ArrayList<Horario> arrayListHorarios = new ArrayList<>();
    private ListView listViewHorariosCadastrados;
    ArrayAdapter<Horario> adapter;

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
                    HorarioFragment horarioFragment = new HorarioFragment();
                    horarioFragment.alterarHorario(horarioSelecionado,1);
                    horarioFragment.show(getSupportFragmentManager(),"alterarHorario");
                    mode.finish();
                    return true;

                case R.id.menuItemExcluir:
                    String mensagem = getString(R.string.excluir_horario);
                    DialogInterface.OnClickListener listener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    switch(which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            apagarHorario();
                                            Toast.makeText(CadastroDisciplina.this, R.string.horario_removido, Toast.LENGTH_SHORT).show();

                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:

                                            break;
                                    }
                                }
                            };
                    UtilsGUI.confirmaAcao(CadastroDisciplina.this, mensagem, listener);
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

            listViewHorariosCadastrados.setEnabled(true);
        }
    };
    private void carregarMenu(){
        listViewHorariosCadastrados.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listViewHorariosCadastrados.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view,
                                                   int position,
                                                   long id) {

                        if (actionMode != null){
                            return false;
                        }

                        horarioSelecionado = (Horario) parent.getItemAtPosition(position);

                        view.setBackgroundColor(Color.LTGRAY);

                        viewSelecionada = view;

                        listViewHorariosCadastrados.setEnabled(false);

                        actionMode = startSupportActionMode(mActionModeCallback);

                        return true;
                    }
                });
    }

    public static void novoHorario(AppCompatActivity activity){

        Intent intent = new Intent(activity, CadastroDisciplina.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, NOVO);
    }

    public static void alterarHorario(AppCompatActivity activity, Disciplina disciplina){

        Intent intent = new Intent(activity, CadastroDisciplina.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(COD_DISCIPLINA, disciplina.getCod_disciplina() );
        intent.putExtra(NOME_DISCIPLINA, disciplina.getNome_disciplina() );
        intent.putExtra(NOME_PROFESSOR, disciplina.getNome_professor());
        intent.putExtra(NOME_CURSO, disciplina.getNome_curso() );
        intent.putExtra(ENVIO_NOTIFICACOES, disciplina.getEnvio_notificacoes()) ;
        intent.putExtra(ATIVAR_ALARME, disciplina.isAtivar_alarme() );
        intent.putExtra(CAMPO_ESTUDO, disciplina.getCampo_estudo() );


        activity.startActivityForResult(intent, ALTERAR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_disciplina);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listViewHorariosCadastrados = findViewById(R.id.listViewHorariosCadastrados);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListHorarios);
        listViewHorariosCadastrados.setAdapter(adapter);


        spinnerNotificacoes = findViewById(R.id.spinner_notificacoes);
        radioGroupAlarme = findViewById(R.id.radioGroupAlarme);
        rbLigado = findViewById(R.id.radioButtonLigado);
        rbDesligado = findViewById(R.id.radioButtonDesligado);

        cbBiologicas = findViewById(R.id.cbBiologicas);
        cbContabeis = findViewById(R.id.cbContabeis);
        cbExatas = findViewById(R.id.cbExatas);
        cbNatureza = findViewById(R.id.cbNatureza);
        cbSociais = findViewById(R.id.cbSociais);
        editTextCodDisciplina = findViewById(R.id.editTextCodDisciplina);
        editTextNomeDisciplina = findViewById(R.id.editTextNomeDisciplina);
        editTextNomeProfessor = findViewById(R.id.editTextNomeProfessor);
        editTextNomeCurso = findViewById(R.id.editTextNomeCurso);
        carregarMenu();
        popularSpinner();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){

            modo = bundle.getInt(MODO, NOVO);

            if (modo == NOVO){
                setTitle(getString(R.string.nova_disciplina));
            }else{
                cod_disciplina_original = bundle.getString(COD_DISCIPLINA);
                nome_disciplina_original = bundle.getString(NOME_DISCIPLINA);
                nome_professor_original = bundle.getString(NOME_PROFESSOR);
                nome_curso_original = bundle.getString(NOME_CURSO);
                envio_notificacoes_original = bundle.getInt(ENVIO_NOTIFICACOES);
                ativar_alarme_original = bundle.getBoolean(ATIVAR_ALARME);
                campo_estudo_original = bundle.getString(CAMPO_ESTUDO);


                editTextCodDisciplina.setText(cod_disciplina_original);
                editTextNomeDisciplina.setText(nome_disciplina_original);
                editTextNomeProfessor.setText(nome_professor_original);
                editTextNomeCurso.setText(nome_curso_original);

               if(ativar_alarme_original == true){
                   rbLigado.setChecked(true);
               }else{
                   rbDesligado.setChecked(true);
               }

               if (campo_estudo_original.contains(getString(R.string.ciencias_biologicas))){
                   cbBiologicas.setChecked(true);
               }
               if (campo_estudo_original.contains(getString(R.string.ciencias_contabeis))){
                    cbContabeis.setChecked(true);
               }
               if (campo_estudo_original.contains(getString(R.string.ciencias_exatas))){
                    cbExatas.setChecked(true);
               }
               if (campo_estudo_original.contains(getString(R.string.ciencias_natureza))){
                    cbNatureza.setChecked(true);
               }
               if (campo_estudo_original.contains(getString(R.string.ciencias_sociais))){
                    cbSociais.setChecked(true);
               }

                AtividadeDatabase database = AtividadeDatabase.getDatabase(this);
                Disciplina dAux = database.disciplinaDao().queryDisciplinaForCod(cod_disciplina_original);
                arrayListHorarios = (ArrayList<Horario>) database.horarioDao().queryForDisciplinaId(dAux.getId());
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListHorarios);
                listViewHorariosCadastrados.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                UtilsListViewSize.setListViewHeightBasedOnChildren(listViewHorariosCadastrados,0);
                System.out.println(arrayListHorarios);
               spinnerNotificacoes.setSelection(envio_notificacoes_original);
               setTitle(getString(R.string.alterar_disciplina));
            }

        }

        editTextCodDisciplina.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(154, 156, 155), BlendMode.SRC_ATOP));
        editTextNomeCurso.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(154, 156, 155), BlendMode.SRC_ATOP));
        editTextNomeDisciplina.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(154, 156, 155), BlendMode.SRC_ATOP));
        editTextNomeProfessor.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(154, 156, 155), BlendMode.SRC_ATOP));



    }

    private void popularSpinner(){
        ArrayList<String> lista = new ArrayList<>();

        lista.add(getString(R.string.todas_notificacoes));
        lista.add(getString(R.string.somente_horario_aula));
        lista.add(getString(R.string.somente_tarefas));
        lista.add(getString(R.string.nao_receber_notificacoes));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, lista);

        spinnerNotificacoes.setAdapter(adapter);
    }

    public void recuperarTodos(){
        String dadosCheckbox=recuperarCheckBox(), dadosEditText=recuperarEditText();
        int dadosSpinner = recuperarSpinner();

        boolean dadosRadio = recuperarRadioButton();
        String msg = "";
        if (dadosEditText == null || dadosEditText.trim().isEmpty()){
            Toast.makeText(this, R.string.error_msg,Toast.LENGTH_LONG).show();
        }else{
            salvar(editTextCodDisciplina.getText().toString(),editTextNomeDisciplina.getText().toString(),editTextNomeProfessor.getText().toString(),editTextNomeCurso.getText().toString(),dadosSpinner,dadosRadio,recuperarCheckBox(),arrayListHorarios);
        }
    }
    public void limparValores(){
        limparCheckBox();
        limparRadioButton();
        limparTextEdit();
        editTextCodDisciplina.requestFocus();
        arrayListHorarios.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, R.string.limpar_valores ,Toast.LENGTH_LONG).show();
    }
    private int recuperarSpinner(){
        return spinnerNotificacoes.getSelectedItemPosition();
    }

    private boolean recuperarRadioButton(){
        boolean valorAlarme = false;
        switch (radioGroupAlarme.getCheckedRadioButtonId()){
            case R.id.radioButtonLigado:
                valorAlarme = true;
                break;
            case R.id.radioButtonDesligado:
                valorAlarme = false;
                break;

        }

        return valorAlarme;
    }
    private void limparRadioButton(){
        radioGroupAlarme.clearCheck();

    }
    private String recuperarCheckBox(){
        String msg = "";
        if(cbSociais.isChecked()){
            msg += getString(R.string.ciencias_sociais)+"\n";
        }
        if(cbNatureza.isChecked()){
            msg += getString(R.string.ciencias_natureza)+"\n";
        }
        if(cbExatas.isChecked()){
            msg += getString(R.string.ciencias_exatas)+"\n";
        }
        if(cbContabeis.isChecked()){
            msg += getString(R.string.ciencias_contabeis)+"\n";
        }
        if(cbBiologicas.isChecked()){
            msg += getString(R.string.ciencias_biologicas)+"\n";
        }
        if (msg.trim().isEmpty()){
            return msg;
        }
        return msg;
    }
    private void limparCheckBox(){
        cbBiologicas.setChecked(false);
        cbContabeis.setChecked(false);
        cbExatas.setChecked(false);
        cbNatureza.setChecked(false);
        cbSociais.setChecked(false);
    }
    private String recuperarEditText(){
        String msg = "";
        if (!(editTextCodDisciplina.getText().toString() == null || editTextCodDisciplina.getText().toString().trim().isEmpty())){
            msg += editTextCodDisciplina.getText().toString();
        }else{
            editTextCodDisciplina.requestFocus();
            return null;
        }
        if (!(editTextNomeDisciplina.getText().toString() == null || editTextNomeDisciplina.getText().toString().trim().isEmpty())){
            msg +=  editTextNomeDisciplina.getText().toString()+"\n";
        }else{
            editTextNomeDisciplina.requestFocus();
            return null;
        }
        if (!(editTextNomeProfessor.getText().toString() == null || editTextNomeProfessor.getText().toString().trim().isEmpty())){
            msg += editTextNomeProfessor.getText().toString();
        }else{
            editTextNomeProfessor.requestFocus();
            return null;
        }
        if (!(editTextNomeCurso.getText().toString() == null || editTextNomeCurso.getText().toString().trim().isEmpty())){
            msg += editTextNomeCurso.getText().toString();
        }else{
            editTextNomeCurso.requestFocus();
            return null;
        }
        if (msg.trim().isEmpty()){
            return null;
        }
        return msg;
    }
    private void limparTextEdit(){
        editTextCodDisciplina.setText(null);
        editTextNomeDisciplina.setText(null);
        editTextNomeProfessor.setText(null);
        editTextNomeCurso.setText(null);
    }
    private void salvar( String codDisciplina ,String nomeDisciplina,
                            String nomeProfessor, String nomeCurso,
                            int envio_notificacoes,boolean alarme,String campoEstudo, ArrayList<Horario> arrayListHorarios){


        Intent intent = new Intent();

        intent.putExtra(COD_DISCIPLINA, codDisciplina);
        intent.putExtra(NOME_DISCIPLINA, nomeDisciplina);
        intent.putExtra(NOME_PROFESSOR, nomeProfessor);
        intent.putExtra(NOME_CURSO, nomeCurso);
        intent.putExtra(ENVIO_NOTIFICACOES, envio_notificacoes);
        intent.putExtra(ATIVAR_ALARME, alarme);
        intent.putExtra(CAMPO_ESTUDO, campoEstudo);
        intent.putExtra(HORARIOS, arrayListHorarios);
        intent.putExtra(ALTERAR_H,alterarHorario);

        setResult(Activity.RESULT_OK, intent);

        finish();

    }



    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        cancelar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editar_horarios_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemSalvar:
                recuperarTodos();
                return true;

            case android.R.id.home:
                cancelar();
                return true;
            case R.id.menuItemLimpar:
                limparValores();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void openDialog(View view){
        HorarioFragment horarioFragment = new HorarioFragment();
        horarioFragment.show(getSupportFragmentManager(),"cadastroHorario");

    }
    private void apagarHorario(){
        arrayListHorarios.remove(horarioSelecionado);
        adapter.notifyDataSetChanged();
        UtilsListViewSize.setListViewHeightBasedOnChildren(listViewHorariosCadastrados);
    }
    @Override
    public void receberHorario(String dayOfWeek, Date hInicio, Date hFim, int Alterar){

        Horario h = new Horario();
        h.setDiaDaSemana(dayOfWeek);
        h.setHorarioIinicio(hInicio);
        h.setHorarioFim(hFim);

        diaDaSemana = dayOfWeek;
        horarioInicio = hInicio;
        horarioFim = hFim;

        //AtividadeDatabase database = AtividadeDatabase.getDatabase(this);
        //database.horarioDao().insert(h);
        alterarHorario = Alterar;
        arrayListHorarios.remove(horarioSelecionado);
        arrayListHorarios.add(h);
        adapter.notifyDataSetChanged();
        UtilsListViewSize.setListViewHeightBasedOnChildren(listViewHorariosCadastrados); // Corrigir a altura do ListView (motivado por estar dentro de uma ScrollView)
    }


}