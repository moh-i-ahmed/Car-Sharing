# Web App for Car Sharing
My third year Computer Science project. Web application for car sharing that explores the algorithmic difficulties of serving customer requests with limited resources, while also implementing a medium-scale software product.

## CI status

| Branch    | CI Status                                                                                                                                                             |
| --------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `master`  | [![Build Status](https://travis-ci.com/moa38/Timesheet-App.svg?token=Wvq1gJxzi2Ju4qdjqBTA&branch=master)](https://travis-ci.com/moa38/Timesheet-App)  |
| `develop` | [![Build Status](https://travis-ci.com/moa38/Timesheet-App.svg?token=Wvq1gJxzi2Ju4qdjqBTA&branch=develop)](https://travis-ci.com/moa38/Timesheet-App) |

## Dependacy Documentation

- Bulma: [https://bulma.io/](https://bulma.io/documentation/)
- Chart.js: [https://www.chartjs.org/](https://www.chartjs.org/docs/latest/)
- TOAST UI: [https://ui.toast.com/](https://ui.toast.com/)
- Thymeleaf: [https://www.thymeleaf.org/](https://www.thymeleaf.org/documentation.html)

## Resources

 - [Commit Message Guidelines](https://gist.github.com/robertpainsi/b632364184e70900af4ab688decf6f53#a-properly-formed-git-commit-subject-line-should-always-be-able-to-complete-the-following-sentence)
 - [Git/GitHub branching standards & conventions ](https://gist.github.com/digitaljhelms/4287848)


## Definition of Done

| Title                           | Description                                                                                                                                                                                                                                                              | How to provide evidence                                                                                                                         | Notes                                                                                                                                                                                                                                                                                                       |
| ------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Code                            | The code is written to implement to story                                                                                                                                                                                                                                | Paste a link to the Github commit(s) that added the code into the pivotal tracker story                                                         |                                                                                                                                                                                                                                                                                                             |
| Unit Testing                    | Each method that returns a value but doesn’t interact with the spring framework must have Junit tests written                                                                                                                                                            | Paste a link to the Github commit(s) that added the code for the unit tests                                                                     | You will only be able to do this if you design your code well so that there are classes and methods that don’t rely on the spring framework. Similar to the challenge that groups had writing unit tests for CO2012.                                                                                       |
| CI Passing                      | When the code and unit tests are written there must be a build on Travis CI that passes (the code compiles and unit tests pass)                                                                                                                                          | Click on the “Build #??” for the passing build and paste the URL into a Pivotal comment                                                       | If you are only running unit tests on Travis and not integration tests you must configure travis to do this through .travis.yml                                                                                                                                                                             |
| Manual Testing                  | Someone other than the develop of a user story must run the application and perform the tasks of the user story with a range of valid and invalid inputs. They must log what they did in a markdown file in Github and add any bugs they discovered into Pivotal Tracker | Paste a link to the file in Github in which the tester logged their testing in Pivotal comments. Link to the bugs added to Pivotal as comments. | Most stories will have introduced errors and bugs. This is normal even in professional software development. We’re looking that there have been serious attempts to find them, not a casual attempt to just “pass” the story.                                                                            |
| Integration Testing             | Each SpringMVC controller method must automated tests that check that the method acts correctly for appropriate inputs.                                                                                                                                                  | Paste a link to the Github commit(s) that added the code for tests                                                                              | [There are examples in CO2006 TDD Ex (https://github.com/uol-inf/CO2006-18-19/tree/master/sprint3/TDD_ex03_solution). The tests must really check the actions of the controller, not just that it returns a HTTP status 200](https://github.com/uol-inf/CO2006-18-19/tree/master/sprint3/TDD_ex03_solution) |
| Integration Tests running on CI | The Integration tests pass on Travis CI                                                                                                                                                                                                                                  | Click on the “Build #??” for the passing build and paste the URL into a Pivotal comment                                                       | This is a challenge because you must get Gradle/Travis to create a database for the tests to run against. There will be guidance on this.                                                                                                                                                                   |
| Coding Standards Checked        | Automatic checking of coding standards is done via gradle and checkstyle. All code added for the story must adhere to the coding standards you choose.                                                                                                                    | Click on the “Build #??” for the passing build and paste the URL into a Pivotal comment. This must show the style being checked.              | [This must be set up in gradle - instructions are here - https://medium.com/@raveensr/how-to-add-checkstyle-and-findbugs-plugins-in-a-gradle-based-project-51759aa843be](https://medium.com/@raveensr/how-to-add-checkstyle-and-findbugs-plugins-in-a-gradle-based-project-51759aa843be)                    |
|                                 |                                                                                                                                                                                                                                                                          |                       |                                                                                                                                                 |                                                                                                                                                                                                                                                                                                             |
