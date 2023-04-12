package at.ac.uibk.plant_health.controllers;

import at.ac.uibk.plant_health.models.annotations.AnyPermission;
import at.ac.uibk.plant_health.models.rest_responses.RestResponseEntity;
import at.ac.uibk.plant_health.models.user.Permission;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class AccessPointController {
    @AnyPermission(Permission.ADMIN)
    @GetMapping("/get-access-points")
    public RestResponseEntity getAccessPoints() {
        throw new NotImplementedException();
    }

    @AnyPermission(Permission.ADMIN)
    @GetMapping("/get-sensor-stations")
    public RestResponseEntity getSensorStations() {
        throw new NotImplementedException();
    }

    @AnyPermission(Permission.ADMIN)
    @PostMapping("/scan-for-sensor-stations")
    public RestResponseEntity scanForSensorStations(
//            @RequestBody final UUID accessPointId
    ) {
        throw new NotImplementedException();
    }

    @AnyPermission(Permission.ADMIN)
    @RequestMapping(value = "/set-lock-access-point", method = {RequestMethod.POST, RequestMethod.PUT})
    public RestResponseEntity setLockAccessPoint(
//            @RequestBody final UUID accessPointId
            @RequestBody final boolean locked
    ) {
        throw new NotImplementedException();
    }

    @AnyPermission(Permission.ADMIN)
    @RequestMapping(value = "/set-lock-access-point", method = {RequestMethod.POST, RequestMethod.PUT})
    public RestResponseEntity setLockSensorStation(
//            @RequestBody final UUID sensorStationId
            @RequestBody final boolean locked
    ) {
        throw new NotImplementedException();
    }

    @AnyPermission(Permission.ADMIN)
    @RequestMapping(value = "/create-plant-qr-code", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
    public RestResponseEntity createPlantQrCode(
//            @RequestBody final UUID plantId
            @RequestBody final UUID qrCode
    ) {
        throw new NotImplementedException();
    }
}
