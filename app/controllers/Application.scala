package controllers

import javax.inject.Inject

import dal.ProductsDAO
import play.api.mvc._
import play.api.i18n._
import services.{ProductsService, CartsService}
import scala.concurrent.{Future, ExecutionContext}
import play.api.data.validation.Constraints._
import models._
import dal._

class Application @Inject()(productService: ProductsService,
                            cartService: CartsService,
                            val messagesApi: MessagesApi)(implicit ec: ExecutionContext)
  extends Controller with I18nSupport {

  import play.api.data.Form
  import play.api.data.Forms._

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "price" -> number.verifying(min(0), max(40000))
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  def addProduct = Action.async { implicit request =>
    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.index(List())))
      },
      product => { //for product passed in POST request, add it to the db
        productService.addProduct(product.name, product.price).map { _ =>
          Redirect(routes.Application.showBuyWindow)
        }
      }
    )
  }

  def getAllProducts = Action.async {
    productService.listAllProducts().map(
      products => Ok(views.html.index(products))
    )
  }

  def index = Action {
    Ok(views.html.addProduct(productForm))
  }

  def showBuyWindow =  Action.async {
    //TODO:Refactor
    val x: Future[Seq[Product]] = productService.listAllProducts()
    val x2: Future[Seq[(String, String)]] = x.map(y => y.map(z => (z.name, z.name)))

    //val k: Future[Seq[CartImtem]] = cartRepo.list()
    //val x3: Fut = x2.flatMap(x => x)
    x.map(
      products => Ok(views.html.addToCart(products, List(), selectProductsForm))
    )
  }

  def purchaseProduct = Action.async { implicit request =>
    println("Hit addCartItem")
    selectProductsForm.bindFromRequest.fold(
      errorForm => {
        println(errorForm)
        Future.successful(Ok(views.html.index(List())))
      },
      cartItemForm => {
        println("Cart item added - "  +" " + cartItemForm.productName)
        //for product passed in POST request, add it to the db
        // TODO: replace 1 with cookie value
        cartService.addCartItem("1", cartItemForm.productName).map ( _ =>
          Redirect(routes.Application.showCart)
        )
      }
    )
  }

  def showCart = Action.async {
    cartService.listAllCartItems().map(
      cartItems => Ok(views.html.showCart(cartItems))
    )
  }

  def clearCart = Action.async {
    implicit request =>

    //TODO: replace 1 with cookie value
      cartService.clearCart("1").map( _ =>
      Redirect(routes.Application.showCart)
    )
  }



  val selectProductsForm: Form[SelectProductsForm] = Form {
    mapping(
      //"cartId" -> nonEmptyText,
      "productName" -> nonEmptyText
    )(SelectProductsForm.apply)(SelectProductsForm.unapply)
  }

}

case class CreateProductForm(name: String, price: Int)
case class SelectProductsForm(productName: String)



