package lettershop.classicscala

import akka.actor._
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import java.util.UUID

object Main
    extends App
    with Routes
    with Domain
    with DomainJson
    with PromotionService
    with Storage {

  implicit val system = ActorSystem("foo")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
}

trait Routes {
  self: Domain with DomainJson with PromotionService with Storage =>

  lazy val route =
    pathPrefix("cart") {
      getCart ~ putCart ~ postCart
    } ~
      putPrice ~
      checkCart ~
      checkoutCart ~
      getReceipts

  import spray.json.DefaultJsonProtocol._
  implicit val cartJsonFormat = CartJsonSupport.CartFormats
  implicit val priceJsonFormat = PriceJsonSupport.PriceFormats
  implicit val checkoutJsonFormat = CheckoutJsonSupport.CheckoutFormats
  implicit val receiptHistoryFormat = ReceiptHistoryJsonSupport.ReceiptHistoryFormats

  lazy val getCart =
    get {
      path(Segment) { cartId =>
        complete(Cart(getCartLetters(cartId)))
      }
    }

  lazy val putCart =
    put {
      path(Segment / Segment) { (cartId, letters) =>
        carts += (cartId -> letters)
        complete(OK)
      } ~
        path(Segment) { cartId =>
          carts += (cartId -> "")
          complete(OK)
        }
    }

  lazy val postCart =
    post {
      path(Segment / Segment) { (cartId, letters) =>
        carts += (cartId -> (getCartLetters(cartId) + letters))
        complete(OK)
      }
    }

  lazy val putPrice =
    put {
      path("price" / Segment) { letter =>
        entity(as[Price]) { price =>
          prices += (letter -> price.price)
          complete(OK)
        }
      }
    }

  lazy val checkCart =
    get {
      path("check" / Segment) { cartId =>
        parameters("promo".?) { promoCode =>
          val letters = carts.getOrElse(cartId, "")
          complete(Price(finalPrice(letters, promoCode)))
        }
      }
    }

  lazy val checkoutCart =
    post {
      path("checkout" / Segment) { cartId =>
        parameters("promo".?) { promoCode =>
          val letters = carts.getOrElse(cartId, "")
          val uuid = UUID.randomUUID.toString
          val fPrice = finalPrice(letters, promoCode)
          receipts += cartId -> ReceiptHistory(fPrice, uuid, letters)
          carts -= cartId
          complete(Checkout(fPrice, uuid))
        }
      }
    }

  lazy val getReceipts =
    get {
      path("receipt") {
        complete(receipts.values.toList)
      }
    }

}
