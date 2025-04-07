package info.kgeorgiy.ja.belugan.hello;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import info.kgeorgiy.java.advanced.hello.NewHelloServer;

public class HelloUDPServer implements NewHelloServer {
    private ExecutorService executor;
    private ArrayList<DatagramSocket> sockets;
    private static final String HELLO = "Hello, $";
    private static final String USAGE = "usage: port threads";

    @Override
    public void start(int threads, Map<Integer, String> ports) {
        try {
            executor = Executors.newFixedThreadPool(Math.max(1, ports.size() + threads));
            sockets = new ArrayList<>(Math.max(1, ports.size()));
            for (Map.Entry<Integer, String> portAndFormatEntry : ports.entrySet()) {
                DatagramSocket socket = new DatagramSocket(portAndFormatEntry.getKey());
                sockets.add(socket);
                executor.submit(() -> {
                    while (!executor.isShutdown()) {
                        try {
                            int bufferSize = socket.getReceiveBufferSize();
                            DatagramPacket packetOnReceive = new DatagramPacket(new byte[bufferSize], bufferSize);
                            socket.receive(packetOnReceive);

                            executor.execute(() -> formatAndSendAnswer(portAndFormatEntry, packetOnReceive, socket));
                        } catch (IOException ignored) {
                        }
                    }
                });
            }
        } catch (SocketException e) {
            System.err.println("couldn't open the server socket" + e.getMessage());
        }
    }

    private void formatAndSendAnswer(
            Map.Entry<Integer, String> portAndFormatEntry,
            DatagramPacket packetOnReceive,
            DatagramSocket socket
    ) {
        SocketAddress address = packetOnReceive.getSocketAddress();
        String strOnSend = toReturnString(packetOnReceive, portAndFormatEntry.getValue());
        byte[] byteOnSend = strOnSend.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packetOnSend = new DatagramPacket(byteOnSend, byteOnSend.length, address);
        try {
            socket.send(packetOnSend);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void close() {
        sockets.forEach(DatagramSocket::close);
        if (executor != null) {
            executor.shutdownNow();
            executor.close();
        }
    }


    public static void main(String[] args) {
        HelloUDPServer server = new HelloUDPServer();
        try {
            server.start(Integer.parseInt(args[0]), Map.of(Integer.parseInt(args[1]), HELLO));
        } catch (NumberFormatException e) {
            System.err.println("couldn't parse integer arguments: " + e.getMessage());
            System.err.println(USAGE);
        }

    }

    private String toReturnString(DatagramPacket packet, String value) {
        String string = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
        value = value.replace("$", string);
        return value;
    }
}
