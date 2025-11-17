<a id="readme-top"></a>
<div align="center">
  <h3 align="center">Lingo Trainer (CISQ-1)</h3>

  <p align="center">
    A trainer for the popular game show Lingo, written in Java.
    <br />
</div>



<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>



## About The Project

![Lingo Trainer][product-screenshot]

For the course `Continuous Integration and Software Quality 1` we were tasked to create a trainer for a popular game show called `Lingo`. The main focus of the course wasn't just writing the trainer itself; instead, we needed to create a CI pipeline using GitHub Workflows, and write extensive `UNIT` and `INTEGRATION` tests. More about this <a href="#readme-tests">here</a>.

`Lingo` is a fast-paced word-guessing game show in which two teams compete to guess words based on a single reveiled letter. Whenever a team correctly guesses a word, they may draw two balls from their ball-basin. The basins contain numbered balls, including the infamous `red` and `green` balls. When three green balls are drawn, the running jackpot is won. A red ball immediately ends a team's draw and hands it over to the opposing team. When a team completes a full row, column or diagonal on their card, they win the jackpot. 

For this trainer though, we're focussing on just the actual guessing part. 

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

The trainer is built in `Java`, using `Spring Boot` as the framework and `Maven` for dependency management. Test coverage is handled through `JaCoCo`, with an in-memory `H2` database. `Lombok` has been used to make my life easier. 

* [![Java][java-shield]][java-url]
* [![Spring Boot][spring-shield]][spring-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



## Getting Started

There are a few simple steps to start training. The steps assume you already have `Docker` installed.

### Prerequisites

First, setup the database using Docker:

  ```sh
  docker compose up -d
  ```

### Installation

After setting up the database, simply run the backend!

```sh
mvn spring-boot:run
```

If you don't have `Maven` installed, you can use the included wrapper:

```sh
./mvnw spring-boot:run
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



## Usage

Train your guessing abilities! You can choose between playing a `sequential` or a `random` game. A sequential game will iterate over a 5-letter word, then a 6 letter word, and finally a 7-letter word (looped until you die). Random will randomly start a round with a `5, 6 or 7` letter word.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## <a id="readme-tests"></a> Tests

The project has extensive test coverage through both `UNIT` and `INTEGRATION` tests. All core domain logic (game mechanics, feedback generation, hints), application services, and REST controllers are tested. Coverage is tracked using `JaCoCo`:

- **96%** instruction coverage (1,357 of 1,404)
- **92%** branch coverage (84 of 91)
- **93%** complexity coverage (151 of 163)
- **97%** line coverage (311 of 320)
- **96%** method coverage (111 of 116)
- **100%** class coverage (29 of 29)

Run the test suite and generate a coverage report:

```sh
make coverage
```

The report will be available at `target/site/jacoco/index.html`.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## License

Distributed under the MIT License. See `LICENSE` for more information.

[product-screenshot]: development/ingame.png
[java-shield]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[java-url]: https://www.java.com/
[spring-shield]: https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[spring-url]: https://spring.io/projects/spring-boot
[maven-shield]: https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white
[maven-url]: https://maven.apache.org/

