module Router = {
  [@react.component]
  let make = () => {
    let url = ReasonReactRouter.useUrl();

    switch url.path {
    | [] => <CoursesScene />
    | _ => React.string("Not found")
    };
  }
}

ReactDOMRe.renderToElementWithId(<Router />, "app");