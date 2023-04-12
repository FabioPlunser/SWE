package at.ac.uibk.plant_health.controllers;

import at.ac.uibk.plant_health.models.annotations.AnyPermission;
import at.ac.uibk.plant_health.models.annotations.PublicEndpoint;
import at.ac.uibk.plant_health.models.plant.SensorLimits;
import at.ac.uibk.plant_health.models.rest_responses.RestResponseEntity;
import at.ac.uibk.plant_health.models.user.Permission;
import at.ac.uibk.plant_health.service.SensorStationService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class PlantController {
    // region Autowired Components
    @Autowired
    private SensorStationService sensorStationService;
    // endregion

    @PublicEndpoint
    @GetMapping("/scan-qr-code")
    public RestResponseEntity scanQrCode(
            @RequestParam("plant-id") final String qrCode
    ) {
        throw new NotImplementedException();
    }

    @PublicEndpoint
    @GetMapping("/get-pictures")
    public RestResponseEntity getPlantPictures(
//            @RequestBody final XXX plants
    ) {
        throw new NotImplementedException();
    }

    @PublicEndpoint
    @RequestMapping(
            value = "/upload-picture",
            method = {RequestMethod.PUT, RequestMethod.POST}
    )
    public RestResponseEntity uploadPlantPicture(
            @RequestParam("plant-id") final String qrCode,
            @RequestBody final String picture
    ) {
        throw new NotImplementedException();
    }

    @AnyPermission(Permission.USER)
    @GetMapping("/get-all-plants")
    public RestResponseEntity getAllPlants() {
        throw new NotImplementedException();
    }

    @AnyPermission(Permission.GARDENER)
    @RequestMapping(value = "/set-sensor-limits", method = {RequestMethod.PUT, RequestMethod.POST})
    public RestResponseEntity setSensorLimits(
            @RequestBody List<SensorLimits> limits
    ) {
        throw new NotImplementedException();
    }

    @AnyPermission(Permission.GARDENER)
    @RequestMapping(value = "/set-transfer-interval", method = {RequestMethod.PUT, RequestMethod.POST})
    public RestResponseEntity setTransferIntervals(
//            @RequestBody List<SensorLimits> limits
    ) {
        throw new NotImplementedException();
    }

    @AnyPermission(Permission.GARDENER)
    @RequestMapping(value = "/delete-picture", method = RequestMethod.DELETE)
    public RestResponseEntity deletePlantPicture(
            @RequestParam("plant-id") final UUID plantId,
            @RequestParam("picture-id") final UUID pictureId
    ) {
        throw new NotImplementedException();
    }
}
