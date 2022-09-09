package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Coffee;
import com.mycompany.myapp.service.dto.CoffeeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Coffee} and its DTO {@link CoffeeDTO}.
 */
@Mapper(componentModel = "spring")
public interface CoffeeMapper extends EntityMapper<CoffeeDTO, Coffee> {}
