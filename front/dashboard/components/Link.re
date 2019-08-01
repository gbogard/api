[@react.component]
let make = (~href: string, ~className="", ~children) =>
  if (href.[0] === '/') {
    <a className onClick={_ => ReasonReactRouter.push(href)}> children </a>;
  } else {
    <a className href> children </a>;
  };
