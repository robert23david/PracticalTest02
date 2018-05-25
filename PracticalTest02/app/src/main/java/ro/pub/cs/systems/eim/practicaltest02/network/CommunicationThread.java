package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.Alarm;


public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;
    private Socket utcSocket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");



            String command = bufferedReader.readLine();
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Am primit " + command +" de la "  + socket.getInetAddress().getHostAddress());

            if (command == null || command.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }

            HashMap<String, Alarm> alarms = serverThread.getAlarms();

            String clientAddress = socket.getInetAddress().getHostAddress();

            if(command.startsWith("set")) {

                String[] cmdSplits = command.split(",");
                Alarm alarm = new Alarm(cmdSplits[0], Integer.parseInt(cmdSplits[1]), Integer.parseInt(cmdSplits[2]));
                alarms.put(clientAddress, alarm);
                Log.i(Constants.TAG, "[COMMUNICATION THREAD]Am adaugat alarma " + alarm);
            }
            if(command.startsWith("reset")) {
                serverThread.getAlarms().remove(clientAddress);
                Log.i(Constants.TAG, "[COMMUNICATION THREAD]Am sters alarma pentru " + clientAddress);
            }

            if(command.startsWith("poll")) {
                utcSocket = new Socket("utcnist.colorado.edu", 13);
                BufferedReader utcReader = Utilities.getReader(utcSocket);

                String line = utcReader.readLine();
                line  = utcReader.readLine();
                String[] lineSplits = line.split(" ");
                String date = lineSplits[2];
                String[] dateSplits = date.split(":");
                int hour = Integer.parseInt(dateSplits[0]);
                int minute = Integer.parseInt(dateSplits[1]);
                Log.i(Constants.TAG, "hour: " + hour + "minute: " + minute);

                int time = hour * 60 + minute;
                Alarm clientAlarm = alarms.get(clientAddress);

                String result = null;
                if(clientAlarm == null) {
                    result = "none";
                } else {
                    int clientTime = clientAlarm.getHour() * 60 + clientAlarm.getMinute();
                    if (clientTime < time) {
                        result = "active";
                    } else {
                        result = "inactive";
                    }
                }


                if(result != null || !result.isEmpty())
                printWriter.println(result);
                printWriter.flush();
                Log.v("SERVER Trimit result ",   result);

            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
            if (utcSocket != null) {
                try {
                    utcSocket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
