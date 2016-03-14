package models

import play.api.libs.json._

case class CartItem(cartId: String, productName: String)

object CartItem {

  implicit val cartItemFormat = Json.format[CartItem]

  //def apply(id: String, productId: String) = CartItem(id, productId)
}


