package com.halnode.atlantis.audit.persistence.model;

import lombok.Data;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;

@Entity
@RevisionEntity(MyRevisionListener.class)
@Data
@Table(name = "custom_rev_info", schema = "public")
public class CustomRevisionEntity {
    @Id
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @RevisionTimestamp
    private long timestamp;
    private String userName;
}
