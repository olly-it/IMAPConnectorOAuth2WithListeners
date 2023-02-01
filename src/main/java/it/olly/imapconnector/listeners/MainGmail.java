package it.olly.imapconnector.listeners;

public class MainGmail {

    public static void main(String[] args) throws Exception {
        String host = "imap.gmail.com";
        int port = 993;
        String username = "jarions.test@gmail.com";

        // <app password>
        // Google's Account -> Security -> Signing in to Google -> App password
        String password = "";
        ListeningIMAPClient ic = new ListeningIMAPClient(host, port, username, password, null);
        // </app password>

        // <access token>
        // String password = "";
        // Extra properties
        // Properties props = new Properties();
        // props.put("mail.imap.auth.mechanisms", "XOAUTH2");
        // props.put("mail.imaps.sasl.mechanisms.oauth2.oauthToken", password);
        // ListeningIMAPClient ic = new ListeningIMAPClient(host, port, username, password, props);
        // </access token>

        try {
            System.out.println("CONNECTING...");
            ic.connect();
            ic.listenFolder("INBOX");
            System.out.println("FOLDER OPENED - listening on INBOX");
            System.out.println("WAITING FOR RETURN TO EXIT");
            System.in.read();
        } finally {
            ic.disconnect();
        }
    }

}
