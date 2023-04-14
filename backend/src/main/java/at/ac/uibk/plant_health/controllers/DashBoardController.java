package at.ac.uibk.plant_health.controllers;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.*;

import at.ac.uibk.plant_health.models.annotations.PrincipalRequired;
import at.ac.uibk.plant_health.models.rest_responses.RestResponseEntity;
import at.ac.uibk.plant_health.models.user.Person;

@RestController
public class DashBoardController {
	@PrincipalRequired(Person.class)
	@GetMapping("/get-dashboard-data")
	public RestResponseEntity getDashboardData(Person person) {
		throw new NotImplementedException();
	}

	@PrincipalRequired(Person.class)
	@RequestMapping(value = "/add-to-dashboard", method = {RequestMethod.PUT, RequestMethod.POST})
	public RestResponseEntity addPlantToDashboard(Person person
												  //            @RequestBody final UUID id
	) {
		throw new NotImplementedException();
	}

	@PrincipalRequired(Person.class)
	@RequestMapping(value = "/remove-from-dashboard", method = RequestMethod.DELETE)
	public RestResponseEntity removePlantFromDashboard(Person person
													   //            @RequestBody final UUID id
	) {
		throw new NotImplementedException();
	}
}
