open Types;
open WidgetInput;

let countLines = str => Array.length(Js.String.split("\n", str));

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

let fontSizeRem = 0.75;

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
    getEditorElement()
    |> Interop.Ace.create(
         _,
         Interop.Ace.options(
           ~mode="ace/mode/scala",
           ~fontSize=Js.Float.toString(fontSizeRem) ++ "rem",
           ~theme="ace/theme/chrome",
           ~maxLines=80,
           (),
         ),
         widget.defaultValue,
       )
    |> ignore;

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
        variant=Button.Info onClick=checkWidget isLoading={state === Pending}>
        <i className="ion ion-ios-play" />
        {React.string("Run")}
      </Button>
      <Button onClick=resetWidget>
        <i className="ion ion-ios-refresh" />
      </Button>
    </div>
    <div className="editor " ref={ReactDOMRe.Ref.domRef(editorRef)} />
    {isOutputOpen
       ? <div className="output">
           <i
             className="ion ion-ios-close-circle"
             onClick={_ => setIsOutputOpen(_ => false)}
           />
           <div className="content"> {renderOutput(state)} </div>
         </div>
       : React.null}
  </div>;
};