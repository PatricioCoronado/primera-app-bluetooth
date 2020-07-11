package com.segainvex.controlremoto;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    TextView edtTextoOut;
    ImageButton btnAdelante, btnIzquierda,btnDerecha,btnReversa,btnStop, btnEnviar;
    Button btnDesconcetar;
    EditText tvtMensaje;

    //Código añadido para bluetooth
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;
    //Fin código añadido para bluetooth
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Código añadido para Bluetooth
        bluetoothIn = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                if (msg.what == handlerState)
                {
                    String mensajeRecibido = (String) msg.obj;
                    edtTextoOut.setText(mensajeRecibido);
                }
            }
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();
        //Fin código añadido para Bluetooth
        edtTextoOut = findViewById(R.id.editTextoOut);
        btnAdelante = findViewById(R.id.btnAdelante);
        btnReversa = findViewById(R.id.btnReversa);
        btnIzquierda = findViewById(R.id.btnIzquierda);
        btnDerecha = findViewById(R.id.btnDerechar);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnStop = findViewById(R.id.btnStop);
        btnDesconcetar = findViewById(R.id.btnDesconectar);
        tvtMensaje = findViewById(R.id.tvtMensaje);
        //Acción del botón btnEnviar
        btnEnviar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //String GetDat = edtTextoOut.getText().toString();
                edtTextoOut.setText("");
                String GetDat = tvtMensaje.getText().toString();
                MyConexionBT.write(GetDat+'\r');
            }
        });
        //Acción del botón btnDesconectar
        btnDesconcetar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (btSocket!=null)
                {
                    try {btSocket.close();}
                    catch (IOException e)
                    { Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();;}
                }
                finish();
            }
        });
        //Acción del botón btnAdelante
        btnAdelante.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                edtTextoOut.setText("");
                MyConexionBT.write("*IDN?"+'\r');
            }
        });
        //Acción del botón btnReversa
        btnReversa.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                edtTextoOut.setText("");
                MyConexionBT.write("MOT:FNA"+'\r');
            }
        });
        //Acción del botón btnIzquieda
        btnIzquierda.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                edtTextoOut.setText("");
                MyConexionBT.write("*IDN?"+'\r');
            }
        });
        //Acción del botón btnDerecha
        btnDerecha.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                edtTextoOut.setText("");
                MyConexionBT.write("*IDN?"+'\r');
            }
        });
        //Acción del botón btnStop
        btnStop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                edtTextoOut.setText("");
                MyConexionBT.write("MOT:IAC"+'\r');

            }
        });
    }//OnCreate
    //Código añadido para el bluetooth
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
    //Fin código añadido para el bluetooth
    // Métodos del ciclo de vida de la MainActivity
    @Override
    public void onResume()
    {
        super.onResume();

        Intent intent = getIntent();
        address = intent.getStringExtra(DispositivosVinculados.EXTRA_DEVICE_ADDRESS);
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }
    @Override
    public void onPause()
    {
        super.onPause();
        try
        { // Cuando se sale de la aplicación esta parte permite que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {}
    }
    //Comprueba que el dispositivo Bluetooth
    //está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
    //Crea el hilo secundario
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        //Esto es lo que está ejecutando el thread del bluetooth
        public void run()
        {
            final byte delimiter = 10;
            int readBufferPosition = 0;
            byte[] readBuffer = new byte[1024];
            while (true)
            {
                try
                {
                    int bytesAvailable = mmInStream.available();
                    //Si hay datos..
                    if (bytesAvailable > 0)
                    {
                        byte[] packetBytes = new byte[bytesAvailable];
                        mmInStream.read(packetBytes);//los lee en packetBytes
                        //Recorre el array de bytes leido
                        for(int i=0;i<bytesAvailable;i++)
                        {
                            byte b = packetBytes[i];//Lee un byte
                            if(b == delimiter) //Si es el delimitador lo añade a readBuffer y sale
                            {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                //Crea el string data con el comando
                                final String data = new String(encodedBytes, "US-ASCII");
                                int longData = readBufferPosition;
                                readBufferPosition = 0;
                                // Send the obtained bytes to the UI Activity via handler
                                bluetoothIn.obtainMessage(handlerState, longData, -1, data).sendToTarget();
                                //Muy interesante. Saca el comando "data" al UI
                                //handler.post(new Runnable(){public void run(){myLabel.setText(data);}});
                            }//if(b == delimiter)
                            else //Si no es el delimitador simplemente añade el byte al buffer de lectura
                            {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }//for(int i=0;i<bytesAvailable;i++)
                    }//if (bytesAvailable > 0)
                }
                catch (IOException ex)
                {
                    break;
                }
            }//While(true)
        }//run
        //Envio de datos a Arduino por Bluetooth
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }// fin de envio de datos a Arduino por Bluetooth
    }//Fin de la clase del hilo secundario
}//Clase