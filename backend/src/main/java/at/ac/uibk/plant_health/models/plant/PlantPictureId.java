package at.ac.uibk.plant_health.models.plant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PlantPictureId implements Serializable {
    private Plant plantId;

    private UUID pictureId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PlantPictureId that = (PlantPictureId) o;
        return plantId != null && Objects.equals(plantId, that.plantId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}