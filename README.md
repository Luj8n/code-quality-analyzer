# Code Quality Analyzer

This application analyzes Java files within a specified directory, reporting function complexity and basic code style checks.

## Functionality

### Code Complexity Evaluator

- Analyzes Java files within a specified directory.
- Calculates the complexity of methods/functions based on the number of conditional statements (if, switch, for, while, etc.).
- Outputs the names of the three methods/functions with the highest complexity scores, along with their complexity scores.

### Basic Code Style Check

- Performs a simple code style check to ensure that all method names follow a specified naming convention (camelCase).
- Reports the percentage of methods that do not adhere to the naming convention.

## How to Run

- Clone the repository:

```bash
git clone https://github.com/Luj8n/code-quality-analyzer
```

- Navigate to the project directory:

```bash
cd code-quality-analyzer
```

### Using any IDE

- Add a command line argument for the directory path to analyze
- Run the src/Main.java

### Using a command line

1. **Compile**: Compile the the project using a Java compiler.

   ```bash
   javac -sourcepath src/ -d build/ src/Main.java

   ```

2. **Execute**: Run the compiled program with the directory path to analyze as a command line argument.

   ```bash
   java -cp build/ Main <directory_path>
   ```

   Replace `<directory_path>` with the path to the directory containing Java files to analyze.

   Example:

   ```bash
   java -cp build/ Main ./
   ```

   Output:

   ```
   24 functions found

   Highest complexity scores:
   Score = 6, function = findBracketRange(JavaFile.java:185)
   Score = 5, function = getFunctions(JavaFile.java:63)
   Score = 4, function = analyzeFunctions(Main.java:37)

   Not camelCase: 0.00%
   ```

## Approach

- The application recursively traverses the specified directory to find Java files.
- It extracts functions from each Java file by:
  - Tokenizing the Java code using `StringTokenizer`.
  - Iteratively going through the tokens and checking if the current token can be a function name (also checks the surrounding tokens).
  - Determining where the function body begins and ends.
- Calculates the complexity of each function.
  - Complexity is determined by counting the number of conditional statements within a function body.
- Results are sorted based on complexity, and the top three functions are displayed.
- Additionally, it calculates how many method names follow the camelCase convention.

## To improve

Currently the application uses many `if` statements to check if a token is a Java function name. However, there might be some edge cases where it fails to detect a function or thinks that a non-function (like a constructor or a record) is a function. The solution would be to construct an AST (abstract syntax tree).
