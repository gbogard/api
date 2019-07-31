module Router = {
  [@react.component]
  let make = () => {
    let url = ReasonReactRouter.useUrl();
    let urlToMatchAgainst =
      url.path
      ->Belt.List.drop(List.length(Configuration.dashboardBasePath))
      ->Belt.Option.getWithDefault([]);

    switch (urlToMatchAgainst) {
    | [] => <CoursesScene />
    | ["courses", id] => <SingleCourseScene id />
    | _ => React.string("Not found")
    };
  };
};

let app = <Store.Provider> <Router /> </Store.Provider>;

ReactDOMRe.renderToElementWithId(app, "app");
