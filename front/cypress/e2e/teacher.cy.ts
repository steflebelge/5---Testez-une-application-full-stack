describe('TeacherService e2e', () => {
  beforeEach(() => {
    // Interception des requêtes HTTP
    cy.intercept('GET', 'http://localhost:8080/api/teacher', {
      statusCode: 200,
      body: [
        { id: 1, firstName: 'John', lastName: 'Doe' },
        { id: 2, firstName: 'Jane', lastName: 'Smith' }
      ]
    }).as('getAllTeachers');

    cy.intercept('GET', 'http://localhost:8080/api/teacher/1', {
      statusCode: 200,
      body: { id: 1, firstName: 'John', lastName: 'Doe' }
    }).as('getTeacherDetail');

    cy.visit('/sessions/create');
  });

  it('should display list of teachers', () => {
    cy.wait('@getAllTeachers');

    // Vérifie que la page contient les noms retournés
    cy.contains('John Doe').should('be.visible');
    cy.contains('Jane Smith').should('be.visible');
  });

  it('should display teacher detail', () => {
    // Si ton app navigue sur /teacher/1 pour afficher le détail
    cy.visit('/teacher/1');
    cy.wait('@getTeacherDetail');

    cy.contains('John Doe').should('be.visible');
  });
});
