package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.CoffeeRepository;
import com.mycompany.myapp.service.CoffeeService;
import com.mycompany.myapp.service.dto.CoffeeDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Coffee}.
 */
@RestController
@RequestMapping("/api")
public class CoffeeResource {

    private final Logger log = LoggerFactory.getLogger(CoffeeResource.class);

    private static final String ENTITY_NAME = "coffee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CoffeeService coffeeService;

    private final CoffeeRepository coffeeRepository;

    public CoffeeResource(CoffeeService coffeeService, CoffeeRepository coffeeRepository) {
        this.coffeeService = coffeeService;
        this.coffeeRepository = coffeeRepository;
    }

    /**
     * {@code POST  /coffees} : Create a new coffee.
     *
     * @param coffeeDTO the coffeeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new coffeeDTO, or with status {@code 400 (Bad Request)} if the coffee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/coffees")
    public ResponseEntity<CoffeeDTO> createCoffee(@Valid @RequestBody CoffeeDTO coffeeDTO) throws URISyntaxException {
        log.debug("REST request to save Coffee : {}", coffeeDTO);
        if (coffeeDTO.getId() != null) {
            throw new BadRequestAlertException("A new coffee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CoffeeDTO result = coffeeService.save(coffeeDTO);
        return ResponseEntity
            .created(new URI("/api/coffees/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /coffees/:id} : Updates an existing coffee.
     *
     * @param id the id of the coffeeDTO to save.
     * @param coffeeDTO the coffeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated coffeeDTO,
     * or with status {@code 400 (Bad Request)} if the coffeeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the coffeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/coffees/{id}")
    public ResponseEntity<CoffeeDTO> updateCoffee(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CoffeeDTO coffeeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Coffee : {}, {}", id, coffeeDTO);
        if (coffeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, coffeeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!coffeeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CoffeeDTO result = coffeeService.update(coffeeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, coffeeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /coffees/:id} : Partial updates given fields of an existing coffee, field will ignore if it is null
     *
     * @param id the id of the coffeeDTO to save.
     * @param coffeeDTO the coffeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated coffeeDTO,
     * or with status {@code 400 (Bad Request)} if the coffeeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the coffeeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the coffeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/coffees/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CoffeeDTO> partialUpdateCoffee(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CoffeeDTO coffeeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Coffee partially : {}, {}", id, coffeeDTO);
        if (coffeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, coffeeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!coffeeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CoffeeDTO> result = coffeeService.partialUpdate(coffeeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, coffeeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /coffees} : get all the coffees.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of coffees in body.
     */
    @GetMapping("/coffees")
    public ResponseEntity<List<CoffeeDTO>> getAllCoffees(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Coffees");
        Page<CoffeeDTO> page = coffeeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /coffees/:id} : get the "id" coffee.
     *
     * @param id the id of the coffeeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the coffeeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/coffees/{id}")
    public ResponseEntity<CoffeeDTO> getCoffee(@PathVariable Long id) {
        log.debug("REST request to get Coffee : {}", id);
        Optional<CoffeeDTO> coffeeDTO = coffeeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(coffeeDTO);
    }

    /**
     * {@code DELETE  /coffees/:id} : delete the "id" coffee.
     *
     * @param id the id of the coffeeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/coffees/{id}")
    public ResponseEntity<Void> deleteCoffee(@PathVariable Long id) {
        log.debug("REST request to delete Coffee : {}", id);
        coffeeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
