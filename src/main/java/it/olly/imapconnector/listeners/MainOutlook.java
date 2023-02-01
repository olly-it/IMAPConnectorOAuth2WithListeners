package it.olly.imapconnector.listeners;

import java.util.Properties;

public class MainOutlook {

    public static void main(String[] args) throws Exception {
        // set up connection
        String host = "outlook.office365.com";
        int port = 993;
        String username = "jarions.test@enginius.com";
        // access token
        String password = "";

        // Extra properties
        Properties props = new Properties();
        /*props.put("mail.store.protocol", "imap");
        props.put("mail.imap.host", host);
        props.put("mail.imap.port", "" + port);
        // props.put("mail.imap.ssl.enable", "true"); // already done in IMAPClient
        props.put("mail.imap.starttls.enable", "true");
        props.put("mail.imap.auth", "true");
        props.put("mail.imap.auth.mechanisms", "XOAUTH2");
        props.put("mail.imap.user", username);
        props.put("mail.debug", "true");
        props.put("mail.debug.auth", "true");*/

        // props.put("mail.imap.ssl.enable", "true"); // already done in IMAPClient
        // props.put("mail.smtp.starttls.enable", "true");
        // props.put("mail.smtp.auth", "true");
        props.put("mail.imap.auth.mechanisms", "XOAUTH2");
        // props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
        props.put("mail.imaps.sasl.mechanisms.oauth2.oauthToken", password);

        ListeningIMAPClient ic = new ListeningIMAPClient(host, port, username, password, props);
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
