package com.vajraiot.VJ_RLY_RFMS_REST_APIs.util;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.DeviceGPSData;
import java.util.List;

public class DistanceCalculateUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double MIN_SPEED_KMH = 5.0;
    private static final double MIN_SEGMENT_DISTANCE_KM = 0.20;

    private DistanceCalculateUtil() { }

//     Calculates total travel distance. Returns distance in KM
    public static Double calculateDistance(List<DeviceGPSData> gpsDataList) {

        if (gpsDataList == null || gpsDataList.size() < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;

        for (int i = 1; i < gpsDataList.size(); i++) {
            DeviceGPSData previous = gpsDataList.get(i - 1);
            DeviceGPSData current = gpsDataList.get(i);

            if (!isValidCoordinate(previous) || !isValidCoordinate(current)) {
                continue;
            }
            // Ignore records with low speed
            Double speed = current.getSpeed();

            if (speed == null || speed < MIN_SPEED_KMH) {
                continue;
            }
            double segmentDistance = haversineDistance(
                    previous.getLatitude(),
                    previous.getLongitude(),
                    current.getLatitude(),
                    current.getLongitude()
            );
            // Ignore GPS noise
            if (segmentDistance < MIN_SEGMENT_DISTANCE_KM) {
                continue;
            }
            totalDistance += segmentDistance;
        }

        return Math.round(totalDistance * 100.0) / 100.0;
    }

//    Haversine formula, Returns distance in KM.
    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return EARTH_RADIUS_KM * c;
    }

//    Validates GPS coordinates.
    private static boolean isValidCoordinate(DeviceGPSData gps) {
        if (gps == null || gps.getLatitude() == null || gps.getLongitude() == null) {
            return false;
        }
        return gps.getLatitude() >= -90
                && gps.getLatitude() <= 90
                && gps.getLongitude() >= -180
                && gps.getLongitude() <= 180;
    }
}