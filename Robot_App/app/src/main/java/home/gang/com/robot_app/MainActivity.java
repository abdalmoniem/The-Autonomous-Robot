package home.gang.com.robot_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private BufferedWriter writer;
    private String address = "192.168.1.94";    //an arbitrary ip address
    private int port = 2000;    //an arbitrary port number above 1024

    private Button up_btn;
    private Button dwn_btn;
    private Button rght_btn;
    private Button lft_btn;
    private Button stp_btn;
    private Menu main_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        up_btn = (Button) findViewById(R.id.up_btn);
        dwn_btn = (Button) findViewById(R.id.dwn_btn);
        rght_btn = (Button) findViewById(R.id.rght_btn);
        lft_btn = (Button) findViewById(R.id.lft_btn);
        stp_btn = (Button) findViewById(R.id.stp_btn);

        //disable robot controls until the app connects with the robot's server
        up_btn.setEnabled(false);
        dwn_btn.setEnabled(false);
        rght_btn.setEnabled(false);
        lft_btn.setEnabled(false);
        stp_btn.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        main_menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        //disable the maps until the app connects with robot's server
        menu.getItem(0).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle item selection
        switch (item.getItemId()) {
            case R.id.maps_option:
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                return true;
            case R.id.cnfgr_server_option:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Connect to server");
                alertDialog.setMessage("Enter ip address and port number:");

                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String addressPort = input.getText().toString();
                                String[] addressAndPort = addressPort.split(":");
                                address = addressAndPort[0];
                                port = Integer.parseInt(addressAndPort[1]);
                                new connect().execute();
                                up_btn.setEnabled(true);
                                dwn_btn.setEnabled(true);
                                rght_btn.setEnabled(true);
                                lft_btn.setEnabled(true);
                                stp_btn.setEnabled(true);
                                main_menu.getItem(0).setEnabled(true);
                            }
                        });

                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void move_robot(View view) {
        try {
            switch (view.getId()) {
                case R.id.up_btn:
                    //move forward
                    writer.write("forward", 0, "forward".length());
                    writer.flush();
                    break;
                case R.id.dwn_btn:
                    //move backward
                    writer.write("backward", 0, "backward".length());
                    writer.flush();
                    break;
                case R.id.rght_btn:
                    //move right
                    writer.write("right", 0, "right".length());
                    writer.flush();
                    break;
                case R.id.lft_btn:
                    //move left
                    writer.write("left", 0, "left".length());
                    writer.flush();
                    break;
                case R.id.stp_btn:
                    //stop
                    writer.write("stop", 0, "stop".length());
                    writer.flush();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Error")
                    .setMessage("can't send command to server\nplease reconnect and try again later...")
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    up_btn.setEnabled(false);
                                    dwn_btn.setEnabled(false);
                                    rght_btn.setEnabled(false);
                                    lft_btn.setEnabled(false);
                                    stp_btn.setEnabled(false);
                                    main_menu.getItem(0).setEnabled(false);
                                    dialog.cancel();
                                }
                            })
                    .show();
        }
    }

    private class connect extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String[] params) {
            //do server calls here
            try {
                socket = new Socket(address, port);
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                Network_Handler.set_socket(socket);
                Network_Handler.set_writer(writer);
            } catch (IOException e) {
                publishProgress("socket_error");
                Log.i("socket_error", e.getMessage());
            } catch (NullPointerException ex) {
                publishProgress("writer_error");
                Log.i("writer_error", ex.getMessage());
            }

            return "message";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values.equals("socket_error")) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage("can't reach server\nplease try again later...")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .show();
            } else if (values.equals("writer_error")) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage("can't send command to server\nplease reconnect and try again later...")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        up_btn.setEnabled(false);
                                        dwn_btn.setEnabled(false);
                                        rght_btn.setEnabled(false);
                                        lft_btn.setEnabled(false);
                                        stp_btn.setEnabled(false);
                                        main_menu.getItem(0).setEnabled(false);
                                        dialog.cancel();
                                    }
                                })
                        .show();
            }
        }

        @Override
        protected void onPostExecute(String message) {
            //process message
            try {
                writer.write("I'm alive", 0, "I'm alive".length());
                writer.flush();
            } catch (IOException e) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage("can't reach server\nplease try again later...")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .show();
                Log.i("socket_error", e.getMessage());
            } catch (NullPointerException ex) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage("can't connect to server\nplease try again later...")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        up_btn.setEnabled(false);
                                        dwn_btn.setEnabled(false);
                                        rght_btn.setEnabled(false);
                                        lft_btn.setEnabled(false);
                                        stp_btn.setEnabled(false);
                                        main_menu.getItem(0).setEnabled(false);
                                        dialog.cancel();
                                    }
                                })
                        .show();
                Log.i("writer_error", ex.getMessage());
            }
        }
    }
}