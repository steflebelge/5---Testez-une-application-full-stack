describe('Session Form E2E', () => {

  beforeEach(() => {
    // Mock sessionService.sessionInformation (admin = true)
    cy.window().then((win) => {
      win.sessionService = {
        sessionInformation: { admin: true }
      };
    });

    // Mock teacher list
    cy.intercept('GET', '/api/teacher', {
      statusCode: 200,
      body: [
        { id: 1, firstName: 'Alice', lastName: 'Martin' },
        { id: 2, firstName: 'Bob', lastName: 'Durand' }
      ]
    }).as('getTeachers');

    // 🔹 Interception du login
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        firstName: 'aze',
        lastName: 'aze',
        email: 'aze@aze.aze',
        admin: true
      }
    }).as('login');

    //interception de la recuperation des sessions
    cy.intercept('GET', `/api/session`, [
      {
        id: '123',
        title: 'Test Session 1',
        teacher_id: 7,
        users: [42],
      }, {
        id: '124',
        title: 'Test Session 2',
        teacher_id: 8,
        users: [42],
      }, {
        id: '125',
        title: 'Test Session 3',
        teacher_id: 9,
        users: [42],
      }
    ]).as('getAllSession');

    // On visite la page login
    cy.visit('/login');

    // Remplit le formulaire
    cy.get('input[formControlName=email]').type('aze@aze.aze');
    cy.get('input[formControlName=password]').type('azertyuiop');
    cy.get('button[type=submit]').click();

    cy.wait('@login'); // login simulé

    // On visite la page "sessions"
    cy.get('span[routerlink=sessions]').click();

    // récuperation des sessions simulé
    cy.wait('@getAllSession');

  });

  it('should render create form and submit successfully', () => {

    cy.intercept('POST', '/api/session', {
      statusCode: 201,
      body: {
        id: 123,
        name: 'My session',
        date: '2025-03-10',
        teacher_id: 1,
        description: 'Test description'
      }
    }).as('createSession');

    //on navigue sur la creation de la session
    cy.get('span.mat-button-wrapper:first').click();

    cy.wait('@getTeachers');

    // remplir form
    cy.get('input[formcontrolname=name]').type('My session');
    cy.get('input[formcontrolname=date]').type('2025-03-10');

    // Teacher select
    cy.get('mat-select[formcontrolname=teacher_id]').click();
    cy.get('mat-option').contains('Alice Martin').click();

    cy.get('textarea[formcontrolname=description]').type('Test description');

    // bouton Save
    cy.get('button[type=submit]').click();

    cy.wait('@createSession');

    // redirection finale
    cy.url().should('include', '/sessions');

    //on verifie que la page contient bien la confirmation
    cy.contains('Session created !').should('be.visible');
  });

  it('should load existing session and update it', () => {
    // Mock détail session
    cy.intercept('GET', '/api/session/125', {
      statusCode: 200,
      body: {
        id: 125,
        name: 'Old name',
        date: '2025-01-12',
        teacher_id: 2,
        description: 'Old description'
      }
    }).as('getSessionDetail');

    // Mock update
    cy.intercept('PUT', '/api/session/125', {
      statusCode: 200,
      body: {}
    }).as('updateSession');

    //on navigue sur la modification de la derniere session disponible
    cy.get('mat-icon.mat-icon-no-color:last').click();

    cy.wait('@getTeachers');
    cy.wait('@getSessionDetail');

    // Vérifier préremplissage
    cy.get('input[formcontrolname=name]').should('have.value', 'Old name');

    // Modifier champs
    cy.get('input[formcontrolname=name]').clear().type('Updated name');
    cy.get('textarea[formcontrolname=description]')
      .clear()
      .type('Updated description');

    // changer teacher
    cy.get('mat-select[formcontrolname=teacher_id]').click();
    cy.get('mat-option').contains('Alice Martin').click();

    // Save
    cy.get('button[type=submit]').click();

    cy.wait('@updateSession');
    cy.url().should('include', '/sessions');

      //on verifie que la page contient bien la confirmation
      cy.contains('Session updated !').should('be.visible');
  });
});
