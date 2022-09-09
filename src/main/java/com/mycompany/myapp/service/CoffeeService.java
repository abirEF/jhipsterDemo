package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Coffee;
import com.mycompany.myapp.repository.CoffeeRepository;
import com.mycompany.myapp.service.dto.CoffeeDTO;
import com.mycompany.myapp.service.mapper.CoffeeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Coffee}.
 */
@Service
@Transactional
public class CoffeeService {

    private final Logger log = LoggerFactory.getLogger(CoffeeService.class);

    private final CoffeeRepository coffeeRepository;

    private final CoffeeMapper coffeeMapper;

    public CoffeeService(CoffeeRepository coffeeRepository, CoffeeMapper coffeeMapper) {
        this.coffeeRepository = coffeeRepository;
        this.coffeeMapper = coffeeMapper;
    }

    /**
     * Save a coffee.
     *
     * @param coffeeDTO the entity to save.
     * @return the persisted entity.
     */
    public CoffeeDTO save(CoffeeDTO coffeeDTO) {
        log.debug("Request to save Coffee : {}", coffeeDTO);
        Coffee coffee = coffeeMapper.toEntity(coffeeDTO);
        coffee = coffeeRepository.save(coffee);
        return coffeeMapper.toDto(coffee);
    }

    /**
     * Update a coffee.
     *
     * @param coffeeDTO the entity to save.
     * @return the persisted entity.
     */
    public CoffeeDTO update(CoffeeDTO coffeeDTO) {
        log.debug("Request to update Coffee : {}", coffeeDTO);
        Coffee coffee = coffeeMapper.toEntity(coffeeDTO);
        coffee = coffeeRepository.save(coffee);
        return coffeeMapper.toDto(coffee);
    }

    /**
     * Partially update a coffee.
     *
     * @param coffeeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CoffeeDTO> partialUpdate(CoffeeDTO coffeeDTO) {
        log.debug("Request to partially update Coffee : {}", coffeeDTO);

        return coffeeRepository
            .findById(coffeeDTO.getId())
            .map(existingCoffee -> {
                coffeeMapper.partialUpdate(existingCoffee, coffeeDTO);

                return existingCoffee;
            })
            .map(coffeeRepository::save)
            .map(coffeeMapper::toDto);
    }

    /**
     * Get all the coffees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CoffeeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Coffees");
        return coffeeRepository.findAll(pageable).map(coffeeMapper::toDto);
    }

    /**
     * Get one coffee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CoffeeDTO> findOne(Long id) {
        log.debug("Request to get Coffee : {}", id);
        return coffeeRepository.findById(id).map(coffeeMapper::toDto);
    }

    /**
     * Delete the coffee by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Coffee : {}", id);
        coffeeRepository.deleteById(id);
    }
}
