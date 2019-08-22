open Types;
open WidgetInput;

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
