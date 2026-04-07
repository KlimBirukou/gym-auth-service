package com.epam.gym.gymauthserver.repository.mapper;

import com.epam.gym.gymauthserver.configuration.IMapStructConfiguration;
import com.epam.gym.gymauthserver.domain.LoginAttempt;
import com.epam.gym.gymauthserver.repository.entity.LoginAttemptEntity;
import lombok.NonNull;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.extensions.spring.DelegatingConverter;
import org.springframework.core.convert.converter.Converter;

@Mapper(config = IMapStructConfiguration.class)
public interface ILoginAttemptEntityToLoginAttemptMapper extends Converter<@NonNull LoginAttemptEntity, LoginAttempt> {

    @Override
    LoginAttempt convert(LoginAttemptEntity source);

    @InheritInverseConfiguration
    @DelegatingConverter
    LoginAttemptEntity convert(LoginAttempt domain);
}
