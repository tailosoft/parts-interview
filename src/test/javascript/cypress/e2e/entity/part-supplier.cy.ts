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

describe('PartSupplier e2e test', () => {
  const partSupplierPageUrl = '/part-supplier';
  const partSupplierPageUrlPattern = new RegExp('/part-supplier(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const partSupplierSample = { price: 5677 };

  let partSupplier;
  let part;
  let supplier;

  beforeEach(() => {
    cy.login(username, password);
  });

  // create an instance at the required relationship entities
  beforeEach(() => {
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/parts',
      body: { name: 'soldier', assemblyCost: 6160, bestPrice: 4598 },
    }).then(({ body }) => {
      part = body;
    });
  });
  beforeEach(() => {
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/suppliers',
      body: { name: 'inasmuch Southwest Senior' },
    }).then(({ body }) => {
      supplier = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/part-suppliers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/part-suppliers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/part-suppliers/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/parts', {
      statusCode: 200,
      body: [part],
    });

    cy.intercept('GET', '/api/suppliers', {
      statusCode: 200,
      body: [supplier],
    });
  });

  afterEach(() => {
    if (partSupplier) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/part-suppliers/partId=${partSupplier.part.id};supplierId=${partSupplier.supplier.id}`,
      }).then(() => {
        partSupplier = undefined;
      });
    }
  });

  afterEach(() => {
    if (supplier) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/suppliers/${supplier.id}`,
      }).then(() => {
        supplier = undefined;
      });
    }
    if (part) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/parts/${part.id}`,
      }).then(() => {
        part = undefined;
      });
    }
  });

  it('PartSuppliers menu should load PartSuppliers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('part-supplier');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PartSupplier').should('exist');
    cy.url().should('match', partSupplierPageUrlPattern);
  });

  describe('PartSupplier page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(partSupplierPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PartSupplier page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/part-supplier/new$'));
        cy.getEntityCreateUpdateHeading('PartSupplier');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', partSupplierPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/part-suppliers',
          body: {
            ...partSupplierSample,
            part: part,
            supplier: supplier,
          },
        }).then(({ body }) => {
          partSupplier = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/part-suppliers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/part-suppliers?page=0&size=20>; rel="last",<http://localhost/api/part-suppliers?page=0&size=20>; rel="first"',
              },
              body: [partSupplier],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(partSupplierPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PartSupplier page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('partSupplier');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', partSupplierPageUrlPattern);
      });

      it('edit button click should load edit PartSupplier page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PartSupplier');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', partSupplierPageUrlPattern);
      });

      it('edit button click should load edit PartSupplier page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PartSupplier');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', partSupplierPageUrlPattern);
      });

      it('last delete button click should delete instance of PartSupplier', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('partSupplier').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', partSupplierPageUrlPattern);

        partSupplier = undefined;
      });
    });
  });

  describe('new PartSupplier page', () => {
    beforeEach(() => {
      cy.visit(`${partSupplierPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PartSupplier');
    });

    it('should create an instance of PartSupplier', () => {
      cy.get(`[data-cy="price"]`).type('24361');
      cy.get(`[data-cy="price"]`).should('have.value', '24361');

      cy.setFieldSelectToLastOfEntity('part');
      cy.setFieldSelectToLastOfEntity('supplier');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        partSupplier = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', partSupplierPageUrlPattern);
    });
  });
});
