package org.example;

import com.sun.net.httpserver.HttpServer;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;

import java.io.File;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.security.MessageDigest;

public class LocalPackServer {

    private static String packHash = "";
    private static final int PORT = 8081;

    public static ResourcePackRequest startAndGetPack() {
        try {
            File packFile = new File("pack.zip");

            // 1. Calculate the SHA-1 Hash
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = digest.digest(Files.readAllBytes(packFile.toPath()));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            packHash = hexString.toString();

            // 2. Start the local HTTP Server
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/pack.zip", exchange -> {
                byte[] bytes = Files.readAllBytes(packFile.toPath());
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            });

            server.start();
            System.out.println("Local Pack Server started on port " + PORT);

            // 3. Return the Minestom Pack Object
            return ResourcePackRequest.resourcePackRequest()
                .packs(ResourcePackInfo.resourcePackInfo()
                    .uri(URI.create("http://127.0.0.1:" + PORT + "/pack.zip"))
                    .hash(packHash)
                    .build())
                .required(true)
                .build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}