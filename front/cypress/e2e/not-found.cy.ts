describe('NotFoundComponent e2e', () => {

  it('should display the 404 page when navigating to an unknown route', () => {
    // On visite une route inexistante
    cy.visit('/some/unknown/path', { failOnStatusCode: false });

    // Vérifie que le message est affiché
    cy.contains('Page not found !').should('be.visible');
  });
});
