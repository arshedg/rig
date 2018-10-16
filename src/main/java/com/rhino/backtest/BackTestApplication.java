package com.rhino.backtest;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.acl.Owner;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class BackTestApplication {

    public static final String KITE_ACCESS_TOKEN = "kite.accessToken";
    public static final String KITE_PUBLIC_TOKEN = "kite.publicToken";

    @Autowired
    private KiteConnect kiteConnect;

    @Bean
    public KiteConnect kiteConnect() {
        KiteConnect kiteSdk = new KiteConnect("dgls7rx5uophvj1t");
        kiteSdk.setUserId("DT1149");
        return kiteSdk;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackTestApplication.class, args);
    }

    @PostConstruct
    public void start() throws KiteException, IOException {

        String accessToken = read(KITE_ACCESS_TOKEN);
        String publicToken = read(KITE_PUBLIC_TOKEN);

        if (accessToken == null || publicToken == null) {
            login();
            return;
        }
        kiteConnect.setAccessToken(accessToken);
        kiteConnect.setPublicToken(publicToken);

        try {
            String email = kiteConnect.getProfile().email;
            System.out.println("Looks like logged in. Successfully retrieved email id " + email);
        } catch (Throwable e) {
            login();
        }

    }

    private void login() throws KiteException, IOException {
        String url = kiteConnect.getLoginURL();
        System.out.println(url);
        Scanner scanner = new Scanner(System.in);
        String token = scanner.next();
        User user = kiteConnect.generateSession(token, "w24hv2io6ueuc7gxc2eqtcna8tghez5l");
        System.out.println(user.accessToken);
        System.out.println(user.publicToken);
        store(KITE_ACCESS_TOKEN, user.accessToken);
        store(KITE_PUBLIC_TOKEN, user.publicToken);

    }

    private void store(String key, String value) throws IOException {
        try (FileWriter writer = new FileWriter("/tmp/" + key, false)) {
            writer.write(value);
        }
    }

    private String read(String key) throws IOException {
        try (Scanner reader = new Scanner(Paths.get("/tmp/", key))) {
            return reader.next();
        } catch (Exception e) {
            return null;
        }

    }
}

