describe('MeComponent e2e (mocked)', () => {
  beforeEach(() => {
    // 🔹 Interception du login
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        firstName: 'aze',
        lastName: 'aze',
        email: 'aze@aze.aze',
        admin: false
      }
    }).as('login');

    // 🔹 Interception du GET user
    cy.intercept('GET', '/api/user/1', {
      statusCode: 200,
      body: {
        id: 1,
        firstName: 'aze',
        lastName: 'aze',
        email: 'aze@aze.aze',
        admin: false,
        createdAt: '2025-01-01T00:00:00.000Z',
        updatedAt: '2025-01-02T00:00:00.000Z'
      }
    }).as('getUser');

    // 🔹 Interception du DELETE user
    cy.intercept('DELETE', '/api/user/1', {
      statusCode: 200,
      body: {}
    }).as('deleteUser');

    // On visite la page login
    cy.visit('/login');

    // Remplit le formulaire
    cy.get('input[formControlName=email]').type('aze@aze.aze');
    cy.get('input[formControlName=password]').type('azertyuiop');
    cy.get('button[type=submit]').click();

    cy.wait('@login'); // login simulé

    // On visite la page "me"
    cy.get('span[routerlink=me]').click();
    cy.wait('@getUser');
  });

  it('should display user information', () => {
    cy.contains('User information').should('be.visible');
    cy.contains('Name: aze AZE').should('be.visible');
    cy.contains('Email: aze@aze.aze').should('be.visible');
    cy.contains('Delete my account:').should('be.visible');
  });

  it('should delete the account and redirect', () => {
    cy.get('button[color=warn]').click();
    cy.wait('@deleteUser').then((interception) => {
      expect(interception.request.method).to.eq('DELETE');
    });
    cy.url().should('match', /\/$/);
    cy.contains('Your account has been deleted !').should('exist');
  });
});
