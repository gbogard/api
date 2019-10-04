open Types;

[@react.component]
let make = (~widget: Widget.markdownText) => {
  React.useEffect0(() => {
    Interop.Prism.highlightAll();
   None; 
  });
<div
    dangerouslySetInnerHTML={
      "__html": Interop.Marked.renderAndSanitize(widget.content),
    }
  />
};
