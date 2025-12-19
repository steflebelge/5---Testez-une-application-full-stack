# Yoga App !


For launch and generate the jacoco code coverage:
> mvn clean test

GL



    comment installer la base de données ;
mysql -u root -p -e "CREATE DATABASE projet5;"
mysql -u root -p projet5 < ressources/sql/script.sql
mysql -u root -p -e "CREATE USER 'projet5'@'%' IDENTIFIED BY 'projet5';GRANT ALL PRIVILEGES ON projet5.* TO 'projet5'@'%';FLUSH PRIVILEGES;"

    comment installer l’application ;
cd front/
npm install

cd back/
mvn clean package -U
mvn clean install -Dmaven.test.skip=true

    comment faire fonctionner l’application ;
cd front/
npm run start
http://localhost:4200/

cd back/
mvn spring-boot:run

    comment lancer les différents tests ;
    comment générer les différents rapports de couverture.
[BACKEND]
cd back/
mvn clean test
mvn verify

intégration : 
-> /target/jacoco-report-it/index.html
unitaire : 
-> /target/jacoco-report-unit/index.html
Commun :
-> /target/jacoco-report-merged/index.html

[FRONTEND]
cd front/
npm install
npm run test

[e2e]
cd front/
ng build --configuration=coverage
node serve-coverage.js
npx cypress run
npx nyc report --reporter=lcov --reporter=text-summary
-> /coverage/lcov-report/index.html