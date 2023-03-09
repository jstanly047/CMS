package com.rjs.cms.model.enity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRelation {

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Unique implements Serializable {
        private String role;
        private String parentRole;
    }

    @EmbeddedId
    Unique unique;
}
