package plasma.servlets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

    class Password {
    
    private String password;
    private String salt;
    private String hashedPassword;
   
    
    // For a new password auto generate the salt based on length given and initialize hashed password
    Password (String password, int length) {
        this.password = password;
        this.salt = generateSalt(length);
        this.hashedPassword=generateHashedPassword(this.password, this.salt);
    }
    
    // For an existing password, return the hash for comparison with the DB

    // Getters + Setters 
    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Generate a salt value with given length
    private String generateSalt(int length) {
        
        String charsForSalt = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.^$#=0123456789";
        StringBuilder generated_salt = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
        generated_salt.append(charsForSalt.charAt(random.nextInt(charsForSalt
                .length())));
        }
        
        return generated_salt.toString();
        
    }
    
    // Return the hashed password as a string 
    
    private String generateHashedPassword(String password, String salt) {
        
        // Concatenate the password and the salt with a + in between
        
        String valueToHash = password + '+' + salt;
        
        try {
            return hash256(valueToHash);
        } catch (Exception ex) {
            Logger.getLogger(Password.class.getName()).log(Level.SEVERE, null, ex);
            return "Could not hash";
        }
       
    }
    private static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(data.getBytes());
        return bytesToHex(digest.digest());
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
        
    
}


