package models
import play.api.libs.json.Json

/*import models.FuelType.FuelType

object FuelType extends Enumeration {
  type FuelType = Value
  val diesel, gasoline = Value

  def isSomething( f : String) : FuelType = f.toLowerCase() match {
    case "diesel" => FuelType.diesel
    case "gasoline" => FuelType.gasoline
  }
}*/

case class CarAdvert(id: Int, title: String, fuelType: String,
                     price: Int, isNew: Boolean, mileage: Int, firstRegistration: String) {

  def areValuesPositive () : Boolean = {
    if(this.price >= 0 && this.mileage >= 0 && this.id >= 0 ){
      true
    }else{
      false
    }
  }

}

object CarAdvert{
  implicit val carAdvertImplicitReads = Json.reads[CarAdvert]
  implicit val carAdvertImplicitWrites = Json.writes[CarAdvert]
}
