package at.ac.uibk.plant_health.models.plant;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PlantPicture {
    @Id
    @Column(name="plant_id")
    protected int plantId;

    @Id
    @Column(name = "picture_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID pictureId;

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "picture_path", nullable = false)
    private String picturePath;

    @ManyToOne(optional = false)
    @PrimaryKeyJoinColumn(name="plant_id", referencedColumnName="plant_id")
    protected Plant plant;
}
