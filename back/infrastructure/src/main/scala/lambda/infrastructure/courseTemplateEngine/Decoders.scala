package lambda.infrastructure.courseTemplateEngine

import cats.syntax.functor._
import io.circe._
import io.circe.generic.auto._
import lambda.domain.courses._
import lambda.domain.code.SourceFile

object Decoders {

  def decodeNestedField[A: Decoder](field: String) =
    Decoder.decodeJsonObject
      .map(_(field))
      .emap({
        case Some(json) => json.as[A].left.map(_.message)
        case None       => Left("Invalid widget type")
      })

  def multipleChoicesDecoder(id: WidgetId): Decoder[MultipleChoices] =
    decodeNestedField[widgets.Question]("question").map(
      q =>
        MultipleChoices(
          id,
          required = q.required.getOrElse(false),
          question = MultipleChoices.Question(
            q.title,
            rightAnswer = MultipleChoices.Answer(
              0,
              q.answer
            ),
            otherPropositions = q.propositions.zipWithIndex.map(
              item =>
                MultipleChoices.Answer(
                  item._2 + 1,
                  item._1
                )
            )
          )
        )
    )

  def scalaCodeWidgetDecoder(id: WidgetId): Decoder[SimpleScala2CodeWidget] =
    decodeNestedField[widgets.ScalaCodeWidget]("scala").map(
      s =>
        SimpleScala2CodeWidget(
          id,
          baseFiles = s.baseFiles
            .getOrElse(List("/templates/scala/WrapInMain.ssp"))
            .map(SourceFile.ClasspathResource(_)),
          mainClass = s.mainClass.getOrElse("Main"),
          dependencies = s.dependencies.getOrElse(Nil),
          required = s.required.getOrElse(false),
          defaultValue = s.defaultValue
        )
    )

  def widgetDecoder(id: WidgetId): Decoder[Widget] =
    List[Decoder[Widget]](
      multipleChoicesDecoder(id).widen,
      scalaCodeWidgetDecoder(id).widen
    ).reduce(_ or _)

}
