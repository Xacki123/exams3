package ru.tsvetkov.exams;

import ru.tsvetkov.exams.base.Connection;
import ru.tsvetkov.exams.base.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {
    // прослушиваемый порт
    private int port;
    // список подключенных пользователей
    private ArrayList<Connection<Message>> connections;
    private HashMap<Connection, Message> messages;

    public Server(int port) {
        this.port = port;
        connections = new ArrayList<>();
        messages = new HashMap<>();

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен...");

            while (true){
                Socket socket = serverSocket.accept(); // прослушка порта

                Connection<Message> connection = new Connection<>(socket); // создание соединения
                connections.add(connection); // добавление в список подключений

                new LoadMsg(connection).start();
                new SendMsg(connection).start();


            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private class LoadMsg extends Thread {
        Connection<Message> connection;

        public LoadMsg(Connection<Message> connection) {
            this.connection = connection;
        }
        @Override
        public void run() {
            while (true){
            try {
                Message fromClient = connection.readMessage();
                // воспроизведение сообщения на сервере для отслеживания
                System.out.println("Сообщение от клиента" + fromClient);
                // добавление сообщения в блокирующую очередь
                synchronized (messages){
                    messages.put(connection, fromClient);
                }
            } catch (IOException|ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
        }
    }

    private class SendMsg extends Thread {
        Connection<Message> connection;

        public SendMsg(Connection<Message> connection) {
            this.connection = connection;
        }
        @Override
        public void run() {
            // отправка сообщений из блокирующей очереди всем пользователям
            while (true) {
                try {
                    synchronized (messages) {
                        for (Map.Entry<Connection, Message> mas : messages.entrySet()) {

                            for (Connection<Message> o : connections) {
                            if(!o.equals(mas.getKey())) {
                                o.sendMessage(mas.getValue());
                            }
                            }
                            messages.remove(mas.getKey(), mas.getValue());
                        }
                    }
                    Thread.sleep(100); // чтобы не спамить
                } catch (IOException|InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}

class ServerStart{
    public static int port = 8080;
    public static void main(String[] args) {
        Server server = new Server(port);
    }
}
