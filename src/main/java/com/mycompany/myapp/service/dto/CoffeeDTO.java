package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Coffee} entity.
 */
@Schema(description = "The Entity entity.\n@author A true hipster")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CoffeeDTO implements Serializable {

    private Long id;

    /**
     * fieldName
     */
    @Schema(description = "fieldName")
    private String fieldName;

    @NotNull
    @Size(max = 10)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoffeeDTO)) {
            return false;
        }

        CoffeeDTO coffeeDTO = (CoffeeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, coffeeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CoffeeDTO{" +
            "id=" + getId() +
            ", fieldName='" + getFieldName() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
