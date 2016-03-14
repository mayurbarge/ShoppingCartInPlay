package services

/**
  * Created by mbn2671 on 2/22/16.
  */
import javax.inject.Inject
import com.google.inject.ImplementedBy
import dal.{ProductsDAO, CartsDAO}
import models._
import scala.concurrent.{Future, ExecutionContext}

@ImplementedBy(classOf[ProductsServiceImpl])
trait ProductsService {

  def addProduct(name: String, price: Int): Future[Product]
  def listAllProducts(): Future[Seq[Product]]
}

class ProductsServiceImpl @Inject()(productsDao: ProductsDAO)
                                (implicit ec: ExecutionContext)
  extends ProductsService {

  override def addProduct(name: String, price: Int): Future[Product] =
  productsDao.add(name, price)

  override def listAllProducts(): Future[Seq[Product]] =
  productsDao.list()

}
