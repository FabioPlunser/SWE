package at.ac.uibk.plant_health.controllers;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import at.ac.uibk.plant_health.models.annotations.AnyPermission;
import at.ac.uibk.plant_health.models.annotations.PrincipalRequired;
import at.ac.uibk.plant_health.models.annotations.PublicEndpoint;
import at.ac.uibk.plant_health.models.device.AccessPoint;
import at.ac.uibk.plant_health.models.rest_responses.*;
import at.ac.uibk.plant_health.models.user.Permission;
import at.ac.uibk.plant_health.service.AccessPointService;

@RestController
public class AccessPointController {
	@Autowired
	private AccessPointService accessPointService;

	@PublicEndpoint
	@PostMapping("/register-access-point")
	public RestResponseEntity register(
			@RequestParam(name = "accessPointId") final UUID accessPointId,
			@RequestParam(name = "roomName") final String roomName
	) {
		if (!accessPointService.isAccessPointRegistered(accessPointId)) {
			if (!accessPointService.register(accessPointId, roomName)) {
				return MessageResponse.builder()
						.statusCode(500)
						.message("Could not register AccessPoint")
						.toEntity();
			}
		}
		if (!accessPointService.isUnlocked(accessPointId)) {
			return MessageResponse.builder()
					.statusCode(401)
					.message("AccessPoint is locked")
					.toEntity();
		}
		return TokenResponse.builder()
				.statusCode(200)
				.token(accessPointService.getAccessPointAccessToken(accessPointId))
				.toEntity();
	}

	@AnyPermission(Permission.ADMIN)
	@GetMapping("/get-access-points")
	public RestResponseEntity getAccessPoints() {
		return new ListResponse<>(accessPointService.findAllAccessPoints()).toEntity();
	}

	@AnyPermission(Permission.ADMIN)
	@RequestMapping(
			value = "/set-lock-access-point", method = {RequestMethod.POST, RequestMethod.PUT}
	)
	public RestResponseEntity
	setLockAccessPoint(
			@RequestParam(name = "accessPointId") final UUID accessPointId,
			@RequestParam(name = "locked") final boolean locked
	) {
		if (!accessPointService.setLocked(locked, accessPointId)) {
			return MessageResponse.builder()
					.statusCode(404)
					.message("Could not set lock state of AccessPoint")
					.toEntity();
		}
		return MessageResponse.builder()
				.statusCode(200)
				.message("Successfully set lock state of AccessPoint")
				.toEntity();
	}

	@ReadOperation
	@GetMapping("/get-access-point-config")
	@PrincipalRequired(AccessPoint.class)
	public RestResponseEntity getAccessPointConfig(AccessPoint accessPoint) {
		return MessageResponse.builder()
				.statusCode(200)
				.message("Successfully send AccessPoint config")
				.toEntity();
	}
	@AnyPermission(Permission.ADMIN)
	@PostMapping("/scan-for-sensor-stations")
	public RestResponseEntity scanForSensorStations(
			//            @RequestBody final UUID accessPointId
	) {
		throw new NotImplementedException();
	}

	@AnyPermission(Permission.ADMIN)
	@RequestMapping(
			value = "/create-plant-qr-code",
			method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT}
	)
	public RestResponseEntity
	createPlantQrCode(
			//            @RequestBody final UUID plantId
			@RequestBody final UUID qrCode
	) {
		throw new NotImplementedException();
	}
}
