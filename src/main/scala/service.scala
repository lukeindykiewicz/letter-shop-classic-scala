package lettershop.classicscala

trait PromotionService {
  storage: Storage =>

  def p(x: String) = prices.getOrElse(x, 10.0)

  val basePrice: String => Double = _.map(x => p(x.toString)).sum

  val threeForTwo: String => Double = { letters =>
    val promoLetter = Set('a', 'X')
    letters.toSeq
      .groupBy(x => x)
      .filter { case (c, cs) => promoLetter.contains(c) }
      .map { case (c, cs) => c -> cs.grouped(3).toList }
      .map { case (c, cs) => c -> cs.filter(_.size == 3) }
      .map { case (c, cs) => c -> cs.size }
      .map { case (c, cs) => c -> cs * p(c.toString) }
      .values
      .sum
  }

  def countThreeForTwo(letters: String): Double => Double =
    _ - threeForTwo(letters)

  val promo: Option[String] => Double =
    _.map(x => if (x == "10percent") 0.1 else 0.0).getOrElse(0.0)

  def countPromo(promoCode: Option[String]): Double => Double =
    _ * (1 - promo(promoCode))

  def promotions(letters: String, promoCode: Option[String]): Double => Double =
    countThreeForTwo(letters) andThen countPromo(promoCode)

  def finalPrice(letters: String, promoCode: Option[String]): Double =
    promotions(letters, promoCode)(basePrice(letters))
}

trait Storage {
  domain: Domain =>
  import scala.collection.concurrent.TrieMap

  def getCartLetters(cartId: String) = carts.getOrElse(cartId, "")
  var carts: TrieMap[String, String] = TrieMap.empty
  var prices: TrieMap[String, Double] = TrieMap.empty
  var receipts: TrieMap[String, ReceiptHistory] = TrieMap.empty
}
