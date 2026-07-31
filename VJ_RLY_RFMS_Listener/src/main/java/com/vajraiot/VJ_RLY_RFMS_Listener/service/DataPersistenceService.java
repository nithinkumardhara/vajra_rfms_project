package com.vajraiot.VJ_RLY_RFMS_Listener.service;

import com.vajraiot.VJ_RLY_RFMS_Listener.dto.DeviceDataDTO;
import com.vajraiot.VJ_RLY_RFMS_Listener.entity.*;
import com.vajraiot.VJ_RLY_RFMS_Listener.repository.*;
import com.vajraiot.VJ_RLY_RFMS_Listener.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataPersistenceService {

    private final DeviceRepo deviceRepo;
    private final DeviceGeneralDataRepo generalDataRepo;
    private final DeviceGPSDataRepo gpsDataRepo;
    private final DeviceFuelDataRepo fuelDataRepo;
    private final DeviceAlarmStatusRepo alarmStatusRepo;
    private final DevicePacketDataRepo packetDataRepo;
    private final DeviceDataSnapshotRepo snapshotRepo;
    private final EntityMapper entityMapper;

    private final TicketService ticketService;
    private final RedisCacheService redisCacheService;

    @Transactional
    public SaveResult saveDeviceData(DeviceDataDTO deviceDataDTO, Long rawDataId) {
        SaveResult result = new SaveResult();
        result.setStartTime(LocalDateTime.now());
        result.setDeviceId(deviceDataDTO.getDeviceId());

        try {
            Device device = updateOrCreateDevice(deviceDataDTO);
            result.setDeviceSaved(true);

            if (deviceDataDTO.getGeneralData() != null) {
                DeviceGeneralData generalData = entityMapper.mapToDeviceGeneralData(deviceDataDTO);
                generalDataRepo.save(generalData);
                result.setGeneralDataSaved(true);
                log.debug("Saved GeneralData for device: {}", deviceDataDTO.getDeviceId());
            }

            if (deviceDataDTO.getGpsData() != null) {
                DeviceGPSData gpsData = entityMapper.mapToDeviceGPSData(deviceDataDTO);
                gpsDataRepo.save(gpsData);
                result.setGpsDataSaved(true);
                log.debug("Saved GPSData for device: {}", deviceDataDTO.getDeviceId());
            }

            if (deviceDataDTO.getFuelData() != null) {
                DeviceFuelData fuelData = entityMapper.mapToDeviceFuelData(deviceDataDTO);
                fuelDataRepo.save(fuelData);
                result.setFuelDataSaved(true);
                log.debug("Saved FuelData for device: {}", deviceDataDTO.getDeviceId());
            }

            if (deviceDataDTO.getAlarmStatus() != null) {
                DeviceAlarmStatus alarmStatus = entityMapper.mapToDeviceAlarmStatus(deviceDataDTO);
                alarmStatusRepo.save(alarmStatus);
                result.setAlarmStatusSaved(true);
                log.debug("Saved AlarmStatus for device: {}", deviceDataDTO.getDeviceId());

                ticketService.processTickets(deviceDataDTO.getDeviceId(), alarmStatus);
            }

            DevicePacketData packetData = entityMapper.mapToDevicePacketData(deviceDataDTO, rawDataId);
            packetDataRepo.save(packetData);
            result.setPacketDataSaved(true);
            log.debug("Saved PacketData for device: {}", deviceDataDTO.getDeviceId());

            DeviceDataSnapshot snapshot = updateDeviceDataSnapshot(deviceDataDTO, device);
            result.setSnapshotUpdated(true);
            log.debug("Updated DataSnapshot for device: {}", deviceDataDTO.getDeviceId());

            result.setSuccess(true);
            result.setEndTime(LocalDateTime.now());

            triggerPostSaveOperations(deviceDataDTO.getDeviceId(), snapshot);

            return result;

        } catch (Exception e) {
            log.error("Error persisting data for device: {}", deviceDataDTO.getDeviceId(), e);
            result.setSuccess(false);
            result.setEndTime(LocalDateTime.now());

            throw e;
        }
    }

    private Device updateOrCreateDevice(DeviceDataDTO dto) {
        Device device = deviceRepo.findByDeviceId(dto.getDeviceId())
                .orElse(null);

        if (device == null) {
            device = Device.builder()
                    .deviceId(dto.getDeviceId())
                    .imei(dto.getImei())
                    .manufacturerId(dto.getFuelData().getManufacturerId())
                    .simNumber(dto.getSimNumber())
                    .version(dto.getVersion())
                    .installationDate(dto.getInstallationDate())
                    .signalStrength(0)
                    .lastCommunicationTime(dto.getPacketTimeStamp())
                    .build();

        } else {
            // Update existing device
            device.setLastCommunicationTime(dto.getPacketTimeStamp());
            device.setImei(dto.getImei());
            device.setSimNumber(dto.getSimNumber());

            log.debug("Updating device: {}", dto.getDeviceId());
        }

        return deviceRepo.save(device);
    }

    private DeviceDataSnapshot updateDeviceDataSnapshot(DeviceDataDTO dto, Device device) {
        try {
            DeviceDataSnapshot snapshot = snapshotRepo.findByDeviceId(dto.getDeviceId())
                    .orElse(null);

            DeviceGPSData gpsData = entityMapper.mapToDeviceGPSData(dto);

            if (snapshot == null) {
                snapshot = new DeviceDataSnapshot();
                snapshot.setDeviceId(dto.getDeviceId());
                snapshot.setCreatedAt(LocalDateTime.now());
            }

            // Update all fields from DTOs
            snapshot.setSignalStrength(dto.getSignalStrength());

            if (dto.getGeneralData() != null) {
                snapshot.setInternalBatteryVoltage(dto.getGeneralData().getInternalBatteryVoltage());
                snapshot.setExternalBatteryVoltage(dto.getGeneralData().getExternalBatteryVoltage());
                snapshot.setTemperature(dto.getGeneralData().getTemperature());
            }

            if (dto.getGpsData() != null) {
                snapshot.setLatitude(dto.getGpsData().getLatitude());
                snapshot.setLongitude(dto.getGpsData().getLongitude());
                snapshot.setSpeed(dto.getGpsData().getSpeed());
                snapshot.setHeading(dto.getGpsData().getHeading());

                snapshot.setMovementStatus(gpsData.getMovementStatus());
            }

            if (dto.getFuelData() != null) {
                snapshot.setFuelLevel(dto.getFuelData().getFuelLevel());
                snapshot.setFuelLevelPercentage(dto.getFuelData().getFuelLevelPercentage());
                snapshot.setFuelHeight(dto.getFuelData().getFuelLevelHeight());
                snapshot.setRunHours(dto.getFuelData().getRunHours());
            }

            if (dto.getAlarmStatus() != null) {
                snapshot.setIsIgnitionON(dto.getAlarmStatus().getIgnitionON());
                snapshot.setIsVehicleON(dto.getAlarmStatus().getVehicleON());
                snapshot.setIsVibrationDetected(dto.getAlarmStatus().getVibrationStatus());
            }

            snapshot.setPacketTimestamp(dto.getPacketTimeStamp());
            snapshot.setPacketVariant(dto.getPacketVariant());
            snapshot.setUpdatedAt(LocalDateTime.now());

            return snapshotRepo.save(snapshot);

        } catch (Exception e) {
            log.error("Error updating snapshot for device: {}", dto.getDeviceId(), e);
            throw e;
        }
    }


    @Async
    private void triggerPostSaveOperations(String deviceId, DeviceDataSnapshot snapshot) {
        try {
            // Broadcast device update via Redis
            if (snapshot != null) {
                redisCacheService.saveLatestData(deviceId, snapshot);
                redisCacheService.publishLatestData(deviceId, snapshot);
            }
        } catch (Exception e) {
            log.error("Error in post-save operations for device: {}", snapshot.getDeviceId(), e);
        }
    }


    @Transactional
    public BatchSaveResult saveBatchDeviceData(List<DeviceDataDTO> dtoList, List<Long> rawDataIds) {
        BatchSaveResult batchResult = new BatchSaveResult();
        batchResult.setStartTime(LocalDateTime.now());
        batchResult.setTotalCount(dtoList.size());

        int successCount = 0;
        List<String> failedDevices = new ArrayList<>();

        for (int i = 0; i < dtoList.size(); i++) {
            try {
                SaveResult result = saveDeviceData(dtoList.get(i), rawDataIds.get(i));
                if (result.isSuccess()) {
                    successCount++;
                } else {
                    failedDevices.add(dtoList.get(i).getDeviceId());
                }
            } catch (Exception e) {
                log.warn("Failed to save data for device: {}", dtoList.get(i).getDeviceId(), e);
                failedDevices.add(dtoList.get(i).getDeviceId());
            }
        }

        batchResult.setSuccessCount(successCount);
        batchResult.setFailureCount(dtoList.size() - successCount);
        batchResult.setFailedDevices(failedDevices);
        batchResult.setEndTime(LocalDateTime.now());

        log.info("Batch save completed: {} success, {} failed in {} ms", successCount, batchResult.getFailureCount(), batchResult.getExecutionTimeMs());

        return batchResult;
    }

    public static class SaveResult {
        private String deviceId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private boolean success;

        private boolean deviceSaved;
        private boolean generalDataSaved;
        private boolean gpsDataSaved;
        private boolean fuelDataSaved;
        private boolean alarmStatusSaved;
        private boolean packetDataSaved;
        private boolean snapshotUpdated;

        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

        public long getExecutionTimeMs() {
            if (startTime != null && endTime != null) {
                return java.time.temporal.ChronoUnit.MILLIS.between(startTime, endTime);
            }
            return 0;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }


        public boolean isDeviceSaved() { return deviceSaved; }
        public void setDeviceSaved(boolean deviceSaved) { this.deviceSaved = deviceSaved; }

        public boolean isGeneralDataSaved() { return generalDataSaved; }
        public void setGeneralDataSaved(boolean generalDataSaved) { this.generalDataSaved = generalDataSaved; }

        public boolean isGpsDataSaved() { return gpsDataSaved; }
        public void setGpsDataSaved(boolean gpsDataSaved) { this.gpsDataSaved = gpsDataSaved; }

        public boolean isFuelDataSaved() { return fuelDataSaved; }
        public void setFuelDataSaved(boolean fuelDataSaved) { this.fuelDataSaved = fuelDataSaved; }

        public boolean isAlarmStatusSaved() { return alarmStatusSaved; }
        public void setAlarmStatusSaved(boolean alarmStatusSaved) { this.alarmStatusSaved = alarmStatusSaved; }

        public boolean isPacketDataSaved() { return packetDataSaved; }
        public void setPacketDataSaved(boolean packetDataSaved) { this.packetDataSaved = packetDataSaved; }

        public boolean isSnapshotUpdated() { return snapshotUpdated; }
        public void setSnapshotUpdated(boolean snapshotUpdated) { this.snapshotUpdated = snapshotUpdated; }

        @Override
        public String toString() {
            return "SaveResult{" +
                    "deviceId='" + deviceId + '\'' +
                    ", success=" + success +
                    ", executionTimeMs=" + getExecutionTimeMs() +
                    ", generalDataSaved=" + generalDataSaved +
                    ", gpsDataSaved=" + gpsDataSaved +
                    ", fuelDataSaved=" + fuelDataSaved +
                    ", alarmStatusSaved=" + alarmStatusSaved +
                    '}';
        }
    }


    public static class BatchSaveResult {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private int totalCount;
        private int successCount;
        private int failureCount;
        private List<String> failedDevices;

        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

        public long getExecutionTimeMs() {
            if (startTime != null && endTime != null) {
                return java.time.temporal.ChronoUnit.MILLIS.between(startTime, endTime);
            }
            return 0;
        }

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }

        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }

        public List<String> getFailedDevices() { return failedDevices; }
        public void setFailedDevices(List<String> failedDevices) { this.failedDevices = failedDevices; }

        public double getSuccessRate() {
            return totalCount > 0 ? (100.0 * successCount / totalCount) : 0;
        }

        @Override
        public String toString() {
            return "BatchSaveResult{" +
                    "totalCount=" + totalCount +
                    ", successCount=" + successCount +
                    ", failureCount=" + failureCount +
                    ", successRate=" + getSuccessRate() + "%" +
                    ", executionTimeMs=" + getExecutionTimeMs() +
                    '}';
        }
    }
}
