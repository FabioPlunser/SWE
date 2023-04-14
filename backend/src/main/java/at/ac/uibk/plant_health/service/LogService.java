package at.ac.uibk.plant_health.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import at.ac.uibk.plant_health.models.Log;
import at.ac.uibk.plant_health.repositories.LogRepository;

@Service
public class LogService {
	@Autowired
	private LogRepository logRepository;

	public List<Log> findAll() {
		return logRepository.findAll();
	}

	public boolean log(Log log) {
		try {
			this.logRepository.save(log);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
}
