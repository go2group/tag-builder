package com.go2group.bamboo.plugins.tagbuilder.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class SCMUtils {

	protected static Cipher myCipher;
	protected static SecretKeyFactory myKeyFactory;
    protected static DESedeKeySpec myKeySpec;
    
    protected static final String DEFAULT_ENCRYPTION_KEY = "Beetlejuice version $version (c) Copyright 2003-2005 Pols Consulting Limited";
    protected static final String UNICODE_FORMAT = "UTF8";
    protected static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    
    protected static void initiliseCipher(int mode) throws InvalidKeySpecException, InvalidKeyException {
		try {
			myKeySpec = new DESedeKeySpec(DEFAULT_ENCRYPTION_KEY.getBytes(UNICODE_FORMAT));
            myKeyFactory = SecretKeyFactory.getInstance(DESEDE_ENCRYPTION_SCHEME);
            myCipher = Cipher.getInstance(DESEDE_ENCRYPTION_SCHEME);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		SecretKey secretKey = myKeyFactory.generateSecret(myKeySpec);
		myCipher.init(mode, secretKey);
	}
}
