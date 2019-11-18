package lambda.dsl.courses

import java.text.Normalizer

trait Ids {
  def slug(title: String): String =
    Normalizer.normalize(title, Normalizer.Form.NFD)
      .replaceAll("[^\\w\\s-]", "")
      .replace('-', ' ')
      .trim
      .replaceAll("\\s+", "-")
      .toLowerCase

  def widgetId(prefix: String, pos: Int): String = s"$prefix--w$pos"
}
