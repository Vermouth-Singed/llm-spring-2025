package com.example.llm.util;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JasyptUtil {

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    StringEncryptor encryptor;

    public String encrypt(String value) {
        return encryptor.encrypt(value);
    }

    public String decrypt(String value) {
        return encryptor.decrypt(value);
    }
}
