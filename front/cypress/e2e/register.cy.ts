describe('Register page', () => {

  it('should register successfully and redirect to /login', () => {
    // On intercepte le register AVANT le visit
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {}
    }).as('register');

    cy.visit('/register');

    // On remplit le formulaire
    cy.get('input[formControlName=firstName]').type('Jean');
    cy.get('input[formControlName=lastName]').type('Dupont');
    cy.get('input[formControlName=email]').type('jean.dupont@example.com');
    cy.get('input[formControlName=password]').type('superpass');

    // Le bouton doit être activé
    cy.get('button[type=submit]').should('not.be.disabled').click();

    // On attend que l'appel API soit bien parti
    cy.wait('@register').then((interception) => {
      expect(interception.request.method).to.eq('POST');
      expect(interception.request.body).to.deep.equal({
        email: 'jean.dupont@example.com',
        firstName: 'Jean',
        lastName: 'Dupont',
        password: 'superpass'
      });
    });

    // Vérifie la redirection
    cy.url().should('include', '/login');
    // Et pas de message d'erreur
    cy.get('.error').should('not.exist');
  });

  it('should display an error message when backend returns an error', () => {
    // Cette fois on simule une erreur
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Email already used' }
    }).as('registerFail');

    cy.visit('/register');

    cy.get('input[formControlName=firstName]').type('Jean');
    cy.get('input[formControlName=lastName]').type('Dupont');
    cy.get('input[formControlName=email]').type('jean.dupont@example.com');
    cy.get('input[formControlName=password]').type('superpass');

    cy.get('button[type=submit]').click();

    cy.wait('@registerFail');

    // On reste sur /register
    cy.url().should('include', '/register');

    // Le message d'erreur doit être affiché
    cy.get('.error').should('be.visible').and('contain', 'An error occurred');
  });
});
