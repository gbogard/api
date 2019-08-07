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

    let (propositions, _) =
      React.useState(() =>
        List.append(
          [widget.question.rightAnswer],
          widget.question.otherPropositions,
        )
        |> Belt.List.shuffle
      );

    let checkWidget = () =>
      switch (currentlySelectedAnswer) {
      | Some(id) => onCheck(MultipleChoicesInput({answerId: id}))
      | None => ()
      };

    let items =
      propositions
      |> List.map((proposition: Widget.MultipleChoices.proposition) => {
           let elementId =
             widget.id ++ "--mc-radio--" ++ string_of_int(proposition.id);

           <div className="radio" key=elementId>
             <label htmlFor=elementId>
               <input
                 id=elementId
                 type_="radio"
                 value={string_of_int(proposition.id)}
                 checked={currentlySelectedAnswer === Some(proposition.id)}
                 onChange={
                   _ => setCurrentlySelectedAnswer(_ => Some(proposition.id))
                 }
               />
               {React.string(" " ++ proposition.value)}
             </label>
           </div>;
         })
      |> Array.of_list
      |> React.array;

    <Box isLoading={state === Pending}>
      <h5> {React.string(widget.question.value)} </h5>
      items
      <button onClick={_ => checkWidget()}> {React.string("Check!")} </button>
      {React.string("State :")}
      {
        switch (state) {
        | Right(output) => React.string("Right")
        | Wrong(_) => React.string("Wrong")
        | Pending => React.string("Pending")
        | _ => React.string("Initial")
        }
      }
    </Box>;
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
