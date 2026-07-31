package com.vajraiot.VJ_RLY_RFMS_Listener.listener;

import com.vajraiot.VJ_RLY_RFMS_Listener.service.PacketProcessingService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TCPServer {

    @Value("${tcp.server.port}")
    private int port;

    private ServerSocket serverSocket;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final PacketProcessingService  packetProcessingService;

    @PostConstruct
    public void start() {
        Thread listenerThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                log.info("TCP Server started on port {}", port);

                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    executor.submit(new ClientHandler(
                            socket,
                            packetProcessingService
                            ));
                }

            } catch (Exception e) {
                log.error("TCP Server error", e);
            }
        });
        listenerThread.start();
    }

    @PreDestroy
    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            executor.shutdown();
        } catch (Exception e) {
            log.error("Error while stopping server", e);
        }
    }
}