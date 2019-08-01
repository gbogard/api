open Types;

type decoder('a) = Js.Json.t => 'a;

module Decode = {
  exception WrongWidgetType;
  exception WrongLanguage;

  let courseManifest = json =>
    Json.Decode.{
      id: json |> field("id", string),
      title: json |> field("title", string),
      description: json |> field("description", string),
      tags: json |> field("tags", list(string)),
    };

  let courseManifests = Json.Decode.list(courseManifest);

  let course = json =>
    Json.Decode.{
      id: json |> field("id", string),
      title: json |> field("title", string),
      description: json |> field("description", string),
      tags: json |> field("tags", list(string)),
      pages: [],
    };

  let courses = Json.Decode.list(course);

  let language = language =>
    language
    |> Json.Decode.string
    |> (
      fun
      | "scala" => Scala2
      | _ => raise(WrongLanguage)
    );

  let widget = json: Widget.t => {
    let decodeMarkdownText = json: Widget.markdownText =>
      Json.Decode.{
        id: json |> field("id", string),
        content: json |> field("content", string),
      };

    let decodeMultipleChoices = json: Widget.MultipleChoices.t => {
      let proposition = json: Widget.MultipleChoices.proposition =>
        Json.Decode.{
          id: json |> field("id", string),
          value: json |> field("value", string),
        };

      let question = json: Widget.MultipleChoices.question =>
        Json.Decode.{
          value: json |> field("value", string),
          rightAnswer: json |> field("rightAnswer", proposition),
          otherPropositions:
            json |> field("otherPropositions", list(proposition)),
        };

      Json.Decode.{
        id: json |> field("id", string),
        required: json |> field("required", bool),
        question: json |> field("question", question),
      };
    };

    let decodeInteractiveCode = json: Widget.interactiveCode =>
      Json.Decode.{
        id: json |> field("id", string),
        defaultValue: json |> field("defaultValue", string),
        language: json |> field("language", language),
        required: json |> field("required", bool),
      };

    let widgetType = json |> Json.Decode.field("type", Json.Decode.string);
    switch (widgetType) {
    | "markdownText" => Widget.MarkdownText(decodeMarkdownText(json))
    | "multipleChoices" =>
      Widget.MultipleChoices(decodeMultipleChoices(json))
    | "interactiveCode" =>
      Widget.InteractiveCode(decodeInteractiveCode(json))
    | _ => raise(WrongWidgetType)
    };
  };
};
