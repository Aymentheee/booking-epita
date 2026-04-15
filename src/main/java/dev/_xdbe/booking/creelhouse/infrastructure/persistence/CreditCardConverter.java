package dev._xdbe.booking.creelhouse.infrastructure.persistence;


import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;

import dev._xdbe.booking.creelhouse.infrastructure.persistence.CryptographyHelper;


@Converter
public class CreditCardConverter implements AttributeConverter<String, String> {

    @Autowired
    private CryptographyHelper cryptographyHelper;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        // Step 7a: Encrypt the PAN before storing it in the database
        if (attribute == null) {
            return null;
        }
        // Appel de la méthode statique encryptData
        return CryptographyHelper.encryptData(attribute);
        // Step 7a: End of PAN encryption
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        // Step 7b: Decrypt the PAN when reading it from the database
        if (dbData == null) {
            return null;
        }
        // Appel de la méthode statique decryptData
        String pan = CryptographyHelper.decryptData(dbData);
        // Step 7b: End of PAN decryption
        
        return panMasking(pan);
    }

    private String panMasking(String ccNumber) {
        if (ccNumber == null || ccNumber.length() <= 8) {
            return ccNumber;
        }
        
        int length = ccNumber.length();
        String firstFour = ccNumber.substring(0, 4);
        String lastFour = ccNumber.substring(length - 4);
        String stars = "*".repeat(length - 8);
        
        return firstFour + stars + lastFour;
    }
}