package at.ac.uibk.plant_health.models.plant;

import at.ac.uibk.plant_health.PlantPersonReference;
import at.ac.uibk.plant_health.models.device.SensorStation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Plant {
    @Id
    @Column(name = "plant_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID plant_id;

    @Column(name = "plant_name", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String name;

    @Column(name = "qr_code_id", nullable = true)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID qrCodeId;

    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "sensor_station_id", nullable = false)
    private SensorStation sensorStation;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "person_reference_id")
    private PlantPersonReference plantPersonReference;

    @OneToMany(mappedBy = "plant", orphanRemoval = true)
    private List<SensorData> sensorData = new ArrayList<>();

    @OneToMany(mappedBy = "plant", orphanRemoval = true)
    private List<SensorLimits> sensorLimits = new ArrayList<>();
}
