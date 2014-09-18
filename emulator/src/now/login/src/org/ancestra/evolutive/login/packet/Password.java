package org.ancestra.evolutive.login.packet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.ancestra.evolutive.login.LoginClient;
import org.ancestra.evolutive.login.LoginClient.Status;

public class Password {
	
	public static char[] hash = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A',
			'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', '-', '_'};
	
	public static void verify(LoginClient client, String pass) {
		if(!cryptPassword(client).equals(pass)) {
			client.send("AlEf");
			client.kick();
			return;
		}
		
		client.setStatus(Status.SERVER);
	}
	
	public static String cryptPassword(LoginClient client) {
		String pass = client.getAccount().getPass();
		String key = client.getKey();
		int i = hash.length;
		
		StringBuilder crypted = new StringBuilder("#1");
        
		for(int y = 0; y < pass.length(); y++) {
			char c1 = pass.charAt(y);
            char c2 = key.charAt(y);
            double d = Math.floor(c1 / 16);
            int j = c1 % 16;
            
            crypted.append(hash[(int) ((d + c2 % i) % i)])
            .append(hash[(j + c2 % i) % i]);
		}
        
		return crypted.toString();
	}
	
	public static String decryptPassword(String pass, String key)
	{
		if(pass.startsWith("#1"))
			pass = pass.substring(2);
		String Chaine = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
		
		char PPass,PKey;
        int APass,AKey,ANB,ANB2,somme1,somme2;
        
        String decrypted = "";
        
        for (int i = 0; i < pass.length(); i+=2)
        {
        	PKey = key.charAt(i/2);
			ANB = Chaine.indexOf(pass.charAt(i));
			ANB2 = Chaine.indexOf(pass.charAt(i+1));
			
			somme1 = ANB + Chaine.length();
			somme2 = ANB2 + Chaine.length();
			
			APass = somme1 - (int)PKey;
			if(APass < 0)APass += 64;
			APass *= 16;
			
			AKey = somme2 - (int)PKey;
			if(AKey < 0)AKey += 64;
			
			PPass = (char)(APass + AKey);
			
			decrypted += PPass;
		}
        
		return decrypted;
	}
	
	public static String CryptSHA512(String message){
        MessageDigest md;
        try {
            md= MessageDigest.getInstance("SHA-1");
 
            md.update(message.getBytes());
            byte[] mb = md.digest();
            String out = "";
            for (int i = 0; i < mb.length; i++) {
                byte temp = mb[i];
                String s = Integer.toHexString(new Byte(temp));
                while (s.length() < 2) {
                    s = "0" + s;
                }
                s = s.substring(s.length() - 2);
                out += s;
            }
            return out;
 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public static boolean isValidPass(String pass, String passHash) {
		return CryptSHA512(pass).equals(passHash);
	}
}
