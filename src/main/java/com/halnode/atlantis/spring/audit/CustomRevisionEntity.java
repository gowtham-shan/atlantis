package com.halnode.atlantis.spring.audit;

import lombok.Data;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;

@Entity
@RevisionEntity(CustomRevisionListener.class)
@Data
@Table(name = "revision_info", schema = "public")
public class CustomRevisionEntity {
    @Id
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @RevisionTimestamp
    private long timestamp;

    @Column(name = "user_name",length = 32)
    private String userName;
}
