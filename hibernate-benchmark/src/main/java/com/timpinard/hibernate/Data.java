package com.timpinard.hibernate;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="hibernate_test.data")
public class Data {
    @Id
    String id;
    String parentId;
}
