open Rationale;

[@react.component]
let make = (~title=?, ~isLoading=false, ~children) =>
  <div className="box">
    {
      title
      |> Option.map(t => <h6 className="title"> {React.string(t)} </h6>)
      |> Option.default(React.null)
    }
    <div className="content"> children </div>
    {
      isLoading ?
        <div className="loading-overlay"> <Spinner.InlineSpinner /> </div> :
        React.null
    }
  </div>;
