package at.ac.uibk.plant_health.models.plant;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sensor_id", nullable = false)
    private UUID sensor_id;

    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "sensor_type", nullable = false)
    private SensorType type;

    public enum SensorType {
        EARTH_HUMIDITY,
        AIR_QUALITY,
        AIR_HUMIDITY,
        AIR_PRESSURE,
        TEMPARATURE,
        LIGHT_INTENSITY;
    }
}
