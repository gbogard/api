type nothing =
  | Nothing;

type result('a, 'b) =
  | NotAsked
  | Loading
  | Failed
  | ClientError('b)
  | Success('a);
type user = {
  id: string,
  username: string,
};

type language =
  | Scala2;

module Widget = {
  type markdownText = {
    id: string,
    content: string,
  };

  module MultipleChoices = {
    type proposition = {
      id: int,
      value: string,
    };

    type question = {
      value: string,
      rightAnswer: proposition,
      otherPropositions: list(proposition),
    };

    type t = {
      id: string,
      required: bool,
      question,
    };
  };

  type interactiveCode = {
    id: string,
    defaultValue: string,
    required: bool,
    language,
  };

  type t =
    | MarkdownText(markdownText)
    | MultipleChoices(MultipleChoices.t)
    | InteractiveCode(interactiveCode);
};

module WidgetInput = {
  type multipleChoicesInput = {answerId: int};
  type codeInput = {
    code: string,
    language,
  };

  type t =
    | MultipleChoicesInput(multipleChoicesInput)
    | CodeInput(codeInput);
};

module WidgetOutput = {
  type t =
    | RightAnswer
    | CodeOutput(string);
};

module WidgetError = {
  type t =
    | WrongAnswer
    | CodeOutput(string);
};

type widgetState =
  | Initial
  | Pending
  | Right(WidgetOutput.t)
  | Wrong(WidgetError.t);

module Page = {
  type simplePage = {
    id: string,
    title: string,
    widgets: list(Widget.t),
  };

  type codePage = {
    id: string,
    title: string,
    widgets: list(Widget.t),
    code: Widget.interactiveCode,
  };

  type t =
    | SimplePage(simplePage)
    | CodePage(codePage);
};

type courseManifest = {
  id: string,
  title: string,
  description: string,
  image: option(string),
  tags: list(string),
};

type course = {
  id: string,
  title: string,
  description: string,
  tags: list(string),
  pages: list(Page.t),
};
