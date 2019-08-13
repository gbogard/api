open Types;
open WidgetInput;

module MultipleChoices = {
  [@react.component]
  let make =
      (
        ~widget: Widget.MultipleChoices.t,
        ~state: widgetState,
        ~onCheck: WidgetInput.t => unit,
        ~resetWidget,
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

    let selectProposition = (id, _) => {
      resetWidget();
      setCurrentlySelectedAnswer(_ => Some(id));
    };

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
           let className =
             switch (state, currentlySelectedAnswer) {
             | (Right(_), Some(id)) when id === proposition.id => "text-success"
             | (Wrong(_), Some(id)) when id === proposition.id => "text-danger"
             | _ => ""
             };

           <div className="radio" key=elementId>
             <label htmlFor=elementId className>
               <input
                 id=elementId
                 type_="radio"
                 disabled={Utils.WidgetState.isRight(state)}
                 value={string_of_int(proposition.id)}
                 checked={currentlySelectedAnswer === Some(proposition.id)}
                 onChange={selectProposition(proposition.id)}
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
      <br />
      <button
        className="button button-info"
        onClick={_ => checkWidget()}
        disabled={Utils.WidgetState.isRight(state)}>
        {React.string("Check!")}
      </button>
      {
        Utils.WidgetState.isRight(state) ?
          <i className="ion ion-ios-checkmark-circle text-success" /> :
          React.null
      }
    </Box>;
  };
};

module InteractiveCode = {
  [@react.component]
  let make =
      (
        ~widget: Widget.interactiveCode,
        ~state: widgetState,
        ~onCheck: WidgetInput.t => unit,
      ) => {
    let editorRef = React.useRef(Js.Nullable.null);

    let (editorContent, setEditorContent) =
      React.useState(() => widget.defaultValue);

    React.useEffect0(() => {
      let editor =
        editorRef
        |> React.Ref.current
        |> Js.Nullable.toOption
        |> Belt.Option.getExn
        |> Interop.Ace.create(
             _,
             Interop.Ace.options(
               ~mode="ace/mode/scala",
               ~fontSize=".75rem",
               ~theme="ace/theme/chrome",
               (),
             ),
             widget.defaultValue,
           );

      editor##on("change", () => setEditorContent(_ => editor##getValue()));
      None;
    });

    let checkWidget = _ =>
      onCheck(
        WidgetInput.CodeInput({code: editorContent, language: Scala2}),
      );

    <div className="code-widget">
      <div className="topbar">
        <button onClick=checkWidget> {React.string("run")} </button>
      </div>
      <div className="editor" ref={ReactDOMRe.Ref.domRef(editorRef)} />
    </div>;
  };
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
      ~resetWidget: unit => unit,
    ) =>
  switch (widget) {
  | InteractiveCode(widget) => <InteractiveCode widget state onCheck />
  | MultipleChoices(widget) =>
    <MultipleChoices widget state onCheck resetWidget />
  | MarkdownText(widget) => <MarkdownText widget />
  };
