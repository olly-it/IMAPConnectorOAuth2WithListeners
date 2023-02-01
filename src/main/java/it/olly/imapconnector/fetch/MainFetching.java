package it.olly.imapconnector.fetch;

import javax.mail.Message;

public class MainFetching {

    public static void main(String[] args) throws Exception {
        String host = "imap.gmail.com";
        int port = 993;
        String username = "alessio.olivieri@gmail.com";
        // google's app password
        // Account -> Security -> Signing in to Google -> App password
        String password = "";

        FetchingIMAPClient ic = new FetchingIMAPClient(host, port, username, password);
        try {
            System.out.println("CONNECTING...");
            ic.connect();
            ic.openFolder("INBOX");
            System.out.println("FOLDER OPENED");
            Message lastMessage = ic.getLastMessage();
            System.out.println("last message: " + lastMessage);
            ic.fetch();
            // System.out.println("WAITING FOR RETURN TO EXIT");
            // System.in.read();
        } finally {
            ic.closeFolder();
            ic.disconnect();
        }
    }
}
