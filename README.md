# 🎮 Wordle CLI

A command-line version of the  Wordle game, built with **Java 21**, **Spring Shell 3.4.0**, and modern best practices.

---

##  Overview

**Wordle CLI** brings the original word-guessing game to your terminal! Guess the hidden five-letter word in five 
attempts. Enjoy intuitive color-coded feedback, smart rules, and robust error handling.

---

## Features

**Gameplay:**
- Run the game with easy CLI commands
- Five attempts to guess a hidden five-letter English word
- All guesses must be exactly five alphabetic letters (no numbers or symbols allowed)
- Input is not case-sensitive, guesses are normalized
- No duplicate guesses within a game, the previous guesses are tracked
- Words not in the dictionary are allowed as guesses, as long as they fit the length and character rules

**Feedback & Game Logic:**
- 🟩 **Green**: Correct letter in the correct position
- 🟨 **Yellow**: Correct letter in the word but wrong position, never more than the letter count in the answer
- ⚫ **Gray**: Letter not present in the answer
- Feedback logic matches the original Wordle (duplicate letters handled correctly)

**Developer/CLI Features:**
- Colorful CLI feedback with clear messages
- Input validation and custom exceptions for several error types (I/O, empty/invalid word list, game state)
- Comprehensive logging for better debugging
- Docker support for easy deployment and execution in a containerized environment
- Modern Java codebase with **Lombok**, **JUnit**, **JaCoCo**, and a clean exception hierarchy

---

## ⚙️ Requirements

- **Java 21+**
- **Maven 3.8+**
- Developed and tested on **Ubuntu 22.04.5 LTS** (should work cross-platform)
- *(Optional)* Docker for containerized running

---

## ️ Installation

### 🔨 Install from Source

To install and run the Wordle CLI application from the source code, ensure you have Java (21+) and Maven (3.8+)
installed on your system.

1. **Clone and build:**

Open your terminal and use the commands above:

   ```bash
    git clone https://github.com/szlaura/wordle.git
    cd wordle
    mvn clean package
   ```

2. **Run the CLI Application:**

    ```bash
    java -jar target/wordle-0.0.1-SNAPSHOT.jar
    ```


### 🐳 Docker Usage

To use the Docker commands below, ensure you have Docker running on your system.

1. **Clone and build:**

    ```bash
    git clone https://github.com/szlaura/wordle.git
    cd wordle
    ```

2. **Build Docker image:**

    ```bash
    docker build -t wordle .
    ```

3. **Run the game:**

    ```bash
    docker run -it wordle
    ```

4. **Clean up Docker image:**

    ```bash
    docker rmi wordle
    ```

---

## 📁 Project Structure & Technologies

- **Maven 3.8.8**: Project build and dependency management.
- **Spring Shell 3.4.0**: Provides a robust, interactive command-line interface framework.
- **Lombok**: Simplifies Java code by auto-generating boilerplate such as getters, constructors, and more.
- **JUnit 5**: Framework for comprehensive unit testing.
- **JaCoCo**: Measures and reports code coverage during test runs.
- **Custom Exception Hierarchy**: Structured exception classes for fine-grained error handling and user-friendly messages.
- **SLF4J with Logback**: Flexible, high-performance logging for both runtime and error diagnostics.

---

## 🖥️ Commands & Example Session

### Available Commands

```bash
start            # Start a new game
guess <word>     # Submit your guess (replace <word> with your 5-letter guess)
info             # Show rules and available commands
exit             # Exit the CLI application
```

### Example

When you launch Wordle CLI, you’ll see an initial banner introducing the game and the rules.

```bash
-----------------------------------------------------------------------------------------------

 __        __            _ _         ____ _     ___   _             _
 \ \      / /__  _ __ __| | | ___   / ___| |   |_ _| | |__  _   _  | |    __ _ _   _ _ __ __ _
  \ \ /\ / / _ \| '__/ _` | |/ _ \ | |   | |    | |  | '_ \| | | | | |   / _` | | | | '__/ _` |
   \ V  V / (_) | | | (_| | |  __/ | |___| |___ | |  | |_) | |_| | | |__| (_| | |_| | | | (_| |
    \_/\_/ \___/|_|  \__,_|_|\___|  \____|_____|___| |_.__/ \__, | |_____\__,_|\__,_|_|  \__,_|
                                                            |___/

-----------------------------------------------------------------------------------------------

Welcome to Wordle CLI!
Guess the 5-letter word in 5 tries.
After each guess, colors show:
 - Green: correct letter and position
 - Yellow: correct letter, wrong position
 - Gray: letter not in the word
Type 'start' to begin a new game!
Type 'info' for available commands.

--------------------------------------
<shell> start
Game started, type 'guess <word>' to make a guess.

<shell> guess pizza
pizza   [feedback: color-coded]
You have 4 more attempts.

<shell> guess apple
apple 
Congratulations! You guessed the word!
Type 'start' for another game or 'exit' to quit.
```

---

## 🧪  Testing & Code Coverage

- JUnit 5 is used for comprehensive unit testing.
- JaCoCo is integrated for test coverage reporting.

### Run all tests
```bash
mvn test
```

### Generate code coverage report
```bash
mvn clean test jacoco:report
```
**Important:**
After running this command, JaCoCo will generate a coverage report at:
target/site/jacoco/index.html. Open that file in your browser to view the coverage report.

---

## 📝 License

This project is licensed under the [MIT License](./LICENSE).

Copyright © 2025 Laura Szekeres
