package dal


import javax.inject.{ Inject, Singleton }
import com.google.inject.ImplementedBy
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.concurrent.{Future, ExecutionContext}
import models.Product

/**
  * Created by mbn2671 on 2/19/16.
  */

@ImplementedBy(classOf[ProductsDAOImpl])
trait ProductsDAO {
  def add(name: String, price: Int): Future[Product]
  def list(): Future[Seq[Product]]
}

@Singleton
class ProductsDAOImpl @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
extends ProductsDAO {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class ProductsTable(tag: Tag) extends Table[Product](tag, "products") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def price = column[Int]("price")
    def * = (id, name, price) <> ((Product.apply _).tupled, Product.unapply)
  }

  def add(name: String, price: Int): Future[Product] = db.run {
    (products.map(p => (p.name, p.price))
      returning products.map(_.id)

      // TODO: Refactor
      into ((namePrice, id) => Product(id, namePrice._1, namePrice._2))
      ) += (name, price)
  }

  private val products = TableQuery[ProductsTable]

  def list(): Future[Seq[Product]] = db.run {
    products.result
  }

}


