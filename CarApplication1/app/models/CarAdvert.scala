package models
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import play.api.libs.json.Json
/*
* Our data object instance of CarAdvert with its associated values and attributes.
* */
case class CarAdvert(id: Int, title: String, fuelType: String,
                     price: Int, isNew: Boolean, mileage: Int, firstRegistration: String) {
  //validating that CarAdvert has no negative int values
  def areValuesPositive () : Boolean = {
    if(this.price >= 0 && this.mileage >= 0 && this.id >= 0 ){
      true
    }else{
      false
    }
  }

}

object CarAdvert{
  implicit val carAdvertDecoder: Decoder[CarAdvert] = deriveDecoder[CarAdvert]
  implicit val carAdvertEncoder: Encoder[CarAdvert] = deriveEncoder[CarAdvert]
}

