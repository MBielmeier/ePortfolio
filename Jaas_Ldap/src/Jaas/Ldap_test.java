package Jaas;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class Ldap_test {

	public static void main(String[] args) throws Exception {

		Ldap_test Ldap = new Ldap_test();
		Ldap.LogInOut();
	}

	public void LogInOut() throws LoginException {

		// LoginContext stellt JAAS Methoden zum Authentifizieren eines Useres
		// bereit z.B. login();
		LoginContext lc = null;

		// im System muss die Konfigurationsdatei als Eigenschaft gesetzt werden
		System.setProperty("java.security.auth.login.config",
				System.getProperty("user.dir") + "\\src\\login.cfg");

		try {
			lc = new LoginContext("Login", new LDAPCallbackHandler());
		} catch (LoginException loginexception) {
			System.err.println("Cannot create LoginContext. "
					+ loginexception.getMessage());
			System.exit(-1);
		} catch (SecurityException securityexception) {
			System.err.println("Cannot create LoginContext. "
					+ securityexception.getMessage());
			System.exit(-1);
		}

		// User hat 3 Versuche sich erfolgreich einzuloggen
		// schlagen alle 3 Versuche fehl bricht das Programm den
		// Authentifizierungsvorgang ab
		int i;
		for (i = 0; i < 3; i++) {
			try {

				lc.login();

				break;

			} catch (LoginException loginexception) {

				System.err.println("Authentifizierung fehlgeschlagen:");
				System.err.println("  " + loginexception.getMessage());
				try {
					Thread.currentThread();
					Thread.sleep(3000);
				} catch (Exception e) {

				}

			}
		}
		// Vorgang wird abgebrochen weil Authentifizierung 3 mal fehlgeschlagen
		// ist!
		if (i == 3) {
			System.out.println("Authentifizierung gescheitert! ");
			System.exit(-1);
		}

	}
}
