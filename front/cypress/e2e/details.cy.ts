describe('DetailComponent E2E', () => {

  const sessionId = '123';
  const userId = '1';

  const mockTeacher = {
    id: 7,
    lastName: "DELAHAYE",
    firstName: "Margot",
    createdAt: "2025-10-17T10:36:28",
    updatedAt: "2025-10-17T10:36:28",
  };

  const mockSession = {
    id: 123,
    name: "Yoga acrobatique",
    date: "2025-09-30T00:00:00.000+00:00",
    teacher_id: 7,
    description: "Ce fameux thierrry va vous transcender dans une session yoga acrobatique comme ja-ja.",
    users: [1],
    createdAt: "2025-10-17T10:50:22",
    updatedAt: "2025-10-31T15:35:27"
  }


  beforeEach(() => {
    //interception de la recuperation des sessions
    cy.intercept('GET', `/api/session`, [
      {
        id: sessionId,
        title: 'Test Session 1',
        teacher_id: 7,
        users: [42],
      }, {
        id: sessionId,
        title: 'Test Session 2',
        teacher_id: 7,
        users: [42],
      }, {
        id: sessionId,
        title: 'Test Session 3',
        teacher_id: 7,
        users: [42],
      }
    ]).as('getAllSession');

    //interception de la recuperation d'une session
    cy.intercept('GET', `/api/session/${sessionId}`, mockSession).as('getSession');

    //interception de la recuperation d'une prof
    cy.intercept('GET', `/api/teacher/${mockSession.teacher_id}`, mockTeacher).as('getTeacher');

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

    //interception de la demande de participation a une session
    cy.intercept(
      'POST',
      `/api/session/${sessionId}/participate/${userId}`,
      {statusCode: 200}
    ).as('participate');

    //interception de la demande de non participation a une session
    cy.intercept(
      'DELETE',
      `/api/session/${sessionId}/participate/${userId}`,
      {statusCode: 200}
    ).as('unparticipate');

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

  it('should load session and teacher details', () => {

    //on navigue sur la premiere session disponible
    cy.get('span.mat-button-wrapper:first').click();

    //on recuepre le detail de la session simulé
    cy.wait('@getSession');

    //on verifie que la page contient bien la data de la session mockée
    cy.contains('Yoga Acrobatique').should('be.visible');


    cy.wait('@getTeacher');
    cy.contains(mockTeacher.lastName).should('exist');
  });

  it('should allow user to go back', () => {

    //on navigue sur la premiere session disponible
    cy.get('span.mat-button-wrapper:first').click();

    cy.window().then(win => cy.spy(win.history, 'back').as('backSpy'));

    cy.get('button.mat-focus-indicator:first').click();

    cy.get('@backSpy').should('have.been.calledOnce');
  });


  it('should allow user to participate', () => {
    // mock user not participating initially
    cy.intercept('GET', `/api/session/${sessionId}`, {
      ...mockSession,
      users: []
    }).as('getNotParticipateSession');


    //on navigue sur la premiere session disponible
    cy.get('span.mat-button-wrapper:first').click();

    cy.wait('@getNotParticipateSession');

    cy.get('button.mat-focus-indicator:last').click();
    cy.wait('@participate');

    //interception de la recuperation d'une session
    cy.intercept('GET', `/api/session/${sessionId}`, mockSession).as('getSession');

    //retour a l accueil
    cy.get('button.mat-focus-indicator:first').click();

    //on navigue sur la premiere session disponible
    cy.get('span.mat-button-wrapper:first').click();

    //on recuepre le detail de la session simulé
    cy.wait('@getSession');

    cy.get('span.mat-button-wrapper:last')
      .should('contain', 'Do not participate');
  });


  it('should allow user to unParticipate', () => {
    //on navigue sur la premiere session disponible
    cy.get('span.mat-button-wrapper:first').click();

    cy.wait('@getSession');

    cy.get('span.mat-button-wrapper:last')
      .should('contain', 'Do not participate');

    cy.get('span.mat-button-wrapper:last').click();
    cy.wait('@unparticipate');

    //retour a l accueil
    cy.get('button.mat-focus-indicator:first').click();

    // mock user not participating initially
    cy.intercept('GET', `/api/session/${sessionId}`, {
      ...mockSession,
      users: []
    }).as('getNotParticipateSession');

    //on navigue sur la premiere session disponible
    cy.get('span.mat-button-wrapper:first').click();

    cy.wait('@getNotParticipateSession');

    cy.get('span.mat-button-wrapper:last')
      .should('contain', 'Participate');
  });
});
