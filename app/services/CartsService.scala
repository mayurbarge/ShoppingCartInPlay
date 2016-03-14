package services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import dal.CartsDAO
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}
import models._


/**
  * Created by mbn2671 on 2/22/16.
  */
@ImplementedBy(classOf[CartsServiceImpl])
trait CartsService {
  def addCartItem(cartId: String, productName: String): Future[Unit]
  def listAllCartItems(): Future[Seq[CartItem]]
  def clearCart(cartId: String): Future[Int]
}

class CartsServiceImpl @Inject()(cartsDao: CartsDAO)
                                (implicit ec: ExecutionContext)
  extends CartsService {

  def addCartItem(cartId: String, productName: String): Future[Unit] = {
    cartsDao.add(CartItem(cartId, productName))
  }

  def listAllCartItems: Future[Seq[CartItem]] =
  cartsDao.list()

  def clearCart(cartId: String): Future[Int] =
  cartsDao.clear(cartId)
}
