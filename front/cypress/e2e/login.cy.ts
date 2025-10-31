describe('Login spec', () => {

  beforeEach(() => {
    // On intercepte les requêtes utilisées par l'application
    cy.intercept('POST', '/api/auth/login', (req) => {
      // facultatif : tu peux vérifier le corps envoyé
      expect(req.body).to.have.keys(['email', 'password']);
    }).as('login');

    cy.intercept('GET', '/api/session', []).as('getSessions');

    // On visite la page
    cy.visit('/login');
  });


  it('should login successfully and redirect to /sessions', () => {
    // Remplit le formulaire
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');

    // Clique sur le bouton submit
    cy.get('button[type=submit]').click();

    // Attend que la requête POST /login soit envoyée et répondue
    cy.wait('@login').then((interception) => {
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

    cy.visit('/login');

    cy.get('input[formControlName=email]').type('wrong@user.com');
    cy.get('input[formControlName=password]').type('wrongpass');
    cy.get('button[type=submit]').click();

    // Attend la requête échouée
    cy.wait('@loginFail');

    // Vérifie que le message d’erreur est affiché
    cy.get('p.error').should('be.visible').and('contain', 'An error occurred');

    // Vérifie qu’on reste sur la page de login
    cy.url().should('include', '/login');
  });
});
