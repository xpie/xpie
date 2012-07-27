package org.xpie.platform;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class BasicCallbackHandler implements CallbackHandler {

	private String name;
	private char[] password;
	public BasicCallbackHandler(String name, char[] password){
		this.name=name;
		this.password=password;
	}
	
	@Override
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		for(Callback cb:callbacks){
			if(cb instanceof NameCallback){
				((NameCallback) cb).setName(name);
			}else if(cb instanceof PasswordCallback){
				((PasswordCallback)cb).setPassword(password);
			}else{
				throw new UnsupportedCallbackException(cb," Unsupported callback");
			}
		}
	}

}
