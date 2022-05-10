package br.edu.utfpr.carloseduardofreitas.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import br.edu.utfpr.carloseduardofreitas.R;


public class UtilsGUI {

    public static void confirmaAcao(Context contexto,
                                    String mensagem,
                                    DialogInterface.OnClickListener listener){

        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);

        builder.setTitle(R.string.confirm);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setMessage(mensagem);

        builder.setPositiveButton(R.string.sim, listener);
        builder.setNegativeButton(R.string.nao, listener);

        AlertDialog alert = builder.create();
        alert.show();
    }




}
