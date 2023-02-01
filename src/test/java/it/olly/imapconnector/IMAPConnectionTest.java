package it.olly.imapconnector;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;
import com.hubspot.imap.ImapClientConfiguration;
import com.hubspot.imap.ImapClientFactory;
import com.hubspot.imap.ImapClientFactoryConfiguration;
import com.hubspot.imap.client.FolderOpenMode;
import com.hubspot.imap.client.ImapClient;
import com.hubspot.imap.protocol.capabilities.AuthMechanism;
import com.hubspot.imap.protocol.response.ResponseCode;
import com.hubspot.imap.protocol.response.tagged.NoopResponse;
import com.hubspot.imap.protocol.response.tagged.OpenResponse;

class IMAPConnectionTest {

	/*
	#- host: imap.gmail.com
	#  port: 993
	#  user: hsimaptest1@gmail.com
	#  password: "{{ password }}"
	#  primaryFolder: "[Gmail]/All Mail"
	#  imapConfiguration:
	#    authType: PASSWORD
	#    hostAndPort: "imap.gmail.com:993"
	*/

	protected static final String DEFAULT_FOLDER = "INBOX";
	private static final String host = "imap.gmail.com";
	private static final int port = 993;
	private static final String user = "jarions.test@";
	private static final String password = "";

	protected ImapClient getLoggedInClient() {
		// client factory
		ImapClientFactory imapClientFactory = new ImapClientFactory(ImapClientFactoryConfiguration.builder().build());

		// configurations
		ImapClientConfiguration clientConfiguration = ImapClientConfiguration.builder() //
				.hostAndPort(HostAndPort.fromParts(host, port)) //
				.useSsl(false) //
				.connectTimeoutMillis(1000) //
				.tracingEnabled(true) //
				.noopKeepAliveIntervalSec(1) //
				.addAllowedAuthMechanisms(AuthMechanism.XOAUTH2) // ok?
				.build();

		// build client
		ImapClient client = imapClientFactory.connect(clientConfiguration).join();
		client.login(user, password).join();
		return client;
	}

	@Test
	public void testLogin_doesAuthenticateConnection() throws Exception {
		ImapClient client = null;
		try {
			client = getLoggedInClient();
			System.out.println("CLIENT: " + client);

			OpenResponse openResponse = client.open(DEFAULT_FOLDER, FolderOpenMode.READ).get();
			System.out.println("OPEN RESPONSE: " + openResponse);

			NoopResponse taggedResponse = client.noop().get();
			System.out.println("noop response");
			assertThat(taggedResponse.getCode()).isEqualTo(ResponseCode.OK);
		} finally {
			if (client != null)
				client.close();
		}
	}

}
