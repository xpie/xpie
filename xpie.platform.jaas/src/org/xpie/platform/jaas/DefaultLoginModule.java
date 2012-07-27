package org.xpie.platform.jaas;

import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class DefaultLoginModule implements LoginModule {

	private Subject subject;
	private CallbackHandler callbackHandler;
	private Map sharedState;
	private Map options;

	private boolean succeeded = false;
	private boolean commitSucceeded = false;

	private String username;
	private char[] password;
	private Principal userPrincipal=null;

	private boolean debug = false;

	@Override
	public boolean abort() throws LoginException {
		if(succeeded==false)return false;
		
		if(succeeded==true&&commitSucceeded==false){
			succeeded=false;
			clean();
		}else{
			logout();
		}
		return true;
	}

	@Override
	public boolean commit() throws LoginException {
		if(succeeded==false) return false;
		
		XPiePrincipal anonymous=new XPiePrincipal(XPiePrincipal.ANONYMOUS);
		subject.getPrincipals().remove(anonymous);
		
		userPrincipal=new XPiePrincipal(username);
		if(!subject.getPrincipals().contains(userPrincipal)){
			subject.getPrincipals().add(userPrincipal);
		}
		if(debug){
			System.out.println("DefaultLoginModule added  "+userPrincipal.toString()+" to subject.");
		}
		
		clean();
		commitSucceeded=true;
		return true;
	}

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;

		debug = "true".equalsIgnoreCase((String) options.get("debug"));

	}

	@Override
	public boolean login() throws LoginException {

		if (this.callbackHandler == null)
			throw new LoginException("No callbackHandler available.");

		Callback[] callbacks = new Callback[2];

		callbacks[0] = new NameCallback("User Name:");
		callbacks[1] = new PasswordCallback("Password:", false);

		try {
			callbackHandler.handle(callbacks);
			username = ((NameCallback) callbacks[0]).getName();
			char[] tmpPassword = ((PasswordCallback) callbacks[1])
					.getPassword();
			if (tmpPassword == null) {
				tmpPassword = new char[0];
			}
			password = new char[tmpPassword.length];
			System.arraycopy(tmpPassword, 0, password, 0, tmpPassword.length);
			((PasswordCallback) callbacks[1]).clearPassword();
		} catch (java.io.IOException e) {
			throw new LoginException(e.getMessage());
		} catch (UnsupportedCallbackException e) {
			throw new LoginException("Callback " + e.getCallback()
					+ " is not support.");
		}

		if (debug) {
			System.out.println("\t\t[DefaultLoginModule] "
					+ "user entered user name: " + username);
			System.out.print("\t\t[DefaultLoginModule] "
					+ "user entered password: ");
			for (int i = 0; i < password.length; i++)
				System.out.print(password[i]);
			System.out.println();
		}
		
		succeeded=true;
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		
		subject.getPrincipals().remove(userPrincipal);
		succeeded=false;
		succeeded = commitSucceeded;
		clean();
		return true;
	}
	private void clean(){
		username=null;
		for (int i = 0; i < password.length; i++)
			password[i] = ' ';
		password = null;
		userPrincipal=null;
	}

}
