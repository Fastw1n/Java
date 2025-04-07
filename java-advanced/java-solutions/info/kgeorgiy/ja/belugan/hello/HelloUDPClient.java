package info.kgeorgiy.ja.belugan.hello;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import info.kgeorgiy.java.advanced.hello.HelloClient;

public class HelloUDPClient implements HelloClient {
    public static final int TIMEOUT = 100;
    public static final String USAGE = "usage: |host-name or ip-address| port prefix threads thread-requests.";
    public static final String UNDERSCORE = "_";


    public static void main(String[] args) {
        if (args == null || args.length != 5) {
            System.err.println("invalid arguments");
            System.err.println(USAGE);
            return;
        }
        try {
            var hostName = args[0];
            var port = Integer.parseInt(args[1]);
            var prefix = args[2];
            var threads = Integer.parseInt(args[3]);
            var reqPerThread = Integer.parseInt(args[4]);
            HelloUDPClient helloClient = new HelloUDPClient();
            helloClient.run(hostName, port, prefix, threads, reqPerThread);
        } catch (NumberFormatException e) {
            System.err.println("couldn't parse integer arguments: " + e.getMessage());
            System.err.println(USAGE);
        }

    }

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        try (ExecutorService executor = Executors.newFixedThreadPool(threads)) {
            InetSocketAddress address = new InetSocketAddress(host, port);
            IntStream.range(1, threads + 1).forEach(threadNumber ->
                    executor.submit(() -> {
                        String reqScheme = prefix + threadNumber + UNDERSCORE;
                        try (DatagramSocket socket = new DatagramSocket()) {
                            socket.setSoTimeout(TIMEOUT);
                            int bufferSize = socket.getReceiveBufferSize();
                            for (int j = 0; j < requests; j++) {
                                String request = reqScheme + (j + 1);
                                DatagramPacket packetOnSend = new DatagramPacket(request.getBytes(), request.length());
                                DatagramPacket packetOnReceive = new DatagramPacket(new byte[bufferSize], bufferSize);
                                socket.connect(address);
                                while (true) {
                                    socket.send(packetOnSend);
                                    try {
                                        socket.receive(packetOnReceive);
                                    } catch (IOException exception) {
                                        continue;
                                    }
                                    String receivedMessage = new String(
                                            packetOnReceive.getData(),
                                            packetOnReceive.getOffset(),
                                            packetOnReceive.getLength()
                                    );
                                    if (receivedMessage.endsWith(request)) {
                                        break;
                                    }
                                }
                            }
                        } catch (SocketException e) {
                            System.err.println("couldn't open the client socket" + e.getMessage());
                        } catch (IOException e) {
                            System.err.println("some IO exception" + e.getMessage());
                        }
                    }));
        }
    }
}
