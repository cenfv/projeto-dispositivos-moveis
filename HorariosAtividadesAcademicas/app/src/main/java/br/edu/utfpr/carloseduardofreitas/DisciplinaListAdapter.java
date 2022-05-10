package br.edu.utfpr.carloseduardofreitas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.utfpr.carloseduardofreitas.modelo.Disciplina;


public class DisciplinaListAdapter extends BaseAdapter {
    Context context;
    List<Disciplina> disciplinas;
    private static class HorarioHolder {
        public TextView textViewCodDisciplina;
        public TextView textViewNomeDisciplina;
        public TextView textViewNomeProfessor;
        public TextView textViewNomeCurso;
        public TextView textViewEnvioNotificacoes;
        public TextView textViewAtivarAlarme;
        public TextView textViewCampoEstudo;
    }
    public DisciplinaListAdapter(Context context, List<Disciplina> disciplinas) {
        this.context = context;
        this.disciplinas = disciplinas;
    }
    @Override
    public int getCount() {
        return disciplinas.size();
    }

    @Override
    public Object getItem(int i) {
        return disciplinas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        HorarioHolder holder;

        if (view == null){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_view_disciplinas, viewGroup, false);

            holder = new HorarioHolder();

            holder.textViewCodDisciplina = view.findViewById(R.id.textViewCodDisciplina);
            holder.textViewNomeDisciplina = view.findViewById(R.id.textViewNomeDisciplina);
            holder.textViewNomeProfessor = view.findViewById(R.id.textViewNomeProfessor);
            holder.textViewNomeCurso = view.findViewById(R.id.textViewNomeCurso);
            holder.textViewEnvioNotificacoes = view.findViewById(R.id.textViewEnvioNotificacoes);
            holder.textViewAtivarAlarme = view.findViewById(R.id.textViewAtivarAlarme);
            holder.textViewCampoEstudo = view.findViewById(R.id.textViewCampoEstudo);
            
            view.setTag(holder);

        }else{

            holder = (HorarioHolder) view.getTag();
        }

        holder.textViewCodDisciplina.setText(disciplinas.get(i).getCod_disciplina());
        holder.textViewNomeDisciplina.setText(disciplinas.get(i).getNome_disciplina());
        holder.textViewNomeProfessor.setText(disciplinas.get(i).getNome_professor());
        holder.textViewNomeCurso.setText(disciplinas.get(i).getNome_curso());
        switch (disciplinas.get(i).getEnvio_notificacoes()){
            case 0:
                holder.textViewEnvioNotificacoes.setText(R.string.todas_notificacoes);
                break;
            case 1:
                holder.textViewEnvioNotificacoes.setText(R.string.somente_horario_aula);
                break;
            case 2:
                holder.textViewEnvioNotificacoes.setText(R.string.somente_tarefas);
                break;
            case 3:
                holder.textViewEnvioNotificacoes.setText(R.string.nao_receber_notificacoes);
                break;
            default:
                holder.textViewEnvioNotificacoes.setText("");

        }
        holder.textViewCampoEstudo.setText(disciplinas.get(i).getCampo_estudo());
        if ( disciplinas.get(i).isAtivar_alarme()){
            holder.textViewAtivarAlarme.setText(R.string.alarme_ligado);
        }else{
            holder.textViewAtivarAlarme.setText(R.string.alarme_desligado);
        }
        return view;
    }

}
