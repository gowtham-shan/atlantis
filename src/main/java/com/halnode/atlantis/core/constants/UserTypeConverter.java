package com.halnode.atlantis.core.constants;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class UserTypeConverter implements AttributeConverter<UserType,Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserType attribute) {
        if(attribute==null)
            return null;
        switch (attribute){
            case ADMIN:
                return 1;
            case STAFF:
                return 2;
            case CUSTOMER:
                return 3;
            default:
                throw new IllegalArgumentException(attribute+" is not supported");
        }
    }

    @Override
    public UserType convertToEntityAttribute(Integer dbData) {
        if(dbData==null)
            return null;
        switch (dbData){
            case 1:
                return UserType.ADMIN;
            case 2:
                return UserType.STAFF;
            case 3:
                return UserType.CUSTOMER;
            default:
                throw new IllegalArgumentException(dbData+"is not supported");
        }
    }
}
