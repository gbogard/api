open Types;

[@react.component]
let make = (~page: Page.codePage) =>
  <div> <h2> {React.string(page.title)} </h2> </div>;