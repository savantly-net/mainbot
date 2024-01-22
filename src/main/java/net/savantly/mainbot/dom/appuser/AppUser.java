package net.savantly.mainbot.dom.appuser;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class AppUser {
    
    @Id
    private String id;
    private String name;
    
}
