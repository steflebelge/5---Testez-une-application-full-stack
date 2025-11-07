describe('Login spec', () => {

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
    }).as('loginSuccess');

    cy.intercept('GET', '/api/session', []).as('getSessions');

    // On visite la page
    cy.visit('/login');
  });


  it('should login successfully and redirect to /sessions', () => {
    // Remplit le formulaire
    cy.get('input[formControlName=email]').type('example@email.com');
    cy.get('input[formControlName=password]').type('ceciestunmdp');
    // Clique sur le bouton submit
    cy.get('button[type=submit]').click();

    // Attend que la requête POST /login soit envoyée et répondue
    cy.wait('@loginSuccess').then((interception) => {
      expect(interception.request.method).to.eq('POST');
      expect(interception.response && interception.response.statusCode || 200).to.eq(200);
    });

    // Attend que la redirection soit terminée
    cy.url().should('include', '/sessions');
  });


  it('should display error message on failed login', () => {
    // Interception avec une erreur HTTP 401
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Unauthorized' },
    }).as('loginFail');

    // Remplit le formulaire
    cy.get('input[formControlName=email]').type('wrong@user.com');
    cy.get('input[formControlName=password]').type('wrongpass');
    // Clique sur le bouton submit
    cy.get('button[type=submit]').click();

    // Attend la requête échouée
    cy.wait('@loginFail');

    // Vérifie que le message d’erreur est affiché
    cy.get('p.error').should('be.visible').and('contain', 'An error occurred');

    // Vérifie qu’on reste sur la page de login
    cy.url().should('include', '/login');
  });
});
