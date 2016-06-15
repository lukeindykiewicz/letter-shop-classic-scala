package lettershop.classicscala

trait Domain {
  case class Cart(letters: String)
  case class Price(price: Double)
  case class Checkout(price: Double, receiptId: String)
  case class ReceiptHistory(price: Double, receiptId: String, letters: String)
}

trait DomainJson {
  domain: Domain =>

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
  import spray.json._

  object CartJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val CartFormats = jsonFormat1(Cart)
  }

  object PriceJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val PriceFormats = jsonFormat1(Price)
  }

  object CheckoutJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val CheckoutFormats = jsonFormat2(Checkout)
  }

  object ReceiptHistoryJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val ReceiptHistoryFormats = jsonFormat3(ReceiptHistory)
  }

}
