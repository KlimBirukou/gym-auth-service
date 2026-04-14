package com.epam.gym.auth.configuration;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.extensions.spring.SpringMapperConfig;

@MapperConfig(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = ConversionServiceAdapter.class,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
@SpringMapperConfig(
    conversionServiceAdapterPackage = "com.epam.gym.auth.configuration"
)
public interface IMapStructConfiguration {

}
