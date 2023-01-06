package ru.tsvetkov.exams;

import ru.tsvetkov.exams.base.Connection;
import ru.tsvetkov.exams.base.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final String ip;
    private final int port;

    private String nickname;

    Scanner scanner = new Scanner(System.in);

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;


        //
        pressNickname();



            try {
                Connection<Message> connection = new Connection<>(new Socket(ip, port)); // устанавливается соединение

                System.out.println("Связь с сервером установлена");
                new WriteMsg(connection).start(); // поток отправитель
                new ReadMsg(connection).start();//поток получатель

            } catch (IOException e) {
                System.out.println("Подключение не установлено, проверьте сервер");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

            }

    }


    public void pressNickname() {
        System.out.print("Введите имя: ");

        nickname = scanner.nextLine();
        System.out.println("Привет " + nickname + "\n");


    }

    public class WriteMsg extends Thread {
        private Connection<Message> connection;

        public WriteMsg(Connection<Message> connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            while (true) {
                String userWord;
                System.out.println("Введите сообщение: ");
                userWord = scanner.next(); // сообщения с консоли

                if (userWord.equals("stop")) break;

                Message message = new Message(nickname, userWord);

                // тут нужно отправлять сообщение

                try {
                    connection.sendMessage(message);
                } catch (IOException e) {
                    System.out.println("Сообщение не отправлено - ошибка");
                }
            }
        }
    }

    private class ReadMsg extends Thread {
        Connection<Message> connection;

        public ReadMsg(Connection<Message> connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            Message messageFromServer;
            while (true) {
                try {
                    messageFromServer = connection.readMessage();
                    System.out.println(messageFromServer);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Ошибка получения");
                    ;
                }
            }
        }
    }
}

class ClientStart {

    public static String ipAddr = "localhost";
    public static int port = 8080;


    public static void main(String[] args) {
        new Client(ipAddr, port);
    }
}

