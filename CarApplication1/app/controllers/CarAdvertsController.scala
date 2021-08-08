package controllers


import models.{CarAdvert, OrderingDirection}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}
import io.circe._
import io.circe.jawn.decode
import javax.inject.{Inject, Singleton}
import scala.collection.mutable

/*
* CarAdvertController controls the handling of the CarAdvert model
* Created in singleton so that the list will remain throughout the runtime of the project until rerun or rebuilding
* */
@Singleton
class CarAdvertsController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  implicit val todoListJson = Json.format[CarAdvert]
  //declaring a carList that will hold carAdverts data
  private val carList = new mutable.ListBuffer[CarAdvert]()//creating out CarAdvert list storage
  //CarAdvert defined instances
  carList += CarAdvert(88, "Title for advert 88", "diesel", 20000, false, 1453, "2014-05-05")
  carList += CarAdvert(199, "Car advert 199", "gasoline", 19000, true, 0, "2015-05-05")
  carList += CarAdvert(200, "car advert 200", "gasoline", 19500, true, 7, "2014-05-06")
  /*
  * Checks to see if the id inserted has no occurrences in the storage list of carAdvert id values.
  * */
  def isCarAdvertUnique (carAdvert: CarAdvert) : Boolean = {
    var isUnique = true
    carList.foreach {
      book => if(book.id == carAdvert.id) { isUnique = false }//comparing each list value with the given id
    }
    isUnique
  }
  //Sorting the list of carAdverts based on the attribute and direction of order
  def sortListBasedOnParam(sortby: String, order: String): Action[AnyContent] = Action {
    val sortDirection = order.toLowerCase()//lowercasing user input
    val descending = OrderingDirection.orderingType(order)//associating the string input with the OrderDirection enum type
    if(descending != null) {//checks if user ordering string is defined properly
      sortby.toLowerCase() match {//now associating the attribute with the ordering algorithm
        case "id" =>
          if (OrderingDirection.descending == descending) {//if user has requested that the attributes be ordered in descending order
            val sortedRatings = carList.sortWith(_.id > _.id)
            Ok(Json.toJson(sortedRatings))
          } else {//if user has requested that the attributes be ordered in ascending order
            val sortedRatings = carList.sortWith(_.id < _.id)
            Ok(Json.toJson(sortedRatings))
          }
        case "title" =>
          if (OrderingDirection.descending == descending) {
            val sortedRatings = carList.sortWith(_.title > _.title)
            Ok(Json.toJson(sortedRatings))
          } else {
            val sortedRatings = carList.sortWith(_.title < _.title)
            Ok(Json.toJson(sortedRatings))
          }
        case "fueltype" =>
          if (OrderingDirection.descending == descending) {
            val sortedRatings = carList.sortWith(_.fuelType > _.fuelType)
            Ok(Json.toJson(sortedRatings))
          } else {
            val sortedRatings = carList.sortWith(_.fuelType < _.fuelType)
            Ok(Json.toJson(sortedRatings))
          }
        case "price" =>
          if (OrderingDirection.descending == descending) {
            val sortedRatings = carList.sortWith(_.price > _.price)
            Ok(Json.toJson(sortedRatings))
          } else {
            val sortedRatings = carList.sortWith(_.price < _.price)
            Ok(Json.toJson(sortedRatings))
          }
        case "mileage" =>
          if (OrderingDirection.descending == descending) {
            val sortedRatings = carList.sortWith(_.mileage > _.mileage)
            Ok(Json.toJson(sortedRatings)+"me")
          } else {
            val sortedRatings = carList.sortWith(_.mileage < _.mileage)
            Ok(Json.toJson(sortedRatings)+"you")
          }
        case "firstregistration" =>
          if (OrderingDirection.descending == descending) {
            val sortedRatings = carList.sortWith(_.firstRegistration > _.firstRegistration)
            Ok(Json.toJson(sortedRatings))
          } else {
            val sortedRatings = carList.sortWith(_.firstRegistration < _.firstRegistration)
            Ok(Json.toJson(sortedRatings))
          }
      }
    }else{
      BadRequest("")
    }
  }
  //finds carAdvert based on its unique id
  def findCarAdvertById (id: Int) : CarAdvert = {
    if (carList.isEmpty) {
        null
    }else {
      val car_advert = carList.find(_.id == id)//find the CarAdvert object based on the associated id attribute
      if(car_advert.isEmpty){
        null
      }else{
        car_advert.get//returning a carAdvert object from the list
      }
    }
  }
  //printing out the entire storage list of CarAdverts
  def getAllCarAdverts = Action {
      Ok(Json.toJson(carList))
  }
//get car advert based on the associated id.
  def getCarAdvertByID(id: Int) = Action {
    if (carList.isEmpty) {
      NotFound("List is empty. No car adverts exist")
    } else {
      val car_advert = carList.find(_.id == id)//find the CarAdvert object based on the associated id attribute
      if(car_advert.isEmpty){
        NotFound("No car advert with given id was found.")
      }else{
        Ok(Json.toJson(car_advert))
      }
    }
  }

  //creating a carAdvert
  def addCarAdvert()= Action(parse.json) { implicit request: Request[JsValue] =>
    val parsedResult: Either[ParsingFailure, Json] = io.circe.parser.parse(request.body.toString)
    parsedResult match {
      case Left(parsingError) =>
        BadRequest(s"Validation failed: ${parsingError.message}")
      case Right(json) =>
        val carAdvertDecoded = decode[CarAdvert](json.toString())
        carAdvertDecoded match {
          case Right(decodedJson) =>
            val carAdvert: CarAdvert = decodedJson
            if(carAdvert.areValuesPositive()){//checking if int values are correct format
              //if caradvert is unique and has correct params add them to the carList
              if(this.isCarAdvertUnique(carAdvert)){//checking to see if the new carAdvert id is unique
                carList.addOne(carAdvert)//adding car advert to list
                Created(CarAdvert.carAdvertEncoder(carAdvert)+"")//return newly added carAdvert in list
              }else{
                BadRequest("")
              }
            }
            else{
              UnprocessableEntity("Validation failed. Id, price or mileage cannot be negative values")
            }
        }
          //checking if Int inputs are positive


    }
  }
//updating caradvert data
  def updateCarAdvert(id: Int)= Action(parse.json) { implicit request: Request[JsValue] =>
    //val bodyAsJson = request.body.validate[CarAdvert]//validating tha the json has the same key/value pairs as the CarAdvert object
    val parsedResult: Either[ParsingFailure, Json] = io.circe.parser.parse(request.body.toString)
    parsedResult match {
      case Left(parsingError) =>
        BadRequest(s"Validation failed: ${parsingError.message}")
      case Right(json) =>
        val carAdvertDecoded = decode[CarAdvert](json.toString())
        carAdvertDecoded match {
          case Right(decodedJson) =>
            if(decodedJson.areValuesPositive()){//checking if int values are correct format
              val newCarAdvert: CarAdvert = decodedJson
              //if caradvert is unique and has correct params add them to the carList
              if(!this.isCarAdvertUnique(newCarAdvert)){//checking to see if the carAdvert that needs updating exists in the list
                val getOldCarAdvertIndex: Int = carList.indexOf(this.findCarAdvertById(id)) //get the corresponding carAdvert by its id in list
                carList.update(getOldCarAdvertIndex, newCarAdvert)//update old carAdvert with the newer one
                Created(CarAdvert.carAdvertEncoder(newCarAdvert)+"")//print out the updated CarAdvert
              }else{
                BadRequest("")
              }
            }
            else{
              UnprocessableEntity("Validation failed. Id, price or mileage cannot be negative values")
            }
        }
        //checking if Int inputs are positive

    }
  }
//removing car advert
  def deleteCarAdvert(id: Int) = Action{
    if (carList.isEmpty) {
      NotFound("List is empty. No car adverts exist to be deleted")
    } else {
      val result :CarAdvert = this.findCarAdvertById(id)//checking to see if carAdvert exists for deletion
      if(result == null){
        NotFound("This is returned if a car advert with given id is not found.")
      }else{
        carList.remove(carList.indexOf(result))//removing specified carAdvert from list
        NoContent
      }
    }
  }

}
