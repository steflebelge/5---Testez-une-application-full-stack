describe('AuthGuard e2e', () => {

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
  });

  it('should redirect to /login when not logged in', () => {
    // On nettoie la session
    cy.clearLocalStorage();

    // On visite une page protégée
    cy.visit('/me', { failOnStatusCode: false });

    // Attends la redirection automatique vers /login
    cy.url().should('include', '/login');

    // Vérifie que le contenu de la page de login est présent
    cy.contains('Login').should('be.visible');
  });

  it('should allow access to protected page when logged in', () => {
    // On visite la page login
    cy.visit('/login');

    // Remplit le formulaire
    cy.get('input[formControlName=email]').type('aze@aze.aze');
    cy.get('input[formControlName=password]').type('azertyuiop');
    cy.get('button[type=submit]').click();

    cy.wait('@login'); // login simulé

    // On visite la page "me"
    cy.get('span[routerlink=me]').click();

    // Vérifie qu'on n'est PAS redirigé vers /login
    cy.url().should('include', '/me');

    // Vérifie que le contenu attendu est visible
    cy.contains('User information').should('be.visible');
  });
});
