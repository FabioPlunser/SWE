package at.ac.uibk.swa.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "person")
// NOTE: This changes the name of the "id"-Column inherited from Authenticable to "person_id"
@AttributeOverride(name = "id", column = @Column(name = "person_id"))
public class Person extends Authenticable implements Serializable {

    public Person(String username, String email, String passwdHash, UUID token, Set<GrantedAuthority> permissions) {
        super(username, passwdHash, token, permissions);
        this.email = email;
    }

    public Person(String username, String email, String passwdHash, Set<GrantedAuthority> permissions) {
        this(username, email, passwdHash, null, permissions);
    }

    public Person(String username, String email, String passwdHash) {
        this(username, email, passwdHash, null, Permission.defaultAuthorities());
    }

    @Setter(AccessLevel.PRIVATE)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "email", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String email;

    /**
     * Gets the Person's ID.
     * This method is a renamed version of {@link Authenticable#getId()} so the ID field will be
     * included in the JSON-Serialization of a {@link Person}.
     *
     * @implNote This may return null if the {@link Person} was not saved to the Database.
     * @return The ID of the Person.
     */
    @JsonInclude
    public UUID getPersonId() {
        return this.getId();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
