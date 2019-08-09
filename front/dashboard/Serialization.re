open Types;

type decoder('a) = Js.Json.t => 'a;

module Decode = {
  exception WrongWidgetType;
  exception WrongLanguage;
  exception WrongPageType;

  let nothing = _json => Nothing;

  let language = language =>
    language
    |> Json.Decode.string
    |> (
      fun
      | "scala2" => Scala2
      | _ => raise(WrongLanguage)
    );

  let courseManifest = json =>
    Json.Decode.{
      id: json |> field("id", string),
      title: json |> field("title", string),
      description: json |> field("description", string),
      tags: json |> field("tags", list(string)),
    };

  let courseManifests = Json.Decode.list(courseManifest);

  module Widget = {
    let decodeMultipleChoices = json: Widget.MultipleChoices.t => {
      let proposition = json: Widget.MultipleChoices.proposition =>
        Json.Decode.{
          id: json |> field("id", int),
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

    let decodeMarkdownText = json: Widget.markdownText =>
      Json.Decode.{
        id: json |> field("id", string),
        content: json |> field("content", string),
      };

    let decodeInteractiveCode = json: Widget.interactiveCode =>
      Json.Decode.{
        id: json |> field("id", string),
        defaultValue: json |> field("defaultValue", string),
        language: json |> field("language", language),
        required: json |> field("required", bool),
      };

    let decode = json: Widget.t =>
      json
      |> Json.Decode.field("type", Json.Decode.string)
      |> (
        fun
        | "markdownText" => Widget.MarkdownText(decodeMarkdownText(json))
        | "multipleChoices" =>
          Widget.MultipleChoices(decodeMultipleChoices(json))
        | "scala2Code" => Widget.InteractiveCode(decodeInteractiveCode(json))
        | _ => raise(WrongWidgetType)
      );
  };

  module WidgetOutput = {
    open WidgetOutput;

    let decode = _json => RightAnswer;
  };

  module WidgetError = {
    open WidgetError;

    let decode = _json => WrongAnswer;
  };

  module Page = {
    let simplePage = json: Page.simplePage =>
      Json.Decode.{
        id: json |> field("id", string),
        title: json |> field("title", string),
        widgets: json |> field("widgets", list(Widget.decode)),
      };

    let codePage = json: Page.codePage =>
      Json.Decode.{
        id: json |> field("id", string),
        title: json |> field("title", string),
        widgets: json |> field("widgets", list(Widget.decode)),
        code: json |> field("code", Widget.decodeInteractiveCode),
      };

    let decode = json: Page.t =>
      json
      |> Json.Decode.field("type", Json.Decode.string)
      |> (
        fun
        | "simplePage" => Page.SimplePage(simplePage(json))
        | "codePage" => Page.CodePage(codePage(json))
        | _ => raise(WrongPageType)
      );
  };

  let course = json =>
    Json.Decode.{
      id: json |> field("id", string),
      title: json |> field("title", string),
      description: json |> field("description", string),
      tags: json |> field("tags", list(string)),
      pages: json |> field("pages", list(Page.decode)),
    };

  let courses = Json.Decode.list(course);
};

module Encode = {
  let language =
    fun
    | Scala2 => Json.Encode.string("scala2");
  module WidgetInput = {
    open WidgetInput;

    let multipleChoicesInput = (input: multipleChoicesInput) =>
      Json.Encode.(object_([("answerId", int(input.answerId))]));

    let codeInput = (input: codeInput) =>
      Json.Encode.(
        object_([
          ("code", string(input.code)),
          ("language", language(input.language)),
        ])
      );

    let encode =
      fun
      | MultipleChoicesInput(input) => multipleChoicesInput(input)
      | CodeInput(input) => codeInput(input);
  };
};
