package Jaas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.util.Arrays;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class LDAPCallbackHandler implements CallbackHandler {

	// überschreibt die handle()-Methode der CallbackHandler-Klasse
	// Login-Modul übergibt ein Array mit "geeigneten" callbacks
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof TextOutputCallback) {

				TextOutputCallback txtoutputcall = (TextOutputCallback) callbacks[i];

				// verschiedene Textoutputcallbacks werden unterschieden
				// dies können sämtliche Informationen, Fehler und Warnungen
				// sein, nachdem der entsprechnede Nachrichtentyp ermittelt
				// wurde

				switch (txtoutputcall.getMessageType()) {

				case TextOutputCallback.INFORMATION:
					System.out.println(txtoutputcall.getMessage());
					break;
				case TextOutputCallback.ERROR:
					System.out.println("ERROR: " + txtoutputcall.getMessage());
					break;
				case TextOutputCallback.WARNING:
					System.out
							.println("WARNING: " + txtoutputcall.getMessage());
					break;

				default:
					throw new IOException("Unsupported message type: "
							+ txtoutputcall.getMessageType());

				}
				// implementiert einen "Callback" zur Abfrage des Benutzernamens
			} else if (callbacks[i] instanceof NameCallback) {

				NameCallback namecallback = (NameCallback) callbacks[i];

				// getPrompt ist die Eingabeaufforderung eines Benutzernamens an
				// den User
				System.err.print(namecallback.getPrompt());
				System.err.flush();
				// setzt eingegebene Zeichenkette als Benutzername
				namecallback.setName((new BufferedReader(new InputStreamReader(
						System.in))).readLine());

			} else if (callbacks[i] instanceof PasswordCallback) {

				PasswordCallback passwordcallback = (PasswordCallback) callbacks[i];
				System.err.print(passwordcallback.getPrompt());
				System.err.flush();

				// setzt eingelesene Zeichenkette als Passwort nachdem über
				// readPasswort() das Eingegebene eingelesen wird
				passwordcallback.setPassword(readPassword(System.in));

			} else {
				throw new UnsupportedCallbackException(callbacks[i],
						"Unrecognized Callback");
			}
		}
	}

	private char[] readPassword(InputStream in) throws IOException {

		char[] lineBuffer;
		char[] buffer;

		buffer = lineBuffer = new char[128];

		int room = buffer.length;
		int offset = 0;
		int c;

		loop: while (true) {
			switch (c = in.read()) {
			case -1:
			case '\n':
				break loop;

			case '\r':
				int c2 = in.read();
				if ((c2 != '\n') && (c2 != -1)) {
					if (!(in instanceof PushbackInputStream)) {
						in = new PushbackInputStream(in);
					}
					((PushbackInputStream) in).unread(c2);
				} else
					break loop;

			default:
				if (--room < 0) {
					buffer = new char[offset + 128];
					room = buffer.length - offset - 1;
					System.arraycopy(lineBuffer, 0, buffer, 0, offset);
					Arrays.fill(lineBuffer, ' ');
					lineBuffer = buffer;
				}
				buffer[offset++] = (char) c;
				break;
			}
		}

		if (offset == 0) {
			return null;
		}

		char[] ret = new char[offset];
		System.arraycopy(buffer, 0, ret, 0, offset);
		Arrays.fill(buffer, ' ');

		return ret;
	}
}
