package br.edu.utfpr.carloseduardofreitas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.utfpr.carloseduardofreitas.modelo.Atividade;
import br.edu.utfpr.carloseduardofreitas.utils.UtilsDate;

public class AtividadeListAdapter extends BaseAdapter {
    Context context;
    List<Atividade> atividades;

    private static class AtividadeHolder {
        public TextView textViewTitulo;
        public TextView textViewDescricao;
        public TextView textViewDataEntrega;
        public TextView textViewHoraEntrega;
    }

    public AtividadeListAdapter(Context context, List<Atividade> atividades) {
        this.context = context;
        this.atividades = atividades;
    }

    @Override
    public int getCount() {
        return atividades.size();
    }

    @Override
    public Object getItem(int i) {
        return atividades.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        AtividadeHolder holder;

        if (view == null){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_view_atividades, viewGroup, false);

            holder = new AtividadeHolder();

            holder.textViewTitulo = view.findViewById(R.id.textViewTitulo);
            holder.textViewDescricao = view.findViewById(R.id.textViewDescrição);
            holder.textViewDataEntrega = view.findViewById(R.id.textViewDataEntrega);
            holder.textViewHoraEntrega = view.findViewById(R.id.textViewHoraEntrega);

            view.setTag(holder);

        }else{

            holder = (AtividadeListAdapter.AtividadeHolder) view.getTag();
        }

        holder.textViewTitulo.setText(atividades.get(i).getTitulo());
        holder.textViewDescricao.setText(atividades.get(i).getDescricao());
        holder.textViewDataEntrega.setText(UtilsDate.formatDate(context,atividades.get(i).getDataEntrega()));
        holder.textViewHoraEntrega.setText(UtilsDate.formatTime(context,atividades.get(i).getHorarioEntrega()));

        return view;
    }


}
