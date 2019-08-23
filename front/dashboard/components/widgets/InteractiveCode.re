open Types;
open WidgetInput;
open Css;

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

let editorHeight = content => {
  let lines = countLines(content);
  let maxHeight = 25;
  Js.Math.min_int(lines + 2, maxHeight);
};

[@react.component]
let make =
    (
      ~widget: Widget.interactiveCode,
      ~state: widgetState,
      ~onCheck: WidgetInput.t => unit,
    ) => {
  let editorRef = React.useRef(Js.Nullable.null);

  let (isOutputOpen, setIsOutputOpen) = React.useState(() => false);
  let (edtitorContent, setEditorContent) =
    React.useState(() => widget.defaultValue);

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
             ~fontSize=Js.Float.toString(fontSizeRem) ++ "rem",
             ~theme="ace/theme/chrome",
             (),
           ),
           widget.defaultValue,
         );

    editor##on("change", () => {
      setEditorContent(_ => editor##getValue());
      editor##resize();
    });

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

  let editorClass =
    style([height(rem(editorHeight(edtitorContent) |> float_of_int))]);

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
    <div
      className={"editor " ++ editorClass}
      ref={ReactDOMRe.Ref.domRef(editorRef)}
    />
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
