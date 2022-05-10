package br.edu.utfpr.carloseduardofreitas;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.carloseduardofreitas.modelo.Atividade;
import br.edu.utfpr.carloseduardofreitas.modelo.Disciplina;
import br.edu.utfpr.carloseduardofreitas.persistencia.AtividadeDatabase;
import br.edu.utfpr.carloseduardofreitas.utils.UtilsDate;

public class CadastroAtividade extends AppCompatActivity {

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;
    private int  modo;
    private Atividade atividade;
    private List<Disciplina> listaDisciplina;
    private Spinner spinnerDisciplina;
    private EditText editTextTitulo,editTextDescricao,editTextHoraEntrega;
    private ArrayList<String> disciplinas;

    private String titulo,descricao;

    private Button buttonCadastrarData;
    private int dia, mes, ano;
    private String dataDeEntrega;
    private Calendar calendarDataDeEntrega;

    private Button buttonCadastrarHora;
    private int segundos,minutos,horas;
    private String horaDeEntrega;
    private Calendar calendarHoraDeEntrega;

    public  boolean formatoHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_atividade);
        lerPreferenciaFormatoHora();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        calendarDataDeEntrega = Calendar.getInstance();
        calendarHoraDeEntrega = Calendar.getInstance();

        spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextDescricao = findViewById(R.id.editTextDescricao);
        //editTextDataEntrega = findViewById(R.id.editTextDataEntrega);
        buttonCadastrarData = findViewById(R.id.buttonCadastrarData);
        buttonCadastrarHora = findViewById(R.id.buttonCadastrarHorarioAtividade);
        //editTextHoraEntrega = findViewById(R.id.editTextHoraEntrega);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        if (bundle != null){
            modo = bundle.getInt(MODO, NOVO);
        }else{
            modo = NOVO;
        }
        carregaDisciplinas();
        carregarSpinnerDisciplinas();

        if (modo == ALTERAR) {
            setTitle(getString(R.string.alterar_atividade));
            int id = bundle.getInt(ID);

            AtividadeDatabase database = AtividadeDatabase.getDatabase(this);
            atividade = database.atividadeDao().queryForId(id);
            editTextTitulo.setText(atividade.getTitulo());
            editTextDescricao.setText(atividade.getDescricao());
            buttonCadastrarData.setText(UtilsDate.formatDate(CadastroAtividade.this,atividade.getDataEntrega()));
            buttonCadastrarHora.setText(UtilsDate.formatTime(CadastroAtividade.this,atividade.getHorarioEntrega()));
            calendarDataDeEntrega.setTime(atividade.getDataEntrega());
            calendarHoraDeEntrega.setTime(atividade.getHorarioEntrega());

            int posicao = posicaoDisciplina(atividade.getDisciplinaId());
            spinnerDisciplina.setSelection(posicao);
        }else{
            setTitle(getString(R.string.nova_atividade));
            atividade = new Atividade("","",null,null);
        }
        editTextTitulo.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(140, 140, 140), BlendMode.SRC_ATOP));
        editTextDescricao.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(140, 140, 140), BlendMode.SRC_ATOP));

    }
    private boolean validar(){
        if(titulo.trim().isEmpty()){
            editTextTitulo.requestFocus();
            Toast.makeText(this, R.string.preencha_titulo,Toast.LENGTH_LONG).show();
            return false;
        }if(descricao.trim().isEmpty() ){
            Toast.makeText(this, R.string.preencha_descricao,Toast.LENGTH_LONG).show();
            editTextDescricao.requestFocus();
            return false;
        }if(dataDeEntrega.trim().isEmpty()){
            Toast.makeText(this, R.string.preencha_datra,Toast.LENGTH_LONG).show();
            return false;
        }if(horaDeEntrega.trim().isEmpty()){
            Toast.makeText(this, R.string.preencha_hora,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    private void limpar(){
        editTextTitulo.setText("");
        editTextDescricao.setText("");
        buttonCadastrarData.setText(getString(R.string.cadastro_data));
        buttonCadastrarHora.setText(getString(R.string.cadastrar_horario_btn));
        spinnerDisciplina.setSelection(0);
        Toast.makeText(this, R.string.campos_limpos,Toast.LENGTH_LONG).show();
    }

    public static void novo(Activity activity, int requestCode) {

        Intent intent = new Intent(activity, CadastroAtividade.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Atividade atividade){

        Intent intent = new Intent(activity, CadastroAtividade.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, atividade.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    private int posicaoDisciplina(int disciplinaId){

        for (int pos = 0; pos < listaDisciplina.size(); pos++){

            Disciplina d = listaDisciplina.get(pos);

            if (d.getId() == disciplinaId){
                return pos;
            }
        }

        return -1;
    }
    private void carregaDisciplinas(){
        AtividadeDatabase database = AtividadeDatabase.getDatabase(this);
        listaDisciplina = database.disciplinaDao().queryAll();


    }
    private void salvar(){

        titulo = editTextTitulo.getText().toString();
        descricao = editTextDescricao.getText().toString();
        dataDeEntrega = "";
        horaDeEntrega = "";
        if (!buttonCadastrarData.getText().toString().equals(getString(R.string.cadastro_data))){
            dataDeEntrega = buttonCadastrarData.getText().toString();
        }
        if (!buttonCadastrarHora.getText().toString().equals(getString(R.string.cadastrar_horario_btn))) {
            horaDeEntrega = buttonCadastrarHora.getText().toString();
        }

        if(validar()==true) {
            if (spinnerDisciplina.getSelectedItem() != null) {

                AtividadeDatabase database = AtividadeDatabase.getDatabase(this);
                atividade.setTitulo(titulo);
                atividade.setDescricao(descricao);
                atividade.setDataEntrega(calendarDataDeEntrega.getTime());
                atividade.setHorarioEntrega(calendarHoraDeEntrega.getTime());

                String item = (String) spinnerDisciplina.getSelectedItem();
                Disciplina d = database.disciplinaDao().queryForCod_disciplina(item);
                atividade.setDisciplinaId(d.getId());
                if (modo == NOVO) {
                    database.atividadeDao().insert(atividade);
                    Toast.makeText(this, R.string.atividade_cadastrada, Toast.LENGTH_LONG).show();

                } else {
                    System.out.println(dataDeEntrega);
                    database.atividadeDao().update(atividade);
                }

                setResult(Activity.RESULT_OK);
                finish();
            }
        }

    }

    private void carregarSpinnerDisciplinas(){
        AtividadeDatabase database = AtividadeDatabase.getDatabase(this);
        disciplinas = (ArrayList<String>) database.disciplinaDao().queryAllcod();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, disciplinas);
        spinnerDisciplina.setAdapter(adapter);

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
                salvar();
                return true;

            case android.R.id.home:
                finish();
                return true;
            case R.id.menuItemLimpar:
                limpar();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void openDialogDate(View view){


        dia = calendarDataDeEntrega.get(Calendar.DAY_OF_MONTH);
        mes = calendarDataDeEntrega.get(Calendar.MONTH);
        ano = calendarDataDeEntrega.get(Calendar.YEAR);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                dataDeEntrega = dayOfMonth + "-" + month + "-" + year;
                calendarDataDeEntrega.set(year, month, dayOfMonth);
                String textoData = UtilsDate.formatDate(CadastroAtividade.this, calendarDataDeEntrega.getTime());
                buttonCadastrarData.setText(textoData);

            }
        },ano,mes,dia);
        datePickerDialog.show();
    }

    public void openDialogTime(View view) {

        segundos = calendarHoraDeEntrega.get(Calendar.SECOND);
        minutos = calendarHoraDeEntrega.get(Calendar.MINUTE);
        horas = calendarHoraDeEntrega.get(Calendar.HOUR_OF_DAY);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                horaDeEntrega = hourOfDay + ":" + minute;
                Date aux = new Date();
                aux.setHours(hourOfDay);
                aux.setMinutes(minute);
                calendarHoraDeEntrega.setTime(aux);
                buttonCadastrarHora.setText(UtilsDate.formatTime(CadastroAtividade.this,calendarHoraDeEntrega.getTime()));
            }
        }, horas, minutos,formatoHora);
        timePickerDialog.setTitle(getString(R.string.entre_horario_entrega));
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();

    }
    private void lerPreferenciaFormatoHora(){

        SharedPreferences shared = this.getSharedPreferences(PrincipalDisciplinas.ARQUIVO,
                Context.MODE_PRIVATE);

        formatoHora = shared.getBoolean(PrincipalDisciplinas.FORMATOHORA, formatoHora);

    }


}