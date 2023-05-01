package ma.distributedsystems.service;

import io.grpc.stub.StreamObserver;
import ma.distributedsystems.stubs.Chat;
import ma.distributedsystems.stubs.ChatServiceGrpc;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatService extends ChatServiceGrpc.ChatServiceImplBase {

    private final Map<String, StreamObserver<Chat.Request>> listUsers = new ConcurrentHashMap<>();
//    private final HashMap<String, StreamObserver<Chat.Request>> listUsers = new HashMap<>();
//    @Override
//    public void join(Chat.Request request, StreamObserver<Chat.Request> responseObserver) {
//        // Generate id for the joined user
//        String userId = UUID.randomUUID().toString();
//        // Map the user StreamObserver with his id.
//        Map.entry(userId, responseObserver);
//
//        System.out.println("New user with Id: " + userId);
//
//        // Return response
//        Chat.Request response = Chat.Request.newBuilder()
//                .setFrom("SERVER")
//                .setContent(userId)
//                .build();
//        responseObserver.onNext(response);
//        responseObserver.onCompleted();
//    }

    @Override
    public StreamObserver<Chat.Request> chat(StreamObserver<Chat.Request> responseObserver) {
        return new StreamObserver<Chat.Request>() {
            @Override
            public void onNext(Chat.Request request) {
                String from = request.getFrom();
                String content = request.getContent();
                List<String> toList = request.getToList().stream().toList();
                System.out.println("List contains " + from + " Is " + listUsers.containsKey(from));
                // If it's a new user
                if(!listUsers.containsKey(from)){
                    // Generate id for the joined user
                    String userId = from;
                    // Map the user StreamObserver with his id.
                    listUsers.put(userId, responseObserver);
                    System.out.println("New user with Id: " + userId);
                }
                for(String to : toList){
                    System.out.println("Sending message: \"" + content + "\" to " +to);
                    if(listUsers.containsKey(to)){
                        StreamObserver<Chat.Request> responseObserver = listUsers.get(to);
                        Chat.Request response = Chat.Request.newBuilder()
                                .setFrom(request.getFrom())
                                .setContent(content)
                                .build();
                        responseObserver.onNext(response);
//                        responseObserver.onCompleted();
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }

    @Override
    public void exit(Chat.Request request, StreamObserver<Chat.Empty> responseObserver) {
        listUsers.remove(request.getFrom());
    }
}
