package schedulingapp

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MainSpec extends AnyFlatSpec with Matchers {
  // Test for creating a person with valid inputs
  "The createPerson method" should "return a Success result with the correct Person object when given valid inputs" in {
    val main = new Main
    val result = main.createPerson("John Doe", "john.doe@example.com")
    result shouldBe a[Success[_]]
    result.asInstanceOf[Success[Person]].value shouldEqual Person(
      "John Doe",
      "john.doe@example.com"
    )
    main.persons should contain key "john.doe@example.com"
  }

  // Test for creating a person with existing email
  it should "return a Failure result with the appropriate error message when the email already exists" in {
    val main = new Main
    main.createPerson("John Doe", "john.doe@example.com")
    val result = main.createPerson("Jane Doe", "john.doe@example.com")
    result shouldBe a[Failure]
    result.asInstanceOf[Failure].reason shouldEqual "Email already exists."
  }

  // Test for creating a meeting with valid inputs
  "The createMeeting method" should "return a Success result with the correct Meeting object and update the participants' schedules when given valid inputs" in {
    val main = new Main
    main.createPerson("John Doe", "john.doe@example.com")
    val result = main.createMeeting(List("john.doe@example.com"), 1000)
    result shouldBe a[Success[_]]
    result.asInstanceOf[Success[Meeting]].value shouldEqual Meeting(
      List("john.doe@example.com"),
      1000
    )
    main.persons("john.doe@example.com").schedule should contain(1000 -> "1000")
  }

  // Test for creating a meeting with invalid start time
  it should "return a Failure result with the appropriate error message when the start time is invalid" in {
    val main = new Main
    main.createPerson("John Doe", "john.doe@example.com")
    val result = main.createMeeting(List("john.doe@example.com"), 1030)
    result shouldBe a[Failure]
    result.asInstanceOf[Failure].reason shouldEqual "Invalid start time."
  }

  // Test for creating a meeting with non-existent participants
  it should "return a Failure result with the appropriate error message when some participants do not exist" in {
    val main = new Main
    main.createPerson("John Doe", "john.doe@example.com")
    val result = main.createMeeting(
      List("john.doe@example.com", "jane.doe@example.com"),
      1000
    )
    result shouldBe a[Failure]
    result
      .asInstanceOf[Failure]
      .reason shouldEqual "Some participants do not exist."
  }

  // Test for creating a meeting with conflicting meeting time
  it should "return a Failure result with the appropriate error message when the meeting time conflicts with an existing meeting" in {
    val main = new Main
    main.createPerson("John Doe", "john.doe@example.com")
    main.createMeeting(List("john.doe@example.com"), 1000)
    val result = main.createMeeting(List("john.doe@example.com"), 1000)
    result shouldBe a[Failure]
    result
      .asInstanceOf[Failure]
      .reason shouldEqual "Meeting time conflicts with an existing meeting."
  }

  // Test for showing a person's schedule with valid email
  "The showSchedule method" should "return a Success result with the correct list of meeting times when given a valid email" in {
    val main = new Main
    main.createPerson("John Doe", "john.doe@example.com")
    main.createMeeting(List("john.doe@example.com"), 1000)
    val result = main.showSchedule("john.doe@example.com")
    result shouldBe a[Success[_]]
    result.asInstanceOf[Success[List[Int]]].value shouldEqual List(1000)
  }

  // Test for showing a person's schedule with invalid email
  it should "return a Failure result with the appropriate error message when the email does not exist" in {
    val main = new Main
    val result = main.showSchedule("john.doe@example.com")
    result shouldBe a[Failure]
    result.asInstanceOf[Failure].reason shouldEqual "Person does not exist."
  }

  // Test for suggesting timeslots for a meeting with valid participants
  "The suggestTimeslots method" should "return a Success result with the correct list of available timeslots when given a valid list of participants" in {
    val main = new Main
    main.createPerson("John Doe", "john.doe@example.com")
    main.createPerson("Jane Doe", "jane.doe@example.com")
    main.createMeeting(List("john.doe@example.com"), 1000)
    val result = main.suggestTimeslots(
      List("john.doe@example.com", "jane.doe@example.com")
    )
    result shouldBe a[Success[_]]
    val suggestedTimeslots = result.asInstanceOf[Success[List[Int]]].value
    suggestedTimeslots should not contain 1000
    suggestedTimeslots shouldEqual (0 to 2300 by 100)
      .filterNot(_ == 1000)
      .toList
  }

  // Test for suggesting timeslots for a meeting with invalid participants
  it should "return a Failure result with the appropriate error message when some participants do not exist" in {
    val main = new Main
    main.createPerson("John Doe", "john.doe@example.com")
    val result = main.suggestTimeslots(
      List("john.doe@example.com", "jane.doe@example.com")
    )
    result shouldBe a[Failure]
    result
      .asInstanceOf[Failure]
      .reason shouldEqual "Some participants do not exist."
  }
}
