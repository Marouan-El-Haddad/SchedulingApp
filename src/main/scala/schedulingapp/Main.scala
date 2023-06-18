package schedulingapp

case class Person(
    name: String,
    email: String,
    schedule: Map[Int, String] = Map()
)
case class Meeting(participants: List[String], startTime: Int)

sealed trait Result[+A]
case class Success[A](value: A) extends Result[A]
case class Failure(reason: String) extends Result[Nothing]

class Main {
  var persons: Map[String, Person] = Map()

  def createPerson(name: String, email: String): Result[Person] = {
    if (name.isEmpty || email.isEmpty) {
      Failure("Name or email cannot be empty.")
    } else if (persons.contains(email)) {
      Failure("Email already exists.")
    } else {
      val person = Person(name, email)
      persons = persons + (person.email -> person)
      Success(person)
    }
  }

  def createMeeting(emails: List[String], startTime: Int): Result[Meeting] = {
    if (startTime % 100 != 0 || startTime < 0 || startTime > 2300) {
      Failure("Invalid start time.")
    } else {
      val participants = emails.flatMap(email => persons.get(email))
      if (participants.length != emails.length) {
        Failure("Some participants do not exist.")
      } else if (
        participants.exists(person => person.schedule.contains(startTime))
      ) {
        Failure("Meeting time conflicts with an existing meeting.")
      } else {
        val meeting = Meeting(emails, startTime)
        participants.foreach(person => {
          val updatedPerson = person.copy(schedule =
            person.schedule + (startTime -> meeting.startTime.toString)
          )
          persons = persons.updated(person.email, updatedPerson)
        })
        Success(meeting)
      }
    }
  }

  def showSchedule(email: String): Result[List[Int]] = {
    persons.get(email) match {
      case Some(person) => Success(person.schedule.keys.toList.sorted)
      case None         => Failure("Person does not exist.")
    }
  }

  def suggestTimeslots(emails: List[String]): Result[List[Int]] = {
    val participants = emails.flatMap(email => persons.get(email))
    if (participants.length != emails.length) {
      Failure("Some participants do not exist.")
    } else {
      val occupiedTimeslots = participants.flatMap(_.schedule.keys).toSet
      val availableTimeslots =
        (0 to 2300 by 100).filterNot(occupiedTimeslots.contains)
      Success(availableTimeslots.toList)
    }
  }
}
object Main extends App {
  val main = new Main
  main.createPerson("John Doe", "john.doe@example.com")
  main.createMeeting(List("john.doe@example.com"), 1000)
  main.showSchedule("john.doe@example.com")
  main.suggestTimeslots(List("john.doe@example.com", "jane.doe@example.com"))
}
