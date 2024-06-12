describe('Initial tests', () => {
  it('The app is named Handby ', () => {
    cy.visit('/');
    cy.title().should('equal', 'Handby');
  })
  it('Home page has greetings card with valid title', () => {

    cy.visit('/');
    cy.get('ion-card-title').should('contain.text', 'Witaj w Handby')
    cy.get('ion-card-subtitle').should('contain.text', 'Poradnik na start')
  });
  it('Home page should have login button that redirects', () => {
    cy.visit('/'); // Adjust the URL to match your application's starting page

    cy.get('ion-button').within(() => {
      cy.get('ion-icon[name="log-in-outline"]').should('exist');
    }).click();

    // Verify that the URL changes to the login page
    cy.url().should('include', '/login');

    // Optionally, check if some element on the login page is visible
    cy.get('ion-input[formControlName="email"]').should('be.visible');
  });
})
