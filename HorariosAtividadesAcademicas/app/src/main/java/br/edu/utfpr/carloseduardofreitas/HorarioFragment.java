package br.edu.utfpr.carloseduardofreitas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;


import br.edu.utfpr.carloseduardofreitas.modelo.Horario;
import br.edu.utfpr.carloseduardofreitas.utils.UtilsDate;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

public class HorarioFragment extends AppCompatDialogFragment {
    private WeekdaysPicker widget;
    private ArrayList<Horario> horarios = new ArrayList<>();
    private LinkedHashMap<Integer, Boolean> map = new LinkedHashMap<>();
    private int tHourInicio = 07 ,tMinuteInicio = 0;
    private int tHourFim = 07 ,tMinuteFim = 0;
    private EditText editTextInicio,editTextFim;
    private View containerHorarioInicio,containerHorarioFim;
    private ImageButton imgRelogioInicio,imgRelogioFim;
    private String horarioInicio = "hh:mm", horarioFim = "hh:mm";
    private Date hInicio = new Date(), hFim = new Date();
    private HorarioDialogListener listener;
    public  boolean formatoHora = true;
    private String diaDaSemana="";
    private int ALTERAR = 0;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
// ------------------------------ Funções AppCompatDialogFragment ------------------------------------
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_horario_principal, null);
        editTextInicio = view.findViewById(R.id.editTextInicio);
        editTextFim = view.findViewById(R.id.editTextFim);
        lerPreferenciaFormatoHora();
        builder.setView(view)
                .setTitle(R.string.time_registration)
                .setNegativeButton(R.string.cancelar_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }

                });

// ----------------------------- Implementando WeekdaysPicker -----------------------------------
        widget = view.findViewById(R.id.weekdays);
        map.put(MONDAY, false);
        map.put(TUESDAY, false);
        map.put(WEDNESDAY,false);
        map.put(THURSDAY, false);
        map.put(FRIDAY, false);
        map.put(SATURDAY, false );
        widget.setCustomDays(map);
        widget.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                widget.selectDay(clickedDayOfWeek);
                diaDaSemana = widget.getSelectedDaysText().get(0);
                System.out.println("selecionado"+widget.getSelectedDays());
            }
        });
        System.out.println(diaDaSemana);
        if(!diaDaSemana.isEmpty()){
            switch (diaDaSemana){
                case "Monday":
                    widget.selectDay(2);
                    break;
                case "Tuesday":
                    widget.selectDay(3);
                    break;
                case "Wednesday":
                    widget.selectDay(4);
                    break;
                case "Thursday":
                    widget.selectDay(5);
                    break;
                case "Friday":
                    widget.selectDay(6);
                    break;
                case "Saturday":
                    widget.selectDay(7);
                    break;
                case "segunda-feira":
                    widget.selectDay(2);
                    break;
                case "terça-feira":
                    widget.selectDay(3);
                    break;
                case "quarta-feira":
                    widget.selectDay(4);
                    break;
                case "quinta-feira":
                    widget.selectDay(5);
                    break;
                case "sexta-feira":
                    widget.selectDay(6);
                    break;
                case "sábado":
                    widget.selectDay(7);
                    break;
            }
        }

 // ----------------------------------- Implementação das funções dos componentes do fragment Horário -----------------------------------
        editTextInicio = view.findViewById(R.id.editTextInicio);
        editTextFim = view.findViewById(R.id.editTextFim);
        containerHorarioInicio = view.findViewById(R.id.containerInicio);
        containerHorarioFim = view.findViewById(R.id.containerFim);
        imgRelogioInicio = view.findViewById(R.id.imgRelogioInicio);
        imgRelogioFim = view.findViewById(R.id.imgRelogioFim);

        editTextInicio.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(154, 156, 155), BlendMode.SRC_ATOP));
        editTextFim.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(154, 156, 155), BlendMode.SRC_ATOP));

        editTextInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horarioInicio(view);
            }

        });

        editTextFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horarioFim(view);
            }

        });

        containerHorarioInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horarioInicio(view);
            }
        });

        containerHorarioFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horarioFim(view);
            }
        });

        imgRelogioInicio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                horarioInicio(view);
            }
        });

        imgRelogioFim.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                horarioFim(view);
            }
        });
        atualizarHorarios();
        return builder.create();
    }

    public void alterarHorario(Horario h,int alterar){
        tHourInicio = h.getHorarioIinicio().getHours();
        tMinuteInicio = h.getHorarioIinicio().getMinutes();
        tHourFim = h.getHorarioFim().getHours();
        tMinuteFim = h.getHorarioFim().getMinutes();
        horarioInicio = tHourInicio +":"+tMinuteInicio;
        horarioFim = tHourFim +":"+ tMinuteFim;
        diaDaSemana = h.getDiaDaSemana();
        ALTERAR = alterar;



    }
    public void atualizarHorarios(){
        System.out.println(ALTERAR);
        if(ALTERAR == 1){
            hInicio.setHours(tHourInicio);
            hInicio.setMinutes(tMinuteInicio);
            editTextInicio.setText(UtilsDate.formatTime(HorarioFragment.this.getContext(), hInicio));
            hFim.setHours(tHourFim);
            hFim.setMinutes(tMinuteFim);
            editTextFim.setText(UtilsDate.formatTime(HorarioFragment.this.getContext(), hFim));

        }

    }
    //--------------------------- realiza a validação das informações --------------------------------
    @Override
    public void onStart()
    {

        super.onStart();
        AlertDialog d = (AlertDialog)getDialog();
        if(d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = true;

                    if(diaDaSemana.equals("")){
                        Toast.makeText(HorarioFragment.this.getContext(), R.string.selecione_um_dia_semana,Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (horarioInicio.equals("hh:mm")||(horarioFim.equals("hh:mm"))){
                        wantToCloseDialog = false;
                    }else {
                        String inicioAux = horarioInicio.replace(":","");
                        String fimAux = horarioFim.replace(":","");
                        int intInicio = Integer.parseInt(inicioAux); // necessário por em variáveis para não ser void
                        int intFim = Integer.parseInt(fimAux);
                        Toast.makeText(HorarioFragment.this.getContext(), HorarioFragment.this.getString(R.string.horario_cadastrado_sucesso),Toast.LENGTH_SHORT).show();
                        if(intInicio >= intFim){
                            Toast.makeText(HorarioFragment.this.getContext(), getString(R.string.horario_fim_maior),Toast.LENGTH_LONG).show();
                            wantToCloseDialog = false;
                        }
                    }

                    if(wantToCloseDialog){
                        listener.receberHorario(diaDaSemana,hInicio,hFim,ALTERAR);

                        dismiss();
                    }else{
                        Toast.makeText(HorarioFragment.this.getContext(), getString(R.string.preencha_corretamente),Toast.LENGTH_LONG).show();
                        editTextInicio.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(196, 21, 2), BlendMode.SRC_ATOP));
                        editTextFim.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(196, 21, 2), BlendMode.SRC_ATOP));
                        return;
                    }

                }
            });
        }
    }

    public void horarioInicio(View view) {

       // Executa o 1 timePickerDialog, recebendo o horário de ínicio de aula
        TimePickerDialog timePickerDialogStart = new TimePickerDialog(HorarioFragment.this.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                horarioInicio = hourOfDay + ":" + minute;
                hInicio.setHours(hourOfDay);
                hInicio.setMinutes(minute);

                validarLineColor(editTextInicio,view);
                editTextInicio.setText(UtilsDate.formatTime(HorarioFragment.this.getContext(),hInicio));
            }
        }, 0, 0,formatoHora);
        //timePickerDialogStart.setCustomTitle(teste);
        //timePickerDialogStart.setMessage("Texto customizado");
        timePickerDialogStart.setTitle(getString(R.string.entre_horario_inicio));
        timePickerDialogStart.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialogStart.updateTime(tHourInicio, tMinuteInicio);
        timePickerDialogStart.show();

    }
    public void horarioFim(View view) {

        TimePickerDialog timePickerDialogFim = new TimePickerDialog(
                HorarioFragment.this.getContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view3, int hourOfDay, int minute) {
                        horarioFim = hourOfDay + ":" + minute;
                        hFim.setHours(hourOfDay);
                        hFim.setMinutes(minute);
                        validarLineColor(editTextFim,view);
                        editTextFim.setText(UtilsDate.formatTime(HorarioFragment.this.getContext(),hFim));

                }
                }, 0, 0, formatoHora
        );

        timePickerDialogFim.setTitle(getString(R.string.entre_horario_fim));
        timePickerDialogFim.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialogFim.updateTime(tHourFim, tMinuteFim);
        timePickerDialogFim.show();

    }
    public void validarLineColor(EditText editText,View view){

       if ((editText.getId() == R.id.editTextInicio) && (!horarioFim.equals("hh:mm"))){
           String inicioAux = horarioInicio.replace(":","");
           String fimAux = horarioFim.replace(":","");
           int intInicio = Integer.parseInt(inicioAux);
           int intFim = Integer.parseInt(fimAux);
           if(intInicio >= intFim){
               editTextFim.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(196, 21, 2), BlendMode.SRC_ATOP));
               editText.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(196, 21, 2), BlendMode.SRC_ATOP));

           }else{
               editText.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(34, 181, 61), BlendMode.SRC_ATOP));
           }
        }
       if ((editText.getId() == R.id.editTextInicio) && (horarioFim.equals("hh:mm"))){
           editText.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(34, 181, 61), BlendMode.SRC_ATOP));
       }
        if ((editText.getId() == R.id.editTextFim) && (!horarioInicio.equals("hh:mm"))){
            String inicioAux = horarioInicio.replace(":","");
            String fimAux = horarioFim.replace(":","");
            int intInicio = Integer.parseInt(inicioAux);
            int intFim = Integer.parseInt(fimAux);
            if(intInicio >= intFim){
                editTextInicio.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(196, 21, 2), BlendMode.SRC_ATOP));
                editText.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(196, 21, 2), BlendMode.SRC_ATOP));
                Toast.makeText(HorarioFragment.this.getContext(),getString(R.string.horario_fim_maior),Toast.LENGTH_LONG).show();
            }else{
                editTextInicio.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(34, 181, 61), BlendMode.SRC_ATOP));
                editText.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(34, 181, 61), BlendMode.SRC_ATOP));
            }
        }
       if ((editText.getId() == R.id.editTextFim) && (horarioInicio.equals("hh:mm"))){
           editText.getBackground().setColorFilter(new BlendModeColorFilter(Color.rgb(34, 181, 61), BlendMode.SRC_ATOP));
       }

   }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (HorarioDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement HorarioDialogListener");
        }
    }
    public interface HorarioDialogListener {
        void receberHorario(String dayOfWeek, Date hInicio, Date hFim, int Alterar);
    }

    private void lerPreferenciaFormatoHora(){

        SharedPreferences shared = this.getActivity().getSharedPreferences(PrincipalDisciplinas.ARQUIVO,
                Context.MODE_PRIVATE);

        formatoHora = shared.getBoolean(PrincipalDisciplinas.FORMATOHORA, formatoHora);

    }


}