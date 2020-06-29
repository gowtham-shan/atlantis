package com.halnode.atlantis.util;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 * Use this class for creating primary key/id {@Link Id } values with the user defined prefix value
 *
 * @author gowtham
 */
public class StringPrefixedIdGenerator extends SequenceStyleGenerator {
    public static final String PREFIX_VALUE = "valuePrefix";
    public static final String NUMBER_FORMAT = "numberFormat";
    private String prefixValue = "";
    private String numberFormat = "";

    @Override
    public Serializable generate(SharedSessionContractImplementor session,
                                 Object object) throws HibernateException {
        /*
        EXAMPLE ONE :
            String query = String.format("select %s from %s",
                session.getEntityPersister(object.getClass().getName(), object)
                        .getIdentifierPropertyName(),object.getClass().getSimpleName());
        EXAMPLE TWO :
             String query = String.format("select %s from %s", "name", "TestEntity");
       EXAMPLE THREE :
            **ONLY WORKS IF WE HAVE MAPPING IN THE ENTITY CLASS**
            String format="%1$s"+"_"+numberFormat;
            String.format(format,((TestEntity) object).getOrganization().getName(), super.generate(session, object))
         */
        return prefixValue + String.format(numberFormat, super.generate(session, object));
    }

    @Override
    public void configure(Type type, Properties params,
                          ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(LongType.INSTANCE, params, serviceRegistry);
        prefixValue = ConfigurationHelper.getString(PREFIX_VALUE,
                params);
        numberFormat = ConfigurationHelper.getString(NUMBER_FORMAT,
                params);
    }
}
