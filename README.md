# Scala Scheduling App

## Overview

This project is a minimal scheduling application implemented in Scala. It provides a simple API for creating persons, scheduling meetings, viewing schedules, and suggesting available timeslots for meetings.

## Features

- Create persons with a name and unique email.
- Schedule one-hour meetings involving one or more persons.
- View the schedule of a person.
- Suggest available timeslots for a group of persons.

## Getting Started

### Prerequisites

- Scala 2
- sbt (Scala Build Tool)

### Installation

1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Compile the project using `sbt compile`.
4. Run the application using `sbt run`.

## Usage

Here's a quick example of how to use the scheduling app:

```scala
val main = new Main
main.createPerson("John Doe", "john.doe@example.com")
main.createMeeting(List("john.doe@example.com"), 1000)
main.showSchedule("john.doe@example.com")
main.suggestTimeslots(List("john.doe@example.com", "jane.doe@example.com"))
```

## Running Tests

Tests are written using the ScalaTest framework. To run the tests, use the `sbt test` command in the project directory.
