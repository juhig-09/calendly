## Verify the project
1. Clone the repo 
2. Ensure docker is installed
3. Run ```docker compose up```

This will run all the required service in docker. Access the service endpoints by Hit the Swagger UI and run the Controller there: http://localhost:8080/swagger-ui/index.html

## Build the project

Note some limitations:

- This project uses **Java version 17**.
- The gradle wrapper pins the gradle version at 8.8.

```./gradlew clean build```

## Run the Application

Before we can run any of the applications, we have two run the following steps:

#### Run Docker Compose

This will start the necessary infra docker containers:

```docker-compose up```

#### Run Liquibase

This will create the appropriate collections along with the indices

```./gradlew calendly-api:runLiquibase```

Now, we are ready to run the applications:

### calendly-calendar-api

```./gradlew calendly-api:bootRun```

### Verify Locally

Hit the **Swagger** UI and run the Controller there: http://localhost:8080/swagger-ui/index.html


### What all a user can do?

- Signup using `UserController POST /signup` 
  - providing name, email, timezone, profile [`PUBLIC`, `PRIVATE`]

- Update name, timezone, profile preference using `UserController PATCH`


- Perform CRUD on user.


- Provide availability using `SchedulesController POST /`
  - providing email, date `2024-12-05`, availableTimes `{"startTime": "10:20:25","endTime": "12:20:25" }`, timezone (Default is user timezone provided during user creation)


- Perform CRUD on schedule.


- Schedule meetings using `MeetingController POST /`
  - provide timezone (Default is user timezone provided during user creation.
  - provide meeting link, meeting type
  - Can have required invitees - These invitees must be available during meeting hours to create the meeting successfully.
  - Can have optional invitees - These invitees availability is not mandatory.


- Perfrom CRUD on meeting scheduled - change time, date, invitees list


- Accept/Reject a meeting using `UsersMeetingsController PATCH /`
  - Can only accept a meeting if availability matches with meeting hours.
  - Can reject a meeting. If meeting is rejected - availability is added back for the user.
  

- Read the meetings.

As there is no authentication involved yet - the `{email}` path param is used in APIs.
