package com.innovadomotics.ctrbt;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {

                    char MyCaracter = (char) msg.obj;

                    if(MyCaracter == 'a'){
                        tvtMensaje.setText("ACELERANDO");
                    }

                    if(MyCaracter == 'i'){
                        tvtMensaje.setText("GIRO IZQUIERDA");
                    }

                    if(MyCaracter == 'd'){
                        tvtMensaje.setText("GIRO DERECHA");
                    }

                    if(MyCaracter == 'r'){
                        tvtMensaje.setText("RETROCEDIENDO");
                    }

                    if(MyCaracter == 's'){
                        tvtMensaje.setText("DETENIDO");
                    }
                }
            }
        };
   
    }

}