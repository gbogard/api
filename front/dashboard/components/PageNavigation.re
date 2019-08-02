open Types;

[@react.component]
let make = (~pages: list(Page.t), ~currentPageId, ~onChange) =>
  <div className="page-navigation bg-white">
    <ul>
      {
        pages
        |> List.map(p => {
             let (id, title) = (
               Utils.Page.extractId(p),
               Utils.Page.extractTitle(p),
             );
             <li onClick={_ => onChange(id)}> {React.string(title)} </li>;
           })
        |> Array.of_list
        |> React.array
      }
    </ul>
  </div>;
