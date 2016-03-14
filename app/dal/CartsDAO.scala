package dal

import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CartsDAOImpl])
trait CartsDAO {
  def add(cartItem: CartItem): Future[Unit]
  def list(): Future[Seq[CartItem]]
  def clear(cartId: String): Future[Int]
}

@Singleton
class CartsDAOImpl @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
extends CartsDAO
{

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import driver.api._
  private val carts = TableQuery[CartsTable]

  private class CartsTable(tag: Tag) extends Table[CartItem](tag, "carts") {
    def cartId = column[String]("cartId")
    def productName = column[String]("productName")
    //def * = (id, cartId, productId) <> ((CartItem.apply _).tupled, CartItem.unapply)
    def * = (cartId, productName) <>((CartItem.apply _).tupled, CartItem.unapply)
  }

  def add(cartItem: CartItem): Future[Unit] =
    db.run(carts += cartItem).map { _ => () }

  def list(): Future[Seq[CartItem]] = db.run {
    carts.result
  }

  def clear(cartId: String): Future[Int] = db.run(
    carts.filter(_.cartId === cartId).delete
  )
}



//def * : ProvenShape[(String, String)] =
//(id, productId)
//def id = column[Long]("id", O.PrimaryKey, O.AutoInc)



/*
def addProduct(v1: Long, v2: String, v3: Int) = db.run {
   (carts.map(p => (p.cartId, p.productId)
    returning carts.map(_.id)
    //into ((a2, id) => CartItem(a2., a3))
    ) += (v2, v3)
}*/
// def addCartItem(id1: String, id2: String): Future[CartItem] =


//  def insert(cat: Cat): Future[Unit] = db.run(Cats += cat).map { _ => () }


