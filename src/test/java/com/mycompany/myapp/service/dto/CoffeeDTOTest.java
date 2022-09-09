package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CoffeeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CoffeeDTO.class);
        CoffeeDTO coffeeDTO1 = new CoffeeDTO();
        coffeeDTO1.setId(1L);
        CoffeeDTO coffeeDTO2 = new CoffeeDTO();
        assertThat(coffeeDTO1).isNotEqualTo(coffeeDTO2);
        coffeeDTO2.setId(coffeeDTO1.getId());
        assertThat(coffeeDTO1).isEqualTo(coffeeDTO2);
        coffeeDTO2.setId(2L);
        assertThat(coffeeDTO1).isNotEqualTo(coffeeDTO2);
        coffeeDTO1.setId(null);
        assertThat(coffeeDTO1).isNotEqualTo(coffeeDTO2);
    }
}
