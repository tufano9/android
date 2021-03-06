package com.tufano.tufanomovil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tufano.tufanomovil.gestion.clientes.ConsultarCliente;
import com.tufano.tufanomovil.gestion.pedidos.ConsultarPedidos;
import com.tufano.tufanomovil.gestion.productos.ConsultarProductos;
import com.tufano.tufanomovil.global.FuncionesMenus;

/**
 * Creado por Gerson el 11/01/2016.
 */
public class Home extends AppCompatActivity
{
    public static Activity fa;
    private final String TAG = "HomeActivity";
    private String  usuario;
    private Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        contexto = getApplicationContext();
        fa = this;

        createToolBar();
        getExtrasVar();
        initImageView();
    }

    /**
     * Inicializa los Linear Layout que contienen los menus para acceder a la aplicacion.
     */
    private void initImageView()
    {
        ImageView consulta = (ImageView) findViewById(R.id.opc1);
        consulta.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                abrirConsultas();
            }
        });

        ImageView productos = (ImageView) findViewById(R.id.opc2);
        productos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                abrirGestionProductos();
            }
        });

        ImageView clientes = (ImageView) findViewById(R.id.opc3);
        clientes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                abrirGestionClientes();
            }
        });

        ImageView pedidos = (ImageView) findViewById(R.id.opc4);
        pedidos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                abrirGestionPedidos();
            }
        });

        ImageView facturas = (ImageView) findViewById(R.id.opc5);
        facturas.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                abrirGestionFacturas();
            }
        });

        ImageView Cobranzas = (ImageView) findViewById(R.id.opc6);
        Cobranzas.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                abrirGestionCobranzas();
            }
        });
    }

    /**
     * Obtiene las variables que fueron pasadas como parametro desde otro activity
     */
    private void getExtrasVar()
    {
        // Obtiene el ID del usuario que se logeo.
        Bundle bundle = getIntent().getExtras();
        usuario = bundle.getString("usuario");
    }

    /**
     * Crea la barra superior con un subtitulo.
     */
    private void createToolBar()
    {
        // Crea la barra superior con un subtitulo.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.home_subtitulo);
        setSupportActionBar(toolbar);
    }

    /**
     * Metodo encargado de abrir el activity de consultas
     */
    private void abrirConsultas()
    {
        Log.w(TAG, "Has presionado sobre Consultas");
        Toast.makeText(contexto, "No implementado aun", Toast.LENGTH_SHORT).show();
    }

    /**
     * Metodo encargado de abrir el activity de productos
     */
    private void abrirGestionProductos()
    {
        Log.w(TAG, "Has presionado sobre Gestion de Productos");
        Intent c = new Intent(Home.this, ConsultarProductos.class);
        c.putExtra("usuario", usuario);
        startActivity(c);
    }

    /**
     * Metodo encargado de abrir el activity de clientes
     */
    private void abrirGestionClientes()
    {
        Log.w(TAG, "Has presionado sobre Gestion de Clientes");
        Intent c = new Intent(Home.this, ConsultarCliente.class);
        c.putExtra("usuario", usuario);
        startActivity(c);
    }

    /**
     * Metodo encargado de abrir el activity de pedidos
     */
    private void abrirGestionPedidos()
    {
        Log.w(TAG, "Has presionado sobre Gestion de Pedidos");
        Intent c = new Intent(Home.this, ConsultarPedidos.class);
        c.putExtra("usuario", usuario);
        startActivity(c);
    }

    /**
     * Metodo encargado de abrir el activity de facturas
     */
    private void abrirGestionFacturas()
    {
        Log.w(TAG, "Has presionado sobre Facturas");
        Toast.makeText(contexto, "No implementado aun", Toast.LENGTH_SHORT).show();
    }

    /**
     * Metodo encargado de abrir el activity de cobranzas
     */
    private void abrirGestionCobranzas()
    {
        Log.w(TAG, "Has presionado sobre Cobranzas");
        Toast.makeText(contexto, "No implementado aun", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            FuncionesMenus.mostrarConfiguracion(usuario, contexto, Home.this);
            return true;
        }
        else if (id == R.id.profile_settings)
        {
            FuncionesMenus.mostrarPerfil(usuario, contexto, Home.this);
            return true;
        }
        else if (id == R.id.get_backup)
        {
            FuncionesMenus.getBackup(contexto, Home.this);
            return true;
        }
        else if (id == R.id.do_backup)
        {
            FuncionesMenus.doBackup(contexto, Home.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo que gestionara la accion a realizar al momento de presionar el boton de volver.
     */
    @Override
    public void onBackPressed()
    {
        // Si estoy en el nivel mas bajo (Home)
        if (getFragmentManager().getBackStackEntryCount() <= 1)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setMessage(R.string.cerrar_sesion);
            dialog.setCancelable(false);
            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    finish();
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
}