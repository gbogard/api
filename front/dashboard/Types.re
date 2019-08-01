type result('a) =
  | NotAsked
  | Loading
  | Failed
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
      id: string,
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
  tags: list(string),
};

type course = {
  id: string,
  title: string,
  description: string,
  tags: list(string),
  pages: list(Page.t),
};
