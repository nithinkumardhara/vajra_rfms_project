package com.vajraiot.VJ_RLY_RFMS_Listener.service;

import com.vajraiot.VJ_RLY_RFMS_Listener.dto.DeviceDataDTO;
import com.vajraiot.VJ_RLY_RFMS_Listener.entity.RawData;
import com.vajraiot.VJ_RLY_RFMS_Listener.listener.ProtocolParser;
import com.vajraiot.VJ_RLY_RFMS_Listener.repository.RawDataRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.ProtocolException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PacketProcessingService {

    private final ProtocolParser protocolParser;
    private final RawDataRepo rawDataRepo;
    private final DataPersistenceService dataPersistenceService;

    public DeviceDataDTO processPacket(byte[] packet) throws ProtocolException {

        String cleanedPacket = sanitizePacket(packet);

        RawData rawData = RawData.builder()
                .receivedTime(LocalDateTime.now())
                .status("RECEIVED")
                .rawPacketString(cleanedPacket)
                .rawPacketHex(hexUtil(packet))
                .build();

        rawData = rawDataRepo.save(rawData);

        try {

            DeviceDataDTO dto = protocolParser.parse(packet);

            rawData.setDeviceId(dto.getDeviceId());
            rawData.setStatus("VALID");

            rawDataRepo.save(rawData);

            dataPersistenceService.saveDeviceData(dto, rawData.getId());

            return dto;

        } catch (Exception ex) {

            log.error("Packet processing failed", ex);

            rawData.setStatus("INVALID");

            rawDataRepo.save(rawData);

            throw ex;
        }
    }


    private String hexUtil(byte[] packet) {
        if (packet == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(packet.length * 2);

        for (byte b : packet) {
            sb.append(String.format("%02X ", b & 0xFF));
        }

        return sb.toString().trim();
    }

    private String sanitizePacket(byte[] packet) {
        if (packet == null) {
            return "";
        }

        return new String(packet, java.nio.charset.StandardCharsets.US_ASCII)
                .replace("\u0000", "")
                .trim();
    }
}
