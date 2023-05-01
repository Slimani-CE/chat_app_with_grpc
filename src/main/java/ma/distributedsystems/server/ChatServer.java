package ma.distributedsystems.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import ma.distributedsystems.service.ChatService;

import java.io.IOException;

public class ChatServer {
    public static void main(String[] args) throws IOException {
        Server server = ServerBuilder.forPort(1337)
                .addService(new ChatService())
                .build();
        server.start();
        System.out.println("Server is running on port " + server.getPort());
        try {
            server.awaitTermination();
            System.out.println("Server is terminated");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
