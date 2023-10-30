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

describe('Assembly e2e test', () => {
  const assemblyPageUrl = '/assembly';
  const assemblyPageUrlPattern = new RegExp('/assembly(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const assemblySample = { quantity: 28681 };

  let assembly;
  let part;

  beforeEach(() => {
    cy.login(username, password);
  });

  // create an instance at the required relationship entities
  beforeEach(() => {
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/parts',
      body: { name: 'Integration Architect male', assemblyCost: 28214, bestPrice: 22890 },
    }).then(({ body }) => {
      part = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/assemblies+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/assemblies').as('postEntityRequest');
    cy.intercept('DELETE', '/api/assemblies/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/parts', {
      statusCode: 200,
      body: [part],
    });
  });

  afterEach(() => {
    if (assembly) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/assemblies/parentId=${assembly.parent.id};childId=${assembly.child.id}`,
      }).then(() => {
        assembly = undefined;
      });
    }
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

  it('Assemblies menu should load Assemblies page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('assembly');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Assembly').should('exist');
    cy.url().should('match', assemblyPageUrlPattern);
  });

  describe('Assembly page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(assemblyPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Assembly page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/assembly/new$'));
        cy.getEntityCreateUpdateHeading('Assembly');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', assemblyPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/assemblies',
          body: {
            ...assemblySample,
            parent: part,
            child: part,
          },
        }).then(({ body }) => {
          assembly = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/assemblies+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/assemblies?page=0&size=20>; rel="last",<http://localhost/api/assemblies?page=0&size=20>; rel="first"',
              },
              body: [assembly],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(assemblyPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Assembly page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('assembly');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', assemblyPageUrlPattern);
      });

      it('edit button click should load edit Assembly page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Assembly');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', assemblyPageUrlPattern);
      });

      it('edit button click should load edit Assembly page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Assembly');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', assemblyPageUrlPattern);
      });

      it('last delete button click should delete instance of Assembly', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('assembly').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', assemblyPageUrlPattern);

        assembly = undefined;
      });
    });
  });

  describe('new Assembly page', () => {
    beforeEach(() => {
      cy.visit(`${assemblyPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Assembly');
    });

    it('should create an instance of Assembly', () => {
      cy.get(`[data-cy="quantity"]`).type('5734');
      cy.get(`[data-cy="quantity"]`).should('have.value', '5734');

      cy.setFieldSelectToLastOfEntity('parent');
      cy.setFieldSelectToLastOfEntity('child');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        assembly = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', assemblyPageUrlPattern);
    });
  });
});
