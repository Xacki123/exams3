package ru.tsvetkov.exams.base;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection<T extends Message> implements AutoCloseable {
    private Socket socket; // для обеспечения соединения
    private ObjectInputStream input; // поток входящий
    private ObjectOutputStream output; // исходщий поток

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        // передача сеариализованного в сокет
        output = new ObjectOutputStream(this.socket.getOutputStream());
        // десиариализация полученного из сокета
        input = new ObjectInputStream(this.socket.getInputStream());

    }

    public void sendMessage(Message message) throws IOException {
        message.setDateTime();
        output.writeObject(message); // отправка по сети
        output.flush(); //принудительная отправка
    }

    public Message readMessage() throws IOException, ClassNotFoundException {

        return (Message) input.readObject();
    }




    // закрытие соединений при автоклоузе
    @Override
    public void close() throws Exception {
        input.close();
        output.close();
        socket.close();
    }
}
