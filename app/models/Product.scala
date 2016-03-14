package models

import play.api.libs.json._

case class Product(id: Long, name: String, price: Int)

object Product {

  implicit val personFormat = Json.format[Product]
}

