package services;

import java.security.SecureRandom;
import java.security.spec.ECField;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Serviço responsável por hashing e verificação de senha.
 * Implementa hashing usando PBKDF2 (Password-Based Key Derivation Function 2), com salt aleatório e múltiplas iterações.
 * Saída do hash: base64(salt) + ":" + base64(hash)</pre>
 * Evita armazenar senha em texto puro no banco.
 * Usa salt para evitar ataques com rainbow tables.
 * Usa iterações para aumentar o custo de brute force.
 */

public class EncryptionService {
	/**
     * Algoritmo usado pelo {@link SecretKeyFactory}.
     * O nome do algoritmo depende do provider Java.
     */
	private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
	
	/**
     * Número de iterações do PBKDF2.
     * Quanto maior, mais custoso fica gerar/verificar senha (mais seguro, mas mais lento).
     */
    private static final int ITERATIONS = 65536; 
    
    /**
     * Tamanho da chave derivada (hash) em bits.
     */
    private static final int KEY_LENGTH = 128;  
    
    /**
    * Gera um hash para a senha informada, com salt aleatório.
    * Cria um {@link SecureRandom}.
    * Gera um salt aleatório (14 bytes) - estamos gerando com um salt fixo
    * Deriva o hash com PBKDF2 via {@link #generateHash(char[], byte[])}.
    * Codifica salt e hash em Base64.
    * Retorna no formato {@code saltBase64:hashBase64}.
    * @param password senha em char[]
    * @return string com salt e hash em Base64
    * @throws Exception se ocorrer falha ao derivar a chave (provider/algoritmo/etc.)
    */
    public static String hashPassword(char[] password) throws Exception{
  
    	SecureRandom random = new SecureRandom();
    	byte[] salt = new byte[14];
    	
    	
    	byte[] hash = generateHash(password,salt);
    	
    	String saltStr = Base64.getEncoder().encodeToString(salt);
    	String hashStr = Base64.getEncoder().encodeToString(hash);
    	
    	return saltStr + ":" + hashStr;
    }
    
    /**
     * Verifica se a senha digitada corresponde ao hash armazenado.
     * Divide {@code passwordHash} no formato {@code salt:hash}.
     * Decodifica o salt e o hash original de Base64.
     * Gera um novo hash a partir da senha digitada e do mesmo salt.
     * Compara {@code originHash} e {@code newHash} usando {@link Arrays#equals(byte[], byte[])}.
     * @param password senha digitada
     * @param passwordHash hash armazenado no banco (formato {@code saltBase64:hashBase64})
     * @return true se a senha corresponder ao hash, false caso contrário
     * @throws Exception se ocorrer falha ao derivar a chave
     */
    public static boolean checkPassword(char[] password,String passwordHash) throws Exception {
    	String[] encryptPassoword = passwordHash.split(":");
    	byte[] salt = Base64.getDecoder().decode(encryptPassoword[0]);
    	byte[] originHash = Base64.getDecoder().decode(encryptPassoword[1]);
    
    	byte[] newHash = generateHash(password,salt); 
    	
    	return Arrays.equals(originHash, newHash);
    }
    
    
    /**
     * Deriva o hash PBKDF2 a partir de uma senha e salt.
     * Monta um {@link PBEKeySpec} com a senha, salt, iterações e tamanho da chave.
     * Obtém {@link SecretKeyFactory} para o algoritmo {@link #ALGORITHM}.
     * Gera a chave derivada ({@code encoded}).
     * @param password senha
     * @param salt salt
     * @return bytes do hash derivado
     * @throws Exception se o algoritmo não existir ou houver falha no provider
     */
    private static byte[] generateHash(char[] password, byte[] salt) throws Exception{
    	
    	KeySpec spec = new PBEKeySpec(password,salt,ITERATIONS,KEY_LENGTH);
    	SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
    	return factory.generateSecret(spec).getEncoded();
    }
}
