package models
/*
* OrderingDirection enum is used to specially define the ordering defined by the string inputed.
* Takes in a string with either ascending or descending string words to associate them with correct order type
* */

object OrderingDirection extends Enumeration {
  type Order = Value
  val ascending, descending = Value//only two orderings are difined
  /*
  * Compare the string with its respective ordering type
  * */
  def orderingType( f : String) : Order = f.toLowerCase() match {
    case "ascending" => OrderingDirection.ascending//if string contains ascending associate it with the order ascending type
    case "descending" => OrderingDirection.descending//if string contains descending associate it with the order descending type
  }
}