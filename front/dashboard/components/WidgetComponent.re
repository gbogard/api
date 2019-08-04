open Types;

module MultipleChoices = {
  [@react.component]
  let make = (~widget: Widget.MultipleChoices.t) =>
    <Box> {React.string(widget.question.value)} </Box>;
};

module InteractiveCode = {
  [@react.component]
  let make = (~widget: Widget.interactiveCode) =>
    <Box> {React.string("Interactive code")} </Box>;
};

module MarkdownText = {
  [@react.component]
  let make = (~widget: Widget.markdownText) =>
    <div> {React.string(widget.content)} </div>;
};

[@react.component]
let make = (~widget: Types.Widget.t) =>
  switch (widget) {
  | InteractiveCode(widget) => <InteractiveCode widget />
  | MultipleChoices(widget) => <MultipleChoices widget />
  | MarkdownText(widget) => <MarkdownText widget />
  };
