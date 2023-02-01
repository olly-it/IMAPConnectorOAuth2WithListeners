package it.olly.imapconnector.listeners;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import javax.mail.event.MessageChangedEvent;
import javax.mail.event.MessageChangedListener;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.event.StoreEvent;
import javax.mail.event.StoreListener;

import com.sun.mail.imap.IMAPFolder;

public class ListeningIMAPClient {
    private Store store;
    private String username;
    private String password;
    private String host;
    private int port;
    private Properties properties;

    public ListeningIMAPClient(String host, int port, String username, String password, Properties extraProperties) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        properties = new Properties();
        properties.put("mail.imap.ssl.enable", "true");
        if (extraProperties != null)
            properties.putAll(extraProperties);
    }

    public void connect() throws MessagingException {
        Session session = Session.getInstance(properties);
        store = session.getStore("imap");
        store.addStoreListener(new StoreListener() {
            @Override
            public void notification(StoreEvent e) {
                System.out.println("store>>> notification - " + e);
            }
        });
        store.connect(host, port, username, password);
    }

    public void listenFolder(String folderName) throws MessagingException {
        System.out.println("try to open in another thread");
        (new Th(folderName)).start();
    }

    boolean running = true;

    public class Th extends Thread {
        String folderName;

        public Th(String folderName) {
            this.folderName = folderName;
        }

        @Override
        public void run() {
            Folder folder = null;
            try {
                folder = store.getFolder(folderName);

                // OPEN
                folder.open(Folder.READ_ONLY);
                System.out.println("TH - folder opened: " + folderName);

                // LISTENERS
                folder.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void opened(ConnectionEvent e) {
                        System.out.println("connection>>> opened - " + e);
                    }

                    @Override
                    public void disconnected(ConnectionEvent e) {
                        System.out.println("connection>>> disconnected - " + e);
                    }

                    @Override
                    public void closed(ConnectionEvent e) {
                        System.out.println("connection>>> closed - " + e);
                    }
                });
                System.out.println("ConnectionListener ok");
                folder.addFolderListener(new FolderListener() {

                    @Override
                    public void folderRenamed(FolderEvent e) {
                        System.out.println("folder>>> renamed - " + e);
                    }

                    @Override
                    public void folderDeleted(FolderEvent e) {
                        System.out.println("folder>>> deleted - " + e);

                    }

                    @Override
                    public void folderCreated(FolderEvent e) {
                        System.out.println("folder>>> created - " + e);
                    }
                });
                System.out.println("FolderListener ok");
                folder.addMessageChangedListener(new MessageChangedListener() {

                    @Override
                    public void messageChanged(MessageChangedEvent e) {
                        // invoked when a message modifies it's status (unread-read)
                        System.out.println("changed>>> changed - " + e);
                    }
                });
                System.out.println("MessageChangedListener ok");
                folder.addMessageCountListener(new MessageCountListener() {

                    @Override
                    public void messagesRemoved(MessageCountEvent e) {
                        System.out.println("count>>> removed - " + e);
                    }

                    @Override
                    public void messagesAdded(MessageCountEvent e) {
                        // invoked when a message comes to this folder
                        System.out.println("count>>> added - " + e);
                    }
                });
                System.out.println("MessageCountListener ok");

                // INFINITE LOOP
                while (running) {
                    if (folder instanceof IMAPFolder) {
                        IMAPFolder f = (IMAPFolder) folder;
                        System.out.println("TH - doing idle on " + folderName);
                        f.idle();
                        // i get here when connection has been closed
                        // maybe also on long timeout?
                        System.out.println("TH - idle done on " + folderName);
                    } else {
                        // This is to force the IMAP server to send us<br />
                        // EXISTS notifications. <br />
                        System.out.println("TH - sleep 5000 then count messages on " + folderName);
                        Thread.sleep(5000);
                        folder.getMessageCount();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (folder != null && folder.isOpen())
                        folder.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("TH - finished: " + folderName);
        }
    }

    public void disconnect() throws MessagingException {
        running = false;
        store.close();
    }
}
