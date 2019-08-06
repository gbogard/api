open Types;
open WidgetInput;

module MultipleChoices = {
  [@react.component]
  let make =
      (
        ~widget: Widget.MultipleChoices.t,
        ~state: widgetState,
        ~onCheck: WidgetInput.t => unit,
      ) => {
    let (currentlySelectedAnswer, setCurrentlySelectedAnswer) =
      React.useState(() => None);

    let checkWidget = () =>
      switch (currentlySelectedAnswer) {
      | Some(id) => onCheck(MultipleChoicesInput({answerId: id}))
      | None => ()
      };

    <Box> {React.string(widget.question.value)} </Box>;
  };
};

module InteractiveCode = {
  [@react.component]
  let make = (~widget: Widget.interactiveCode) =>
    <Box> {React.string("Interactive code")} </Box>;
};

module MarkdownText = {
  [@react.component]
  let make = (~widget: Widget.markdownText) =>
    <div
      dangerouslySetInnerHTML={
        "__html": Interop.Marked.renderAndSanitize(widget.content),
      }
    />;
};

[@react.component]
let make =
    (
      ~widget: Types.Widget.t,
      ~state: widgetState,
      ~onCheck: WidgetInput.t => unit,
    ) =>
  switch (widget) {
  | InteractiveCode(widget) => <InteractiveCode widget />
  | MultipleChoices(widget) => <MultipleChoices widget state onCheck />
  | MarkdownText(widget) => <MarkdownText widget />
  };
