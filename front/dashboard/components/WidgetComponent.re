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
             | (Right(_), Some(id)) when id === proposition.id => "text-success text-bold"
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
      <Button
        variant=Button.Info
        onClick={_ => checkWidget()}
        disabled={Utils.WidgetState.isRight(state)}>
        {React.string("Check!")}
      </Button>
      {
        Utils.WidgetState.isRight(state) ?
          <i className="ion ion-ios-checkmark-circle text-success" /> :
          React.null
      }
    </Box>;
  };
};

module InteractiveCode = {
  let lineBreaks = str =>
    str
    |> Js.String.split("\n")
    |> Array.map(line => <span> {React.string(line)} <br /> </span>)
    |> React.array;

  let renderOutput =
    fun
    | Right(CodeOutput(output)) => lineBreaks(output)
    | Wrong(CodeOutput(output)) => lineBreaks(output)
    | _ => <Spinner.InlineSpinner />;

  [@react.component]
  let make =
      (
        ~widget: Widget.interactiveCode,
        ~state: widgetState,
        ~onCheck: WidgetInput.t => unit,
      ) => {
    let editorRef = React.useRef(Js.Nullable.null);

    let (isOutputOpen, setIsOutputOpen) = React.useState(() => false);

    let getEditorElement = () =>
      editorRef
      |> React.Ref.current
      |> Js.Nullable.toOption
      |> Belt.Option.getExn;

    React.useEffect0(() => {
      let editor =
        getEditorElement()
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

      None;
    });

    React.useEffect1(
      () => {
        if (state !== Initial && state !== Pending) {
          setIsOutputOpen(_ => true);
        };
        None;
      },
      [|state|],
    );

    let checkWidget = _ => {
      let code = Interop.Ace.aceEditor##edit(getEditorElement())##getValue();
      onCheck(WidgetInput.CodeInput({code, language: Scala2}));
    };

    let resetWidget = _ => {
      setIsOutputOpen(_ => false);
      Interop.Ace.aceEditor##edit(getEditorElement())##setValue(
        widget.defaultValue,
        1,
      );
    };

    <div className="code-widget">
      <div className="topbar">
        <Button
          variant=Button.Info
          onClick=checkWidget
          isLoading={state === Pending}>
          <i className="ion ion-ios-play" />
          {React.string("Run")}
        </Button>
        <Button onClick=resetWidget>
          <i className="ion ion-ios-refresh" />
        </Button>
      </div>
      <div className="editor" ref={ReactDOMRe.Ref.domRef(editorRef)} />
      {
        isOutputOpen ?
          <div className="output">
            <i
              className="ion ion-ios-close-circle"
              onClick={_ => setIsOutputOpen(_ => false)}
            />
            <div className="content"> {renderOutput(state)} </div>
          </div> :
          React.null
      }
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
