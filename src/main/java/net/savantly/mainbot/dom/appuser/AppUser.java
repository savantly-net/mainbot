package net.savantly.mainbot.dom.appuser;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.experimental.Accessors;
import net.savantly.mainbot.identity.UserDto;

@Entity
@Data
@Accessors(chain = true)
public class AppUser {
    
    @Id
    private String id;
    private String name;

    @Transient
    public UserDto toDto() {
        return new UserDto()
            .setUid(this.id)
            .setName(this.name);
    }
}
