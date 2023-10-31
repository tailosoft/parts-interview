/* eslint-disable @typescript-eslint/no-namespace */
/* eslint-disable @typescript-eslint/no-use-before-define */
// eslint-disable-next-line spaced-comment
/// <reference types="cypress" />

// ***********************************************
// Begin Specific Selector Attributes for Cypress
// ***********************************************

// Entity
export const entityTableSelector = '[data-cy="entityTable"]';
export const entityCreateButtonSelector = '[data-cy="entityCreateButton"]';
export const entityCreateSaveButtonSelector = '[data-cy="entityCreateSaveButton"]';
export const entityCreateCancelButtonSelector = '[data-cy="entityCreateCancelButton"]';
export const entityDetailsButtonSelector = '[data-cy="entityDetailsButton"]'; // can return multiple elements
export const entityDetailsBackButtonSelector = '[data-cy="entityDetailsBackButton"]';
export const entityEditButtonSelector = '[data-cy="entityEditButton"]';
export const entityDeleteButtonSelector = '[data-cy="entityDeleteButton"]';
export const entityConfirmDeleteButtonSelector = 'p-confirmdialog .p-confirm-dialog-accept';

// ***********************************************
// End Specific Selector Attributes for Cypress
// ***********************************************

Cypress.Commands.add('getEntityHeading', (entityName: string) => {
  return cy.get(`[data-cy="${entityName}Heading"]`);
});

Cypress.Commands.add('getEntityCreateUpdateHeading', (entityName: string) => {
  return cy.get(`[data-cy="${entityName}CreateUpdateHeading"]`);
});

Cypress.Commands.add('getEntityDetailsHeading', (entityInstanceName: string) => {
  return cy.get(`[data-cy="${entityInstanceName}DetailsHeading"]`);
});

Cypress.Commands.add('getEntityDeleteDialogHeading', () => {
  return cy.get(`p-confirmdialog .p-dialog-title`).contains('Confirm delete operation');
});

Cypress.Commands.add('setFieldImageAsBytesOfEntity', (fieldName: string, fileName: string, mimeType: string) => {
  cy.fixture(fileName).then(fileContent => {
    cy.get(`[data-cy="${fieldName}"] .p-fileupload-content`).attachFile(
      { fileContent, fileName, mimeType },
      { subjectType: 'drag-n-drop' }
    );
  });
});

Cypress.Commands.add('setFieldSelectToLastOfEntity', (fieldName: string, multiSelect = false, filter?: string) => {
  const typeSelect = multiSelect ? 'multiselect' : 'dropdown';
  cy.get(`[data-cy="${fieldName}"] > .p-${typeSelect} > .p-${typeSelect}-trigger`).click();
  if (filter) {
    cy.get(`[data-cy="${fieldName}"]`).get(`.p-${typeSelect}-filter`).type(filter);
  }
  cy.get(`[data-cy="${fieldName}"]`).find(`.p-${typeSelect}-item`).last().click();
  if (multiSelect) {
    cy.get(`[data-cy="${fieldName}"] > .p-${typeSelect} .p-${typeSelect}-close`).click();
  }
});

Cypress.Commands.add('setFieldSelectToIndexOfEntity', (index: number, fieldName: string, multiSelect = false, filter?: string) => {
  const typeSelect = multiSelect ? 'multiselect' : 'dropdown';
  cy.get(`[data-cy="${fieldName}"] > .p-${typeSelect} > .p-${typeSelect}-trigger`).click();
  if (filter) {
    cy.get(`[data-cy="${fieldName}"]`).get(`.p-${typeSelect}-filter`).type(filter);
  }
  cy.get(`[data-cy="${fieldName}"]`).find(`.p-${typeSelect}-item`).eq(index).click();
  if (multiSelect) {
    cy.get(`[data-cy="${fieldName}"] > .p-${typeSelect} > .p-${typeSelect}-trigger`).click();
  }
});

declare global {
  namespace Cypress {
    interface Chainable<> {
      getEntityHeading(entityName: string): Cypress.Chainable;
      getEntityCreateUpdateHeading(entityName: string): Cypress.Chainable;
      getEntityDetailsHeading(entityInstanceName: string): Cypress.Chainable;
      getEntityDeleteDialogHeading(entityInstanceName: string): Cypress.Chainable;
      setFieldImageAsBytesOfEntity(fieldName: string, fileName: string, mimeType: string): Cypress.Chainable;
      setFieldSelectToLastOfEntity(fieldName: string, multiSelect?: boolean, filter?: string): Cypress.Chainable;
      setFieldSelectToIndexOfEntity(index: number, fieldName: string, multiSelect?: boolean, filter?: string): Cypress.Chainable;
    }
  }
}

// Convert this to a module instead of script (allows import/export)
export {};
