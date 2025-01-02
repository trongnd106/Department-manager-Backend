# Department Manager Backend

## Introdution
Here is the backend source code for the Bluemoon apartment fee management software.

API Design: https://docs.google.com/spreadsheets/d/1-VWnJ0egj7uWib9meoAm_yRT35s7QjjlMHfRe_51vjo/edit?usp=sharing

Frontend project link: https://github.com/tmtuan04/department-manager-frontend

Detailed project report link: https://nlink.at/PG4P

## Installation
### System Requirements
Frontend:

Node.js: ≥ 16.0.0

npm: ≥ 8.0.0

Backend:

JDK: ≥ 17

Maven: ≥ 3.6.0

If running via Docker container: Simply install Docker.

### Detailed Installation Steps
*Option 1: Run Directly on the Machine*

Step 1: Run the Frontend

Execute the following commands:
```bash
cd frontend
npm install
npm start
```

Step 2: Run the Backend

Execute the following commands:
```bash
cd backend
mvn install
mvn spring-boot:run
```

Step 3: Run the Project

Open any web browser and navigate to localhost:5173 to run the program.

*Option 2: Use Docker*

Create the image using the repository link:
https://github.com/trongnd106/Bluemoon-department

Step 1: Build the project code

Run the following command:
```bash
docker-compose up --build
```

Step 2: Run the project

Open any web browser and navigate to localhost:5173 to run the program.
