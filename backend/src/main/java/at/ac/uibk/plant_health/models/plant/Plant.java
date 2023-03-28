package at.ac.uibk.plant_health.models.plant;

import at.ac.uibk.plant_health.models.PlantPersonReference;
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
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private UUID plant_id;

    @Column(name = "plant_name", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String name;

    @Column(name = "qr_code_id", nullable = true)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private UUID qrCodeId;

    @OneToOne(optional = false)
    @JoinColumn(name = "sensor_station_id", nullable = false)
    private SensorStation sensorStation;

    @OneToMany(mappedBy = "plant")
    private List<PlantPersonReference> plantPersonReferences = new ArrayList<>();

    @OneToMany(mappedBy = "plant")
    private List<SensorData> sensorData = new ArrayList<>();

    @OneToMany(mappedBy = "plant")
    private List<SensorLimits> sensorLimits = new ArrayList<>();
}
