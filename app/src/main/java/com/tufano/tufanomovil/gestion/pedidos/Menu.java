package com.tufano.tufanomovil.gestion.pedidos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.tufano.tufanomovil.R;

/**
 * Created por Usuario Tufano on 20/01/2016.
 */
public class Menu extends AppCompatActivity
{
    private String usuario;
    //private final String TAG = "Menu";
    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_pedidos);
        fa = this;

        //Context contexto = getApplicationContext();
        getExtrasVar();
        initImageViews();
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
     * Inicializa los imageViews
     */
    private void initImageViews()
    {
        ImageView agregar = (ImageView) findViewById(R.id.img_agregar);
        ImageView editar = (ImageView) findViewById(R.id.img_editar);

        agregar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent c = new Intent(Menu.this, SeleccionarCliente.class);
                c.putExtra("usuario", usuario);
                startActivity(c);
            }
        });

        editar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent c = new Intent(Menu.this, Consultar.class);
                c.putExtra("usuario", usuario);
                startActivity(c);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            //mostrarConfiguracion();
            return true;
        }
        else if (id == R.id.profile_settings)
        {
            //mostrarPerfil();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}