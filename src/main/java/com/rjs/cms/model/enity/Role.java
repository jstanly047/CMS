package com.rjs.cms.model.enity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    private String name;
    // TODO Call the commit
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
