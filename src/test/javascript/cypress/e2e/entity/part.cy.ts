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

describe('Part e2e test', () => {
  const partPageUrl = '/part';
  const partPageUrlPattern = new RegExp('/part(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const partSample = { name: 'Northeast Finland common' };

  let part;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/parts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/parts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/parts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (part) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/parts/${part.id}`,
      }).then(() => {
        part = undefined;
      });
    }
  });

  it('Parts menu should load Parts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('part');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Part').should('exist');
    cy.url().should('match', partPageUrlPattern);
  });

  describe('Part page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(partPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Part page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/part/new$'));
        cy.getEntityCreateUpdateHeading('Part');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', partPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/parts',
          body: partSample,
        }).then(({ body }) => {
          part = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/parts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/parts?page=0&size=20>; rel="last",<http://localhost/api/parts?page=0&size=20>; rel="first"',
              },
              body: [part],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(partPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Part page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('part');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', partPageUrlPattern);
      });

      it('edit button click should load edit Part page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Part');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', partPageUrlPattern);
      });

      it('edit button click should load edit Part page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Part');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', partPageUrlPattern);
      });

      it('last delete button click should delete instance of Part', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('part').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', partPageUrlPattern);

        part = undefined;
      });
    });
  });

  describe('new Part page', () => {
    beforeEach(() => {
      cy.visit(`${partPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Part');
    });

    it('should create an instance of Part', () => {
      cy.get(`[data-cy="name"]`).type('orchestrate Convertible nicely');
      cy.get(`[data-cy="name"]`).should('have.value', 'orchestrate Convertible nicely');

      cy.get(`[data-cy="assemblyCost"]`).type('11357');
      cy.get(`[data-cy="assemblyCost"]`).should('have.value', '11357');

      cy.get(`[data-cy="bestPrice"]`).type('6526');
      cy.get(`[data-cy="bestPrice"]`).should('have.value', '6526');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        part = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', partPageUrlPattern);
    });
  });
});
