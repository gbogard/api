open Types;

[@react.component]
let make =
    (
      ~widget: Types.Widget.t,
      ~state: widgetState,
      ~onCheck: WidgetInput.t => unit,
      ~resetWidget: unit => unit,
    ) =>
  switch (widget) {
  | InteractiveCode(widget) => <InteractiveCode widget state onCheck />
  | MultipleChoices(widget) =>
    <MultipleChoices widget state onCheck resetWidget />
  | MarkdownText(widget) => <MarkdownText widget />
  };
