describe('UnauthGuard e2e', () => {
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


  it('should allow access when not logged in', () => {
    // Supprime toute session (utilisateur déconnecté)
    cy.clearLocalStorage();

    // Accès à une route protégée par UnauthGuard (ex: /login)
    cy.visit('/login');

    // Vérifie qu’on reste bien sur /login
    cy.url().should('include', '/login');

    // Vérifie que le contenu de la page est bien visible
    cy.contains('Login').should('be.visible');
  });

  it('should redirect to /rentals when user is already logged in', () => {
    // On visite la page login
    cy.visit('/login');

    // Remplit le formulaire
    cy.get('input[formControlName=email]').type('aze@aze.aze');
    cy.get('input[formControlName=password]').type('azertyuiop');
    cy.get('button[type=submit]').click();

    cy.wait('@login'); // login simulé

    // Visite une page normalement réservée aux invités
    cy.visit('/login');

    // Doit être redirigé automatiquement
    cy.url().should('include', '/rentals');

    // Et la page de location doit s’afficher
    cy.contains('Rentals').should('be.visible');
  });
});
