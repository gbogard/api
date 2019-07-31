module Router = {
  [@react.component]
  let make = () => {
    let url = ReasonReactRouter.useUrl();
    let extractBasePathRegex = [%bs.re "/https?:\/\/.*?\/(.*)/"];
    let basePathLength =
      extractBasePathRegex
      ->Js.Re.exec_(Configuration.dashboardUrl)
      ->Belt.Option.flatMap(result =>
          Js.Re.captures(result)[1] |> Js.Nullable.toOption
        )
      ->Belt.Option.map(path => Js.String.split("/", path) |> Array.length)
      ->Belt.Option.getWithDefault(0);

    let urlToMatchAgainst =
      url.path
      ->Belt.List.drop(basePathLength)
      ->Belt.Option.getWithDefault([]);

    Js.log(Array.of_list(url.path));
    Js.log(Array.of_list(urlToMatchAgainst));

    switch (urlToMatchAgainst) {
    | [] => <CoursesScene />
    | ["courses", id] => <SingleCourseScene id />
    | _ => React.string("Not found")
    };
  };
};

let app = <Store.Provider> <Router /> </Store.Provider>;

ReactDOMRe.renderToElementWithId(app, "app");
