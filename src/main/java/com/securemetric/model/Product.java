package com.securemetric.model;

import jakarta.persistence.*;
import lombok.Data;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Data
@Entity
@Table(name = "Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Convert(converter = BigDecimalConverter.class) // call converter
    @Column(name = "price", nullable = false)
    private BigDecimal price;


    @Converter
    public static class BigDecimalConverter implements AttributeConverter<BigDecimal, String> {

        private static final String SECRET_KEY = "125";
        private static final String INIT_VECTOR = "encryptionIntVec";

        @Override
        public String convertToDatabaseColumn(BigDecimal attribute) {
            if (attribute == null) {
                return null;
            }
            try {
                IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
                SecretKeySpec skeySpec = new SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), 16), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

                byte[] encrypted = cipher.doFinal(attribute.toString().getBytes());
                return Base64.getEncoder().encodeToString(encrypted);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public BigDecimal convertToEntityAttribute(String dbData) {
            if (dbData == null) {
                return null;
            }
            try {
                IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
                SecretKeySpec skeySpec = new SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), 16), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

                byte[] original = cipher.doFinal(Base64.getDecoder().decode(dbData));

                return new BigDecimal(new String(original));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
