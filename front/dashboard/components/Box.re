open Rationale;

[@react.component]
let make = (~title=?, ~children) =>
  <div className="box">
    {
      title
      |> Option.map(t => <h6 className="title"> {React.string(t)} </h6>)
      |> Option.default(React.null)
    }
    <div className="content"> children </div>
  </div>;
