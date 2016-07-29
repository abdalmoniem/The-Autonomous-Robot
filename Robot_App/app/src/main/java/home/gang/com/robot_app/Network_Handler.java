package home.gang.com.robot_app;

import java.io.BufferedWriter;
import java.net.Socket;

/**
 * Created by mn3m on July 24th 2016
 * hifnawy_moniem@hotmail.com
 * Handles socket and writer passing between activities
 */
public class Network_Handler {
    private static Socket socket;
    private static BufferedWriter writer;

    public static void set_socket(Socket socket) {
        Network_Handler.socket = socket;
    }

    public static Socket get_socket() {
        return Network_Handler.socket;
    }

    public static void set_writer(BufferedWriter writer) {
        Network_Handler.writer = writer;
    }

    public static BufferedWriter get_writer() {
        return Network_Handler.writer;
    }
}