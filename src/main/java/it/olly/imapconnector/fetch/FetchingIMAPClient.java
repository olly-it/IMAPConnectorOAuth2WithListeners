package it.olly.imapconnector.fetch;

import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;

import com.sun.mail.imap.IMAPMessage;

public class FetchingIMAPClient {
	private Store store;
	private Folder folder;
	private String username;
	private String password;
	private String host;
	private int port;

	public FetchingIMAPClient(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public void connect() throws MessagingException {
		Properties properties = new Properties();
		properties.put("mail.imap.ssl.enable", "true");
		Session session = Session.getInstance(properties);
		store = session.getStore("imap");
		store.connect(host, port, username, password);
	}

	public void openFolder(String folderName) throws MessagingException {
		folder = store.getFolder(folderName);

		// OPEN
		folder.open(Folder.READ_ONLY);
		System.out.println("folder opened");
	}

	public Message getLastMessage() throws MessagingException {
		int tot = folder.getMessageCount();
		return folder.getMessage(tot - 1);
	}

	long now = 0;

	public void now(String text) {
		if (now == 0)
			now = System.currentTimeMillis();
		System.out.println(text + " * " + now + " (" + (System.currentTimeMillis() - now) + ")");
		now = System.currentTimeMillis();
	}

	public void fetch() throws Exception {
		long now = System.currentTimeMillis();
		now("start opening inbox");
		Folder emailFolder = store.getFolder("INBOX");
		emailFolder.open(Folder.READ_ONLY);

		now("opened");
		// still not fetched yet
		Message[] allMessages = folder.getMessages();
		// System.out.println("F3: " + allMessages[0].getFolder());

		now("got all messages");

		// pre-fetch data
		FetchProfile contentsProfile = new FetchProfile();
		contentsProfile.add(FetchProfile.Item.FLAGS);
		contentsProfile.add(UIDFolder.FetchProfileItem.UID);
		folder.fetch(allMessages, contentsProfile);

		now("fetch done");

		// download messages info only now
		for (int i = 0; i < folder.getMessageCount(); i++) {
			int n = (folder.getMessageCount() - 1) - i;
			IMAPMessage im = (IMAPMessage) allMessages[n];
			UIDFolder uf = (UIDFolder) im.getFolder();
			// System.out.println(i + " - NUMBER: " + im.getMessageNumber());
			System.out.println(i + " - ID: " + uf.getUID(im) + ", FLAGS: " + im.getFlags());
		}
		now("messages parsed");
		System.out.println("*TOT: " + (System.currentTimeMillis() - now));
	}

	public void closeFolder() throws MessagingException {
		folder.close(false);
		System.out.println("folder closed");
	}

	public void disconnect() throws MessagingException {
		store.close();
		System.out.println("client disconnected");
	}
}
