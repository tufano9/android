package com.tufano.tufanomovil.gestion.talla;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tufano.tufanomovil.R;
import com.tufano.tufanomovil.database.DBAdapter;
import com.tufano.tufanomovil.global.Funciones;

/**
 * Created por Usuario Tufano on 14/01/2016.
 */
public class ConsultarTallas extends AppCompatActivity
{
    public static Activity  fa;
    private final int    id_mensaje = Funciones.generateViewId();
    private final String TAG        = "ConsultarTallas";
    private       Context   contexto;
    private       DBAdapter manager;
    private       String    usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_tallas);

        fa = this;
        contexto = getApplicationContext();
        manager = new DBAdapter(contexto);

        getExtrasVar();
        createToolBar();
        initComponents();
        inicializarTabla();
    }

    /**
     * Obtiene las variables que fueron pasadas como parametro desde otro activity
     */
    private void getExtrasVar()
    {
        Bundle bundle = getIntent().getExtras();
        usuario = bundle.getString("usuario");
    }

    /**
     * Crea la barra superior con un subtitulo.
     */
    private void createToolBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.gestion_talla_subtitulo);
        setSupportActionBar(toolbar);
    }

    /**
     * Inicializa los componentes. En este caso el Boton flotante para agregar tallas.
     */
    private void initComponents()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent c = new Intent(ConsultarTallas.this, AgregarTalla.class);
                c.putExtra("usuario", usuario);
                startActivity(c);
            }
        });
    }

    /**
     * Inicializa la tabla que contiene las tallas de la BD
     */
    private void inicializarTabla()
    {
        String TAG = "ConsultarTallas";
        Log.i(TAG, "Inicializando tabla..");
        final TableLayout tabla = (TableLayout) findViewById(R.id.table_gestion_tallas);

        TableRow.LayoutParams params = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);

        // Llenando la tabla de forma iterativa
        Cursor cursor = manager.cargarTallas();
        if (cursor.getCount() > 0)
        {
            mostrarTodo();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                Log.i(TAG, "Agregando fila..");
                TableRow fila = new TableRow(contexto);

                final String id_tallas      = String.valueOf(cursor.getInt(0));
                final String talla_producto = cursor.getString(1);
                final String numeraciones   = cursor.getString(2);

                /* Talla */
                TextView talla = generarTextViewTalla(contexto, talla_producto, params);

                /* Numeracion */
                TextView numeracion = generarTextViewNumeracion(contexto, numeraciones, params);

                /* Editar */
                Button editar = generarButtonEditar(contexto, id_tallas, talla_producto, numeraciones);

                /*

                final Button eliminar = new Button(contexto);

                eliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Log.i(TAG, "Eliminar presionado");
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ConsultarTallas.this);

                        dialog.setMessage(R.string.confirmacion_eliminar_talla);
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                new async_eliminarTallaBD().execute(id_tallas);
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
                });
                eliminar.setBackgroundResource(android.R.drawable.ic_menu_delete);
                eliminar.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                */

                LinearLayout opciones = new LinearLayout(contexto);
                opciones.setGravity(Gravity.CENTER);
                opciones.setLayoutParams(params);
                opciones.addView(editar);
                //opciones.addView(eliminar);

                fila.setBackgroundColor(Color.WHITE);
                fila.addView(talla);
                fila.addView(numeracion);
                fila.addView(opciones);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    fila.setBackground(Funciones.intToDrawable(contexto, R.drawable.table_border));
                else
                    //noinspection deprecation
                    fila.setBackgroundDrawable(Funciones.intToDrawable(contexto,
                            R.drawable.table_border));

                tabla.addView(fila);
            }
        }
        else
        {
            final Thread hilo = new Thread()
            {
                @Override
                public void run()
                {
                    synchronized (this)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ocultarTodo();
                                agregarMensaje(R.string.msj_talla_vacio);

                                /*TextView mensaje = new TextView(contexto);
                                mensaje.setText(R.string.msj_talla_vacio);
                                mensaje.setGravity(Gravity.CENTER);
                                mensaje.setTextSize(20f);

                                LinearLayout contenedor = (LinearLayout) findViewById(R.id.contenedor);
                                contenedor.addView(mensaje);*/
                            }
                        });
                    }
                }
            };
            hilo.start();
        }

        cursor.close();
    }

    /**
     * Boton para la edicion de la talla presionada.
     *
     * @param contexto       Contexto de la aplicacion.
     * @param id_tallas      ID de la talla a editar.
     * @param talla_producto Nombre de la talla.
     * @param numeraciones   Numeracion de la talla.
     * @return Boton generado para la edicion de la talla.
     */
    private Button generarButtonEditar(Context contexto, final String id_tallas, final String talla_producto, final String numeraciones)
    {
        Button editar = new Button(contexto);
        editar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent c = new Intent(ConsultarTallas.this, EditarTalla.class);
                c.putExtra("id_talla", id_tallas);
                c.putExtra("talla_producto", talla_producto);
                c.putExtra("numeraciones", numeraciones);
                startActivity(c);
            }
        });
        editar.setBackgroundResource(R.drawable.icn_edit);
        int                       edit_image_width  = 70;
        int                       edit_image_height = 70;
        LinearLayout.LayoutParams parms             = new LinearLayout.LayoutParams(edit_image_width, edit_image_height);
        editar.setLayoutParams(parms);
        editar.setPadding(2, 10, 2, 10);

        return editar;
    }

    /**
     * Generar TextView que contiene la numeracion de la talla.
     *
     * @param contexto     Contexto de la aplicacion.
     * @param numeraciones Numeracion de la aplicacion.
     * @param params       Parametros de la tabla.
     * @return TextView generado con la numeracion.
     */
    private TextView generarTextViewNumeracion(Context contexto, String numeraciones, TableRow.LayoutParams params)
    {
        TextView numeracion = new TextView(contexto);
        numeracion.setText(numeraciones.replace("(", "").replace(")", ""));
        numeracion.setTextColor(Color.DKGRAY);
        numeracion.setGravity(Gravity.CENTER);
        numeracion.setLayoutParams(params);
        numeracion.setTextSize(16f);
        return numeracion;
    }

    /**
     * Generar TextView que contiene el nombre de la talla.
     *
     * @param contexto       Contexto de la aplicacion.
     * @param talla_producto Talla del producto.
     * @param params         Parametros de la tabla
     * @return TextView generado con la talla.
     */
    private TextView generarTextViewTalla(Context contexto, String talla_producto, TableRow.LayoutParams params)
    {
        TextView talla = new TextView(contexto);
        talla.setText(talla_producto);
        talla.setTextColor(Color.DKGRAY);
        talla.setGravity(Gravity.CENTER);
        talla.setLayoutParams(params);
        talla.setTextSize(16f);
        return talla;
    }

    private void agregarMensaje(final int msj)
    {
        final Thread hilo1 = new Thread()
        {
            @Override
            public void run()
            {
                synchronized (this)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Log.i(TAG, "Agregando mensaje");
                            TextView mensaje = new TextView(contexto);
                            mensaje.setText(msj);
                            mensaje.setGravity(Gravity.CENTER);
                            mensaje.setTextSize(20f);
                            mensaje.setId(id_mensaje);
                            mensaje.setLayoutParams(
                                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT));

                            LinearLayout contenedor = (LinearLayout) findViewById(R.id.contenedor_base);
                            contenedor.addView(mensaje);
                        }
                    });
                }
            }
        };
        hilo1.start();
    }

    /**
     * Funcion encargada de ocultar los elementos de la tabla.
     */
    private void ocultarTodo()
    {
        findViewById(R.id.contenedor).setVisibility(View.GONE);
    }

    /**
     * Funcion encargada de mostrar los elementos de la tabla.
     */
    private void mostrarTodo()
    {
        findViewById(R.id.contenedor).setVisibility(View.VISIBLE);
    }

    /*
    class async_eliminarTallaBD extends AsyncTask< String, String, String >
    {
        String id;

        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(ConsultarTallas.this);
            pDialog.setTitle("Por favor espere...");
            pDialog.setMessage("Eliminando la talla...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            id = params[0];

            try
            {
                //enviamos y recibimos y analizamos los datos en segundo plano.
                if (eliminarTalla(id))
                {
                    return "ok";
                }
                else
                {
                    Log.d(TAG, "err");
                    return "err";
                }
            }
            catch (RuntimeException e)
            {
                Log.d(TAG, "Error: " + e);
                return "err2";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();

            if (result.equals("ok"))
            {
                // Muestra al usuario un mensaje de operacion exitosa
                Toast.makeText(contexto, "Talla eliminada exitosamente!!", Toast.LENGTH_LONG).show();

                // Redirige
                Intent c = new Intent(ConsultarTallas.this, ConsultarTallas.class);
                //c.putExtra("usuario",usuario);
                startActivity(c);

                // Prevent the user to go back to this activity
                finish();
            }
            else
            {
                Toast.makeText(contexto, "Hubo un error eliminando la talla..", Toast.LENGTH_LONG).show();
            }
        }

        private boolean eliminarTalla(String id)
        {
            Log.d(TAG, "Eliminar talla con id_talla: " + id);
            long id_talla = manager.eliminarTalla(id);
            return id_talla != -1;
        }
    }
    */
}