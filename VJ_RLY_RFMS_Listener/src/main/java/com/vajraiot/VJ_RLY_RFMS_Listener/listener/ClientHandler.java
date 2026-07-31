package com.vajraiot.VJ_RLY_RFMS_Listener.listener;

import com.vajraiot.VJ_RLY_RFMS_Listener.dto.DeviceDataDTO;
import com.vajraiot.VJ_RLY_RFMS_Listener.service.PacketProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final PacketProcessingService packetProcessingService;

    @Override
    public void run() {

        try (InputStream inputStream = socket.getInputStream();
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            log.info("Connected : {}", socket.getInetAddress().getHostAddress());

            byte[] buffer = new byte[4096];

            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) > 0) {

                byte[] packet = java.util.Arrays.copyOf(buffer, bytesRead);

                DeviceDataDTO dto = packetProcessingService.processPacket(packet);

                writer.println("ACK|" + dto.getDeviceId() + "|SUCCESS");
            }

        } catch (Exception e) {
            log.error("Client processing failed", e);

        } finally {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }
}