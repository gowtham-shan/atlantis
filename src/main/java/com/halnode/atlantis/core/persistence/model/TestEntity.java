package com.halnode.atlantis.core.persistence.model;

import com.halnode.atlantis.util.StringPrefixedIdGenerator;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

/**
 * Example class for Custom Id Generation. Can be deleted if not needed/once used.
 *
 * @author gowtham
 */
@Entity
@Data
@Table(name = "test_entity")
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test")
    @GenericGenerator(
            name = "test",
            strategy = "com.halnode.atlantis.util.StringPrefixedIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedIdGenerator.INCREMENT_PARAM, value = "1"),
                    @Parameter(name = StringPrefixedIdGenerator.PREFIX_VALUE, value = "TEST_"),
                    @Parameter(name = StringPrefixedIdGenerator.NUMBER_FORMAT, value = "%05d")})
    private String id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private Organization organization;
}
