package at.ac.uibk.plant_health.models.annotations;

import at.ac.uibk.plant_health.models.AccessPoint;

@PrincipalRequired(AccessPoint.class)
public @interface RequiresAccessPoint {}
