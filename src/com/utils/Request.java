package com.utils;

public class Request {

    private String command;     // Field COMANDO
    private String status;      // Field STATUS
    private String data;        // Field DADOS
    private String message;     // Original message used to create the request object

    public Request () {
        this.command = null;
        this.status = null;
        this.data = null;
    }
    public Request (String message) throws IllegalArgumentException{

        // Save original message
        this.message = message;

        // Breack message into fields
        message = message.replace("\r\n", "\n");
        String[] fields = message.split("\n");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];

            // Capture data
            if (field.contains("COMANDO:")) {

                this.command = field.substring(field.indexOf(":") + 1, field.length());
            } else if (field.contains("STATUS:")) {

                this.status = field.substring(field.indexOf(":") + 1, field.length());
            } else if (field.contains("DADOS:")) {

                this.data = field.substring(field.indexOf(":") + 1, field.length());
            }
        }

        if (this.command == null || (this.command == "GET" && this.data == null) || (this.command == "CHECKFILE" && this.data == null) ||
                (this.command == "POST" && this.data == null)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        String message = "";

        if (this.command != null) {
            message += "COMANDO:" + this.command + "\n";
        }

        if (this.status != null) {
            message += "STATUS:" + this.status + "\n";
        }

        if (this.data != null) {
            message += "DADOS:" + this.data + "\n";
        }

        message = message.substring(0, message.lastIndexOf("\n")) + "\r\n";

        return message;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
