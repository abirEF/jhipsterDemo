package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Coffee;
import com.mycompany.myapp.repository.CoffeeRepository;
import com.mycompany.myapp.service.dto.CoffeeDTO;
import com.mycompany.myapp.service.mapper.CoffeeMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CoffeeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CoffeeResourceIT {

    private static final String DEFAULT_FIELD_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIELD_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/coffees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private CoffeeMapper coffeeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCoffeeMockMvc;

    private Coffee coffee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coffee createEntity(EntityManager em) {
        Coffee coffee = new Coffee().fieldName(DEFAULT_FIELD_NAME).name(DEFAULT_NAME);
        return coffee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coffee createUpdatedEntity(EntityManager em) {
        Coffee coffee = new Coffee().fieldName(UPDATED_FIELD_NAME).name(UPDATED_NAME);
        return coffee;
    }

    @BeforeEach
    public void initTest() {
        coffee = createEntity(em);
    }

    @Test
    @Transactional
    void createCoffee() throws Exception {
        int databaseSizeBeforeCreate = coffeeRepository.findAll().size();
        // Create the Coffee
        CoffeeDTO coffeeDTO = coffeeMapper.toDto(coffee);
        restCoffeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(coffeeDTO)))
            .andExpect(status().isCreated());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeCreate + 1);
        Coffee testCoffee = coffeeList.get(coffeeList.size() - 1);
        assertThat(testCoffee.getFieldName()).isEqualTo(DEFAULT_FIELD_NAME);
        assertThat(testCoffee.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createCoffeeWithExistingId() throws Exception {
        // Create the Coffee with an existing ID
        coffee.setId(1L);
        CoffeeDTO coffeeDTO = coffeeMapper.toDto(coffee);

        int databaseSizeBeforeCreate = coffeeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCoffeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(coffeeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = coffeeRepository.findAll().size();
        // set the field null
        coffee.setName(null);

        // Create the Coffee, which fails.
        CoffeeDTO coffeeDTO = coffeeMapper.toDto(coffee);

        restCoffeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(coffeeDTO)))
            .andExpect(status().isBadRequest());

        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCoffees() throws Exception {
        // Initialize the database
        coffeeRepository.saveAndFlush(coffee);

        // Get all the coffeeList
        restCoffeeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coffee.getId().intValue())))
            .andExpect(jsonPath("$.[*].fieldName").value(hasItem(DEFAULT_FIELD_NAME)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getCoffee() throws Exception {
        // Initialize the database
        coffeeRepository.saveAndFlush(coffee);

        // Get the coffee
        restCoffeeMockMvc
            .perform(get(ENTITY_API_URL_ID, coffee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(coffee.getId().intValue()))
            .andExpect(jsonPath("$.fieldName").value(DEFAULT_FIELD_NAME))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingCoffee() throws Exception {
        // Get the coffee
        restCoffeeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCoffee() throws Exception {
        // Initialize the database
        coffeeRepository.saveAndFlush(coffee);

        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();

        // Update the coffee
        Coffee updatedCoffee = coffeeRepository.findById(coffee.getId()).get();
        // Disconnect from session so that the updates on updatedCoffee are not directly saved in db
        em.detach(updatedCoffee);
        updatedCoffee.fieldName(UPDATED_FIELD_NAME).name(UPDATED_NAME);
        CoffeeDTO coffeeDTO = coffeeMapper.toDto(updatedCoffee);

        restCoffeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, coffeeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(coffeeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
        Coffee testCoffee = coffeeList.get(coffeeList.size() - 1);
        assertThat(testCoffee.getFieldName()).isEqualTo(UPDATED_FIELD_NAME);
        assertThat(testCoffee.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingCoffee() throws Exception {
        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();
        coffee.setId(count.incrementAndGet());

        // Create the Coffee
        CoffeeDTO coffeeDTO = coffeeMapper.toDto(coffee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCoffeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, coffeeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(coffeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCoffee() throws Exception {
        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();
        coffee.setId(count.incrementAndGet());

        // Create the Coffee
        CoffeeDTO coffeeDTO = coffeeMapper.toDto(coffee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoffeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(coffeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCoffee() throws Exception {
        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();
        coffee.setId(count.incrementAndGet());

        // Create the Coffee
        CoffeeDTO coffeeDTO = coffeeMapper.toDto(coffee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoffeeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(coffeeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCoffeeWithPatch() throws Exception {
        // Initialize the database
        coffeeRepository.saveAndFlush(coffee);

        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();

        // Update the coffee using partial update
        Coffee partialUpdatedCoffee = new Coffee();
        partialUpdatedCoffee.setId(coffee.getId());

        partialUpdatedCoffee.fieldName(UPDATED_FIELD_NAME).name(UPDATED_NAME);

        restCoffeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoffee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCoffee))
            )
            .andExpect(status().isOk());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
        Coffee testCoffee = coffeeList.get(coffeeList.size() - 1);
        assertThat(testCoffee.getFieldName()).isEqualTo(UPDATED_FIELD_NAME);
        assertThat(testCoffee.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateCoffeeWithPatch() throws Exception {
        // Initialize the database
        coffeeRepository.saveAndFlush(coffee);

        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();

        // Update the coffee using partial update
        Coffee partialUpdatedCoffee = new Coffee();
        partialUpdatedCoffee.setId(coffee.getId());

        partialUpdatedCoffee.fieldName(UPDATED_FIELD_NAME).name(UPDATED_NAME);

        restCoffeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoffee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCoffee))
            )
            .andExpect(status().isOk());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
        Coffee testCoffee = coffeeList.get(coffeeList.size() - 1);
        assertThat(testCoffee.getFieldName()).isEqualTo(UPDATED_FIELD_NAME);
        assertThat(testCoffee.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingCoffee() throws Exception {
        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();
        coffee.setId(count.incrementAndGet());

        // Create the Coffee
        CoffeeDTO coffeeDTO = coffeeMapper.toDto(coffee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCoffeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, coffeeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(coffeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCoffee() throws Exception {
        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();
        coffee.setId(count.incrementAndGet());

        // Create the Coffee
        CoffeeDTO coffeeDTO = coffeeMapper.toDto(coffee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoffeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(coffeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCoffee() throws Exception {
        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();
        coffee.setId(count.incrementAndGet());

        // Create the Coffee
        CoffeeDTO coffeeDTO = coffeeMapper.toDto(coffee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoffeeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(coffeeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCoffee() throws Exception {
        // Initialize the database
        coffeeRepository.saveAndFlush(coffee);

        int databaseSizeBeforeDelete = coffeeRepository.findAll().size();

        // Delete the coffee
        restCoffeeMockMvc
            .perform(delete(ENTITY_API_URL_ID, coffee.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
