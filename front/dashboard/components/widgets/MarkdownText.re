open Types;

[@react.component]
let make = (~widget: Widget.markdownText) =>
  <div
    dangerouslySetInnerHTML={
      "__html": Interop.Marked.renderAndSanitize(widget.content),
    }
  />;
