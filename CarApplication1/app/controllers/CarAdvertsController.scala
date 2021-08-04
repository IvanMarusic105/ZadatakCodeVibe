package controllers


import models.CarAdvert
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, ControllerComponents, Request}
//import io.circe._//vraća error cannot resolve symbol circe

import javax.inject.{Inject, Singleton}
import scala.collection.mutable

@Singleton
class CarAdvertsController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  implicit val todoListJson = Json.format[CarAdvert]
  //declaring a list that will hold carAdverts data
  private val list = new mutable.ListBuffer[CarAdvert]()
  list += CarAdvert(88, "Title for advert 88", "diesel", 20000, false, 1453, "2014-05-05")
  list += CarAdvert(199, "car advert 199", "gasoline", 19000, true, 0, "")

  def isCarAdvertUnique (carAdvert: CarAdvert) : Boolean = {
    var isUnique = true
    list.foreach {
      book => if(book.id == carAdvert.id) { isUnique = false }
    }
    return isUnique
  }

  def findCarAdvertById (id: Int) : CarAdvert = {
    if (list.isEmpty) {
        null
    }else {
      val car_advert = list.find(_.id == id)
      if(car_advert.isEmpty){
        null
      }else{
        car_advert.get
      }
    }
  }

  def getAllCarAdverts = Action {
      Ok(Json.toJson(list))
  }

  def getCarAdvertByID(id: Int) = Action {
    if (list.isEmpty) {
      NotFound("List is empty. No car adverts exist")
    } else {
      val car_advert = list.find(_.id == id)
      if(car_advert.isEmpty){
        NotFound("No car advert with given id was found.")
      }else{
        Ok(Json.toJson(car_advert))
      }
    }
  }

  def addCarAdvert()= Action(parse.json) { implicit request: Request[JsValue] =>
    val bodyAsJson = request.body.validate[CarAdvert]

    bodyAsJson match {
      case success: JsSuccess[CarAdvert] => {
        //checking if Int inputs are positive
        if(success.get.areValuesPositive()){
          val carAdvert: CarAdvert = success.get
          //if caradvert is unique and has correct params add them to the list
          if(this.isCarAdvertUnique(carAdvert)){
            list.addOne(carAdvert)
            Created(Json.toJson(carAdvert)).as("application/json")
          }else{
            BadRequest("")
          }
        }
        else{
          UnprocessableEntity("Validation failed. Id, price or mileage cannot be negative values")
        }
      }
      case JsError(error) => BadRequest("This is returned if json is invalid or cannot be parsed.")
    }
  }

  def updateCarAdvert(id: Int)= Action(parse.json) { implicit request: Request[JsValue] =>
    val bodyAsJson = request.body.validate[CarAdvert]

    bodyAsJson match {
      case success: JsSuccess[CarAdvert] => {
        //checking if Int inputs are positive
        if(success.get.areValuesPositive()){
          val newCarAdvert: CarAdvert = success.get
          //if caradvert is unique and has correct params add them to the list
          if(!this.isCarAdvertUnique(newCarAdvert)){
            var getOldCarAdvertIndex: Int = list.indexOf(this.findCarAdvertById(id))//index returning 0
            list.update(getOldCarAdvertIndex, newCarAdvert)
            Created(Json.toJson(newCarAdvert)).as("application/json")
          }else{
            BadRequest("")
          }
        }
        else{
          UnprocessableEntity("Validation failed. Id, price or mileage cannot be negative values")
        }
      }
      case JsError(error) => BadRequest("This is returned if json is invalid or cannot be parsed.")
    }
  }

  def deleteCarAdvert(id: Int) = Action{
    if (list.isEmpty) {
      NotFound("List is empty. No car adverts exist to be deleted")
    } else {
      val result :CarAdvert = this.findCarAdvertById(id)
      if(result == null){
        NotFound("This is returned if a car advert with given id is not found.")
      }else{
        list.remove(list.indexOf(result))
        NoContent
      }
    }
  }

}
