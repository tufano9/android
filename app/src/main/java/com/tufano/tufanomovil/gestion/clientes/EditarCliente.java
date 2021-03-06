package com.tufano.tufanomovil.gestion.clientes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tufano.tufanomovil.R;
import com.tufano.tufanomovil.database.DBAdapter;
import com.tufano.tufanomovil.gestion.pedidos.SeleccionarCliente;
import com.tufano.tufanomovil.global.Funciones;

import java.util.List;

/**
 * Created por Usuario Tufano on 19/01/2016.
 */
public class EditarCliente extends AppCompatActivity
{
    private final String TAG = "EditarClienteDetalles";
    private String usuario, id_cliente, rs, rif, estados, tlf, estatus, mail, dir;
    private Context        contexto;
    private ProgressDialog pDialog;
    private DBAdapter      manager;
    private Spinner        sp_rif, sp_estado;
    private EditText razon_social, rif1, rif2, telefono, email, direccion;
    private boolean desdePedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_clientes);

        contexto = getApplicationContext();
        manager = new DBAdapter(contexto);

        createToolBar();
        getExtrasVar();
        initComponents();
        initButtons();
        initListeners();
        loadSpinnerData();
        cargarValoresPrevios();
        noInitialFocus();
    }

    /**
     * Inicializa los listeners
     */
    private void initListeners()
    {
        rif1.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                Log.d(TAG, "LENGTH: " + rif1.getText().toString().length());
                // Si el rif esta lleno, pasa automaticamente al siguiente.
                if (rif1.getText().toString().length() == 8)
                    rif2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });
    }

    /**
     * Inicializa los botones.
     */
    private void initButtons()
    {
        Button editar = (Button) findViewById(R.id.btn_editar_cliente);
        editar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (camposValidados())
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(EditarCliente.this);

                    dialog.setMessage(R.string.confirmacion_editar_cliente);
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("Si", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            editarCliente();
                        }
                    });

                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }
            }
        });
    }

    /**
     * Inicializa los componentes primarios de la activity
     */
    private void initComponents()
    {
        sp_rif = (Spinner) findViewById(R.id.sp_rif_editar_cliente);
        sp_estado = (Spinner) findViewById(R.id.sp_estados_editar_cliente);
        razon_social = (EditText) findViewById(R.id.et_razon_social_editar_cliente);
        rif1 = (EditText) findViewById(R.id.et_rif1_editar_cliente);
        rif2 = (EditText) findViewById(R.id.et_rif2_editar_cliente);
        telefono = (EditText) findViewById(R.id.et_telefono_editar_cliente);
        email = (EditText) findViewById(R.id.et_email_editar_cliente);
        direccion = (EditText) findViewById(R.id.et_direccion_editar_cliente);
    }

    /**
     * Obtiene las variables que fueron pasadas como parametro desde otro activity
     */
    private void getExtrasVar()
    {
        Bundle bundle = getIntent().getExtras();
        usuario = bundle.getString("usuario");
        id_cliente = bundle.getString("id_cliente");

        // Obtiene un valor booleano que indicara si este activity fue instanciado a traves del
        // activity de pedidos.
        desdePedidos = bundle.getBoolean("desdePedidos");

        List<String> datos = manager.cargarDatosClientes(id_cliente);
        rs = datos.get(0);
        rif = datos.get(1);
        estados = datos.get(2);
        tlf = datos.get(3);
        mail = datos.get(4);
        dir = datos.get(5);
        estatus = datos.get(6);
    }

    /**
     * Crea la barra superior con un subtitulo.
     */
    private void createToolBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.editar_cliente_subtitulo);
        setSupportActionBar(toolbar);
    }

    /**
     * Evita el focus principal al abrir la activity, el cual despliega automaticamente el teclado
     */
    private void noInitialFocus()
    {
        LinearLayout layout = (LinearLayout) findViewById(R.id.LinearLayout_MainActivity);
        layout.requestFocus();
    }

    /**
     * Edita un cliente en la BD. Los datos seran obtenidos automaticamente desde los campos.
     */
    private void editarCliente()
    {
        String rs = Funciones.capitalizeWords(razon_social.getText().toString().trim());
        String rif = sp_rif.getSelectedItem().toString() + rif1.getText().toString().trim() + "-" +
                rif2.getText().toString().trim();
        String estado = Funciones.capitalizeWords(sp_estado.getSelectedItem().toString().trim());
        String tlf    = telefono.getText().toString().trim();
        String mail   = email.getText().toString().trim();
        String dir    = Funciones.capitalizeWords(direccion.getText().toString().trim());
        new async_editarClienteBD().execute(rs, rif, estado, tlf, mail, dir, estatus);
    }

    /**
     * Valida los campos antes de editar el cliente.
     *
     * @return True si los campos son correctos, false en caso contrario.
     */
    private boolean camposValidados()
    {
        Log.i(TAG, "Validando campos");

        if (razon_social.getText().toString().trim().isEmpty())
        {
            razon_social.setError("Introduzca una razon social!!");
            return false;
        }
        else if (sp_rif.getSelectedItemPosition() == 0)
        {
            TextView errorText = (TextView) sp_rif.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(R.string.error_rif);//changes the selected item text to this
            return false;
        }
        else if (rif1.getText().toString().trim().isEmpty())
        {
            rif1.setError("Introduzca un rif!!");
            return false;
        }
        else if (rif2.getText().toString().trim().isEmpty())
        {
            rif2.setError("Introduzca un rif!!");
            return false;
        }
        else if (sp_estado.getSelectedItemPosition() == 0)
        {
            TextView errorText = (TextView) sp_estado.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(R.string.error_estado);//changes the selected item text to this
            return false;
        }
        else if (telefono.getText().toString().trim().isEmpty())
        {
            telefono.setError("Introduzca un telefono!!");
            return false;
        }
        else if (email.getText().toString().trim().isEmpty())
        {
            email.setError("Introduzca un email!!");
            return false;
        }
        else if (Funciones.isValidEmail(email.getText().toString()))
        {
            email.setError("Por favor, ingrese un email valido!!");
            return false;
        }
        else if (direccion.getText().toString().trim().isEmpty())
        {
            direccion.setError("Introduzca una direccion!!");
            return false;
        }
        else
        {
            razon_social.setError(null);
            rif1.setError(null);
            rif2.setError(null);
            telefono.setError(null);
            email.setError(null);
            direccion.setError(null);
            return true;
        }
    }

    /**
     * Carga la data por defecto en los spinners (Tipo de Rif y Estados).
     */
    private void loadSpinnerData()
    {
        Log.i(TAG, "loadSpinnerData");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(contexto, R.array.rif_lista, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_rif.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                contexto, R.array.estados_lista, R.layout.spinner_item);
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_estado.setAdapter(adapter2);
    }

    /**
     * Carga la data real actualmente utilizada por el cliente.
     */
    private void cargarValoresPrevios()
    {
        String tipo_rif = rif.substring(0, 1);

        int pos = Funciones.buscarPosicionElemento(tipo_rif, sp_rif);
        sp_rif.setSelection(pos);

        pos = Funciones.buscarPosicionElemento(estados, sp_estado);
        sp_estado.setSelection(pos);

        String rif_1 = rif.substring(1, rif.length() - 2);
        String rif_2 = rif.substring(rif.length() - 1);
        razon_social.setText(rs);
        rif1.setText(rif_1);
        rif2.setText(rif_2);
        telefono.setText(tlf);
        email.setText(mail);
        direccion.setText(dir);
    }

    class async_editarClienteBD extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                long res = editarCliente(params);

                if (res == -1)
                {
                    Log.d(TAG, "err");
                    return "err";
                }
                else if (res == -2)
                {
                    Log.d(TAG, "Cliente ya existente");
                    return "existente";
                }
                else
                {
                    return "ok";
                }
            }
            catch (RuntimeException e)
            {
                Log.d(TAG, "Error: " + e);
                return "err2";
            }
        }

        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(EditarCliente.this);
            pDialog.setTitle("Por favor espere...");
            pDialog.setMessage("Editando el cliente...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();

            switch (result)
            {
                case "ok":
                    // Muestra al usuario un mensaje de operacion exitosa
                    Toast.makeText(contexto, "Cliente editado exitosamente!!", Toast.LENGTH_LONG).show();

                    if (desdePedidos)
                    {
                        // Redirige a la pantalla de Pedidos
                        Intent c = new Intent(EditarCliente.this, SeleccionarCliente.class);
                        c.putExtra("usuario", usuario);
                        c.putExtra("desdeClientes", true);
                        c.putExtra("idClienteCreado", id_cliente);
                        startActivity(c);
                        SeleccionarCliente.fa.finish();
                    }
                    else
                    {
                        // Redirige a la pantalla de Home
                        Intent c = new Intent(EditarCliente.this, ConsultarCliente.class);
                        c.putExtra("usuario", usuario);
                        startActivity(c);
                        ConsultarCliente.fa.finish();
                    }

                    // Prevent the user to go back to this activity
                    finish();
                    break;
                case "existente":
                    Toast.makeText(contexto, "Ya existe un cliente con la misma razon social o rif..", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(contexto, "Hubo un error editando el cliente..", Toast.LENGTH_LONG).show();
                    break;
            }
        }

        private long editarCliente(String[] datos)
        {
            if (!existeCliente(datos[0], datos[1], id_cliente))
            {
                // El cliente no existe, puedo continuar..
                return manager.editarCliente(id_cliente, datos);
            }
            else
            {
                String rifs = sp_rif.getSelectedItem().toString() + rif1.getText().toString().trim() + "-" +
                        rif2.getText().toString().trim();
                // El cliente existe, valido si cambio el campo o se mantienen igual
                if (!razon_social.getText().toString().trim().equals(rs) || !rif.equals(rifs))
                {
                    Log.d(TAG, "El campo ingresado por el usuario cambio y por lo tanto ya tengo ese cliente");
                    //Log.d(TAG, "1-->"+razon_social.getText().toString().trim().equals(rs)+", 2-->"+rif.equals(rifs));
                    // El campo ingresado por el usuario cambio y por lo tanto ya tengo ese cliente
                    return -2;
                }
                else
                {
                    // Los campos se mantienen, asi que el cliente que ya existe, es el mismo que
                    // ando editando.. prosigo como si nada
                    Log.d(TAG, "Los campos se mantienen, asi que el cliente que ya existe, es el mismo que ando editando.. prosigo como si nadae");
                    return manager.editarCliente(id_cliente, datos);
                }
            }
        }

        private boolean existeCliente(String rs, String rif, String id_cliente)
        {
            Cursor cursor = manager.cargarClientesNombre(rs, rif, id_cliente);
            Log.d(TAG, "Existen " + cursor.getCount() + " clientes con esa razon social o rif..");
            return cursor.getCount() > 0;
        }
    }
}
