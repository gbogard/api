[@react.component]
let make = (~href: string, ~className="", ~children) =>
  if (href.[0] === '/') {
    let href =
      href
      |> Js.String.split("/")
      |> Array.to_list
      |> (
        path =>
          List.concat([Configuration.dashboardBasePath, path])
          |> String.concat("/")
      );
    <a className onClick={_ => ReasonReactRouter.push(href)}> children </a>;
  } else {
    <a className href> children </a>;
  };
