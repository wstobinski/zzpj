import {environment} from "../../src/environments/environment";

describe('Login page tests', () => {
  beforeEach(() => {
    // Intercept the login request and mock the response
    cy.intercept('POST', `${environment.API_URL}/users/login`, {
      statusCode: 200,
      body: {
        response: {
          token: 'eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.bQTnz6AuMJvmXXQsVPrxeQNvzDkimo7VNXxHeSBfClLufmCVZRUuyTwJF311JHuh',
          user: {
            uuid: 1,
            email: 'mock@test.com',
            role: 'admin'
          }
        },
        ok: true
      }
    }).as('loginRequest');
  });

  it('should login and redirect to the home page', () => {
    // Visit the login page
    cy.visit('/login');

    // Fill in the login form
    cy.get('ion-input[formControlName="email"]').type('mock@test.com');
    cy.get('ion-input[formControlName="password"]').type('password');

    // Submit the form
    cy.get('ion-button[type="submit"]').click();


    // Wait for the mocked request
    cy.wait('@loginRequest');

    // Check if redirected to home page
    cy.url().should('include', '/home');

  });

  it('can change login types', () => {
    // Visit the login page
    cy.visit('/login');

    // Login form
    cy.get('ion-input[formControlName="email"]').should('be.visible');
    cy.get('ion-input[formControlName="password"]').should('be.visible');

    // Switch to activate form
    cy.get('ion-card-subtitle ion-text').should('be.visible').click();

    // Activation form
    cy.get('ion-input[formControlName="code"]').should('be.visible');
    cy.get('ion-input[formControlName="password"]').should('be.visible');
    cy.get('ion-input[formControlName="passwordConfirm"]').should('be.visible');

  });
});
