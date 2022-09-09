import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Coffee e2e test', () => {
  const coffeePageUrl = '/coffee';
  const coffeePageUrlPattern = new RegExp('/coffee(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const coffeeSample = { name: 'Data Rwand' };

  let coffee;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/coffees+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/coffees').as('postEntityRequest');
    cy.intercept('DELETE', '/api/coffees/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (coffee) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/coffees/${coffee.id}`,
      }).then(() => {
        coffee = undefined;
      });
    }
  });

  it('Coffees menu should load Coffees page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('coffee');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Coffee').should('exist');
    cy.url().should('match', coffeePageUrlPattern);
  });

  describe('Coffee page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(coffeePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Coffee page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/coffee/new$'));
        cy.getEntityCreateUpdateHeading('Coffee');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', coffeePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/coffees',
          body: coffeeSample,
        }).then(({ body }) => {
          coffee = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/coffees+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/coffees?page=0&size=20>; rel="last",<http://localhost/api/coffees?page=0&size=20>; rel="first"',
              },
              body: [coffee],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(coffeePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Coffee page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('coffee');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', coffeePageUrlPattern);
      });

      it('edit button click should load edit Coffee page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Coffee');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', coffeePageUrlPattern);
      });

      it('edit button click should load edit Coffee page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Coffee');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', coffeePageUrlPattern);
      });

      it('last delete button click should delete instance of Coffee', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('coffee').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', coffeePageUrlPattern);

        coffee = undefined;
      });
    });
  });

  describe('new Coffee page', () => {
    beforeEach(() => {
      cy.visit(`${coffeePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Coffee');
    });

    it('should create an instance of Coffee', () => {
      cy.get(`[data-cy="fieldName"]`).type('Moroccan').should('have.value', 'Moroccan');

      cy.get(`[data-cy="name"]`).type('Metrics ge').should('have.value', 'Metrics ge');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        coffee = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', coffeePageUrlPattern);
    });
  });
});
