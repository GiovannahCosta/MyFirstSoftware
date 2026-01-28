package services;

import java.security.SecureRandom;
import java.security.spec.ECField;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class EncryptionService {

	private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATIONS = 65536; 
    private static final int KEY_LENGTH = 128;  
    
    public static String hashPassword(char[] password) throws Exception{
    	
    	SecureRandom random = new SecureRandom();
    	byte[] salt = new byte[14];
    	
   
    	byte[] hash = generateHash(password,salt);
    	
    	String saltStr = Base64.getEncoder().encodeToString(salt);
    	String hashStr = Base64.getEncoder().encodeToString(hash);
    	
    	return saltStr + ":" + hashStr;
    }
    
    public static boolean checkPassword(char[] password,String passwordHash) throws Exception {
    	String[] encryptPassoword = passwordHash.split(":");
    	byte[] salt = Base64.getDecoder().decode(encryptPassoword[0]);
    	byte[] originHash = Base64.getDecoder().decode(encryptPassoword[1]);
    
    	byte[] newHash = generateHash(password,salt); 
    	
    	return Arrays.equals(originHash, newHash);
    }
    
    
    
    private static byte[] generateHash(char[] password, byte[] salt) throws Exception{
    	
    	KeySpec spec = new PBEKeySpec(password,salt,ITERATIONS,KEY_LENGTH);
    	SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
    	return factory.generateSecret(spec).getEncoded();
    }
}
