package at.ac.uibk.plant_health;

import at.ac.uibk.plant_health.models.plant.Plant;
import at.ac.uibk.plant_health.models.user.Person;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "plant_person_reference")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PlantPersonReference {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @MapsId("plant_id")
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @ManyToOne
    @MapsId("person_id")
    @JoinColumn(name = "person_id")
    private Person person;

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "is_assigned", nullable = false)
    private boolean isAssigned;

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "in_dashboard", nullable = false)
    private boolean inDashboard;
}