public void run()
        {
            byte[] arrby = new byte[512];
            try {
                do {
                    int n = this.mmInStream.read(arrby);
                    int[] arrn = new int[n];
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("");
                    stringBuilder.append(n);
                    for (int i = 0; i < n; ++i)
                    {
                        arrn[i] = 255 & arrby[i];
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("");
                        stringBuilder2.append(arrn[i]);
                    }
                    String string2 = new String(arrn, 0, n);
                    bluetoothIn.obtainMessage(handlerState, n, -1, (Object)string2.toString()).sendToTarget();
                } while (true);
            }
            catch (IOException iOException) {
                 return;
            }
        }
*/

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity

                    if ((buffer[bytes] == '\n')||(buffer[bytes]=='\r'))
                    {
                        bluetoothIn.obtainMessage(handlerState, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }


        /*
        public void run()
        {
            byte[] byte_in = new byte[1];
            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    mmInStream.read(byte_in);
                    char ch = (char) byte_in[0];
                    bluetoothIn.obtainMessage(handlerState, ch).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        */